package com.mesh4j.sync.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

import com.mesh4j.sync.validations.MeshException;

public class XMLHelper {
	
	private final static Log Logger = LogFactory.getLog(XMLHelper.class);

	public static void write(Document document, File file) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(file));
			writer.write(document);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				Logger.error(e.getMessage(), e); 
				throw new MeshException(e);
			}
		}
	}

	public static void write(String xml, File file) {
		Document document;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e); 
			throw new MeshException(e);
		}
		write(document, file);		
	}		
}
