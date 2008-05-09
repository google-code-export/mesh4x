package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.utils.IdGenerator;

public class KMLContentAdapterTests {

	// TODO (JMT) test
	
//	@Test
//	public void spike() throws DocumentException{
//		File kmlFile = new File("D:\\temp_dev\\files\\tests\\samples0.kml");
////		
//		String id = IdGenerator.newID();
//		String xml = "<Placemark ID=\""+ id +"\">"+
//					"		<name>Other</name>"+
//					"		<LookAt>"+
//					"			<longitude>-58.4792542781588</longitude>"+
//					"			<latitude>-34.50852533747415</latitude>"+
//					"			<altitude>0</altitude>"+
//					"			<range>98.68518595027489</range>"+
//					"			<tilt>0</tilt>"+
//					"			<heading>0.1064137236836578</heading>"+
//					"			<altitudeMode>relativeToGround</altitudeMode>"+
//					"		</LookAt>"+
//					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
//					"		<Point>"+
//					"			<coordinates>-58.47912525193172,-34.50842431949285,0</coordinates>"+
//					"		</Point>"+
//					"	</Placemark>";
//				
//		KMLContentAdapter kmlAdapter = new KMLContentAdapter(kmlFile);
//		List<EntityContent> items = kmlAdapter.getAll();
//		String name = kmlAdapter.getEntityName();
//		
//		Element element = DocumentHelper.parseText(xml).getRootElement();
//		EntityContent entity = new EntityContent(element, kmlAdapter.getEntityName(), id);
//		kmlAdapter.save(entity);
//		EntityContent content = kmlAdapter.get(id);
//		kmlAdapter.delete(content);
//	}
}

// TODO (JMT) test

//void save(EntityContent entity);
//EntityContent get(String entityId);
//void delete(EntityContent entity);
//List<EntityContent> getAll();
//String getEntityName();
