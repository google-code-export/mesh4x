package org.sms.exchanger.message.repository.factory;

import org.sms.exchanger.IProperties;
import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.message.repository.MessageRepository;
import org.sms.exchanger.properties.PropertiesProvider;

public class MessageRepositoryFactory implements IMessageRepositoryFactory{

	@Override
	public IMessageRepository createMessageManager(PropertiesProvider prop) {
		String inboundDirectory = prop.getString(IProperties.INBOUND_DIR, prop.getCurrentDirectory()+"\\inbox\\");
		String outboundDirectory = prop.getString(IProperties.OUTBOUND_DIR, prop.getCurrentDirectory()+"\\outbox\\");
		return new MessageRepository(inboundDirectory, outboundDirectory);
	}

}
