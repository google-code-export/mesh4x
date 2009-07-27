package org.mesh4j.sync.adapters.feed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;

import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class FeedWriter {

	// MODEL VARIABLES
	ISyndicationFormat syndicationFormat;
	IIdentityProvider identityProvider;
	
	// BUSINESS METHODS

	public FeedWriter(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.syndicationFormat = syndicationFormat;
		this.identityProvider = identityProvider;
	}
	
	public void write(Writer writer, Feed feed) throws Exception{
		
		this.syndicationFormat.writeStartDocument(writer);
		
		this.syndicationFormat.addFeedInformation(writer, feed.getTitle(), feed.getDescription(), feed.getLink(), feed.getLastUpdate());
		
		if(feed.getPayload() != null && feed.getPayload().trim().length() > 0){
			writer.write(feed.getPayload());
		}
		
		for (Item item : feed.getItems()) {
			write(writer, item);
		}
        
		this.syndicationFormat.writeEndDocument(writer);
	}	
	
	public void write(Writer writer, Item item) throws Exception 
	{
		this.syndicationFormat.writeStartItem(writer, item);
		
		this.writeContent(writer, item);
		
		String by = this.identityProvider.getAuthenticatedUser();
		History lastUpdate = item.getLastUpdate();
		if (lastUpdate != null && lastUpdate.getBy() != null){
			by = lastUpdate.getBy();
		}
		
		this.syndicationFormat.addAuthorElement(writer, by);
		
		if(item.getSync() != null){
			writeSync(writer, item.getSync());
		}
		
		this.syndicationFormat.writeEndItem(writer);
	}	
		
	private void writeContent(Writer writer, Item item) throws Exception {
		item.getContent().addToFeedPayload(writer, item, this.syndicationFormat);
	}

	public void writeSync(Writer writer, Sync sync) throws Exception {		

		writer.write("<sx:sync ");
		writer.write(ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID);
		writer.write("=\"");
		writer.write(sync.getId());
		writer.write("\" ");
		writer.write(ISyndicationFormat.SX_ATTRIBUTE_SYNC_UPDATES);
		writer.write("=\"");
		writer.write(String.valueOf(sync.getUpdates()));
		writer.write("\" ");
		writer.write(ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED);
		writer.write("=\"");
		writer.write(String.valueOf(sync.isDeleted()));
		writer.write("\" ");
		writer.write(ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS);
		writer.write("=\"");
		writer.write(String.valueOf(sync.isNoConflicts()));
		writer.write("\" ");
		writer.write(">");
		
		int size = sync.getUpdatesHistory().size();
		for (int i = 0; i < size; i++) {
			History history = sync.getUpdatesHistory().elementAt(size - i -1);
			writeHistory(writer, history);
		}
				
		if (sync.getConflicts().size() > 0) {
			writer.write("<sx:conflicts>");
			for (Item item : sync.getConflicts()) {
				write(writer, item);
			}
			writer.write("</sx:conflicts>");
		}
		
		writer.write("</sx:sync>");
	}
		
	public void writeHistory(Writer writer, History history) throws IOException
	{
		writer.write("<sx:history ");
		writer.write(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE);
		writer.write("=\"");
		writer.write(String.valueOf(history.getSequence()));
		writer.write("\" ");
		if (history.getWhen() != null){
			writer.write(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN);
			writer.write("=\"");
			writer.write(this.syndicationFormat.formatDate(history.getWhen()));
			writer.write("\" ");
		}
		writer.write(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY);
		writer.write("=\"");
		writer.write(history.getBy());
		writer.write("\" ");
		writer.write("/>");
	}

	public String writeSyncAsXml(Sync sync) throws Exception {
		ByteArrayOutputStream baosSync = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baosSync);
		
		try{
			this.writeSync(writer, sync);
			writer.flush();
			baosSync.flush();
		}finally{			
			writer.close();			
			baosSync.close();
		}		
		return new String(baosSync.toByteArray());
	}

	public String writeAsXml(Feed feed) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		try{
			this.write(writer, feed);
			writer.flush();
			baos.flush();
			return new String(baos.toByteArray());
		} finally{
			writer.close();
			baos.close();
		}
	}

	public static void logAsFeed(String header, Vector<Item> items) {
		try{
			System.out.println(header + " --------------------------------------------------");
			Feed feed = new Feed();
			feed.addItems(items);
			
			FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
			System.out.println(writer.writeAsXml(feed));
			System.out.println("--------------------------------------------------");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
