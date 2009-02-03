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
			MappingGenerator.writeID(writer, getNodeName(columnID), columnID.getName(), getHibernateType(columnID));
			
			for (Column column : table.getColumns()) {
				if(!column.getName().equals(columnNamePrimaryKey) && !column.isAutoNumber() ){
					MappingGenerator.writeProperty(writer, getNodeName(column), column.getName(), getHibernateType(column));
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
			try{
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String getEntityName(String tableName) {
		return tableName.trim().replaceAll(" ", "_");
	}
	
	private static String getNodeName(Column column) {
		return column.getName().trim().replaceAll(" ", "_");
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

//	public static void createSyncTableIfAbsent(String mdbFileName, String baseTableName) throws Exception {
//
//		File mdbFile = new File(mdbFileName);
//		Database db = Database.open(mdbFile, false, true);
//		String tableName = getSyncTableName(baseTableName);
//		try{
//			Table table = db.getTable(tableName);
//			if(table == null){
//				TableBuilder tableBuilder = new TableBuilder(tableName);
//				
//				Database dbTemplate = Database.open(new File(MsAccessHelper.class.getResource("template.mdb").getFile()));
//				Table syncTemplateTable = dbTemplate.getTable("sync_info");
//				tableBuilder.addColumn(syncTemplateTable.getColumn("sync_id"));
//				tableBuilder.addColumn(syncTemplateTable.getColumn("entity_name"));
//				tableBuilder.addColumn(syncTemplateTable.getColumn("entity_id"));
//				tableBuilder.addColumn(syncTemplateTable.getColumn("entity_version"));
//				tableBuilder.addColumn(syncTemplateTable.getColumn("sync_data"));
//
//				dbTemplate.close();
//				
//				table = tableBuilder.toTable(db);
//				
//				db.flush();
//				table = new TableBuilder(tableName)
//					.addColumn(new ColumnBuilder("sync_id")
//						.setType(DataType.TEXT)
//						.setMaxLength()
//						.toColumn())
//					.addColumn(new ColumnBuilder("entity_name")
//						.setType(DataType.TEXT)
//						.setMaxLength()
//						.setCompressedUnicode(true)
//						.toColumn())
//					.addColumn(new ColumnBuilder("entity_id")
//						.setType(DataType.TEXT)
//						.setMaxLength()
//						.setCompressedUnicode(true)
//						.toColumn())
//					.addColumn(new ColumnBuilder("entity_version")
//						.setType(DataType.TEXT)
//						.setMaxLength()
//						.toColumn())
//					.addColumn(new ColumnBuilder("sync_data")
//						.setType(DataType.MEMO)
//						.setCompressedUnicode(true)
//						.toColumn())
//					.toTable(db);
//			}
//		} finally{
//			db.close();
//		}
//	}

	private static String getSyncTableName(String baseTableName) {
		return baseTableName+"_sync";
	}
	
}
