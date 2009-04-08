package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;

public class GoogleSpreadAdapterTest {

	@Test
	public void ShouldSync(){
		
		String idColumName = "id";
		int lastUpdateColumnPosition = 6;
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw","saiful.raju@gmail.com","");
		ISpreadSheetToXMLMapper mapper = new SpreadSheetToXMLMapper(idColumName,lastUpdateColumnPosition);
		GSWorksheet workSheet = spreadsheet.getGSWorksheet(0);
		GoogleSpreadSheetContentAdapter adapter = new GoogleSpreadSheetContentAdapter(spreadsheet,workSheet,mapper,"user");
		
	}
}
