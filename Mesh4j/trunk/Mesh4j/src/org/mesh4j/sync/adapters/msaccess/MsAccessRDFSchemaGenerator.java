package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessToRDFMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MsAccessRDFSchemaGenerator {
	
	public static IRDFSchema extractRDFSchema(String mdbFileName, String tableName, String rdfBaseUri){
		return extractRDFSchema(mdbFileName, tableName, null, rdfBaseUri);
	}
	
	public static IRDFSchema extractRDFSchema(String mdbFileName, String tableName, List<String> columnIds, String rdfBaseUri){

		Guard.argumentNotNullOrEmptyString(mdbFileName, "mdbFileName");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		Guard.argumentNotNullOrEmptyString(rdfBaseUri, "rdfBaseUri");
			
		File mdbFile = new File(mdbFileName);
		if(!mdbFile.exists()){
			Guard.throwsArgumentException("mdbFileName", mdbFileName);
		}
		
		ArrayList<String> identifiablePropertyNames = new ArrayList<String>();		
		ArrayList<String> guidPropertyNames = new ArrayList<String>();
		
		String entityName = MsAccessHelper.getEntityName(tableName);
		RDFSchema rdfSchema = new RDFSchema(entityName, rdfBaseUri+"/"+entityName+"#", entityName);
		try{
			Database db = Database.open(mdbFile);
			try{
	
				Table table = db.getTable(entityName);
				if(table == null){
					table = db.getTable(entityName.trim().replaceAll("_", " "));
					if(table == null){
						Guard.throwsArgumentException("tableName", tableName);
					}
				}
				
				for (Column column : table.getColumns()) {
					addProperty(rdfSchema, column);
					
					if(DataType.GUID.equals(column.getType())){
						String propName = RDFSchema.normalizePropertyName(column.getName());
						identifiablePropertyNames.add(propName);
						guidPropertyNames.add(propName);
					}
				}
				
				List<Column> pks;
				if (columnIds == null) {
					pks = MsAccessHelper.getPrimaryKeys(table);
					if(!pks.isEmpty()){
						identifiablePropertyNames = new ArrayList<String>();
						for (Column column: pks) {
							identifiablePropertyNames.add(RDFSchema.normalizePropertyName(column.getName()));	
						}				
					}
				} else {
					identifiablePropertyNames = new ArrayList<String>();
					for(String columnId : columnIds) {
						identifiablePropertyNames.add(RDFSchema.normalizePropertyName(columnId));
					}
				}
				rdfSchema.setIdentifiablePropertyNames(identifiablePropertyNames);	
				rdfSchema.setGUIDPropertyNames(guidPropertyNames);
				
			} finally{
				db.close();
			}
		}catch(IllegalArgumentException iae){
			throw iae;
		}catch (Exception e) {
			throw new MeshException(e);
		}
		return rdfSchema;
	}

	// TODO (JMT) RDF: improve MSAccess to RDF type mapper
	private static void addProperty(RDFSchema rdfSchema, Column column) {
		String columName = column.getName();
		String propertyName = RDFSchema.normalizePropertyName(columName);
				
		if(DataType.GUID.equals(column.getType())){
			rdfSchema.addStringProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(DataType.BOOLEAN.equals(column.getType())){
			rdfSchema.addBooleanProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(DataType.SHORT_DATE_TIME.equals(column.getType())){
			rdfSchema.addDateTimeProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(DataType.TEXT.equals(column.getType())  || DataType.MEMO.equals(column.getType()) ){
			rdfSchema.addStringProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}

		if(DataType.BYTE.equals(column.getType()) || DataType.LONG.equals(column.getType())){
			rdfSchema.addLongProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(DataType.INT.equals(column.getType())){
			rdfSchema.addIntegerProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(DataType.LONG.equals(column.getType())){
			rdfSchema.addIntegerProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}

		if(DataType.DOUBLE.equals(column.getType())){
			rdfSchema.addDoubleProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}

		if(DataType.NUMERIC.equals(column.getType())){
			rdfSchema.addDecimalProperty(propertyName, columName, IRDFSchema.DEFAULT_LANGUAGE);
		}
	}
	
	public static MsAccessToRDFMapping extractRDFSchemaAndMappings(String fileName, String tableName, String rdfBaseURL) {
		IRDFSchema rdfSchema = extractRDFSchema(fileName, tableName, rdfBaseURL);
		MsAccessToRDFMapping mapping = new MsAccessToRDFMapping(rdfSchema);
		return mapping;
	}
}
