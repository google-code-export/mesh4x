package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IBeginSyncMessageProcessor;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;


public class BeginSyncMessageProcessor implements IMessageProcessor, IBeginSyncMessageProcessor {

	public static final String MESSAGE_TYPE = "1";

	private static final Comparator<Item> ITEM_ORDER_BY_SYNC_ID = new Comparator<Item>(){

		@Override
		public int compare(Item o1, Item o2) {
			return o1.getSyncId().compareTo(o2.getSyncId());
		}
		
	};
	
	// MODEL VARIABLES
	private NoChangesMessageProcessor noChanges;
	private LastVersionStatusMessageProcessor lastVersionStatus;
	private IMessageSyncProtocol messageSyncProtocol;
	private EqualStatusMessageProcessor equalStatus;

	// METHODS
	public BeginSyncMessageProcessor(NoChangesMessageProcessor noChanges, LastVersionStatusMessageProcessor lastVersionStatus, EqualStatusMessageProcessor equalStatus) {
		super();
		this.noChanges = noChanges;
		this.lastVersionStatus = lastVersionStatus;
		this.equalStatus = equalStatus;
	}
	
	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public IMessage createMessage(ISyncSession syncSession){
		Guard.argumentNotNull(syncSession, "syncSession");
		
		String data = encode(syncSession);			
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				data,
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(!syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			
			Date sinceDate = decodeSyncDate(message.getData());
			boolean fullProtocol = getFullProtocol(message.getData());
			boolean shouldSendChanges = getSendChanges(message.getData());
			boolean shouldReceiveChanges = getReceiveChanges(message.getData());
			syncSession.beginSync(fullProtocol, shouldSendChanges, shouldReceiveChanges, sinceDate, message.getSessionVersion());
			
			if(this.messageSyncProtocol != null){
				this.messageSyncProtocol.notifyBeginSync(syncSession);
			}
			
			List<Item> items = syncSession.getAll();
			
			List<IMessage> response = new ArrayList<IMessage>();
			if(items.isEmpty()){
				response.add(this.noChanges.createMessage(syncSession));
				return response;
			} else {								
				int localHash = calculateGlobalHash(items);
				String localHashAsString = String.valueOf(localHash);
				String globalHash = decodeGlobalHash(message.getData());
				
				if(localHashAsString.equals(globalHash)){
					response.add(this.equalStatus.createMessage(syncSession));
				} else {
					response.add(this.lastVersionStatus.createMessage(syncSession, items));
				}
				return response;
			}
			
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}	
	}

	private String encode(ISyncSession syncSession) {
		StringBuilder sb = new StringBuilder();
		sb.append(syncSession.getSourceId());				// MSAccess:Oswego
	
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);	// MSAccess
		sb.append(syncSession.getSourceType());
		
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		sb.append(syncSession.isFullProtocol() ? "T" : "F");
		
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		sb.append(syncSession.shouldReceiveChanges() ? "T" : "F");  // send value for endpoint B - when endpoint A (actual syncSession) receive => endpoint B send ===> Point of view according to process method
		
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		sb.append(syncSession.shouldSendChanges() ? "T" : "F");		// receive value for endpoint B - when endpoint A (actual syncSession) send => endpoint B receive ===> Point of view according to process method
		
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		sb.append(calculateGlobalHash(syncSession.getAll()));
		
		if(syncSession.getLastSyncDate() != null){
			sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
			sb.append(DateHelper.formatDateTime(syncSession.getLastSyncDate()));
		}
		
		return sb.toString();
	}

	private int calculateGlobalHash(List<Item> items) {
		TreeSet<Item> itemsOrderBySyncId = new TreeSet<Item>(ITEM_ORDER_BY_SYNC_ID);
		itemsOrderBySyncId.addAll(items);
		
		int globalHash = 0;
		for (Item item : itemsOrderBySyncId) {
			globalHash = globalHash + item.hashCode();
		}
		return globalHash;
	}
	
	private Date decodeSyncDate(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		st.nextToken();	// skip source type
		st.nextToken();	// skip full protocol
		st.nextToken();	// skip send changes
		st.nextToken();	// skip receive changes
		st.nextToken();	// skip source global hash
		if(st.hasMoreTokens()){
			return DateHelper.parseDateTime(st.nextToken());
		} else {
			return null;
		}
	}
	
	private String decodeGlobalHash(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		st.nextToken();	// skip source type
		st.nextToken();	// skip full protocol
		st.nextToken();	// skip send changes
		st.nextToken();	// skip receive changes
		return st.nextToken();
	}

	@Override
	public String getSourceId(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		return st.nextToken();
	}
	
	public static String getSourceType(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		return st.nextToken();
	}

	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}

	@Override
	public boolean getFullProtocol(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		st.nextToken();	// skip source type
		return "T".equals(st.nextToken());
	}

	@Override
	public boolean getReceiveChanges(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		st.nextToken();	// skip source type
		st.nextToken();	// skip full protocol
		st.nextToken();	// skip send changes
		return "T".equals(st.nextToken());
	}

	@Override
	public boolean getSendChanges(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		st.nextToken();	// skip source type
		st.nextToken();	// skip full protocol
		return "T".equals(st.nextToken());
	}
}
