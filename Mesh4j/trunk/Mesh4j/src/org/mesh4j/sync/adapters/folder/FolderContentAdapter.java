package org.mesh4j.sync.adapters.folder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class FolderContentAdapter implements IContentAdapter {

	// MODEL VARIABLES
	private File folder;
	private FilenameFilter filenameFilter;
	
	// BUSINESS METHODS
	public FolderContentAdapter(File folder, FilenameFilter filenameFilter){
		super();
		Guard.argumentNotNull(folder, "folder");
		Guard.argumentNotNull(filenameFilter, "filenameFilter");
		
		if(!folder.isDirectory()){
			Guard.throwsArgumentException("folder", folder.getName());
		}
		this.folder = folder;
		this.filenameFilter = filenameFilter;
	}
	
	@Override
	public void delete(IContent content) {
		String fileName = content.getId();
		File file = getFile(fileName);
		if(file.exists()){
			file.delete();
		}
	}

	@Override
	public IContent get(String fileName) {
		File file = getFile(fileName);
		if(file.exists()){
			return makeFileContent(fileName, file);
		}else{
			return new NullContent(fileName);
		}
	}

	@Override
	public List<IContent> getAll(Date since) {
		return getFileContents(this.folder, "", since);
	}

	private ArrayList<IContent> getFileContents(File fileFolder, String path, Date since) {
		ArrayList<IContent> result = new ArrayList<IContent>();
		File[] files = fileFolder.listFiles(this.filenameFilter);
		
		for (File file : files) {
			if(file.isFile()){
				String fileName = path + file.getName();
				if(since == null){
					result.add(makeFileContent(fileName, file));
				} else {
					if(file.lastModified() >= since.getTime()){
						result.add(makeFileContent(fileName, file));
					}
				}
			} else {
				String newPath;
				if(path.length() == 0){
					newPath = file.getName()+ File.separator;
				} else {
					newPath = path + file.getName()+ File.separator;
				}
				result.addAll(getFileContents(file, newPath, since));
			}
		}
		return result;
	}

	@Override
	public String getType() {
		return this.folder.getName();
	}

	@Override
	public void save(IContent content) {
		try{
			FileContent fileContent = FileContent.normalize(content);
			File file = getFile(fileContent.getFileName());
			if(!file.exists()){
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}

			FileUtils.write(file, fileContent.getFileContent());
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	private File getFile(String fileName) {
		try{
			return new File(this.folder.getCanonicalPath() + File.separator + fileName);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private IContent makeFileContent(String fileName, File file) {
		try{
			byte[] bytes = FileUtils.read(file);
			return new FileContent(fileName, bytes);
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
