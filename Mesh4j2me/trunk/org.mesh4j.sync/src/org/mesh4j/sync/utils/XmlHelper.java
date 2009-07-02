package org.mesh4j.sync.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.mesh4j.sync.validations.MeshException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class XmlHelper {

	public static Element getElement(String payload) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append(payload);
		
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		InputStreamReader reader = new InputStreamReader(is);
		try {
			XmlPullParser xmlPullParser = new KXmlParser();
			xmlPullParser.setInput(reader);
			
			Document document = new Document();
			document.parse(xmlPullParser);
			return document.getRootElement();
		}catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			try{
				reader.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getXml(Element element) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		
		try{
			XmlSerializer xmlSerializer= new KXmlSerializer();
			xmlSerializer.setOutput(writer);
			element.write(xmlSerializer);
			
			writer.flush();
			baos.flush();
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			try{
				writer.close();
				baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return baos.toString();
	}

	public static String canonicalizeXML(String payload) {
		// TODO (JMT) XML canonicalization
		return payload;
	}

}
