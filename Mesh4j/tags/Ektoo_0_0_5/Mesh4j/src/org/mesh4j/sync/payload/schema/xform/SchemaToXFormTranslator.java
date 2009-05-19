package org.mesh4j.sync.payload.schema.xform;

import java.io.StringWriter;
import java.text.MessageFormat;

import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class SchemaToXFormTranslator {
	
	private static String TEMPLATE = 
		"<h:html xmlns:h=\"http://www.w3.org/1999/xhtml\""+"\n"+
		"xmlns=\"http://www.w3.org/2002/xforms\""+"\n"+
		"xmlns:ev=\"http://www.w3.org/2001/xml-events\""+"\n"+
		"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""+"\n"+
		"xmlns:jr=\"http://openrosa.org/javarosa\">"+"\n"+
		"<h:head>"+"\n"+
		"\t<h:title>{0}</h:title>"+"\n"+
		"\t<model>"+"\n"+
		"\t\t<instance>"+"\n"+
		"\t\t\t<{1}>"+"\n"+
		"{2}"+"\n"+
		"\t\t\t</{1}>"+"\n"+
		"\t\t</instance>"+"\n"+
		"\t\t\t{3}"+"\n"+
		"\t</model>"+"\n"+
		"</h:head>"+"\n"+
		"<h:body>"+"\n"+
		"\t{4}"+"\n"+
		"</h:body>"+"\n"+
		"</h:html>";

	public static String translate(ISchema schema){
		if(schema instanceof IRDFSchema){
			return translate((IRDFSchema)schema);
		} else {
			return schema.asXML();
		}
	}
	
	public static String translate(IRDFSchema schema){
		
		String model = schema.getOntologyNameSpace();
		String title = schema.getOntologyNameSpace();
		
		StringWriter writerHeadInstance = new StringWriter();
		StringWriter writerHeadBinIds = new StringWriter();
		StringWriter writerBody = new StringWriter();
		
		int size = schema.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = schema.getPropertyName(i);
			String propertyType = schema.getPropertyType(propertyName);
			String propertyLabel = schema.getPropertyLabel(propertyName, "en");
			
			writeComponent(model, propertyName, propertyLabel, propertyType, writerHeadInstance, writerHeadBinIds, writerBody);
		}
		
		writerHeadInstance.flush();
		writerHeadBinIds.flush();
		writerBody.flush();
		
		String result = MessageFormat.format(
				TEMPLATE, 
				title, 
				model, 
				writerHeadInstance.toString(), 
				writerHeadBinIds.toString(), 
				writerBody.toString());
		
		return result;
		
	}

	private static void writeComponent(String model, String propertyName, String propertyLabel, String propertyType, StringWriter writerHeadInstance, StringWriter writerBinIds, StringWriter writerBody) {
		if (IRDFSchema.XLS_BOOLEAN.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeSelect1(model, propertyName, propertyLabel, writerBody);
		} else if (IRDFSchema.XLS_STRING.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeInput(model, propertyName, propertyLabel, "xsd:string", writerBinIds, writerBody);
		} else if (IRDFSchema.XLS_DATETIME.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeInputDate(model, propertyName, propertyLabel, writerBinIds, writerBody);
		} else if (IRDFSchema.XLS_DECIMAL.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeInput(model, propertyName, propertyLabel, "xsd:decimal", writerBinIds, writerBody);
		} else if (IRDFSchema.XLS_DOUBLE.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeInput(model, propertyName, propertyLabel, "xsd:double", writerBinIds, writerBody);
		} else if (IRDFSchema.XLS_INTEGER.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeInput(model, propertyName, propertyLabel, "xsd:int", writerBinIds, writerBody);
		} else if (IRDFSchema.XLS_LONG.equals(propertyType)){
			writeInstance(writerHeadInstance, propertyName);
			writeInput(model, propertyName, propertyLabel, "xsd:long", writerBinIds, writerBody);
		} 	
	}

	private static void writeSelect1(String model, String name, String label, StringWriter writerBody) {
		writerBody.append("\t\t\t\t<select1 ref=\"/");
		writerBody.append(model);
		writerBody.append("/");
		writerBody.append(name);
		writerBody.append("\">\n\t\t\t\t<label>");
		writerBody.append(label);
		writerBody.append("?</label>\n");
		writerBody.append("\t\t\t\t<item><label>Yes</label><value>true</value></item>");
		writerBody.append("\t\t\t\t<item><label>No</label><value>false</value></item>");
		writerBody.append("\t\t\t\t</select1>");
		writerBody.append("\n");
	}
	
	private static void writeInputDate(String model, String name, String label, StringWriter writerBinIds, StringWriter writerBody) {
		writerBinIds.append("\t\t\t\t<bind id=\"p");
		writerBinIds.append(name);
		writerBinIds.append("\" nodeset=\"/");
		writerBinIds.append(model);
		writerBinIds.append("/");
		writerBinIds.append(name);
		writerBinIds.append("\" type=\"xsd:date\" jr:preload=\"date\" jr:preloadParams=\"today\" />");
		writerBinIds.append("\n");
		
		writerBody.append("\t\t\t\t<input bind=\"p");
		writerBody.append(name);
		writerBody.append("\"><label>");
		writerBody.append(label);
		writerBody.append(":</label></input>");
		writerBody.append("\n");
	}
	
	private static void writeInput(String model, String name, String label, String type, StringWriter writerBinIds, StringWriter writerBody) {
		writerBinIds.append("\t\t\t\t<bind id=\"p");
		writerBinIds.append(name);
		writerBinIds.append("\" nodeset=\"/");
		writerBinIds.append(model);
		writerBinIds.append("/");
		writerBinIds.append(name);
		writerBinIds.append("\" type=\"");
		writerBinIds.append(type);
		writerBinIds.append("\"/>");
		writerBinIds.append("\n");
		
		writerBody.append("\t\t\t\t<input bind=\"p");
		writerBody.append(name);
		writerBody.append("\"><label>");
		writerBody.append(label);
		writerBody.append(":</label></input>");
		writerBody.append("\n");
	}

	private static void writeInstance(StringWriter writer, String name) {
		writer.append("<");
		writer.append(name);
		writer.append("/>");
		writer.append("\n");
	}

}
