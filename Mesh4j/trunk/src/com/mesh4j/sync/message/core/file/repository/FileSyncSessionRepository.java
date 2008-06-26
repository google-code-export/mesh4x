package com.mesh4j.sync.message.core.file.repository;

import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.SyncSession;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class FileSyncSessionRepository {

	// CONSTANTS
	private final static String ELEMENT_SYNC_SESSION = "session";
	
	// MODEL VARIANBLES
	private String rootDirectory;
	private FeedWriter feedWriter;
	private FeedReader feedReader;
	
	// BUSINESS METHODS

	public FileSyncSessionRepository(String rootDirectory) {
		Guard.argumentNotNullOrEmptyString(rootDirectory, "rootDirectory");
		
		this.rootDirectory = rootDirectory;
		this.feedWriter = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		this.feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
	}
	
	public List<Item> readSnapshot(String sessionId){
		try {
			File file = new File(this.rootDirectory + sessionId + "_snapshot.xml");
			Feed feed = this.feedReader.read(file);
			return feed.getItems();
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public void snapshot(ISyncSession syncSession){
		File file = new File(this.rootDirectory + syncSession.getSessionId() + "_snapshot.xml");
		List<Item> items = syncSession.getSnapshot();
		write(syncSession, file, items);
	}
	
	public void flush(ISyncSession syncSession){
		File file = new File(this.rootDirectory + syncSession.getSessionId() + "_current.xml");
		List<Item> items = syncSession.getCurrentSnapshot();
		write(syncSession, file, items);
	}

	private void write(ISyncSession syncSession, File file, List<Item> items) {
		try{
			Feed feed = new Feed(items);
			feed.setPayload(this.createPayload(syncSession));
			
			XMLWriter writer = new XMLWriter(new FileWriter(file), OutputFormat.createPrettyPrint());
			this.feedWriter.write(writer, feed);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	private Element createPayload(ISyncSession syncSession) {
		Element payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		
		Element elementSession = payload.addElement(ELEMENT_SYNC_SESSION);
		elementSession.addAttribute("sourceId", syncSession.getSourceId());
		elementSession.addAttribute("endpointId", syncSession.getTarget().getEndpointId());
		elementSession.addAttribute("lastSyncDate", syncSession.getLastSyncDate() == null ? "" : DateHelper.formatRFC822(syncSession.getLastSyncDate()));
		elementSession.addAttribute("full", syncSession.isFullProtocol() ? "true" : "false");
		return payload;
	}
}
