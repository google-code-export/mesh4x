package org.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IBeginSyncMessageProcessor;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.StringTokenizer;


public class BeginSyncMessageProcessor implements IMessageProcessor, IBeginSyncMessageProcessor {

	// CONSTANTS 
	public final static String MESSAGE_TYPE = "1";
	
	// MODEL VARIABLES
	private NoChangesMessageProcessor noChanges;
	private LastVersionStatusMessageProcessor lastVersionStatus;
	private IMessageSyncProtocol messageSyncProtocol;

	// METHODS
	public BeginSyncMessageProcessor(NoChangesMessageProcessor noChanges, LastVersionStatusMessageProcessor lastVersionStatus) {
		super();
		this.noChanges = noChanges;
		this.lastVersionStatus = lastVersionStatus;
	}
	

	public String getMessageType() {
		return MESSAGE_TYPE;
	}


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


	public Vector<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(!syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			
			Date sinceDate = decodeSyncDate(message.getData());
			this.messageSyncProtocol.beginSync(syncSession, sinceDate, message.getSessionVersion());
			
			Vector<Item> items = syncSession.getAll();
			
			Vector<IMessage> response = new Vector<IMessage>();
			
			if(items.isEmpty()){
				response.addElement(this.noChanges.createMessage(syncSession));
				return response;
			} else {
				response.addElement(this.lastVersionStatus.createMessage(syncSession, items));
				return response;
			}
			
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}	
	}

	private String encode(ISyncSession syncSession) {
		StringBuilder sb = new StringBuilder();
		sb.append(syncSession.getSourceId());
		if(syncSession.getLastSyncDate() != null){
			sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
			sb.append(DateHelper.formatW3CDateTime(syncSession.getLastSyncDate()));
		}
		return sb.toString();
	}

	
	private Date decodeSyncDate(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		if(st.hasMoreTokens()){
			return DateHelper.parseW3CDateTime(st.nextToken());
		} else {
			return null;
		}
	}


	public String getSourceId(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		return st.nextToken();
	}

	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}
}
