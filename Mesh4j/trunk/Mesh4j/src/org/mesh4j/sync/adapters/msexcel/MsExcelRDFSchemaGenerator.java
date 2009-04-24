package org.mesh4j.sync.adapters.msexcel;

import java.io.File;

import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.validations.Guard;

public class MsExcelRDFSchemaGenerator {

	public IRDFSchema extractRDFSchema(String excelFileName,String sheetName){
		
		Guard.argumentNotNullOrEmptyString(excelFileName, "excelFileName");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		
		String ontologyURI = "http://mesh4x/" + sheetName +"#";
		File excelFile = new File(excelFileName);
		if(!excelFile.exists()){
			Guard.throwsArgumentException("excelFileName", excelFileName);
		}
		
		RDFSchema schema = new RDFSchema(sheetName,ontologyURI,sheetName);
		
		
		
		return schema; 
	}
	
}
