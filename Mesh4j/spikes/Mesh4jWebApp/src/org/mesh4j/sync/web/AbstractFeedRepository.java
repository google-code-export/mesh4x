package org.mesh4j.sync.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
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

public abstract class AbstractFeedRepository implements IFeedRepository{

	// MODEL VARIABLES
	private HashMap<ISyndicationFormat, FeedWriter> writers = new HashMap<ISyndicationFormat, FeedWriter>();
	private HashMap<ISyndicationFormat, FeedReader> readers = new HashMap<ISyndicationFormat, FeedReader>();
	
	// BUSINESS METHODS
	public AbstractFeedRepository(){
		super();
		this.writers.put(RssSyndicationFormat.INSTANCE, new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE));
		this.writers.put(AtomSyndicationFormat.INSTANCE, new FeedWriter(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE));
		
		this.readers.put(RssSyndicationFormat.INSTANCE, new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE));
		this.readers.put(AtomSyndicationFormat.INSTANCE, new FeedReader(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE));
	}
	
	
	protected abstract ISyncAdapter getSyncAdapter(String sourceID);
	protected abstract ISyncAdapter getParentSyncAdapter(String sourceID);
	protected abstract void addNewFeed(String sourceID, Feed feed, ISyndicationFormat syndicationFormat);

	
	private String writeFeedAsXml(Feed feed, ISyndicationFormat syndicationFormat){
		FeedWriter writer = (FeedWriter)this.writers.get(syndicationFormat);
		try {
			return writer.writeAsXml(feed);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String readFeed(String sourceID, String link, Date sinceDate, ISyndicationFormat syndicationFormat) {
		
		ISyncAdapter adapter = getSyncAdapter(sourceID);
		
		List<Item> items = adapter.getAllSince(sinceDate);

		String title = getFeedTitle(sourceID);
		Feed feed = new Feed(title, "", link, syndicationFormat);
		feed.addItems(items);
		
		String xml = writeFeedAsXml(feed, syndicationFormat);
		return xml;
	}
	
	public String synchronize(String sourceID, String link, String feedXml, ISyndicationFormat syndicationFormat) {
		
		Feed feedLoaded = this.readFeedFromXml(feedXml, syndicationFormat);		
		InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(sourceID, NullIdentityProvider.INSTANCE, feedLoaded.getItems());
		
		ISyncAdapter adapter = getSyncAdapter(sourceID);
		
		SyncEngine syncEngine = new SyncEngine(adapter, inMemoryAdapter);
		List<Item> conflicts = syncEngine.synchronize();
		
		String title = getFeedTitle(sourceID);
		Feed feedResult = new Feed(title, "conflicts", link, syndicationFormat);
		feedResult.addItems(conflicts);
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

	public void addNewFeed(String sourceID, ISyndicationFormat syndicationFormat, String link, String description) {
		String title = getFeedTitle(sourceID);
		Feed feed = new Feed(title, description, link, syndicationFormat);
		
		this.addNewFeed(sourceID, feed, syndicationFormat);
		
		XMLContent content = new XMLContent(sourceID, title, description, link, feed.getPayload());
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), NullIdentityProvider.INSTANCE.getAuthenticatedUser(), new Date(), false);
		Item item = new Item(content, sync);
		
		ISyncAdapter parentAdapter = this.getParentSyncAdapter(sourceID);
		parentAdapter.add(item);
	}

	protected String getFeedTitle(String sourceID) {
		if(sourceID == null){
			return "Mesh";
		} else {
			String[] sourceIds = sourceID.split("/");
			return sourceIds[sourceIds.length -1];
		}
	}

	public boolean isAddNewFeedAction(String sourceID) {
		return sourceID == null || (sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length());
	}
	
	private Feed readFeedFromXml(String xml, ISyndicationFormat syndicationFormat){
		FeedReader reader = (FeedReader)this.readers.get(syndicationFormat);
		try {
			return reader.read(xml);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
