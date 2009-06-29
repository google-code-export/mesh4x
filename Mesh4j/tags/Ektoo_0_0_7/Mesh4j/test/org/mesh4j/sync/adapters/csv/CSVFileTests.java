package org.mesh4j.sync.adapters.csv;

import java.io.IOException;

import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;

public class CSVFileTests {

	@Test
	public void shouldRead() throws IOException{
		
		CSVToPlainXMLMapping mapping = new CSVToPlainXMLMapping("sheet1", "statName", null, null);
		
		CSVFile csvFile = new CSVFile(this.getClass().getResource("file.csv").getFile());
		csvFile.read(mapping);
		
		csvFile.flush(TestHelper.makeFileAndDeleteIfExists("csv_test.csv"));
	}
	
}
