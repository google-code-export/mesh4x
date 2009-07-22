package org.mesh4j.sync.adapters.csv;

import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;


public class CSVHeaderTest {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfHeaderIsNull(){
		new CSVHeader(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfHeaderIsEmpty(){
		String header = "";
		new CSVHeader(header);
	}
	
	@Test
	public void ShouldGetAllColumnName(){
		String headerAsString = "id,age,name";
		CSVHeader csvHeader = new CSVHeader(headerAsString);
		Assert.assertEquals(3, csvHeader.getColumnNames().length);
		String listOfCol[] = csvHeader.getColumnNames();
		Assert.assertEquals("id", listOfCol[0]);
		Assert.assertEquals("age", listOfCol[1]);
		Assert.assertEquals("name", listOfCol[2]);
	}

	@Test
	public void ShouldGetColumnName(){
		String headerAsString = "id,age,name";
		CSVHeader csvHeader = new CSVHeader(headerAsString);
		Assert.assertEquals("id",csvHeader.getColumnName(0));
		Assert.assertEquals("age",csvHeader.getColumnName(1));
		Assert.assertEquals("name",csvHeader.getColumnName(2));
	}
	
	
	@Test
	public void ShouldGetColumnIndex(){
		String headerAsString = "id,age,name";
		CSVHeader csvHeader = new CSVHeader(headerAsString);
		Assert.assertEquals(0,csvHeader.getCellIndex("id"));
		Assert.assertEquals(1,csvHeader.getCellIndex("age"));
		Assert.assertEquals(2,csvHeader.getCellIndex("name"));
	}
	
	@Test
	public void ShouldWriterHeaderInBuffer(){
		StringWriter writer = new StringWriter();
		String headerAsString = "id,age,name";
		CSVHeader csvHeader = new CSVHeader(headerAsString);
		csvHeader.write(writer);
		Assert.assertEquals(headerAsString + "\n", writer.toString());
	}
	
	
	
}
