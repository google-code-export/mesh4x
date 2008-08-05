package org.mesh4j.sync.message.core.repository.file;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.message.core.repository.ISyncSessionFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class FileSyncSessionRepository implements ISyncSessionRepository{

	private final static Log LOGGER = LogFactory.getLog(FileSyncSessionRepository.class);
	
	// CONSTANTS
	private static final String SUBFIX_CURRENT = "_current.xml";
	private static final String SUBFIX_SNAPSHOT = "_snapshot.xml";
	public static final String ATTRIBUTE_OPEN = "open";
	public static final String ATTRIBUTE_FULL = "full";
	public static final String ATTRIBUTE_LAST_SYNC_DATE = "lastSyncDate";
	public static final String ATTRIBUTE_ENDPOINT_ID = "endpointId";
	public static final String ATTRIBUTE_SOURCE_ID = "sourceId";
	public static final String ATTRIBUTE_SESSION_ID = "sessionId";
	public static final String ATTRIBUTE_VERSION = "sessionVersion";

	public final static String ELEMENT_SYNC_SESSION = "session";
	public final static String ELEMENT_ACK = "ack";
	public final static String ELEMENT_CONFLICT = "conflict";
	
	// MODEL VARIANBLES
	private String rootDirectory;
	private FeedWriter feedWriter;
	private FeedReader feedReader;
	private ISyncSessionFactory sessionFactory;
	
	// BUSINESS METHODS

	public FileSyncSessionRepository(String rootDirectory, ISyncSessionFactory sessionFactory) {
		Guard.argumentNotNullOrEmptyString(rootDirectory, "rootDirectory");
		Guard.argumentNotNull(sessionFactory, "sessionFactory");
		
		this.sessionFactory = sessionFactory;
		this.rootDirectory = rootDirectory;
		this.feedWriter = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		this.feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		
		File fileDir = new File(rootDirectory);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		} else {
			this.readAllSessions();
		}
	}
	
	public List<ISyncSession> readAllSessions() {
		HashSet<ISyncSession> all = new HashSet<ISyncSession>();
		
		File rootDir = new File(this.rootDirectory);
		File[] files = rootDir.listFiles();
		for (File file : files) {
			if(file.isFile()){
				String sessionId = null;
				String fileName = file.getName();
				if(fileName.endsWith(SUBFIX_SNAPSHOT)){
					sessionId = fileName.substring(0, fileName.length() - SUBFIX_SNAPSHOT.length());
				} else if(fileName.endsWith(SUBFIX_CURRENT)){
					sessionId = fileName.substring(0, fileName.length() - SUBFIX_CURRENT.length());
				}
				if(sessionId != null){
					ISyncSession syncSession = getSession(sessionId);
					if(syncSession == null){
						try{
							syncSession = readSession(sessionId);
							if(syncSession != null){
								all.add(syncSession);
							}
						} catch(MeshException e){
							LOGGER.error(e.getMessage(), e);
						}
					}else{
						all.add(syncSession);
					}
				}
			}
		}
		return new ArrayList<ISyncSession>(all);
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

	@Override
	public void snapshot(ISyncSession syncSession){
		if(syncSession.getLastSyncDate() == null || syncSession.isOpen()){
			Guard.throwsException("INVALID_SNAPSHOT_SYNC_SESSION");
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
	
	@Override
	public void flush(ISyncSession syncSession){
		if(!syncSession.isOpen()){
			Guard.throwsException("INVALID_FLUSH_SYNC_SESSION");
		}
		File file = getCurrentSessionFile(syncSession.getSessionId());
		List<Item> items = syncSession.getCurrentSnapshot();
		write(syncSession, file, items, false);
	}

	public ISyncSession readSession(String sessionId){
		File fileSnapshot = getSnapshotFile(sessionId);
		File fileCurrent = getCurrentSessionFile(sessionId);
		if(!fileCurrent.exists() && !fileSnapshot.exists()){
			Guard.throwsException("INVALID_SYNC_SESSION");
		} 
		
		try{
			ISyncSession syncSession =null; 
			if(fileCurrent.exists()){
				Feed feedCurrent = this.feedReader.read(fileCurrent);
				if(fileSnapshot.exists()){
					Feed feedSnapshot = this.feedReader.read(fileSnapshot);
					if(!getSessionID(feedSnapshot.getPayload()).equals(sessionId)){
						Guard.throwsException("INVALID_SYNC_SESSION");			
					}
					syncSession = this.createSyncSession(feedCurrent.getPayload(), feedCurrent.getItems(), feedSnapshot.getItems());
				} else {
					syncSession = this.createSyncSession(feedCurrent.getPayload(), feedCurrent.getItems(), new ArrayList<Item>());
				}
				if(syncSession == null){
					return null;
				}
				if(!syncSession.isOpen() || !syncSession.getSessionId().equals(sessionId)){
					Guard.throwsException("INVALID_SYNC_SESSION");			
				}
			} else{
				Feed feedSnapshot = this.feedReader.read(fileSnapshot);
				syncSession = this.createSyncSession(feedSnapshot.getPayload(), new ArrayList<Item>(), feedSnapshot.getItems());
				if(syncSession == null){
					return null;
				}
				if(syncSession.isOpen() || !syncSession.getSessionId().equals(sessionId)){
					Guard.throwsException("INVALID_SYNC_SESSION");			
				}
			}
			return syncSession;
		}catch(DocumentException e){
			throw new MeshException(e);
		}
	}
	
	@Override
	public void cancel(ISyncSession syncSession) {
		Guard.argumentNotNull(syncSession, "syncSession");
		this.deleteCurrentSessionFile(syncSession.getSessionId());		
	}

	private String getSessionID(Element payload) {
		Element elementSession = payload.element(ELEMENT_SYNC_SESSION);
		return elementSession.attributeValue(ATTRIBUTE_SESSION_ID);

	}

	public File getSnapshotFile(String sessionId) {
		File file = new File(this.rootDirectory + sessionId + SUBFIX_SNAPSHOT);
		return file;
	}
	
	public File getCurrentSessionFile(String sessionId) {
		File file = new File(this.rootDirectory + sessionId + SUBFIX_CURRENT);
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
		elementSession.addAttribute(ATTRIBUTE_VERSION, String.valueOf(syncSession.getVersion()));
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
	private ISyncSession createSyncSession(Element payload, List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot) {
		Element syncElement = payload.element(ELEMENT_SYNC_SESSION);
		String sessionId = syncElement.attributeValue(ATTRIBUTE_SESSION_ID);
		int version = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_VERSION));
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
		
		ISyncSession syncSession = this.sessionFactory.createSession(sessionId, version, sourceId, endpointId, isFull, isOpen, date, currentSyncSnapshot, lastSyncSnapshot, conflicts, acks);
		return syncSession;
	}
	
	@Override
	public ISyncSession createSession(String sessionID, int version, String sourceId, IEndpoint target, boolean fullProtocol) {
		return this.sessionFactory.createSession(sessionID, version, sourceId, target, fullProtocol);
	}

	@Override
	public ISyncSession getSession(String sessionId) {
		return this.sessionFactory.get(sessionId);
	}

	@Override
	public ISyncSession getSession(String sourceId, String endpointId) {
		return sessionFactory.get(sourceId, endpointId);
	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		this.sessionFactory.registerSourceIfAbsent(adapter);		
	}

	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		return this.sessionFactory.getSource(sourceId);
	}	
}
