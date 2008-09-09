package org.sms.exchanger.message.repository.factory;

import org.sms.exchanger.IProperties;
import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.message.repository.MessageRepository;
import org.sms.exchanger.properties.PropertiesProvider;

public class MessageRepositoryFactory implements IMessageRepositoryFactory{

	@Override
	public IMessageRepository createMessageManager(PropertiesProvider prop) {
		String inboxDirectory = prop.getString(IProperties.INBOX_DIR, prop.getCurrentDirectory()+"\\inbox\\");
		String outboxDirectory = prop.getString(IProperties.OUTBOX_DIR, prop.getCurrentDirectory()+"\\outbox\\");
		return new MessageRepository(inboxDirectory, outboxDirectory);
	}

}
