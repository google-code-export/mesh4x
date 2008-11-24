package org.mesh4j.sync.adapters.hibernate.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

public class MappingGenerator {
	
	public static void createSyncInfoMapping(String mappingFileName, String tableName) throws IOException{
		File mappingFile = new File(mappingFileName);
		if(!mappingFile.exists()){
			mappingFile.createNewFile();
		}
		
		FileWriter writer = new FileWriter(mappingFile);
		try{
			writeHeader(writer);
			writerClass(writer, "SyncInfo", tableName);
			writeID(writer, "sync_id", "sync_id", "string");
			writeProperty(writer, "entity_name", "entity_name", "string");
			writeProperty(writer, "entity_id", "entity_id", "string");
			writeProperty(writer, "entity_version", "entity_version", "string");
			writeProperty(writer, "sync_data", "sync_data", "string");
			writerFooter(writer);
		} finally{
			writer.close();
		}
	}


	public static void writeHeader(FileWriter writer) throws IOException {
		writer.write("<?xml version=\"1.0\"?><!DOCTYPE hibernate-mapping PUBLIC");
		writer.write(" \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" ");
		writer.write("\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">");
		writer.write("\n");
		writer.write("<hibernate-mapping>");
	}

	public static void writerClass(FileWriter writer, String entityName, String tableName) throws IOException {
		writer.write("\n");
		writer.write("\t");
		writer.write(MessageFormat.format("<class entity-name=\"{0}\" node=\"{0}\" table=\"{1}\">", entityName, tableName));
	}
	
	public static void writeID(FileWriter writer, String idName, String idColumnName, String idType) throws IOException {
		writer.write("\n");
		writer.write("\t\t");
		writer.write(MessageFormat.format("<id name=\"{0}\" type=\"{1}\" column=\"{2}\">", idName, idType, idColumnName));
		writer.write("\n");
		writer.write("\t\t\t");
		writer.write("<generator class=\"assigned\"/>");
		writer.write("\n");
		writer.write("\t\t");
		writer.write("</id>");
	}
	
	public static void writeProperty(FileWriter writer, String name, String column, String type) throws IOException {
		writer.write("\n");
		writer.write("\t\t");
		writer.write(MessageFormat.format("<property name=\"{0}\" column=\"{1}\" node=\"{0}\" type=\"{2}\"/>", name, column, type));
	}
	
	public static void writerFooter(FileWriter writer) throws IOException {
		writer.write("\n");
		writer.write("\t");
		writer.write("</class>");
		writer.write("\n");
		writer.write("</hibernate-mapping>");
	}
}
