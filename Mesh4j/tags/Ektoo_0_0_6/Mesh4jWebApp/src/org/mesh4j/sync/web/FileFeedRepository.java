package org.mesh4j.sync.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.history.FeedHistoryRepository;
import org.mesh4j.sync.adapters.history.HistoryChangeContentWriter;
import org.mesh4j.sync.adapters.history.HistorySyncAdapter;
import org.mesh4j.sync.adapters.history.IHistoryRepository;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class FileFeedRepository extends AbstractFeedRepository {
	
	// MODEL VARIABLES
	private String rootPath = "";
	
	// BUSINESS METHODS
	
	public FileFeedRepository(String rootPath){
		super();
		this.rootPath = rootPath;
		Feed feed = new Feed("Mesh", "Available Mesh", "mesh4x/feeds");
		new FeedAdapter(this.rootPath + "mesh.xml", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
	}	
	
	public boolean existsFeed(String sourceID) {
		if(sourceID == null){
			return false;
		}
		
		String feedFileName = getFeedFileName(sourceID);
		File file = new File(feedFileName);
		return file.exists();
	}
	
	private String getParentFileName(String sourceID) {
		if(sourceID == null){
			return null;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				return this.rootPath + "mesh.xml";
			} else {
				String[] sourceIds = sourceID.split("/");
				return this.rootPath + "mesh_" + sourceIds[0] + ".xml";
			}
		}
	}

	private String getFeedFileName(String sourceID) {
		if(sourceID == null){
			return this.rootPath + "mesh.xml";
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				String normalizedSourceID = sourceID.replaceAll("/", "");
				return this.rootPath + "mesh_" + normalizedSourceID + ".xml";
			} else {
				String[] sourceIds = sourceID.split("/");
				return this.rootPath + "mesh_" + sourceIds[0] + "_feed_" + sourceIds[1] + ".xml";
			}
		}
	}

	@Override
	protected void addNewFeed(String sourceID, Feed feed, ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider) {
		String feedFileName = this.getFeedFileName(sourceID);
		new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE, syndicationFormat, feed);
	}

	@Override
	protected FeedAdapter getParentSyncAdapter(String sourceID, IIdentityProvider identityProvider) {
		String parentFileName = this.getParentFileName(sourceID);
		return new FeedAdapter(parentFileName, identityProvider, IdGenerator.INSTANCE);
	}
	
	@Override
	protected ISyncAdapter getSyncAdapter(String sourceID, IIdentityProvider identityProvider) {
		FeedAdapter feedAdapter = getFeedAdapter(sourceID, identityProvider);
		feedAdapter.refresh();
		
		IHistoryRepository historyRepository = getFeedHistoryRepository(sourceID, "");
		if(historyRepository == null){
			return feedAdapter;
		} else {
			HistorySyncAdapter historyAdapter = new HistorySyncAdapter(feedAdapter, historyRepository);
			return historyAdapter;
		}
	}
	
	protected FeedAdapter getFeedAdapter(String sourceID, IIdentityProvider identityProvider) {
		String feedFileName = this.getFeedFileName(sourceID);
		
		FeedAdapter adapter = new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE);
		adapter.refresh();
		return adapter;
	}
	
	@Override
	public void cleanFeed(String sourceID) {
		FeedAdapter feedAdapter = getFeedAdapter(sourceID, NullIdentityProvider.INSTANCE);
		feedAdapter.getFeed().deleteAllItems();
		feedAdapter.flush();
	}

	@Override
	protected void basicDeleteFeed(String sourceID) {
		String feedFileName = this.getFeedFileName(sourceID);
		File file = new File(feedFileName);
		if(file.exists()){
			file.delete();
		}
	}

	@Override
	public String getHistory(String sourceID, String link, ISyndicationFormat syndicationFormat, String syncId){
		List<Item> items;
		FeedHistoryRepository repo = getFeedHistoryRepository(sourceID, link);
		if(repo == null){
			items = new ArrayList<Item>();
		} else {
			items = repo.getHistoryItems(syncId);
		}
		
		String title = getFeedTitle(sourceID) + " - History for syncId: " + syncId;
		Feed feed = new Feed(title, "History changes", link);
		feed.addItems(items);
		FeedWriter writer = new FeedWriter(syndicationFormat, NullIdentityProvider.INSTANCE, new HistoryChangeContentWriter());
		try {
			return writer.writeAsXml(feed);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private FeedHistoryRepository getFeedHistoryRepository(String sourceID, String link) {
		String fileName = getFeedHistoryFileName(sourceID);
		if(fileName == null){
			return null;
		} else {
			FeedHistoryRepository repo = new FeedHistoryRepository(sourceID, "History changes", link, fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE);
			return repo;
		}
	}
	
	private String getFeedHistoryFileName(String sourceID) {
		if(sourceID == null){
			return null;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				return null;
			} else {
				String[] sourceIds = sourceID.split("/");
				return this.rootPath + "mesh_" + sourceIds[0] + "_feed_" + sourceIds[1] + "_history.xml";
			}
		}
	}


}