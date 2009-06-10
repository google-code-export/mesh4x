package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.mesh4j.sync.adapters.hibernate.mapping.MappingGenerator;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;

public class MsAccessHibernateMappingGenerator {

	public static void createMapping(String mdbFileName, String tableName, String mappingFileName) throws Exception{

		StringWriter writer = new StringWriter();
		
		File mdbFile = new File(mdbFileName);
		Database db = Database.open(mdbFile);
		try{

			Table table = db.getTable(tableName);
						
			MappingGenerator.writeHeader(writer);
			MappingGenerator.writerClass(writer, getEntityName(tableName), tableName);
			
			String columnNamePrimaryKey = getPrimaryKey(table);
			Column columnID = table.getColumn(columnNamePrimaryKey);
			MappingGenerator.writeID(writer, getNodeName(columnID.getName()), columnID.getName(), getHibernateType(columnID));
			
			for (Column column : table.getColumns()) {
				if(!column.getName().equals(columnNamePrimaryKey) && !column.isAutoNumber() ){
					String columnName = column.getName();
					MappingGenerator.writeProperty(writer, getNodeName(columnName), columnName, getHibernateType(column));
				}
			}
			MappingGenerator.writerFooter(writer);
		} finally{
			db.close();
		}
		
		File mappingFile = new File(mappingFileName);
		FileWriter fileWriter = new FileWriter(mappingFile);
		try{
			writer.flush();
			fileWriter.write(writer.toString());
		}finally{
			fileWriter.close();
		}
	}

	private static String getEntityName(String tableName) {
		return tableName.trim().replaceAll(" ", "_");
	}
	
	private static String getNodeName(String columnName) {
		return columnName.trim().replaceAll(" ", "_");
	}

	private static String getHibernateType(Column column) throws HibernateException, SQLException {
		return MsAccessDialect.INSTANCE.getHibernateTypeName(column.getSQLType());
	}

	private static String getPrimaryKey(Table table) {
		for (Index index : table.getIndexes()) {
			if(index.isPrimaryKey()){
				return index.getColumns().get(0).getName();
			}
		}
		return null;
	}

	/**
	 * create mapping file for source and sync repository if the file does not
	 * exists. This method is no longer in use because of the the Issue#104
	 * (http://code.google.com/p/mesh4x/issues/detail?id=104)
	 * 
	 * @param mdbFileName
	 * @param tableName
	 * @param contentMappingFileName
	 * @param syncMappingFileName
	 * @throws Exception
	 */
	@Deprecated
	public static void createMappingsIfAbsent(String mdbFileName, String tableName, String contentMappingFileName, String syncMappingFileName) throws Exception {
		File contentMappingFile = new File(contentMappingFileName);
		if(!contentMappingFile.exists()){
			createMapping(mdbFileName, tableName, contentMappingFileName);
		}
		
		File syncMappingFile = new File(syncMappingFileName);
		if(!syncMappingFile.exists()){
			MappingGenerator.createSyncInfoMapping(syncMappingFileName, getSyncTableName(tableName));
		}
	}

	/**
	 * create mapping file for source and sync repository
	 * Please see the Issue#104 (http://code.google.com/p/mesh4x/issues/detail?id=104)
	 * 
	 * @param mdbFileName
	 * @param tableName
	 * @param contentMappingFileName
	 * @param syncMappingFileName
	 * @throws Exception
	 */
	public static void forceCreateMappings(String mdbFileName, String tableName,
			String contentMappingFileName, String syncMappingFileName)
			throws Exception {
		createMapping(mdbFileName, tableName, contentMappingFileName);
		MappingGenerator.createSyncInfoMapping(syncMappingFileName,
				getSyncTableName(tableName));
	}

	public static String getSyncTableName(String baseTableName) {
		return baseTableName+"_sync";
	}

	public static boolean isSyncTableName(String tableName) {
		return tableName != null && tableName.toLowerCase().endsWith("_sync");
	}
	
}
