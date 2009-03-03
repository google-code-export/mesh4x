package org.mesh4j.sync.utils;

import java.io.File;
import java.io.StringWriter;
import java.text.MessageFormat;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MsAccessXFormGenerator {

	public static void createXFormSchema(String schemaFileName, String mdbFileName, String tableName, String title, String model, String templateFileName) throws Exception{
		StringWriter writerHeadInstance = new StringWriter();
		StringWriter writerHeadBinIds = new StringWriter();
		StringWriter writerBody = new StringWriter();
		
		File mdbFile = new File(mdbFileName);
		Database db = Database.open(mdbFile);
		try{

			Table table = db.getTable(tableName);
									
			for (Column column : table.getColumns()) {
				if(!column.isAutoNumber() ){
					writeComponent(model, column, writerHeadInstance, writerHeadBinIds, writerBody);
				}
			}
			writerHeadInstance.flush();
			writerHeadBinIds.flush();
			writerBody.flush();
		} finally{
			db.close();
		}
		
		byte[] templateBytes = FileUtils.read(templateFileName);
		String template = new String(templateBytes, "UTF-8");		
		String result = MessageFormat.format(
				template, 
				title, 
				model, 
				writerHeadInstance.toString(), 
				writerHeadBinIds.toString(), 
				writerBody.toString());
		
		FileUtils.write(schemaFileName, result.getBytes());
		
	}

	private static void writeComponent(String model, Column column, StringWriter writerHeadInstance, StringWriter writerBinIds, StringWriter writerBody) {
		if (DataType.BINARY == column.getType() || DataType.BOOLEAN == column.getType()){
			writeInstance(writerHeadInstance, column);
			writeSelect1(model, column.getName(), writerBody);
		} else if (DataType.TEXT == column.getType()){
			writeInstance(writerHeadInstance, column);
			writeInput(model, column.getName(), "xsd:string", writerBinIds, writerBody);
		} else if(DataType.BYTE == column.getType()){
			writeInstance(writerHeadInstance, column);
			writeSelect1(model, column.getName(), writerBody);
		} else if(DataType.DOUBLE == column.getType()){
			writeInstance(writerHeadInstance, column);
			writeInput(model, column.getName(), "xsd:double", writerBinIds, writerBody);
		}else if(DataType.INT == column.getType()){
			writeInstance(writerHeadInstance, column);
			writeInput(model, column.getName(), "xsd:int", writerBinIds, writerBody);
		}else if(DataType.SHORT_DATE_TIME== column.getType()){
			writeInstance(writerHeadInstance, column);
			//writeInputDate(model, column.getName(), writerBinIds, writerBody);
			writeInput(model, column.getName(), "xsd:string", writerBinIds, writerBody);
		}else{
			// TODO (JMT)
		}		
	}

	private static void writeSelect1(String model, String name, StringWriter writerBody) {
		writerBody.append("<select1 ref=\"/");
		writerBody.append(model);
		writerBody.append("/");
		writerBody.append(name);
		writerBody.append("\"><label>");
		writerBody.append(name);
		writerBody.append("?</label>");
		writerBody.append("<item><label>Yes</label><value>1</value></item>");
		writerBody.append("<item><label>No</label><value>0</value></item>");
		writerBody.append("</select1>");
		writerBody.append("\n");
	}
	
	private static void writeInputDate(String model, String name, StringWriter writerBinIds, StringWriter writerBody) {
		writerBinIds.append("<bind id=\"p");
		writerBinIds.append(name);
		writerBinIds.append("\" nodeset=\"/");
		writerBinIds.append(model);
		writerBinIds.append("/");
		writerBinIds.append(name);
		writerBinIds.append("\" type=\"xsd:dateTime\" jr:preload=\"timestamp\" jr:preloadParams=\"today\" />");
		writerBinIds.append("\n");
		
		writerBody.append("<input bind=\"p");
		writerBody.append(name);
		writerBody.append("\"><label>");
		writerBody.append(name);
		writerBody.append(":</label></input>");
		writerBody.append("\n");
	}
	
	private static void writeInput(String model, String name, String type, StringWriter writerBinIds, StringWriter writerBody) {
		writerBinIds.append("<bind id=\"p");
		writerBinIds.append(name);
		writerBinIds.append("\" nodeset=\"/");
		writerBinIds.append(model);
		writerBinIds.append("/");
		writerBinIds.append(name);
		writerBinIds.append("\" type=\"");
		writerBinIds.append(type);
		writerBinIds.append("\"/>");
		writerBinIds.append("\n");
		
		writerBody.append("<input bind=\"p");
		writerBody.append(name);
		writerBody.append("\"><label>");
		writerBody.append(name);
		writerBody.append(":</label></input>");
		writerBody.append("\n");
	}

	private static void writeInstance(StringWriter writer, Column column) {
		writer.append("<");
		writer.append(column.getName());
		writer.append("/>");
		writer.append("\n");
	}

}
