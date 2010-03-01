package org.mesh4j.sync.adapters.csv;

import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;

public class CSVRowTest {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfHeaderIsNull(){
		new CSVRow(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfHeaderIsEmpty(){
		String header = "";
		CSVHeader cHeader = new CSVHeader(header);
		new CSVRow(cHeader);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfCSVCellValueIsNullOrEmpty(){
		String header = "id,name,age";
		CSVHeader cHeader = new CSVHeader(header);
		new CSVRow(cHeader,null);
	}
	
	@Test
	public void ShouldGetCellValue(){
		String header = "id,name,country";
//		String rowAsString = "\"1\"" +"," + "\"raju\"" + ","+ "\"bd\"";
		String rowAsString = "1" + "," + "raju" + ","+ "bd";
		CSVHeader cHeader = new CSVHeader(header);
		
		CSVRow csRow = new CSVRow(cHeader,rowAsString);
		Assert.assertEquals("1",csRow.getCellValue("id"));
		Assert.assertEquals("raju",csRow.getCellValue("name"));
		Assert.assertEquals("bd",csRow.getCellValue("country"));
		
		Assert.assertEquals("1",csRow.getCellValue(0));
		Assert.assertEquals("raju",csRow.getCellValue(1));
		Assert.assertEquals("bd",csRow.getCellValue(2));
		
	}

	@Test
	public void ShouldGetCellCount(){
		String header = "id,name,country";
		String rowAsString = "1" + "," + "raju" + ","+ "bd";
		CSVHeader cHeader = new CSVHeader(header);
		CSVRow csvRow = new CSVRow(cHeader,rowAsString);
		Assert.assertEquals(3,csvRow.getCellCount());
	}

	@Test
	public void ShouldUpdateCellValue(){
		String header = "id,name,country";
		String rowAsString = "1" + "," + "raju" + ","+ "bd";
		CSVHeader cHeader = new CSVHeader(header);
		CSVRow csvRow = new CSVRow(cHeader,rowAsString);
		csvRow.setCellValue("name", "jmt");
		csvRow.setCellValue("country", "ag");
		
		Assert.assertEquals("1", csvRow.getCellValue("id"));
		Assert.assertEquals("jmt", csvRow.getCellValue("name"));
		Assert.assertEquals("ag", csvRow.getCellValue("country"));
	}
	
	@Test
	public void ShouldGetHeader(){
		String header = "id,name,country";
		String rowAsString = "1" + "," + "raju" + ","+ "bd";
		CSVHeader cHeader = new CSVHeader(header);
		CSVRow csvRow = new CSVRow(cHeader,rowAsString);
		Assert.assertEquals("id",csvRow.getHeader(0));
		Assert.assertEquals("name",csvRow.getHeader(1));
		Assert.assertEquals("country",csvRow.getHeader(2));
		
		Assert.assertTrue(csvRow.hashHeader("id"));
		Assert.assertTrue(csvRow.hashHeader("name"));
		Assert.assertTrue(csvRow.hashHeader("country"));
		
	}
	
	@Test
	public void ShouldWriteInBuffer(){
		String header = "id,name,country";
		String rowAsString = "1" + "," + "raju" + ","+ "bd";
		CSVHeader cHeader = new CSVHeader(header);
		CSVRow csvRow = new CSVRow(cHeader,rowAsString);
		StringWriter writer = new StringWriter();
		csvRow.write(writer);
		Assert.assertEquals(rowAsString + "\n", writer.toString());
	}
	
}
