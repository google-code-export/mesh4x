package org.mesh4j.sync.adapters.dom.parsers;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;

public class FileManagerTests {

	@Test
	public void shouldCreateFileManagerWithEmptyFileList(){
		Assert.assertEquals(0, new FileManager().getFileContents().size());
	}
	
	@Test
	public void shouldAddFileContent(){
		FileManager fm = new FileManager();		
		Assert.assertEquals(0, fm.getFileContents().size());		
		fm.setFileContent(TestHelper.fileName("a.txt"), "123");
		Assert.assertEquals(1, fm.getFileContents().size());
		
		fm.setFileContent(TestHelper.fileName("b.txt"), "12345");
		Assert.assertEquals(2, fm.getFileContents().size());
	}
	
	@Test
	public void shouldGetFileContent(){
		FileManager fm = new FileManager();		
		Assert.assertEquals(0, fm.getFileContents().size());		
		Assert.assertNull(fm.getFileContent(TestHelper.fileName("a.txt")));
		
		fm.setFileContent(TestHelper.fileName("a.txt"), "123");
		Assert.assertEquals("123", fm.getFileContent(TestHelper.fileName("a.txt")));
		
		fm.setFileContent(TestHelper.fileName("b.txt"), "321");
		Assert.assertEquals("321", fm.getFileContent(TestHelper.fileName("b.txt")));
		
		Assert.assertNull(fm.getFileContent("c.txt"));
	}
	
	@Test
	public void shouldRemoveFileContent(){
		FileManager fm = new FileManager();		
		Assert.assertEquals(0, fm.getFileContents().size());		
		fm.removeFileContent(TestHelper.fileName("a.txt"));
		Assert.assertEquals(0, fm.getFileContents().size());
		
		fm.setFileContent(TestHelper.fileName("a.txt"), "123");
		Assert.assertEquals(1, fm.getFileContents().size());
		fm.removeFileContent(TestHelper.fileName("a.txt"));
		Assert.assertEquals(0, fm.getFileContents().size());

		fm.setFileContent(TestHelper.fileName("a.txt"), "123");
		fm.setFileContent(TestHelper.fileName("b.txt"), "321");
		Assert.assertEquals(2, fm.getFileContents().size());

		fm.removeFileContent(TestHelper.fileName("a.txt"));
		Assert.assertEquals(1, fm.getFileContents().size());

	}
	
	@Test
	public void shouldGetFiles(){
		FileManager fm = new FileManager();		
		Map<String, String> fileContents = fm.getFileContents();
		
		Assert.assertEquals(0, fileContents.size());		
		fm.setFileContent(TestHelper.fileName("a.txt"), "123");
		fm.setFileContent(TestHelper.fileName("b.txt"), "1234");
		fm.setFileContent(TestHelper.fileName("c.txt"), "12345");
		
		fileContents = fm.getFileContents();
		Assert.assertEquals(3, fileContents.size());
		Assert.assertEquals("123", fileContents.get(TestHelper.fileName("a.txt")));
		Assert.assertEquals("1234", fileContents.get(TestHelper.fileName("b.txt")));
		Assert.assertEquals("12345", fileContents.get(TestHelper.fileName("c.txt")));
		
	}
}
