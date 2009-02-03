package org.mesh4j.sync.translator;

import org.junit.Assert;
import org.junit.Test;

public class MessageTranslatorTests {

	@Test
	public void shouldBeTranslate(){		
		Assert.assertEquals("MyArg can no be null or empty", MessageTranslator.translate("ArgumentCanNotBeNullOrEmpty", "MyArg"));
	}
	
	@Test
	public void shouldBeReturnsKeyWhenResourceBundleDoesNotExists(){	
		String key = "JMT";
		Assert.assertEquals(key, MessageTranslator.translate(key, 1, 2, 3, 4));		
	}
}
