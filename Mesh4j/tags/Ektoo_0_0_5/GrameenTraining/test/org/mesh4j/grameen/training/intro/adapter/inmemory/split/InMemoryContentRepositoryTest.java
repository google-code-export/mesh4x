package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.Date;

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
public class InMemoryContentRepositoryTest {

	
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
	public void ShouldCreate(){
		Storage storage = new Storage();
		storage.addRow(content1);
		InMemoryContentRepository repository = new InMemoryContentRepository(storage,"inMemory","student");
		repository.save(content2);
		
		Assert.assertEquals(repository.getAll(new Date()).size(), 2);
	}
	
	@Test
	public void ShouldDelete(){
		Storage storage = new Storage();
		storage.addRow(content1);
		InMemoryContentRepository repository = new InMemoryContentRepository(storage,"inMemory","student");
		repository.save(content2);
		
		Assert.assertEquals(repository.getAll(new Date()).size(), 2);
		repository.delete(content2);
		
		Assert.assertEquals(repository.getAll(new Date()).size(), 1);
		repository.delete(content1);
		Assert.assertEquals(repository.getAll(new Date()).size(), 0);
	}
	
	@Test
	public void ShouldDetectAddedContent(){
		Storage storage = new Storage();
		storage.addRow(content1);
		InMemoryContentRepository repository = new InMemoryContentRepository(storage,"inMemory","student");
		repository.save(content2);
		
		IContent content = repository.get("1");
		Assert.assertEquals(content1, content);
		
		content = repository.get("2");
		Assert.assertEquals(content2, content);
	}
	
	@Test
	public void ShouldGetAll(){
		Storage storage = new Storage();
		storage.addRow(content1);
		InMemoryContentRepository repository = new InMemoryContentRepository(storage,"inMemory","student");
		repository.save(content2);
		
		Assert.assertEquals(repository.getAll(new Date()).size(),2);
	}
}
