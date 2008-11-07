package org.mesh4j.sync.web;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class SyncEngineManager {
	
	// MODEL VARIABLES
	private HashMap<ISyndicationFormat, FeedWriter> writers = new HashMap<ISyndicationFormat, FeedWriter>();
	private HashMap<ISyndicationFormat, FeedReader> readers = new HashMap<ISyndicationFormat, FeedReader>();
	private String rootPath = "";
	
	// BUSINESS METHODS
	
	public SyncEngineManager(String rootPath){
		
		super();
		this.writers.put(RssSyndicationFormat.INSTANCE, new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE));
		this.writers.put(AtomSyndicationFormat.INSTANCE, new FeedWriter(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE));
		
		this.readers.put(RssSyndicationFormat.INSTANCE, new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE));
		this.readers.put(AtomSyndicationFormat.INSTANCE, new FeedReader(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE));
		
		this.rootPath = rootPath;
	}	
	
	private String writeFeedAsXml(Feed feed, ISyndicationFormat syndicationFormat){
		FeedWriter writer = (FeedWriter)this.writers.get(syndicationFormat);
		try {
			return writer.writeAsXml(feed);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String readFeed(String sourceID, Date sinceDate, ISyndicationFormat syndicationFormat) {
		
		String feedFileName = this.getFeedFileName(sourceID);
		
		FeedAdapter adapter = new FeedAdapter(feedFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		adapter.refresh();
		
		List<Item> items = adapter.getAllSince(sinceDate);

		Feed feed = new Feed(items);
		feed.setPayload(adapter.getFeed().getPayload().createCopy());
		
		String xml = writeFeedAsXml(feed, syndicationFormat);
		return xml;
	}
	
	private Feed readFeedFromXml(String xml, ISyndicationFormat syndicationFormat){
		FeedReader reader = (FeedReader)this.readers.get(syndicationFormat);
		try {
			return reader.read(xml);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String synchronize(String sourceID, String feedXml, ISyndicationFormat syndicationFormat) {
		
		Feed feedLoaded = this.readFeedFromXml(feedXml, syndicationFormat);		
		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(sourceID, NullIdentityProvider.INSTANCE, feedLoaded.getItems());
		
		String feedFileName = this.getFeedFileName(sourceID);
		FeedAdapter adapter = new FeedAdapter(feedFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		adapter.refresh();
		
		SyncEngine syncEngine = new SyncEngine(adapter, inMemoryAdapter);
		List<Item> conflicts = syncEngine.synchronize();
					
		Feed feedResult = new Feed(conflicts);
		feedResult.setPayload(adapter.getFeed().getPayload().createCopy());
		return this.writeFeedAsXml(feedResult, syndicationFormat);
	}
	
	public ISyndicationFormat getSyndicationFormat(String formatName) {
		if(formatName == null){
			return RssSyndicationFormat.INSTANCE;
		}else if(RssSyndicationFormat.INSTANCE.getName().equals(formatName)){
			return RssSyndicationFormat.INSTANCE;
		}else if(AtomSyndicationFormat.INSTANCE.getName().equals(formatName)){
			return AtomSyndicationFormat.INSTANCE;
		} else {
			return null;
		}
	}

	public boolean isValidSourceID(String sourceID) {
		if(sourceID == null){
			return false;
		}
		
		String feedFileName = getFeedFileName(sourceID);
		File file = new File(feedFileName);
		return file.exists();
	}

	public void addNewFeed(String sourceID, ISyndicationFormat syndicationFormat, String link, String title, String description) {
		String feedFileName = this.getFeedFileName(sourceID);
		File file = new File(feedFileName);
		if(file.exists()){
			return;				// feed already exists
		}
		
		FeedAdapter adapter = new FeedAdapter(feedFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, syndicationFormat);
		Feed feed = adapter.getFeed();
		
		XMLContent content = new XMLContent(sourceID, title, description, feed.getPayload());
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), NullIdentityProvider.INSTANCE.getAuthenticatedUser(), new Date(), false);
		Item item = new Item(content, sync);
		
		String feedsFileName = this.getAllFeedsFileName();
		FeedAdapter adapterAllFeeds = new FeedAdapter(feedsFileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		adapterAllFeeds.add(item);
	}

	private String getAllFeedsFileName() {
		return this.rootPath + "feeds.xml";
	}	

	private String getFeedFileName(String sourceID) {
		if(sourceID == null){
			return this.getAllFeedsFileName();
		} else {
			return this.rootPath + sourceID + ".xml";
		}
	}
}