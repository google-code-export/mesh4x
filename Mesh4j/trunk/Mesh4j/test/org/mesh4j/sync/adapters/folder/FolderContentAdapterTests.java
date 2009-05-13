package org.mesh4j.sync.adapters.folder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;

public class FolderContentAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFolderIsNull(){
		new FolderContentAdapter(null, new FilesFilter());
	}

	@Test(expected=IllegalArgumentException.class) 
	public void shouldCreateAdapterFailsIfFileFilterIsNull(){
		File file = getFolder("example.txt");
		Assert.assertNotNull(file);
		new FolderContentAdapter(file, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFolderIsNotDirectory(){
		File file = getFile("example.txt");
		Assert.assertNotNull(file);
		Assert.assertFalse(file.isDirectory());
		new FolderContentAdapter(file, new FilesFilter());
	}
	
	@Test
	public void shouldCreateAdapter(){
		File folder = getFolder("example.txt");
		Assert.assertNotNull(folder);
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		Assert.assertEquals(folder.getName(), adapter.getType());
	}
	
	@Test 
	public void shouldDeleteFileIfFileExists() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		FileContent fileContent = new FileContent(file.getName(), FileUtils.read(file));
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.delete(fileContent);
		
		Assert.assertTrue(folder.exists());
		Assert.assertFalse(file.exists());
		
		folder.delete();
	}	
	
	@Test 
	public void shouldDeleteNotFailsIfFileDoesNotExists() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = FileUtils.getFile(folder.getCanonicalPath(), IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertFalse(file.exists());
		
		FileContent fileContent = new FileContent(file.getName(), "no exists".getBytes());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.delete(fileContent);
		
		Assert.assertTrue(folder.exists());
		Assert.assertFalse(file.exists());
		
		folder.delete();
	}	
	
	@Test
	public void shouldGetReturnsFileContentIfFileExists() throws Exception{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		FileContent fileContent = (FileContent)adapter.get(file.getName());
		
		Assert.assertNotNull(fileContent);
		Assert.assertEquals(file.getName(), fileContent.getFileName());
		Assert.assertEquals(file.getName(), fileContent.getId());
		Assert.assertArrayEquals(FileUtils.read(file), fileContent.getFileContent());
		
		Assert.assertTrue(folder.exists());
		Assert.assertTrue(file.exists());
		
		file.delete();
		folder.delete();
	}

	@Test
	public void shouldGetReturnsNullContentIfFileDoesNotExists() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = FileUtils.getFile(folder.getCanonicalPath(), IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertFalse(file.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		IContent fileContent = adapter.get(file.getName());
		
		Assert.assertNotNull(fileContent);
		Assert.assertEquals(file.getName(), fileContent.getId());
		Assert.assertEquals(NullContent.class, fileContent.getClass());
		
		Assert.assertTrue(folder.exists());
		folder.delete();
	}
	
	@Test
	public void shouldGetAllReturnsEmptyListIfFolderIsEmpty(){
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		Assert.assertEquals(0, folder.list().length);
				
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(null);
		
		Assert.assertNotNull(fileContents);
		Assert.assertTrue(fileContents.isEmpty());
		
		Assert.assertTrue(folder.exists());
		folder.delete();
	}	

	@Test
	public void shouldGetAllReturnsEmptyListIfFolderIsEmptyWithSinceDate(){
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		Assert.assertEquals(0, folder.list().length);
				
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(new Date());
		
		Assert.assertNotNull(fileContents);
		Assert.assertTrue(fileContents.isEmpty());
		
		Assert.assertTrue(folder.exists());
		folder.delete();
	}	
	
	@Test
	public void shouldGetAllReturnsListWithAllFolderFiles() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		File file2 = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(null);
		
		Assert.assertNotNull(fileContents);
		Assert.assertFalse(fileContents.isEmpty());
		Assert.assertEquals(2, fileContents.size());
		
		Assert.assertTrue(file.getName().equals(fileContents.get(0).getId()) || file.getName().equals(fileContents.get(1).getId()));
		Assert.assertTrue(file2.getName().equals(fileContents.get(0).getId()) || file2.getName().equals(fileContents.get(1).getId()));
		Assert.assertFalse(fileContents.get(0).getId().equals(fileContents.get(1).getId()));
		
		file.delete();
		file2.delete();
		folder.delete();
	}
	
	@Test
	public void shouldGetAllReturnsListWithAllFolderFilesModifiedSinceDate() throws Exception{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		Thread.sleep(500);
		Date since = new Date();
		
		File file2 = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(since);
		
		Assert.assertNotNull(fileContents);
		Assert.assertFalse(fileContents.isEmpty());
		Assert.assertEquals(1, fileContents.size());
		Assert.assertEquals(file2.getName(), fileContents.get(0).getId());
		
		file.delete();
		file2.delete();
		folder.delete();
	}
	
	@Test
	public void shouldSaveCreateFileIfFileDoesNotExists() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		Assert.assertEquals(0, folder.list().length);
		
		FileContent fileContent = new FileContent("example.txt", "example".getBytes());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.save(fileContent);

		File[] files = folder.listFiles();
		Assert.assertEquals(1, files.length);
		
		File file = files[0];
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("example".getBytes(), FileUtils.read(file));
		
		file.delete();
		folder.delete();
	}
	
	@Test
	public void shouldSaveUpdateFileIfFileExists() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, "example.txt");
		
		File[] files = folder.listFiles();
		Assert.assertEquals(1, files.length);
		Assert.assertEquals(file.getName(), files[0].getName());
		Assert.assertArrayEquals("example.txt".getBytes(), FileUtils.read(file.getCanonicalPath()));
		
		FileContent fileContent = new FileContent("example.txt", "example4444".getBytes());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.save(fileContent);

		files = folder.listFiles();
		Assert.assertEquals(1, files.length);
		
		file = files[0];
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("example4444".getBytes(), FileUtils.read(file));
		
		file.delete();
		folder.delete();
	}
	
	@Test
	public void shouldGetAllReturnsListWithAllSubFolderFiles() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		File subFolder = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
		
		File file2 = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(null);
		
		Assert.assertNotNull(fileContents);
		Assert.assertFalse(fileContents.isEmpty());
		Assert.assertEquals(2, fileContents.size());
		
		String expectedSubfolderFileName = subFolder.getName() + File.separator + file2.getName();
		
		Assert.assertTrue(file.getName().equals(fileContents.get(0).getId()) || file.getName().equals(fileContents.get(1).getId()));
		Assert.assertTrue(expectedSubfolderFileName.equals(fileContents.get(0).getId()) || expectedSubfolderFileName.equals(fileContents.get(1).getId()));
		Assert.assertFalse(fileContents.get(0).getId().equals(fileContents.get(1).getId()));
		
		file.delete();
		file2.delete();
		subFolder.delete();
		folder.delete();
	}
	
	@Test
	public void shouldGetAllReturnsListWithAllSubFolderFilesModifiedSinceDate() throws Exception{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolder = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
		
		File fileX = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(fileX);
		Assert.assertTrue(fileX.exists());
		
		File fileX2 = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(fileX2);
		Assert.assertTrue(fileX2.exists());
		
		Thread.sleep(500);
		Date since = new Date();
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		File file2 = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(since);
		
		Assert.assertNotNull(fileContents);
		Assert.assertFalse(fileContents.isEmpty());
		Assert.assertEquals(2, fileContents.size());
		
		String expectedSubfolderFileName = subFolder.getName() + File.separator + file2.getName();
		
		Assert.assertTrue(file.getName().equals(fileContents.get(0).getId()) || file.getName().equals(fileContents.get(1).getId()));
		Assert.assertTrue(expectedSubfolderFileName.equals(fileContents.get(0).getId()) || expectedSubfolderFileName.equals(fileContents.get(1).getId()));
		Assert.assertFalse(fileContents.get(0).getId().equals(fileContents.get(1).getId()));
		
		file.delete();
		file2.delete();
		fileX.delete();
		fileX2.delete();
		subFolder.delete();
		folder.delete();
	}
	
	@Test
	public void shouldGetReturnsSubFolderFile() throws Exception{
		
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolder = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
	
		File file = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		String fileName = subFolder.getName() + File.separator + file.getName();
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		FileContent fileContent = (FileContent)adapter.get(fileName);
		
		Assert.assertNotNull(fileContent);
		Assert.assertEquals(fileName, fileContent.getFileName());
		Assert.assertEquals(fileName, fileContent.getId());
		Assert.assertArrayEquals(FileUtils.read(file), fileContent.getFileContent());

		file.delete();
		subFolder.delete();
		folder.delete();
	}
	
	@Test 
	public void shouldDeleteFileInSubFolder() throws IOException{
		
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolder = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
	
		File file = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		String fileName = subFolder.getName() + File.separator + file.getName();
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.delete(new FileContent(fileName, "file to delete".getBytes()));
		
		Assert.assertFalse(file.exists());

		subFolder.delete();
		folder.delete();
	}	

	@Test 
	public void shouldSaveFileInSubFolder() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolder = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
		Assert.assertEquals(0, subFolder.list().length);
	
		String fileName = subFolder.getName() + File.separator +  "example.txt";
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.save(new FileContent(fileName, "new file".getBytes()));
		
		File[] files = subFolder.listFiles();
		Assert.assertEquals(1, files.length);
		
		File file = files[0];
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("new file".getBytes(), FileUtils.read(file));

		file.delete();
		subFolder.delete();
		folder.delete();
	}	
	
	@Test 
	public void shouldUpdateFileInSubFolder() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolder = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
	
		File file = makeNewFile(subFolder, "example.txt");
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("example.txt".getBytes(), FileUtils.read(file));
		
		String fileName = subFolder.getName() + File.separator + file.getName();
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.save(new FileContent(fileName, "file to update".getBytes()));
		
		File[] files = subFolder.listFiles();
		Assert.assertEquals(1, files.length);
		
		file = files[0];
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("file to update".getBytes(), FileUtils.read(file));

		file.delete();
		subFolder.delete();
		folder.delete();
	}	
	
	@Test
	public void shouldGetAll() throws IOException, Exception{
		FilesFilter filter = new FilesFilter();
		filter.includeFileName("example.txt");
		
		FolderContentAdapter folderAdapter = new FolderContentAdapter(getFolder("example.txt"), filter);
		List<IContent> files = folderAdapter.getAll(TestHelper.makeDate(2009, 01, 01, 01, 01, 01, 01));

		Assert.assertEquals(1, files.size());
		FileContent fileContent = (FileContent) files.get(0);
		
		Assert.assertEquals("example.txt", fileContent.getFileName());
		
		String fullFileName = this.getClass().getResource("example.txt").getFile();
		Assert.assertEquals(new String(FileUtils.read(fullFileName)), new String(fileContent.getFileContent()));

	}
	
	@Test
	public void shouldGetAllReturnsListWithAllSubSubFolderFiles() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		File subFolderAux = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolderAux);
		Assert.assertTrue(subFolderAux.exists());
		
		File subFolder = makeNewFolder(subFolderAux, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
		
		File file2 = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(null);
		
		Assert.assertNotNull(fileContents);
		Assert.assertFalse(fileContents.isEmpty());
		Assert.assertEquals(2, fileContents.size());
		
		String expectedSubfolderFileName = subFolderAux.getName() + File.separator + subFolder.getName() + File.separator + file2.getName();
		
		Assert.assertTrue(file.getName().equals(fileContents.get(0).getId()) || file.getName().equals(fileContents.get(1).getId()));
		Assert.assertTrue(expectedSubfolderFileName.equals(fileContents.get(0).getId()) || expectedSubfolderFileName.equals(fileContents.get(1).getId()));
		Assert.assertFalse(fileContents.get(0).getId().equals(fileContents.get(1).getId()));
		
		file.delete();
		file2.delete();
		subFolder.delete();
		subFolderAux.delete();
		folder.delete();
	}
	
	@Test
	public void shouldGetAllReturnsListWithAllSubSubFolderFilesModifiedSinceDate() throws Exception{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolderAux = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolderAux);
		Assert.assertTrue(subFolderAux.exists());
		
		File subFolder = makeNewFolder(subFolderAux, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
		
		File fileX = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(fileX);
		Assert.assertTrue(fileX.exists());
		
		File fileX2 = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(fileX2);
		Assert.assertTrue(fileX2.exists());
		
		Thread.sleep(500);
		Date since = new Date();
		
		File file = makeNewFile(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		File file2 = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		List<IContent> fileContents = adapter.getAll(since);
		
		Assert.assertNotNull(fileContents);
		Assert.assertFalse(fileContents.isEmpty());
		Assert.assertEquals(2, fileContents.size());
		
		String expectedSubfolderFileName = subFolderAux.getName() + File.separator + subFolder.getName() + File.separator + file2.getName();
		
		Assert.assertTrue(file.getName().equals(fileContents.get(0).getId()) || file.getName().equals(fileContents.get(1).getId()));
		Assert.assertTrue(expectedSubfolderFileName.equals(fileContents.get(0).getId()) || expectedSubfolderFileName.equals(fileContents.get(1).getId()));
		Assert.assertFalse(fileContents.get(0).getId().equals(fileContents.get(1).getId()));
		
		file.delete();
		file2.delete();
		fileX.delete();
		fileX2.delete();
		subFolder.delete();
		subFolderAux.delete();
		folder.delete();
	}
	
	@Test
	public void shouldGetReturnsSubSubFolderFile() throws Exception{
		
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolderAux = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolderAux);
		Assert.assertTrue(subFolderAux.exists());
		
		File subFolder = makeNewFolder(subFolderAux, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
	
		File file = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		String fileName = subFolderAux.getName() + File.separator + subFolder.getName() + File.separator + file.getName();
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		FileContent fileContent = (FileContent)adapter.get(fileName);
		
		Assert.assertNotNull(fileContent);
		Assert.assertEquals(fileName, fileContent.getFileName());
		Assert.assertEquals(fileName, fileContent.getId());
		Assert.assertArrayEquals(FileUtils.read(file), fileContent.getFileContent());

		file.delete();
		subFolder.delete();
		subFolderAux.delete();
		folder.delete();
	}
	
	@Test 
	public void shouldDeleteFileInSubSubFolder() throws IOException{
		
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolderAux = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolderAux);
		Assert.assertTrue(subFolderAux.exists());
		
		File subFolder = makeNewFolder(subFolderAux, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
	
		File file = makeNewFile(subFolder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		String fileName = subFolderAux.getName() + File.separator + subFolder.getName() + File.separator + file.getName();
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.delete(new FileContent(fileName, "file to delete".getBytes()));
		
		Assert.assertFalse(file.exists());

		subFolder.delete();
		subFolderAux.delete();
		folder.delete();
	}	

	@Test 
	public void shouldSaveFileInSubSubFolder() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolderAux = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolderAux);
		Assert.assertTrue(subFolderAux.exists());
		
		File subFolder = makeNewFolder(subFolderAux, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
		Assert.assertEquals(0, subFolder.list().length);
	
		String fileName = subFolderAux.getName() + File.separator + subFolder.getName() + File.separator +  "example.txt";
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.save(new FileContent(fileName, "new file".getBytes()));
		
		File[] files = subFolder.listFiles();
		Assert.assertEquals(1, files.length);
		
		File file = files[0];
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("new file".getBytes(), FileUtils.read(file));

		file.delete();
		subFolder.delete();
		subFolderAux.delete();
		folder.delete();
	}	
	
	@Test 
	public void shouldUpdateFileInSubSubFolder() throws IOException{
		File folder = makeNewFolder(IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(folder);
		Assert.assertTrue(folder.exists());

		File subFolderAux = makeNewFolder(folder, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolderAux);
		Assert.assertTrue(subFolderAux.exists());
		
		File subFolder = makeNewFolder(subFolderAux, IdGenerator.INSTANCE.newID());
		Assert.assertNotNull(subFolder);
		Assert.assertTrue(subFolder.exists());
	
		File file = makeNewFile(subFolder, "example.txt");
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("example.txt".getBytes(), FileUtils.read(file));
		
		String fileName = subFolderAux.getName() + File.separator + subFolder.getName() + File.separator + file.getName();
		
		FolderContentAdapter adapter = new FolderContentAdapter(folder, new FilesFilter());
		adapter.save(new FileContent(fileName, "file to update".getBytes()));
		
		File[] files = subFolder.listFiles();
		Assert.assertEquals(1, files.length);
		
		file = files[0];
		Assert.assertEquals("example.txt", file.getName());
		Assert.assertArrayEquals("file to update".getBytes(), FileUtils.read(file));

		file.delete();
		subFolder.delete();
		subFolderAux.delete();
		folder.delete();
	}	
	
	// ACCESS METHODS
	private File getFile(String fileName) {
		String fullFileName = this.getClass().getResource(fileName).getFile();
		File file = new File(fullFileName);
		return file;
	}

	private File getFolder(String fileName) {
		String fullFileName = this.getClass().getResource(fileName).getFile();
		String folderName =  fullFileName.substring(0, fullFileName.length() - fileName.length());
		File folder = new File(folderName);
		return folder;
	}

	private File makeNewFile(File folder, String fileName) throws IOException {
		File file = FileUtils.getFile(folder.getCanonicalPath(), fileName);
		if(!file.exists()){
			FileUtils.write(file.getCanonicalPath(), fileName.getBytes());
		}
		return file;
	}

	private File makeNewFolder(String folderName) {
		File folder = new File(TestHelper.baseDirectoryRootForTest()+ File.separator + folderName);
		if(!folder.exists()){
			folder.mkdir();
		}
		return folder;
	}
	
	private File makeNewFolder(File folder, String folderName) throws IOException {
		File subFolder = new File(folder.getCanonicalPath() + File.separator + folderName);
		if(!subFolder.exists()){
			subFolder.mkdir();
		}
		return subFolder;
	}
}
