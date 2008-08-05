package org.mesh4j.sync.adapters.dom.parsers;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.dom.IMeshDOM;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.utils.IdGenerator;


public class FileXMLViewElementTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateNotAcceptNullFileManager(){
		new FileXMLViewElement(null);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseDOMIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.add(doc, element);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseDocumentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.add(null, element);
	}

	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseElementIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.add(doc, null);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseSyncRepoIsNull(){
		Document doc = DocumentHelper.createDocument();
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		
		fileView.add(doc, element);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseFileIDAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.add(doc, element);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseFileContentIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.add(doc, element);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldAddFailsBecauseSyncIDAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element fileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		fileContent.setText("123");
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.add(doc, newElement);
	}
	
	@Test
	public void shouldAdd(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, IdGenerator.newID());
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element fileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		fileContent.setText("123");
		
		FileManager fileManager = new FileManager();
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		fileView.setDOM(new MockDOM());
		
		Element added = fileView.add(doc, newElement);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", added.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(added.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Element addedToDoc = doc.getRootElement().element(MeshNames.MESH_QNAME_FILE);
		Assert.assertNotNull(addedToDoc);
		Assert.assertNotNull(addedToDoc.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", addedToDoc.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(addedToDoc.element(MeshNames.MESH_QNAME_FILE_CONTENT));

		Assert.assertNotSame(addedToDoc, added);
		Assert.assertEquals("123", fileManager.getFileContent("a.txt"));
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldDeleteFailsBecauseDocumentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.delete(null, element);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldDeleteFailsBecauseElementIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.delete(doc, null);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldDeleteFailsBecauseFileIdAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.delete(doc, element);
	}

	@Test(expected=IllegalArgumentException.class) 
	public void shouldDeleteFailsBecauseElementParentIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
			
		FileManager fileManager = new FileManager();
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		fileView.delete(doc, element);	
	}
	
	@Test
	public void shouldDelete(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "123");

		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		fileView.delete(doc, element);	
		
		Element deleted = doc.getRootElement().element(MeshNames.MESH_QNAME_FILE);
		Assert.assertNull(deleted);
		Assert.assertNull(fileManager.getFileContent("a.txt"));
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldIsValidFailsBecauseElementIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.isValid(doc, null);
	}	
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldIsValidFailsBecauseDocumentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.isValid(null, element);
	}

	@Test
	public void shouldIsValidReturnsFalseBecauseInvalidElementType(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement("bar");
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		Assert.assertFalse(fileView.isValid(doc, element));
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseFileIDAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		Assert.assertFalse(fileView.isValid(doc, element));
	}
		
	@Test
	public void shouldIsValid(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");

		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		Assert.assertTrue(fileView.isValid(doc, element));
	}
		
	@Test
	public void shouldNormalizeReturnsNullBecauseElementIsNull(){
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		Assert.assertNull(fileView.normalize(null));
	}
	
	@Test
	public void shouldNormalizeReturnsNullBecauseInvalidElementType(){
		Element element = DocumentHelper.createElement("foo");
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		Assert.assertNull(fileView.normalize(element));
	}
	
	@Test
	public void shouldNormalize(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		Assert.assertNotNull(fileView.normalize(element));
		Assert.assertSame(element, fileView.normalize(element));
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldRefreshFailsBecauseDocumentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.refresh(null, element);
	}

	@Test(expected=IllegalArgumentException.class) 
	public void shouldRefreshFailsBecauseElementIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.refresh(doc, null);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldRefreshFailsBecauseFileIDAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.refresh(doc, element);
	}
	
	@Test
	public void shouldRefreshDeleteElement(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileManager fileManager = new FileManager();

		Assert.assertNull(fileManager.getFileContent("a.txt"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Assert.assertNull(fileView.refresh(doc, element));	
		
		Element deleted = doc.getRootElement().element(MeshNames.MESH_QNAME_FILE);
		Assert.assertNull(deleted);
		Assert.assertNull(fileManager.getFileContent("a.txt"));
	}
	
	@Test
	public void shouldRefreshAddFileContentElement(){

		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "123");

		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element refreshElement = fileView.refresh(doc, element);
		Assert.assertNotNull(refreshElement);
		Assert.assertNotSame(element, refreshElement);
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(refreshElement);
		Assert.assertNotNull(refreshElement.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", refreshElement.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(refreshElement.element(MeshNames.MESH_QNAME_FILE_CONTENT));				
		Assert.assertEquals(fileManager.getFileContent("a.txt"), refreshElement.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
	}
	
	@Test
	public void shouldRefreshFileContentElement(){

		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element fileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		fileContent.setText("456");
		
		Assert.assertNotSame(element, newElement);
				
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "123");

		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element refreshElement = fileView.refresh(doc, newElement);
		Assert.assertNotNull(refreshElement);
		Assert.assertNotSame(element, refreshElement);
		Assert.assertNotSame(newElement, refreshElement);

		Assert.assertNotNull(refreshElement);
		Assert.assertNotNull(refreshElement.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", refreshElement.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(refreshElement.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
	}
	
	@Test
	public void shouldRefreshDontChangeFileContentElement(){

		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));		
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element fileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		fileContent.setText("456");
		
		Assert.assertNotSame(element, newElement);
				
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "456");

		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element refreshElement = fileView.refresh(doc, newElement);
		Assert.assertNotNull(refreshElement);
		Assert.assertNotSame(element, refreshElement);
		Assert.assertSame(newElement, refreshElement);

		Assert.assertNotNull(refreshElement);
		Assert.assertNotNull(refreshElement.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", refreshElement.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(refreshElement.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldUpdateFailsBecauseDocumentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.update(null, element, element);	
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldUpdateFailsBecauseElementIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.update(doc, null, element);	
	}	
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldUpdateFailsBecauseNewElementIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.update(doc, element, null);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldUpdateFailsBecauseNewElementFileIDAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.update(doc, element, element);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shouldUpdateFailsBecauseNewElementFileContentIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element =  doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.update(doc, element, newElement);
	}

	@Test(expected=IllegalArgumentException.class) 
	public void shouldUpdateFailsBecauseNewElementFileContentTextIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element =  doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.update(doc, element, newElement);		
	}
	
	@Test
	public void shouldUpdateAddFileIDAttr(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element =  doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element newFileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		newFileContent.setText("123");
		
		FileManager fileManager = new FileManager();
		Assert.assertNull(fileManager.getFileContent("a.txt"));
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element updated = fileView.update(doc, element, newElement);
		
		Assert.assertNotNull(updated);
		Assert.assertNotSame(element, updated);
		Assert.assertSame(newElement, updated);

		Assert.assertNotNull(updated);
		Assert.assertNotNull(updated.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", updated.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		Assert.assertEquals("123", updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		Assert.assertEquals(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText(), fileManager.getFileContent("a.txt"));
	}
	
	@Test
	public void shouldUpdateRefreshFileIDAttr(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element =  doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "xxx.txt");
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element newFileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		newFileContent.setText("123");
		
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "123");
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element updated = fileView.update(doc, element, newElement);
		
		Assert.assertNotNull(updated);
		Assert.assertNotSame(element, updated);
		Assert.assertSame(newElement, updated);

		Assert.assertNotNull(updated);
		Assert.assertNotNull(updated.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", updated.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		Assert.assertEquals("123", updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		Assert.assertEquals(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText(), fileManager.getFileContent("a.txt"));
	}	
	
	@Test
	public void shouldAddFileContent(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element =  doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element newFileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		newFileContent.setText("123");

		FileManager fileManager = new FileManager();
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element updated = fileView.update(doc, element, newElement);
		
		Assert.assertNotNull(updated);
		Assert.assertNotSame(element, updated);
		Assert.assertSame(newElement, updated);

		Assert.assertNotNull(updated.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", updated.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		Assert.assertEquals("123", updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		Assert.assertEquals(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText(), fileManager.getFileContent("a.txt"));
	}
	
	@Test
	public void shouldUpdateFileContent(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element =  doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		Element newElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		newElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		Element newFileContent = newElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		newFileContent.setText("123");
		
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "5693286598456");
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		Element updated = fileView.update(doc, element, newElement);
		
		Assert.assertNotNull(updated);
		Assert.assertNotSame(element, updated);
		Assert.assertSame(newElement, updated);

		Assert.assertNotNull(updated.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", updated.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		Assert.assertEquals("123", updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
		
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attribute(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertNotNull(fileManager.getFileContent("a.txt"));
		Assert.assertEquals(updated.element(MeshNames.MESH_QNAME_FILE_CONTENT).getText(), fileManager.getFileContent("a.txt"));

	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shoulgGetAllFailsBecauseDOMIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.getAllElements(doc);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shoulgGetAllFailsBecauseDocumentIsNull(){
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.getAllElements(null);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void shoulgGetAllFailsBecauseDocumentRootIsNull(){
		Document doc = DocumentHelper.createDocument();
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());
		fileView.getAllElements(doc);
	}
	
	@Test
	public void shoulgGetAllRemoveElementBecauseFileIDAttrIsNull(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
		doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());

		List<Element> all = fileView.getAllElements(doc);
		Assert.assertNotNull(all);
		Assert.assertEquals(0, all.size());
		
		Assert.assertNull(doc.getRootElement().element(MeshNames.MESH_QNAME_FILE));		
	}
	
	@Test
	public void shoulgGetAllRemoveElementBecauseFileManagerHasNotContainsFile(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileXMLViewElement fileView = new FileXMLViewElement(new FileManager());
		fileView.setDOM(new MockDOM());

		List<Element> all = fileView.getAllElements(doc);
		Assert.assertNotNull(all);
		Assert.assertEquals(0, all.size());
		
		Assert.assertNull(doc.getRootElement().element(MeshNames.MESH_QNAME_FILE));
	}
	
	@Test
	public void shoulgGetAllMergeFileElementsWithFileManagerFileContents(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "a.txt");
		
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "123");
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		fileView.setDOM(new MockDOM());

		List<Element> all = fileView.getAllElements(doc);
		Assert.assertNotNull(all);
		Assert.assertEquals(1, all.size());

		element = doc.getRootElement().element(MeshNames.MESH_QNAME_FILE);
		Assert.assertNotNull(element);
		Assert.assertNotSame(all.get(0), element);
		
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertEquals("a.txt", all.get(0).attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(all.get(0).element(MeshNames.MESH_QNAME_FILE_CONTENT));
		Assert.assertEquals("123", all.get(0).element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
	}	
	
	@Test
	public void shoulgGetAllAddNewElementsWithFileManagerNewFiles(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));	
			
		FileManager fileManager = new FileManager();
		fileManager.setFileContent("a.txt", "123");
		
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		fileView.setDOM(new MockDOM());
		
		List<Element> all = fileView.getAllElements(doc);
		Assert.assertNotNull(all);
		Assert.assertEquals(1, all.size());

		Element element = doc.getRootElement().element(MeshNames.MESH_QNAME_FILE);
		Assert.assertNotNull(element);
		Assert.assertNotSame(all.get(0), element);
		
		Assert.assertEquals("a.txt", element.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNull(element.element(MeshNames.MESH_QNAME_FILE_CONTENT));
		
		Assert.assertEquals("a.txt", all.get(0).attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		Assert.assertNotNull(all.get(0).element(MeshNames.MESH_QNAME_FILE_CONTENT));
		Assert.assertEquals("123", all.get(0).element(MeshNames.MESH_QNAME_FILE_CONTENT).getText());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementIsNull(){
		FileXMLViewElement viewElement = new FileXMLViewElement(new FileManager());
		viewElement.clean(DocumentHelper.createDocument(), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementParentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		
		FileXMLViewElement viewElement = new FileXMLViewElement(new FileManager());
		viewElement.clean(null, element);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementInvalidElementType() throws DocumentException{
		String xml = "<foo><bar></bar></foo>";
		Document document = DocumentHelper.parseText(xml);
		
		Element element = document.getRootElement().element("bar");
		Assert.assertNotNull(element);
				
		FileXMLViewElement viewElement = new FileXMLViewElement(new FileManager());
		viewElement.clean(document, element);
	}
	
	@Test
	public void shouldClean() throws DocumentException{
		String xml = "<foo><mesh4x:file xmlns:mesh4x=\"http://mesh4x.org/kml\"></mesh4x:file></foo>";
		Document document = DocumentHelper.parseText(xml);

		Element element = document.getRootElement().element(MeshNames.MESH_QNAME_FILE);
		Assert.assertNotNull(element);
		
		FileXMLViewElement viewElement = new FileXMLViewElement(new FileManager());
		viewElement.clean(document, element);
		
		Assert.assertNull(document.getRootElement().element(MeshNames.MESH_QNAME_FILE));
	}
	
	private class MockDOM implements IMeshDOM{

		@Override
		public Element addElement(Element element) {
			return null;
		}

		@Override
		public String asXML() {
			return null;
		}

		@Override
		public IContent createContent(Element element, String syncID) {
			return null;
		}

		@Override
		public void deleteElement(String id) {
		}

		@Override
		public List<Element> getAllElements() {
		return null;
		}

		@Override
		public List<SyncInfo> getAllSyncs() {
			return null;
		}

		@Override
		public Element getContentRepository(Document document) {
			return document.getRootElement();
		}

		@Override
		public Element getElement(String id) {
			return null;
		}

		@Override
		public IIdentityProvider getIdentityProvider() {
			return null;
		}

		@Override
		public String getMeshSyncId(Element element) {
			return null;
		}

		@Override
		public SyncInfo getSync(String syncId) {
			return null;
		}

		@Override
		public Element getSyncRepository(Document document) {
			return document.getRootElement();
		}

		@Override
		public String getType() {
			return null;
		}

		@Override
		public boolean isValid(Element element) {
			return false;
		}

		@Override
		public String newID() {
			return null;
		}

		@Override
		public Element normalize(Element element) {
			return null;
		}

		@Override
		public void normalize() {
			
		}

		@Override
		public IContent normalizeContent(IContent content) {
			return null;
		}

		@Override
		public Document toDocument() {
			return null;
		}

		@Override
		public Element updateElement(Element element) {
			return null;
		}

		@Override
		public void updateMeshStatus() {
		}

		@Override
		public void updateSync(SyncInfo syncInfo) {
		}

		@Override
		public void clean() {
		}

		@Override
		public void purgue() {
		}
	}
}
