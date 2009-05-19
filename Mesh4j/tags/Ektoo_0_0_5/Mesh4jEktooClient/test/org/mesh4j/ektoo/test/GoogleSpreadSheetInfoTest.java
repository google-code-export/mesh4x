package org.mesh4j.ektoo.test;

import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
/**
 * @author raju
 */
public class GoogleSpreadSheetInfoTest {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfSpreadSheetFiledIsNullOrEmpty(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfUserNameIsNullOrEmpty(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"",
				"java123456",
				"id",
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfPasswordIsNullOrEmpty(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"",
				"id",
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfIdIsNullOrEmpty(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"",
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfSheetNameIsNullOrEmpty(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfTypeIsNullOrEmpty(){
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				""
				);
	}
}
