package org.mesh4j.sync.adapters.folder;

import java.io.IOException;

import org.apache.xerces.impl.dv.util.Base64;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.ZipUtils;

public class FileContentTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFileContentFailsIfFileNameIsNull(){
		byte[] bytes = getBytes("example.txt");
		new FileContent(null, bytes);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFileContentFailsIfFileNameIsEmpty(){
		byte[] bytes = getBytes("example.txt");
		new FileContent("", bytes);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFileContentFailsIfFileContentIsNull(){
		new FileContent("example.txt", null);
	}
	
	@Test
	public void shouldCreateFileContent() throws Exception{
		byte[] bytes = getBytes("example.txt");
		FileContent fileContent = new FileContent("example.txt", bytes);
		
		Assert.assertNotNull(fileContent);
		Assert.assertEquals("example.txt", fileContent.getFileName());
		Assert.assertEquals("example.txt", fileContent.getId());
		Assert.assertArrayEquals(bytes, fileContent.getFileContent());
		Assert.assertEquals(FileContent.makeFileElement("example.txt", bytes).asXML(), fileContent.getPayload().asXML());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldMakeFileElementFailsIfFileNameIsNull(){
		byte[] bytes = getBytes("example.txt");
		FileContent.makeFileElement(null, bytes);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldMakeFileElementFailsIfFileNameIsEmpty(){
		byte[] bytes = getBytes("example.txt");
		FileContent.makeFileElement("", bytes);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldMakeFileElementFailsIfFileContentIsNull(){
		FileContent.makeFileElement("example.txt", null);
	}
	
	@Test
	public void shouldMakeFileElement() throws IOException{
		byte[] bytes = getBytes("example.txt");
		Element fileElement = FileContent.makeFileElement("example.txt", bytes);
		
		Assert.assertNotNull(fileElement);
		Assert.assertEquals(MeshNames.MESH_QNAME_FILE, fileElement.getQName());
		Assert.assertEquals("example.txt", fileElement.attributeValue(MeshNames.MESH_QNAME_FILE_ID));
		
		Element fileContent = fileElement.element(MeshNames.MESH_QNAME_FILE_CONTENT);
		Assert.assertNotNull(fileContent);
		
		String base64ZippedBytes = Base64.encode(ZipUtils.compress(bytes));
		Assert.assertEquals(base64ZippedBytes, fileContent.getText());

	}
	
	@Test
	public void shouldNormalizeReturnNullIfContentIsNull(){
		Assert.assertNull(FileContent.normalize(null));
	}
	
	@Test
	public void shouldNormalizeReturnFileContentWhenContentIsFileContent(){
		byte[] bytes = getBytes("example.txt");
		FileContent fileContent = new FileContent("example.txt", bytes);
		Assert.assertEquals(fileContent, FileContent.normalize(fileContent));
	}

	@Test
	public void shouldNormalizeReturnFileContentFromXMLContentSinglePayload() throws Exception{
		byte[] bytes = getBytes("example.txt");
		Element fileElement = FileContent.makeFileElement("example.txt", bytes);
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", fileElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNotNull(fileContent);
		
		Assert.assertEquals("example.txt", fileContent.getFileName());
		Assert.assertEquals("example.txt", fileContent.getId());
		
		Assert.assertArrayEquals(bytes, fileContent.getFileContent());
		
		Assert.assertEquals(fileElement.asXML(), fileContent.getPayload().asXML());
	}

	@Test
	public void shouldNormalizeReturnFileContentFromXMLContentMultiPayload() throws Exception{
		byte[] bytes = getBytes("example.txt");
		Element fileElement = FileContent.makeFileElement("example.txt", bytes);
		Element payloadElement = DocumentHelper.createElement("foo");
		payloadElement.add(fileElement);
		
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", payloadElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNotNull(fileContent);
		
		Assert.assertEquals("example.txt", fileContent.getFileName());
		Assert.assertEquals("example.txt", fileContent.getId());
		
		Assert.assertArrayEquals(bytes, fileContent.getFileContent());
		
		Assert.assertEquals(fileElement.asXML(), fileContent.getPayload().asXML());
	}
	
	@Test
	public void shouldNormalizeReturnsNullWhenPayloadDoesNotContainsAFileContentElement(){
		Element payloadElement = DocumentHelper.createElement("foo");
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", payloadElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNull(fileContent);
	}

	@Test
	public void shouldNormalizeReturnsNullWhenXMLContentSinglePayloadDoesNotContainsFileIdAttribute(){
		Element payloadElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", payloadElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNull(fileContent);
	}

	@Test
	public void shouldNormalizeReturnsNullWhenXMLContentMultiPayloadDoesNotContainsFileIdAttribute(){
		Element fileElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		Element payloadElement = DocumentHelper.createElement("foo");
		payloadElement.add(fileElement);
		
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", payloadElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNull(fileContent);
	}
	
	@Test
	public void shouldNormalizeReturnsNullWhenXMLContentSinglePayloadDoesNotContainsFileContentElement(){
		Element payloadElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		payloadElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "example.txt");
		
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", payloadElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNull(fileContent);
	}

	@Test
	public void shouldNormalizeReturnsNullWhenXMLContentMultiPayloadDoesNotContainsFileContentElement(){
		Element fileElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
		fileElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "example.txt");
		Element payloadElement = DocumentHelper.createElement("foo");
		payloadElement.add(fileElement);
		
		XMLContent xmlContent = new XMLContent(IdGenerator.INSTANCE.newID(), "myTitle", "myDesc", payloadElement);
		FileContent fileContent = FileContent.normalize(xmlContent);
		
		Assert.assertNull(fileContent);
	}
	
	// ACCESS METHODS
	private byte[] getBytes(String resourceName){
		try{
			return FileUtils.read(getFileName("example.txt"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}
	
	private String getFileName(String resourceName) {
		return this.getClass().getResource(resourceName).getFile();
	}
}
