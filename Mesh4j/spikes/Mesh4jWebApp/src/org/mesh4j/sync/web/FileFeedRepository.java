package org.mesh4j.sync.web;

import java.io.File;

import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;

public class FileFeedRepository extends AbstractFeedRepository {
	
	// MODEL VARIABLES
	private String rootPath = "";
	
	// BUSINESS METHODS
	
	public FileFeedRepository(String rootPath){
		super();
		this.rootPath = rootPath;
		Feed feed = new Feed("Mesh", "Available Mesh", "mesh4x/feeds", RssSyndicationFormat.INSTANCE);
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
	protected void addNewFeed(String sourceID, Feed feed, ISyndicationFormat syndicationFormat) {
		String feedFileName = this.getFeedFileName(sourceID);
		new FeedAdapter(feedFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, syndicationFormat, feed);
	}

	@Override
	protected FeedAdapter getParentSyncAdapter(String sourceID) {
		String parentFileName = this.getParentFileName(sourceID);
		return new FeedAdapter(parentFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Override
	protected FeedAdapter getSyncAdapter(String sourceID) {
		String feedFileName = this.getFeedFileName(sourceID);
		
		FeedAdapter adapter = new FeedAdapter(feedFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		adapter.refresh();
		return adapter;
	}
	
	@Override
	public void cleanFeed(String sourceID) {
		FeedAdapter feedAdapter = (FeedAdapter) getSyncAdapter(sourceID);
		feedAdapter.getFeed().deleteAllItems();
		feedAdapter.flush();
	}

//	@Override
//	protected void basicRemoveFeed(String sourceID) {
//		String feedFileName = this.getFeedFileName(sourceID);
//		File file = new File(feedFileName);
//		if(file.exists()){
//			file.delete();
//		}
//	}
}