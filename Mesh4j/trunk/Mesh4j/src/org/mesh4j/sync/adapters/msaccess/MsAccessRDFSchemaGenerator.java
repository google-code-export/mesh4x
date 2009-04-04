package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.validations.Guard;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MsAccessRDFSchemaGenerator {
	
	public static IRDFSchema readSchema(String fileName) throws Exception {

		FileReader reader = new FileReader(fileName);
		try{
			RDFSchema rdfSchema = new RDFSchema(reader);
			return rdfSchema;
		} finally{
			reader.close();
		}
	}	

	public static IRDFSchema extractRDFSchema(String mdbFileName, String tableName, String ontologyNameSpace, String ontologyBaseUri) throws IOException{

		Guard.argumentNotNullOrEmptyString(mdbFileName, "mdbFileName");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		Guard.argumentNotNullOrEmptyString(ontologyNameSpace, "ontologyNameSpace");
		Guard.argumentNotNullOrEmptyString(ontologyBaseUri, "ontologyBaseUri");
			
		File mdbFile = new File(mdbFileName);
		if(!mdbFile.exists()){
			Guard.throwsArgumentException("mdbFileName", mdbFileName);
		}
		
		RDFSchema rdfSchema = new RDFSchema(ontologyNameSpace, ontologyBaseUri, getEntityName(tableName));
		Database db = Database.open(mdbFile);
		try{

			Table table = db.getTable(tableName);
			if(table == null){
				Guard.throwsArgumentException("tableName", table);
			}
			
			for (Column column : table.getColumns()) {
				if(!column.isAutoNumber() ){
					addProperty(rdfSchema, column);
				}
			}
		} finally{
			db.close();
		}
		
		return rdfSchema;
	}

	// TODO (JMT) RDF: improve MSAccess to RDF type mapper
	private static void addProperty(RDFSchema rdfSchema, Column column) {
		String propertyName = getNodeName(column);
		
		if(DataType.BOOLEAN.equals(column.getType())){
			rdfSchema.addBooleanProperty(propertyName, propertyName, "en");
		}
		
		if(DataType.SHORT_DATE_TIME.equals(column.getType())){
			rdfSchema.addDateTimeProperty(propertyName, propertyName, "en");
		}
		
		if(DataType.TEXT.equals(column.getType())  || DataType.MEMO.equals(column.getType()) ){
			rdfSchema.addStringProperty(propertyName, propertyName, "en");
		}

		if(DataType.BYTE.equals(column.getType()) || DataType.LONG.equals(column.getType())){
			rdfSchema.addLongProperty(propertyName, propertyName, "en");
		}
		
		if(DataType.INT.equals(column.getType())){
			rdfSchema.addIntegerProperty(propertyName, propertyName, "en");
		}

		if(DataType.DOUBLE.equals(column.getType())){
			rdfSchema.addDoubleProperty(propertyName, propertyName, "en");
		}

		if(DataType.NUMERIC.equals(column.getType())){
			rdfSchema.addDecimalProperty(propertyName, propertyName, "en");
		}
	}

	private static String getEntityName(String tableName) {
		return tableName.trim().replaceAll(" ", "_");
	}
	
	private static String getNodeName(Column column) {
		return column.getName().trim().replaceAll(" ", "_");
	}

}
