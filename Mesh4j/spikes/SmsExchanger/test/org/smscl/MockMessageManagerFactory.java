package org.smscl;

import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.message.repository.factory.IMessageRepositoryFactory;
import org.sms.exchanger.properties.PropertiesProvider;

public class MockMessageManagerFactory implements IMessageRepositoryFactory {

	@Override
	public IMessageRepository createMessageManager(PropertiesProvider prop) {
		return new MockMessageManager();
	}

}
