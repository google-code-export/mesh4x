package org.mesh4j.ektoo.test;

import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
/**
 * @author raju
 */
public class GoogleSpreadSheetInfoTest {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfSpreadSheetFiledIsNullOrEmpty(){
		new GoogleSpreadSheetInfo(
				"",
				"gspreadsheet.test@gmail.com",
				"java123456",
				new String[] {"id"},
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfUserNameIsNullOrEmpty(){
		new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"",
				"java123456",
				new String[] {"id"},
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfPasswordIsNullOrEmpty(){
		new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"",
				new String[] {"id"},
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfIdIsNullOrEmpty(){
		new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				new String[]{},
				"user_source",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfSheetNameIsNullOrEmpty(){
		new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				new String[] {"id"},
				"",
				"user"
				);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfTypeIsNullOrEmpty(){
		new GoogleSpreadSheetInfo(
				"peo4fu7AitTo8e3v0D8FCew",
				"gspreadsheet.test@gmail.com",
				"java123456",
				new String[] {"id"},
				"user_source",
				""
				);
	}
}
