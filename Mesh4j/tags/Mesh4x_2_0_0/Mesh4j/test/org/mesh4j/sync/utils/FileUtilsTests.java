package org.mesh4j.sync.utils;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;

public class FileUtilsTests {

	@Test
	public void shouldGetFileNameWithOutExtension(){
		Assert.assertEquals("myFile", FileUtils.getFileNameWithOutExtension(new File("myFile.tx")));
		Assert.assertEquals("myFile", FileUtils.getFileNameWithOutExtension(new File("myFile.txt")));
		Assert.assertEquals("myFile", FileUtils.getFileNameWithOutExtension(new File("myFile.txtz")));
	}
	
	@Test
	public void shouldGetFileNameWithOutExtensionWhenFileHasNotExtension(){
		Assert.assertEquals("myFile", FileUtils.getFileNameWithOutExtension(new File("myFile")));
	}
	
	@Test
	public void shouldGetFileName(){
		Assert.assertEquals("c:\\tests\\myFile.txt", FileUtils.getFileName("c:\\tests", "myFile.txt"));
	}
	
	@Test
	public void shouldGetFileNameWhenFolderHasFileSeparator(){
		Assert.assertEquals("c:\\tests\\myFile.txt", FileUtils.getFileName("c:\\tests\\", "myFile.txt"));
	}

	@Test
	public void shouldDeleteFile() throws IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("testDeleteFile.txt");
		Assert.assertFalse(file.exists());
		file.createNewFile();
		Assert.assertTrue(file.exists());
		
		FileUtils.delete(file);
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void shouldDeleteFolder() throws IOException{
		File file = TestHelper.makeFileAndDeleteIfExists("testDeleteFolder");
		Assert.assertFalse(file.exists());
		file.mkdir();
		Assert.assertTrue(file.exists());
		
		FileUtils.delete(file);
		Assert.assertFalse(file.exists());
		
	}
	
	@Test
	public void shouldDeleteFolderWithSubFoldersAndFiles() throws IOException{
		File folder = TestHelper.makeFileAndDeleteIfExists("testDeleteFolder");
		Assert.assertFalse(folder.exists());
		folder.mkdir();
		Assert.assertTrue(folder.exists());
		
		File subFolder = new File(folder, "subfolder");
		subFolder.mkdir();
		Assert.assertTrue(subFolder.exists());
		
		File file = new File(subFolder, "myFile.txt");
		file.createNewFile();
		Assert.assertTrue(file.exists());
		
		FileUtils.delete(folder);
		
		Assert.assertFalse(file.exists());
		Assert.assertFalse(subFolder.exists());
		Assert.assertFalse(folder.exists());
	}
}
