package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.mesh4j.sync.adapters.hibernate.mapping.MappingGenerator;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.Guard;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.Index.ColumnDescriptor;

public class MsAccessHibernateMappingGenerator {

	public static void createMapping(String mdbFileName, String tableName, String mappingFileName) throws Exception{

		StringWriter writer = new StringWriter();
		
		File mdbFile = new File(mdbFileName);
		Database db = Database.open(mdbFile);
		try{
			String entityName = getEntityName(tableName);
			Table table = db.getTable(entityName);
			if(table == null){
				table = db.getTable(entityName.trim().replaceAll("_", " "));
				if(table == null){
					Guard.throwsArgumentException("tableName", tableName);
				}
			}
			
			MappingGenerator.writeHeader(writer);
			MappingGenerator.writerClass(writer, entityName, getMsAccessTableName(tableName));
			
			List<ColumnDescriptor> ids =  getPrimaryKey(table);
			if(ids == null || ids.isEmpty()){
				Guard.throwsArgumentException("PKs", tableName);
			}
			
			ArrayList<String> idNames = new ArrayList<String>();
			if(ids.size() == 1){
				ColumnDescriptor columnDescriptor = ids.get(0);
				Column columnID = columnDescriptor.getColumn();
				String propertyName = getNodeName(columnID.getName());
				String msAccessColumnName = getMsAccessColumnName(columnID.getName());				
				
				if(columnID.getType().name().equals("GUID")){
					writer.write("\n");
					writer.write("\t\t");
					writer.write(MessageFormat.format("<id name=\"{0}\" type=\"{1}\" column=\"{2}\">", propertyName, Hibernate.BINARY.getName(), msAccessColumnName));
					writer.write("\n");
					writer.write("\t\t\t");
					writer.write("<generator class=\"assigned\"/>");
					writer.write("\n");
					writer.write("\t\t");
					writer.write("</id>");	
					
				} else {
					MappingGenerator.writeID(writer, propertyName, msAccessColumnName, getHibernateType(columnID));
				}
				
				idNames.add(propertyName);
			} else {
				writer.write("\n");
				writer.write("\t\t");
				writer.write("<composite-id name=\"id\">");	
				
				for (ColumnDescriptor columnDescriptor : ids) {
					Column column = columnDescriptor.getColumn();
					String propertyName = getNodeName(column.getName());
					String msAccessColumnName = getMsAccessColumnName(column.getName());					
					idNames.add(propertyName);
					
					writer.write("\n");
					writer.write("\t\t\t");
					writer.write(MessageFormat.format("<key-property name=\"{0}\" node=\"{0}\" type=\"{1}\">", propertyName, getHibernateType(column)));
					writer.write("\n");
					writer.write("\t\t\t\t");
					writer.write(MessageFormat.format("<column name=\"{0}\"/>", msAccessColumnName));
					writer.write("\n");
					writer.write("\t\t\t");
					writer.write("</key-property>");
				}
				writer.write("\n");
				writer.write("\t\t");
				writer.write("</composite-id>");	
			}
			
			for (Column column : table.getColumns()) {
				String columnName = column.getName();
				String propertyName = getNodeName(columnName);
				String msAccessColumnName = getMsAccessColumnName(columnName);
				if(!idNames.contains(propertyName) && !column.isAutoNumber() ){	
					if(column.getType().name().equals("GUID")){
						MappingGenerator.writeProperty(writer, propertyName, msAccessColumnName, Hibernate.BINARY.getName());
					} else {
						MappingGenerator.writeProperty(writer, propertyName, msAccessColumnName, getHibernateType(column));
					}
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

	public static String getEntityName(String tableName) {
		return tableName.trim().replaceAll(" ", "_");
	}
	
	private static String getNodeName(String columnName) {
		return columnName.trim().replaceAll(" ", "_");
	}
	private static String getMsAccessColumnName(String column) {
		return column.contains(" ") ? "["+column+"]" : column;
	}	
	private static String getMsAccessTableName(String tableName) {
		return tableName.contains(" ") ? "["+tableName+"]" : tableName;
	}	
	private static String getHibernateType(Column column) throws HibernateException, SQLException {
		return MsAccessDialect.INSTANCE.getHibernateTypeName(column.getSQLType());
	}

	private static List<ColumnDescriptor> getPrimaryKey(Table table) {
		for (Index index : table.getIndexes()) {
			if(index.isPrimaryKey()){				
				return index.getColumns();				
			}
		}
		return null;
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
	public static void forceCreateMappings(String mdbFileName, String tableName, String contentMappingFileName, String syncMappingFileName) throws Exception {
		createMapping(mdbFileName, tableName, contentMappingFileName);
		MappingGenerator.createSyncInfoMapping(syncMappingFileName, getSyncTableName(tableName));
	}

	public static String getSyncTableName(String baseTableName) {
		String entityName = getEntityName(baseTableName);
		return entityName+"_sync";
	}

	public static boolean isSyncTableName(String tableName) {
		return tableName != null && tableName.toLowerCase().endsWith("_sync");
	}

	public static String getContentMappingFileName(String tableName, String baseDirectory) {
		String entityName = getEntityName(tableName);
		return FileUtils.getFileName(baseDirectory , entityName + ".hbm.xml");
	}

	public static String getSyncMappingFileName(String tableName, String baseDirectory) {
		String entityName = getEntityName(tableName);
		return FileUtils.getFileName(baseDirectory , entityName + "_sync.hbm.xml");
	}


}
