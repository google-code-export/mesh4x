package org.mesh4j.sync.payload.schema;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;

public class SchemaTypeFormatTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfFormatIsNull(){
		new SchemaTypeFormat(null);
	}
	
	@Test
	public void shouldFormat(){
		Date date = TestHelper.makeDate(2009, 4, 1, 1, 1, 1, 1);
		SchemaTypeFormat format = new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd"));
		Assert.assertEquals("2009-05-01", format.format(date));
	}
	
	@Test
	public void shouldParse() throws Exception{
		SchemaTypeFormat format = new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd"));
		
		Date date = TestHelper.makeDate(2009, 4, 1, 0, 0, 0, 0);
		Date generatedDate = (Date)format.parseObject("2009-05-01");
		
		Assert.assertNotNull(generatedDate);
		Assert.assertEquals(date.getTime(), generatedDate.getTime());
	}
	
	@Test(expected=Exception.class)
	public void shouldParseFails() throws Exception{
		SchemaTypeFormat format = new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd"));
		format.parseObject("2009-");
	}
}
