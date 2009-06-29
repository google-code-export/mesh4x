package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.IOException;
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
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.Index.ColumnDescriptor;

public class MsAccessRDFSchemaGenerator {
	
	public static IRDFSchema extractRDFSchema(String mdbFileName, String tableName, String ontologyNameSpace, String ontologyBaseUri) throws IOException{

		Guard.argumentNotNullOrEmptyString(mdbFileName, "mdbFileName");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		Guard.argumentNotNullOrEmptyString(ontologyNameSpace, "ontologyNameSpace");
		Guard.argumentNotNullOrEmptyString(ontologyBaseUri, "ontologyBaseUri");
			
		File mdbFile = new File(mdbFileName);
		if(!mdbFile.exists()){
			Guard.throwsArgumentException("mdbFileName", mdbFileName);
		}
		
		ArrayList<String> identifiablePropertyNames = new ArrayList<String>();		
		
		RDFSchema rdfSchema = new RDFSchema(ontologyNameSpace, ontologyBaseUri, getEntityName(tableName));
		Database db = Database.open(mdbFile);
		try{

			Table table = db.getTable(tableName);
			if(table == null){
				Guard.throwsArgumentException("tableName", table);
			}
			
			for (Column column : table.getColumns()) {
				addProperty(rdfSchema, column);
				
				if(DataType.GUID.equals(column.getType())){
					identifiablePropertyNames.add(column.getName());
				}
			}
			
			List<ColumnDescriptor> pks = getPrimaryKeys(table);
			if(!pks.isEmpty()){
				identifiablePropertyNames = new ArrayList<String>();
				for (ColumnDescriptor columnDescriptor : pks) {
					identifiablePropertyNames.add(columnDescriptor.getName());	
				}				
			}
			rdfSchema.setIdentifiablePropertyNames(identifiablePropertyNames);			
			
		} finally{
			db.close();
		}
		
		return rdfSchema;
	}

	// TODO (JMT) RDF: improve MSAccess to RDF type mapper
	private static void addProperty(RDFSchema rdfSchema, Column column) {
		String propertyName = getNodeName(column);
		if(column.isAutoNumber()){
			if(DataType.GUID.equals(column.getType())){
				rdfSchema.addStringProperty(propertyName, propertyName, "en");
			}
		} else {
			if(DataType.GUID.equals(column.getType())){
				rdfSchema.addStringProperty(propertyName, propertyName, "en");
			}
			
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
	}

	private static String getEntityName(String tableName) {
		return tableName.trim().replaceAll(" ", "_");
	}
	
	private static String getNodeName(Column column) {
		return column.getName().trim().replaceAll(" ", "_");
	}

	public static MsAccessToRDFMapping extractRDFSchemaAndMappings(String fileName, String tableName, String rdfBaseURL) {
		Guard.argumentNotNullOrEmptyString(fileName, "mdbFileName");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		Guard.argumentNotNullOrEmptyString(rdfBaseURL, "rdfBaseURL");
			
		File mdbFile = new File(fileName);
		if(!mdbFile.exists()){
			Guard.throwsArgumentException("mdbFileName", fileName);
		}
		
		String className = getEntityName(tableName);
		RDFSchema rdfSchema = new RDFSchema(className, rdfBaseURL+"/"+className+"#", className);

		Database db = null;
		try{
			db = Database.open(mdbFile);
			Table table = db.getTable(tableName);
			if(table == null){
				Guard.throwsArgumentException("tableName", table);
			}
			
			ArrayList<String> identifiablePropertyNames = new ArrayList<String>();
			ArrayList<String> guidPropertyNames = new ArrayList<String>();
			
			for (Column column : table.getColumns()) {
				addProperty(rdfSchema, column);
				
				if(DataType.GUID.equals(column.getType())){
					guidPropertyNames.add(column.getName());
					identifiablePropertyNames.add(column.getName());
				}
			}
			
			List<ColumnDescriptor> pks = getPrimaryKeys(table);
			if(!pks.isEmpty()){
				identifiablePropertyNames = new ArrayList<String>();
				for (ColumnDescriptor columnDescriptor : pks) {
					identifiablePropertyNames.add(columnDescriptor.getName());	
				}	
			}
			
			rdfSchema.setIdentifiablePropertyNames(identifiablePropertyNames);
			rdfSchema.setGUIDPropertyNames(guidPropertyNames);
		} catch (Exception e) {
			throw new MeshException(e);
		}finally{
			if(db != null){
				try{
					db.close();
				}catch (Exception e) {
					throw new MeshException(e);
				}			
			}
		}		
		
		MsAccessToRDFMapping mapping = new MsAccessToRDFMapping(rdfSchema);
		return mapping;
	}
	
	private static List<ColumnDescriptor> getPrimaryKeys(Table table) {
		for (Index index : table.getIndexes()) {
			if(index.isPrimaryKey()){
				return index.getColumns();
			}
		}
		return new ArrayList<ColumnDescriptor>();
	}
}
