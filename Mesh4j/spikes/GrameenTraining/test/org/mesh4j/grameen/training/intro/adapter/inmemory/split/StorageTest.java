package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.utils.XMLHelper;

/**
 * @author Raju
 * @version 1.0 
 * @since 19/3/2009
 */

public class StorageTest {

	private IContent content1,content2,content3;
	
	@Before
	public void setUp() throws Exception {
		
		String id = "1";
		String title = "Student Info";
		String description = "Student Information(id,name,roll,department)";
		String rawDataAsXML = "<Student>" +
								"<id>" +"1" +"</id>" +
								"<name>" +"raju" +"</name>" +
								"<roll>" +"5004" +"</roll>" +
								"<department>" +"CSE" +"</department>" +
								"</Student>";
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		content1 = new XMLContent(id,title,description,payload);
		
		id = "2";
		title = "Student Info";
		description = "Student Information(id,name,roll,department)";
		rawDataAsXML = "<Student>" +
						"<id>" +"2" +"</id>" +
						"<name>" +"marcelo" +"</name>" +
						"<roll>" +"4004" +"</roll>" +
						"<department>" +"CSE" +"</department>" +
						"</Student>";
		payload = XMLHelper.parseElement(rawDataAsXML);
		content2 = new XMLContent(id,title,description,payload);
		
		
		id = "3";
		title = "Student Info";
		description = "Student Information(id,name,roll,department)";
		rawDataAsXML = "<Student>" +
						"<id>" +"3" +"</id>" +
						"<name>" +"sharif" +"</name>" +
						"<roll>" +"3004" +"</roll>" +
						"<department>" +"CSE" +"</department>" +
						"</Student>";
		payload = XMLHelper.parseElement(rawDataAsXML);
		content3 = new XMLContent(id,title,description,payload);
	}
	
	@Test
	public void ShouldAddRow(){
		Storage storage = new Storage();
		storage.addRow(content1);
		
		Assert.assertEquals(storage.getRow("1"),content1);
		Assert.assertEquals(storage.getStorage().size(), 1);
	}
	
	@Test
	public void ShouldDeleteRow(){
		Storage storage = new Storage("1",content1);
		
		Assert.assertEquals(storage.getStorage().size(), 1);
		
		storage.deletRow(content1);
		
		Assert.assertEquals(storage.getStorage().size(), 0);
	}
	
	@Test
	public void ShouldUpdateRow(){
		Storage storage = new Storage("1",content1);
		
		Assert.assertEquals(storage.getStorage().size(), 1);
		Assert.assertEquals(storage.getRow("1"),content1);
		IContent beforeUpdate = (IContent)storage.getRow("1");
		System.out.println(beforeUpdate.getPayload().asXML());
		
		storage.updateRow("1",content2);
//		storage.updateRow(content2);
		
		Assert.assertEquals(storage.getStorage().size(), 1);
		Assert.assertEquals(storage.getRow("1"),content2);
		
		IContent afterUpdate = (IContent)storage.getRow("1");
		System.out.println(afterUpdate.getPayload().asXML());
	}
}
