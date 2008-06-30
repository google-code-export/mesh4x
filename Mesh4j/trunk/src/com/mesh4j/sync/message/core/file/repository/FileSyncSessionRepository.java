package com.mesh4j.sync.message.core.file.repository;

import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
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
import com.mesh4j.sync.message.ISyncSessionFactory;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class FileSyncSessionRepository {

	// CONSTANTS
	public static final String ATTRIBUTE_OPEN = "open";
	public static final String ATTRIBUTE_FULL = "full";
	public static final String ATTRIBUTE_LAST_SYNC_DATE = "lastSyncDate";
	public static final String ATTRIBUTE_ENDPOINT_ID = "endpointId";
	public static final String ATTRIBUTE_SOURCE_ID = "sourceId";
	public static final String ATTRIBUTE_SESSION_ID = "sessionId";

	public final static String ELEMENT_SYNC_SESSION = "session";
	public final static String ELEMENT_ACK = "ack";
	public final static String ELEMENT_CONFLICT = "conflict";
	
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
			File file = getSnapshotFile(sessionId);
			Feed feed = this.feedReader.read(file);
			return feed.getItems();
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}

	public void snapshot(ISyncSession syncSession){
		if(syncSession.getLastSyncDate() == null || syncSession.isOpen()){
			Guard.throwsException("INVALID_SNAPSHOT_SYNC_SESSION");						// TODO ooooooooooooooooooo
		}
		
		File file = getSnapshotFile(syncSession.getSessionId());
		List<Item> items = syncSession.getSnapshot();
		write(syncSession, file, items, true);
		
		deleteCurrentSessionFile(syncSession.getSessionId());
	}

	public void deleteCurrentSessionFile(String sessionId) {
		File currentFile = getCurrentSessionFile(sessionId);
		if(currentFile.exists()){
			currentFile.delete();
		}
	}
	
	public void flush(ISyncSession syncSession){
		if(!syncSession.isOpen()){
			Guard.throwsException("INVALID_FLUSH_SYNC_SESSION");						// TODO ooooooooooooooooooo
		}
		File file = getCurrentSessionFile(syncSession.getSessionId());
		List<Item> items = syncSession.getCurrentSnapshot();
		write(syncSession, file, items, false);
	}

	public ISyncSession readSession(String sessionId, ISyncSessionFactory syncSessionFactory){
		File fileSnapshot = getSnapshotFile(sessionId);
		File fileCurrent = getCurrentSessionFile(sessionId);
		if(!fileCurrent.exists() && !fileSnapshot.exists()){
			Guard.throwsException("INVALID_SYNC_SESSION");								// TODO ooooooooooooooooooo
		} 
		
		try{
			ISyncSession syncSession =null; 
			if(fileCurrent.exists()){
				Feed feedCurrent = this.feedReader.read(fileCurrent);
				if(fileSnapshot.exists()){
					Feed feedSnapshot = this.feedReader.read(fileSnapshot);
					if(!getSessionID(feedSnapshot.getPayload()).equals(sessionId)){
						Guard.throwsException("INVALID_SYNC_SESSION");								// TODO ooooooooooooooooooo			
					}
					syncSession = this.createSyncSession(feedCurrent.getPayload(), feedCurrent.getItems(), feedSnapshot.getItems(), syncSessionFactory);
				} else {
					syncSession = this.createSyncSession(feedCurrent.getPayload(), feedCurrent.getItems(), new ArrayList<Item>(), syncSessionFactory);
				}
				if(!syncSession.isOpen() || !syncSession.getSessionId().equals(sessionId)){
					Guard.throwsException("INVALID_SYNC_SESSION");								// TODO ooooooooooooooooooo			
				}
			} else{
				Feed feedSnapshot = this.feedReader.read(fileSnapshot);
				syncSession = this.createSyncSession(feedSnapshot.getPayload(), new ArrayList<Item>(), feedSnapshot.getItems(), syncSessionFactory);
				if(syncSession.isOpen() || !syncSession.getSessionId().equals(sessionId)){
					Guard.throwsException("INVALID_SYNC_SESSION");								// TODO ooooooooooooooooooo			
				}
			}
			return syncSession;
		}catch(DocumentException e){
			throw new MeshException(e);
		}
	}

	private String getSessionID(Element payload) {
		Element elementSession = payload.element(ELEMENT_SYNC_SESSION);
		return elementSession.attributeValue(ATTRIBUTE_SESSION_ID);

	}

	public File getSnapshotFile(String sessionId) {
		File file = new File(this.rootDirectory + sessionId + "_snapshot.xml");
		return file;
	}
	
	public File getCurrentSessionFile(String sessionId) {
		File file = new File(this.rootDirectory + sessionId + "_current.xml");
		return file;
	}

	private void write(ISyncSession syncSession, File file, List<Item> items, boolean isSnapshot) {
		try{
			Feed feed = new Feed(items);
			feed.setPayload(this.createPayload(syncSession, isSnapshot));
			
			XMLWriter writer = new XMLWriter(new FileWriter(file), OutputFormat.createPrettyPrint());
			this.feedWriter.write(writer, feed);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	private Element createPayload(ISyncSession syncSession, boolean isSnapshot) {
		Element payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		
		Element elementSession = payload.addElement(ELEMENT_SYNC_SESSION);
		elementSession.addAttribute(ATTRIBUTE_SESSION_ID, syncSession.getSessionId());
		elementSession.addAttribute(ATTRIBUTE_SOURCE_ID, syncSession.getSourceId());
		elementSession.addAttribute(ATTRIBUTE_ENDPOINT_ID, syncSession.getTarget().getEndpointId());
		elementSession.addAttribute(ATTRIBUTE_LAST_SYNC_DATE, syncSession.getLastSyncDate() == null ? "" : DateHelper.formatRFC822(syncSession.getLastSyncDate()));
		elementSession.addAttribute(ATTRIBUTE_FULL, syncSession.isFullProtocol() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_OPEN, syncSession.isOpen() ? "true" : "false");
		
		if(!isSnapshot){
			List<String> pendingAcks = syncSession.getAllPendingACKs();
			for (String pendingAck : pendingAcks) {
				Element pendingAckElement = elementSession.addElement(ELEMENT_ACK);
				pendingAckElement.setText(pendingAck);
			}
		}
		
		for (String conflictSyncID : syncSession.getConflictsSyncIDs()) {
			Element conflictElement = elementSession.addElement(ELEMENT_CONFLICT);
			conflictElement.setText(conflictSyncID);
			
		}
		return payload;
	}
	
	
	@SuppressWarnings("unchecked")
	private ISyncSession createSyncSession(Element payload, List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot, ISyncSessionFactory syncSessionFactory) {
		Element syncElement = payload.element(ELEMENT_SYNC_SESSION);
		String sessionId = syncElement.attributeValue(ATTRIBUTE_SESSION_ID);
		String sourceId = syncElement.attributeValue(ATTRIBUTE_SOURCE_ID);
		String endpointId = syncElement.attributeValue(ATTRIBUTE_ENDPOINT_ID);
		String dateAsString = syncElement.attributeValue(ATTRIBUTE_LAST_SYNC_DATE);
		Date date = (dateAsString == null || dateAsString.length() == 0) ? null : DateHelper.parseRFC822(dateAsString);
		boolean isFull = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_FULL));
		boolean isOpen = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_OPEN));
		
		List<String> acks = new ArrayList<String>();
		List<Element> ackElements = syncElement.elements(ELEMENT_ACK);
		for (Element ackElement : ackElements) {
			acks.add(ackElement.getText());
		}
		
		List<String> conflicts = new ArrayList<String>();
		List<Element> conflictElements = syncElement.elements(ELEMENT_CONFLICT);
		for (Element conflictElement : conflictElements) {
			conflicts.add(conflictElement.getText());
		}
		
		ISyncSession syncSession = syncSessionFactory.createSession(sessionId, sourceId, endpointId, isFull, isOpen, date, currentSyncSnapshot, lastSyncSnapshot, conflicts, acks);
		return syncSession;
	}
}
