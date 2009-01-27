package org.mesh4j.sync.message.core.repository.file;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

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
import org.mesh4j.sync.id.generator.IdGenerator;
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
	public static final String ATTRIBUTE_BROKEN = "broken";
	public static final String ATTRIBUTE_FULL = "full";
	public static final String ATTRIBUTE_LAST_SYNC_DATE = "lastSyncDate";
	public static final String ATTRIBUTE_START_DATE = "startDateLocal";
	public static final String ATTRIBUTE_END_DATE = "endDateLocal";
	public static final String ATTRIBUTE_LAST_IN = "lastIn";
	public static final String ATTRIBUTE_LAST_OUT = "lastOut";
	public static final String ATTRIBUTE_ENDPOINT_ID = "endpointId";
	public static final String ATTRIBUTE_SOURCE_ID = "sourceId";
	public static final String ATTRIBUTE_SESSION_ID = "sessionId";
	public static final String ATTRIBUTE_VERSION = "sessionVersion";
	public static final String ATTRIBUTE_CANCELLED = "cancelled";
	public static final String ATTRIBUTE_SHOULD_SEND_CHANGES = "sendChanges";
	public static final String ATTRIBUTE_SHOULD_RECEIVE_CHANGES = "receiveChanges";
	public static final String ATTRIBUTE_NUMBER_OF_ADDED_ITEMS = "addedItems";
	public static final String ATTRIBUTE_NUMBER_OF_UPDATED_ITEMS = "updatedItems";
	public static final String ATTRIBUTE_NUMBER_OF_DELETED_ITEMS = "deletedItems";
	public static final String ATTRIBUTE_TARGET_SOURCE_TYPE = "endpointSourceType";
	public static final String ATTRIBUTE_TARGET_NUMBER_OF_ADDED_ITEMS = "endpointAddedItems";
	public static final String ATTRIBUTE_TARGET_NUMBER_OF_UPDATED_ITEMS = "endpointUpdatedItems";
	public static final String ATTRIBUTE_TARGET_NUMBER_OF_DELETED_ITEMS = "endpointDeletedItems";
	
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
		this.feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
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
		//this.deleteCurrentSessionFile(syncSession.getSessionId());
		File file = getCurrentSessionFile(syncSession.getSessionId());
		List<Item> items = syncSession.getCurrentSnapshot();
		write(syncSession, file, items, false);
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
		elementSession.addAttribute(ATTRIBUTE_START_DATE, syncSession.getStartDate() == null ? "" : DateHelper.formatDateYYYYMMDDHHMMSS(syncSession.getStartDate(), "/", ":", "", TimeZone.getDefault()));
		elementSession.addAttribute(ATTRIBUTE_END_DATE, syncSession.getEndDate() == null ? "" : DateHelper.formatDateYYYYMMDDHHMMSS(syncSession.getEndDate(), "/", ":", "", TimeZone.getDefault()));
		elementSession.addAttribute(ATTRIBUTE_LAST_SYNC_DATE, syncSession.getLastSyncDate() == null ? "" : DateHelper.formatW3CDateTime(syncSession.getLastSyncDate()));
		elementSession.addAttribute(ATTRIBUTE_LAST_IN, String.valueOf(syncSession.getLastNumberInMessages()));
		elementSession.addAttribute(ATTRIBUTE_LAST_OUT, String.valueOf(syncSession.getLastNumberOutMessages()));
		elementSession.addAttribute(ATTRIBUTE_FULL, syncSession.isFullProtocol() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_OPEN, syncSession.isOpen() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_BROKEN, syncSession.isBroken() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_CANCELLED, syncSession.isCancelled() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_SHOULD_SEND_CHANGES, syncSession.shouldSendChanges() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_SHOULD_RECEIVE_CHANGES, syncSession.shouldReceiveChanges() ? "true" : "false");
		elementSession.addAttribute(ATTRIBUTE_NUMBER_OF_ADDED_ITEMS, String.valueOf(syncSession.getNumberOfAddedItems()));
		elementSession.addAttribute(ATTRIBUTE_NUMBER_OF_UPDATED_ITEMS, String.valueOf(syncSession.getNumberOfUpdatedItems()));
		elementSession.addAttribute(ATTRIBUTE_NUMBER_OF_DELETED_ITEMS, String.valueOf(syncSession.getNumberOfDeletedItems()));
		elementSession.addAttribute(ATTRIBUTE_TARGET_SOURCE_TYPE, syncSession.getTargetSourceType() == null ? "undefined" : syncSession.getTargetSourceType());
		elementSession.addAttribute(ATTRIBUTE_TARGET_NUMBER_OF_ADDED_ITEMS, String.valueOf(syncSession.getTargetNumberOfAddedItems()));
		elementSession.addAttribute(ATTRIBUTE_TARGET_NUMBER_OF_UPDATED_ITEMS, String.valueOf(syncSession.getTargetNumberOfUpdatedItems()));
		elementSession.addAttribute(ATTRIBUTE_TARGET_NUMBER_OF_DELETED_ITEMS, String.valueOf(syncSession.getTargetNumberOfDeletedItems()));
		
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
		
		String lastSyncDateAsString = syncElement.attributeValue(ATTRIBUTE_LAST_SYNC_DATE);
		Date lastSyncDate = (lastSyncDateAsString == null || lastSyncDateAsString.length() == 0) ? null : DateHelper.parseW3CDateTime(lastSyncDateAsString);
		
		String startDateAsString = syncElement.attributeValue(ATTRIBUTE_START_DATE);
		Date startDate = (startDateAsString == null || startDateAsString.length() == 0) ? null : DateHelper.parseDateYYYYMMDDHHMMSS(startDateAsString, TimeZone.getDefault());

		String endDateAsString = syncElement.attributeValue(ATTRIBUTE_END_DATE);
		Date endDate = (endDateAsString == null || endDateAsString.length() == 0) ? null : DateHelper.parseDateYYYYMMDDHHMMSS(endDateAsString, TimeZone.getDefault());
		
		int lastIn = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_LAST_IN));
		int lastOut = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_LAST_OUT));
		boolean isFull = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_FULL));
		boolean isOpen = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_OPEN));
		boolean isBroken = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_BROKEN));
		boolean isCancelled = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_CANCELLED));
		boolean shouldSendChanges = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_SHOULD_SEND_CHANGES));
		boolean shouldReceiveChanges = Boolean.valueOf(syncElement.attributeValue(ATTRIBUTE_SHOULD_RECEIVE_CHANGES));
		int numberOfAddedItems = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_NUMBER_OF_ADDED_ITEMS));
		int numberOfUpdatedItems = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_NUMBER_OF_UPDATED_ITEMS));
		int numberOfDeletedItems = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_NUMBER_OF_DELETED_ITEMS));
		
		String targetSourceType = syncElement.attributeValue(ATTRIBUTE_TARGET_SOURCE_TYPE);
		if("undefined".equals(targetSourceType)){
			targetSourceType = null;
		}
		
		int targetNumberOfAddedItems = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_TARGET_NUMBER_OF_ADDED_ITEMS));
		int targetNumberOfUpdatedItems = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_TARGET_NUMBER_OF_UPDATED_ITEMS));
		int targetNumberOfDeletedItems = Integer.valueOf(syncElement.attributeValue(ATTRIBUTE_TARGET_NUMBER_OF_DELETED_ITEMS));
		
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
		
		ISyncSession syncSession = this.sessionFactory.createSession(
			sessionId, 
			version, 
			sourceId, 
			endpointId, 
			isFull, 
			shouldSendChanges, 
			shouldReceiveChanges, 
			isOpen, 
			isBroken,
			isCancelled,
			startDate,
			endDate,
			lastSyncDate, 
			lastIn,
			lastOut,
			currentSyncSnapshot, 
			lastSyncSnapshot, 
			conflicts, 
			acks,
			numberOfAddedItems,
			numberOfUpdatedItems,
			numberOfDeletedItems,
			targetSourceType,
			targetNumberOfAddedItems,
			targetNumberOfUpdatedItems,
			targetNumberOfDeletedItems);
		return syncSession;
	}
	
	@Override
	public ISyncSession createSession(String sessionID, int version, String sourceId, IEndpoint target, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {
		return this.sessionFactory.createSession(sessionID, version, sourceId, target, fullProtocol, shouldSendChanges, shouldReceiveChanges);
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
	public void registerSource(IMessageSyncAdapter adapter) {
		this.sessionFactory.registerSource(adapter);		
	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		this.sessionFactory.registerSourceIfAbsent(adapter);		
	}
	
	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		return this.sessionFactory.getSource(sourceId);
	}	
	
	@Override
	public IMessageSyncAdapter getSourceOrCreateIfAbsent(String sourceId) {
		return this.sessionFactory.getSourceOrCreateIfAbsent(sourceId);
	}

	@Override
	public List<ISyncSession> getAllSyncSessions() {
		return this.sessionFactory.getAll();
	}

	@Override
	public void removeSourceId(String sourceId) {
		List<ISyncSession> allSessions = this.sessionFactory.getAll(sourceId);

		this.sessionFactory.removeSourceId(sourceId);
		
		for (ISyncSession syncSession : allSessions) {
			deleteSessionFiles(syncSession.getSessionId());
		}
	}

	private void deleteSessionFiles(String sessionId) {
		File currentFile = getCurrentSessionFile(sessionId);
		if(currentFile.exists()){
			currentFile.delete();
		}
		
		File snappshotFile = getSnapshotFile(sessionId);
		if(snappshotFile.exists()){
			snappshotFile.delete();
		}
	}	
}
