package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

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
	
	@Override
	public String getMessageType() {
		return "1";
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
			syncSession.beginSync(sinceDate, message.getSessionVersion());
			
			if(this.messageSyncProtocol != null){
				this.messageSyncProtocol.notifyBeginSync(syncSession);
			}
			
			List<Item> items = syncSession.getAll();
			
			List<IMessage> response = new ArrayList<IMessage>();
			
			if(items.isEmpty()){
				response.add(this.noChanges.createMessage(syncSession));
				return response;
			} else {
				response.add(this.lastVersionStatus.createMessage(syncSession, items));
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
			sb.append(DateHelper.formatDateTime(syncSession.getLastSyncDate()));
		}
		return sb.toString();
	}

	
	private Date decodeSyncDate(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip source id
		if(st.hasMoreTokens()){
			return DateHelper.parseDateTime(st.nextToken());
		} else {
			return null;
		}
	}

	@Override
	public String getSourceId(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		return st.nextToken();
	}

	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}
}
