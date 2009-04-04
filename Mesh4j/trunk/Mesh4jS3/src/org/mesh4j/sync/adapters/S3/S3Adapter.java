package org.mesh4j.sync.adapters.S3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class S3Adapter extends AbstractSyncAdapter {

	private final static String NAME = "S3";
	
	protected final Comparator<ObjectData> OBJECT_DATA_ORDER_BY_HISTORY_DESC = new Comparator<ObjectData>(){
		@Override
		public int compare(ObjectData o1, ObjectData o2) {
			return o1.getId().compareTo(o2.getId()) * -1;
		}		
	};
	
	private final Comparator<Item> ITEM_ORDER_BY_HISTORY_DESC = new Comparator<Item>(){
		@Override
		public int compare(Item o1, Item o2) {
			return (o1.getLastUpdate().getWhen().compareTo(o2.getLastUpdate().getWhen())) * -1;
		}		
	};
	
	// MODEL VARIABLES
	protected IS3Service s3;
	protected String bucket;
	protected String objectPath;
	private IIdentityProvider identityProvider;
	private FeedReader reader;
	private FeedWriter writer;
	
	// BUSINESS SERVICE
	public S3Adapter(String bucket, String objectPath, IS3Service s3, IIdentityProvider identityProvider){
		super();
		this.bucket = bucket;
		this.objectPath = objectPath;
		this.s3 = s3;
		this.identityProvider = identityProvider;
		
		this.reader = new FeedReader(getSyndicationFormat(), identityProvider, IdGenerator.INSTANCE, ContentReader.INSTANCE);
		this.writer = new FeedWriter(getSyndicationFormat(), identityProvider, ContentWriter.INSTANCE);
	}	

	private void writeItem(Item item) {
		String oid = this.getOID(item);
		byte[] data = this.getData(item);
		this.s3.writeObject(this.bucket, oid, data);
	}
	
	// ISyncAdapter METHODS

	@Override
	public void add(Item item) {
		Guard.argumentNotNull(item, "item");
		this.writeItem(item);		
	}
	
	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		this.writeItem(item);	
	}

	@Override
	public void delete(String syncId) {
		Guard.argumentNotNullOrEmptyString(syncId, "id");
		Item item = this.get(syncId);
		if(!item.isDeleted()){
			item.getSync().delete(this.getAuthenticatedUser(), new Date());
			this.writeItem(item);
		}
	}

	@Override
	public Item get(String syncId) {
		List<Item> branches = this.getBranches(syncId);
		return getItemFromBranches(branches);
	}

	public Set<ObjectData> getObjects(String syncId) {
		List<ObjectData> objs = this.s3.readObjectsStartsWith(this.bucket, getS3OID(syncId));
		TreeSet<ObjectData> orderedObjs = new TreeSet<ObjectData>(OBJECT_DATA_ORDER_BY_HISTORY_DESC);
		orderedObjs.addAll(objs);
		return orderedObjs;
	}

	public List<Item> getBranches(String syncId) {
		Set<ObjectData> objs = this.getObjects(syncId);
		
		ArrayList<Item> branches = new ArrayList<Item>();
		Item item;
		for (ObjectData obj : objs) {
			item = this.parseItem(obj.getData());
			Item branch = this.getBranch(branches, item);
			
			if(branch == null){
				branches.add(item);  // new branch
			} else {				// update with last history
				if(branch.getLastUpdate().getSequence() < item.getLastUpdate().getSequence()){
					branches.remove(branch);
					branches.add(item);
				}
			}
		}
		return branches;
	}
	
	protected Item getItemFromBranches(List<Item> branches) {
		if(branches.size() == 0){
			return null;
		} else if(branches.size() == 1){
			return branches.get(0);
		} else {
			Item mergedItem = null;
			for (Item item : branches) {
				if(mergedItem == null){
					mergedItem = item;
				} else {
					MergeResult result = MergeBehavior.merge(mergedItem, item);
					if(result.getOperation().isNone()){
						mergedItem = result.getOriginal();
					}else{
						mergedItem = result.getProposed();
					}
				}
			}
			return mergedItem;
		}
	}
	
	protected Item getBranch(ArrayList<Item> branches, Item item) {
		for (Item branch : branches) {
			if(Sync.isSameBranchHistory(branch.getSync().getUpdatesHistory(), item.getSync().getUpdatesHistory())){
				return branch;
			}
		}
		return null;
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) { 
		StringBuffer sb = new StringBuffer();
		sb.append(this.objectPath);
		if(!this.objectPath.endsWith(".")){
			sb.append(".");
		}
		
		List<ObjectData> objs = this.s3.readObjectsStartsWith(this.bucket, sb.toString());
		HashMap<String, ArrayList<Item>> allBranches = new HashMap<String, ArrayList<Item>>();
		
		for (ObjectData obj : objs) {
			Item item = this.parseItem(obj.getData());

			ArrayList<Item> branches = allBranches.get(item.getSyncId());
			if(branches==null){
				branches = new ArrayList<Item>();
				allBranches.put(item.getSyncId(), branches);
			}
			
			Item branch = this.getBranch(branches, item);
			
			if(branch == null){
				branches.add(item);  // new branch
			} else {				// update with last history
				if(branch.getLastUpdate().getSequence() < item.getLastUpdate().getSequence()){
					branches.remove(branch);
					branches.add(item);
				}
			}
		}
		
		ArrayList<Item> result = new ArrayList<Item>();
		for (String syncId : allBranches.keySet()) {
			ArrayList<Item> branches = allBranches.get(syncId);
			Item item = this.getItemFromBranches(branches);
			if(filter.applies(item) && SinceLastUpdateFilter.applies(item, since)){
				result.add(item);
			}
		}
		
		Collections.sort(result, ITEM_ORDER_BY_HISTORY_DESC);
		return result;
	}

	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

	@Override
	public String getFriendlyName() {
		return NAME;
	}
	
	public void purgeBranches(String syncId) {
		
		Set<ObjectData> objs = this.getObjects(syncId);
		
		TreeSet<String> branchesToDelete = new TreeSet<String>();
		ArrayList<Item> branches = new ArrayList<Item>();
		Item item;
		for (ObjectData obj : objs) {
			item = this.parseItem(obj.getData());
			Item branch = this.getBranch(branches, item);
			
			if(branch == null){
				branches.add(item);  // new branch
			} else {				
				if(branch.getLastUpdate().getSequence() < item.getLastUpdate().getSequence()){
					branches.remove(branch);   // update with last history
					branches.add(item);
					
					branchesToDelete.add(this.getOID(branch)); // delete old history
				} else {			
					branchesToDelete.add(obj.getId());	// delete old history
				}
			}
		}
		
		this.s3.deleteObjects(this.bucket, branchesToDelete);
	}

	// ACCESSORS

	private byte[] getData(Item item) {
		try{
			String xml = this.writer.writeAsXml(item);
			return xml.getBytes();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	protected Item parseItem(byte[] data) {
		try{
			String xml = new String(data);
			Document document = DocumentHelper.parseText(xml);
			Item item = this.reader.readItem(document.getRootElement());
			return item;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String getOID(Item item) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.objectPath);
		if(!this.objectPath.endsWith(".")){
			sb.append(".");
		}
		sb.append(item.getSyncId());
		sb.append(".");
		sb.append(item.getLastUpdate().getSequence());
		sb.append(".");
		sb.append(item.getLastUpdate().getBy());
		return sb.toString();
	}
	
	protected String getS3OID(String syncId) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.objectPath);
		if(!this.objectPath.endsWith(".")){
			sb.append(".");
		}
		sb.append(syncId);
		return sb.toString();
	}

	public ISyndicationFormat getSyndicationFormat() {
		return RssSyndicationFormat.INSTANCE;
	}
	
}