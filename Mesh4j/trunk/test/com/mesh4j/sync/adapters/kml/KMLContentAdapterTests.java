package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.utils.IdGenerator;

public class KMLContentAdapterTests {

	// TODO (JMT) test
	
	@Test
	public void spike() throws Exception{
		File kmlFile = new File("D:\\temp_dev\\files\\tests\\samples0.kml");
				
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(kmlFile);
		List<EntityContent> entities = kmlAdapter.getAll();
		Assert.assertFalse(entities.isEmpty());
		
		String id = IdGenerator.newID();
		Element style = makeNewStyle(id, "aaaaaaaaa");
		kmlAdapter.save(new EntityContent(style, "kml", id));
		
		EntityContent content = kmlAdapter.get(id);
		Assert.assertNotNull(content);
		Assert.assertEquals(id, content.getEntityId());
		Assert.assertEquals(style.asXML(), content.getPayload().asXML());
		
		Element style1 = makeNewStyle(id, "ffffff44");
		kmlAdapter.save(new EntityContent(style1, "kml", id));
		
		content = kmlAdapter.get(id);
		Assert.assertNotNull(content);
		Assert.assertEquals(id, content.getEntityId());
		Assert.assertEquals(style1.asXML(), content.getPayload().asXML());
		
	}

	private Element makeNewStyle(String id, String color) throws DocumentException {
		String xml = "<Style xmlns=\"http://earth.google.com/kml/2.2\" id=\"sn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
					"<IconStyle>"+
					"<color>"+ color+"</color>"+
					"<scale>1.1</scale>"+
					"<Icon>"+
					"<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
					"</Icon>"+
					"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
					"</IconStyle>" +
					"<LabelStyle>" +
					"<color>ff00ff55</color>" +
					"</LabelStyle>" +
					"</Style>";
		Element style = DocumentHelper.parseText(xml).getRootElement();
		return style;
	}
}

// TODO (JMT) test

//void save(EntityContent entity);
//EntityContent get(String entityId);
//void delete(EntityContent entity);
//List<EntityContent> getAll();
//String getEntityName();
