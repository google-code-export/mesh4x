package com.mesh4j.sync.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;

public class XMLHelper {
	
	private final static Log Logger = LogFactory.getLog(XMLHelper.class);

	public static void write(Document document, File file) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(file));
			writer.write(document);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);  // TODO (JMT) throws runtime exception ?
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);  // TODO (JMT) throws runtime exception ?
			}
		}
	}		
}
