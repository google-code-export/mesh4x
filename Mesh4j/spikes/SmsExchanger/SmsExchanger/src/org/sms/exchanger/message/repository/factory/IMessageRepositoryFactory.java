package org.sms.exchanger.message.repository.factory;

import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.properties.PropertiesProvider;

public interface IMessageRepositoryFactory {

	IMessageRepository createMessageManager(PropertiesProvider prop);

}
