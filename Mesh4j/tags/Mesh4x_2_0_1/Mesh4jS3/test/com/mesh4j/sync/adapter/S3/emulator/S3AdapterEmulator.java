package com.mesh4j.sync.adapter.S3.emulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mesh4j.sync.adapters.S3.ObjectData;
import org.mesh4j.sync.adapters.S3.S3Adapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;

public class S3AdapterEmulator extends S3Adapter {

	public S3AdapterEmulator(String bucket, String objectPath, S3Service s3,
			IIdentityProvider identityProvider) {
		super(bucket, objectPath, s3, identityProvider);		
	}

	public Set<ObjectData> getObjects(String syncId, String node) {
		List<ObjectData> objs = ((S3Service)this.s3).readObjectsStartsWith(node, this.bucket, getS3OID(syncId));
		TreeSet<ObjectData> orderedObjs = new TreeSet<ObjectData>(OBJECT_DATA_ORDER_BY_HISTORY_DESC);
		orderedObjs.addAll(objs);
		return orderedObjs;
	}

	public List<Item> getBranches(String syncId, String node) {
		Set<ObjectData> objs = this.getObjects(syncId, node);
		
		ArrayList<Item> branches = new ArrayList<Item>();
		Item item;
		for (ObjectData obj : objs) {
			item = parseItem(obj.getData());
			Item branch = getBranch(branches, item);
			
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

	public Item get(String syncId, String node) {
		List<Item> branches = this.getBranches(syncId, node);
		return getItemFromBranches(branches);
	}

}
