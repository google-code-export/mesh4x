package org.mesh4j.sync.adapters.feed.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.MeshException;

public class ZipFeedsSyncAdapter implements ISyncAdapter, ISyncAware{
	
	// MODEL VARIABLES
	private CompositeSyncAdapter compositeSyncAdapter;
	private File zipFile;
	private IIdentityProvider identityProvider;
	private File tempDirectory;

	// BUSINESS METHODS
	
	public ZipFeedsSyncAdapter(String zipFileName, IIdentityProvider identityProvider, String tempDirectoryName) {
		super();
		this.identityProvider = identityProvider;
		this.zipFile = new File(zipFileName);
		
		String entryName = this.zipFile.getName().substring(0, this.zipFile.getName().length() - 4);
		this.tempDirectory = FileUtils.getFile(tempDirectoryName, entryName);

		this.compositeSyncAdapter  = makeCompositeAdapterFromZip();
	}
	
	@Override
	public void add(Item item) {
		this.compositeSyncAdapter.add(item);		
	}
	
	@Override
	public void delete(String id) {
		this.compositeSyncAdapter.delete(id);		
	}
	
	@Override
	public Item get(String id) {
		return this.compositeSyncAdapter.get(id);
	}
	
	@Override
	public List<Item> getAll() {
		return this.compositeSyncAdapter.getAll();
	}
	
	@Override
	public List<Item> getAll(IFilter<Item> filter) {
		return this.compositeSyncAdapter.getAll(filter);
	}
	
	@Override
	public List<Item> getAllSince(Date since) {
		return this.compositeSyncAdapter.getAllSince(since);
	}
	
	@Override
	public List<Item> getAllSince(Date since, IFilter<Item> filter) {
		return this.compositeSyncAdapter.getAllSince(since, filter);
	}
	
	@Override
	public List<Item> getConflicts() {
		return this.compositeSyncAdapter.getConflicts();
	}
	
	@Override
	public String getFriendlyName() {
		return this.compositeSyncAdapter.getFriendlyName();
	}
	
	@Override
	public void update(Item item) {
		this.compositeSyncAdapter.update(item);
	}
	
	@Override
	public void update(Item item, boolean resolveConflicts) {
		this.compositeSyncAdapter.update(item, resolveConflicts);
	}
	
	@Override
	public void beginSync() {
		this.compositeSyncAdapter = makeCompositeAdapterFromZip();
		this.compositeSyncAdapter.beginSync();
	}
	
	@Override
	public void endSync() {
		this.compositeSyncAdapter.endSync();
		this.flush();
		this.removeTempDirectory();
	}

	private void flush() {
		try{
			Map<String, byte[]> entries = new HashMap<String, byte[]>();
			for (IIdentifiableSyncAdapter adapter : this.compositeSyncAdapter.getAdapters()){
				FeedAdapter feedAdapter = (FeedAdapter)((IdentifiableSyncAdapter)adapter).getSyncAdapter();
				File feedFile = feedAdapter.getFile();
				entries.put(feedFile.getName(), FileUtils.read(feedFile));
			}
			
			ZipUtils.write(this.zipFile, entries);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private CompositeSyncAdapter makeCompositeAdapterFromZip(){
		try{
			this.createTempDirectory();
			Map<String, byte[]> entries = new HashMap<String, byte[]>();
			if(this.zipFile.exists()){
				entries = ZipUtils.getEntries(this.zipFile);
			}
			
			List<IIdentifiableSyncAdapter> adapters = new ArrayList<IIdentifiableSyncAdapter>();

			for (String entryName : entries.keySet()) {
				
				String fileName = FileUtils.getFileName(tempDirectory.getCanonicalPath(), entryName);
				File feedFile = new File(fileName);
				FileUtils.write(feedFile, entries.get(entryName));
				
				String dataset = feedFile.getName().substring(0, feedFile.getName().length() - 4);
				IdentifiableSyncAdapter adapter = makeNewSyncAdapter(fileName, dataset);
				adapters.add(adapter);
			}
			
			ZipFeedsOpaqueSyncAdapter opaqueAdapter = new ZipFeedsOpaqueSyncAdapter(this);
			return new CompositeSyncAdapter(this.zipFile.getName(), opaqueAdapter, this.identityProvider, adapters.toArray(new IIdentifiableSyncAdapter[0]));
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public void addNewSyncAdapter(String datasetName) {
		try{
			String dataset = datasetName.toLowerCase().endsWith(".xml") ? datasetName.substring(0, datasetName.length() -4): datasetName;
			String feedFileName = datasetName.toLowerCase().endsWith(".xml") ? datasetName : datasetName + ".xml";
	
			String fileName = FileUtils.getFileName(this.tempDirectory.getCanonicalPath(), feedFileName);
			FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName, this.identityProvider);
			IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(dataset, feedAdapter);
			
			adapter.beginSync();
			this.compositeSyncAdapter.addSyncAdapter(adapter);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private IdentifiableSyncAdapter makeNewSyncAdapter(String fileName, String dataset) {
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName, this.identityProvider);
		IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(dataset, feedAdapter);
		return adapter;
	}

	private void createTempDirectory() {
		FileUtils.delete(this.tempDirectory);		
		this.tempDirectory.mkdirs();
	}
	
	private void removeTempDirectory() {
		FileUtils.delete(this.tempDirectory);
	}

	public CompositeSyncAdapter getCompositeAdapter() {
		return this.compositeSyncAdapter;
	}
	

}
