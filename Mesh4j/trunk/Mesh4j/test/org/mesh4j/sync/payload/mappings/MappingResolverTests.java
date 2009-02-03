package org.mesh4j.sync.payload.mappings;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class MappingResolverTests {

	@Test
	public void shouldResolveSchema() throws DocumentException{
		
		Element mappings = DocumentHelper.parseText("<mappings><name>Name of patient: {patient/name} {patient/lastName}.</name></mappings>").getRootElement();
		MappingResolver schemaResolver = new MappingResolver(mappings);
		
		String propertyName = "name";
		Element element = DocumentHelper.parseText("<patient><name>Juan</name><lastName>Tondato</lastName></patient>").getRootElement();
		String value = schemaResolver.getValue(element, propertyName);
		
		Assert.assertEquals("Name of patient: Juan Tondato.", value);

		Element elementPayload = DocumentHelper.parseText("<payload><patient><name>Juan</name><lastName>Tondato</lastName></patient></payload>").getRootElement();
		value = schemaResolver.getValue(elementPayload, propertyName);
		
		Assert.assertEquals("Name of patient: Juan Tondato.", value);

	}
	
}
