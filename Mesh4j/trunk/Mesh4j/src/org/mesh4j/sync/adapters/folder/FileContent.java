package org.mesh4j.sync.adapters.folder;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.utils.Base64Helper;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class FileContent extends Content{

	// BUSINESS  METHODS
	private FileContent(Element payload, String id) {
		super(payload, id);
	}

	public FileContent(String fileName, byte[] fileContentBytes) {
		this(makeFileElement(fileName, fileContentBytes), fileName);
	}

	@Override
	public FileContent clone() {
		return new FileContent(this.getPayload(), this.getId());
	}

	public String getFileName() {
		return this.getId();
	}

	public byte[] getFileContent() throws Exception {
		Element fileContentElement = getPayload().element(MeshNames.MESH_QNAME_FILE_CONTENT);
		String fileContentAsBase64 = fileContentElement.getText();
		byte[] fileContentAsZipBytes = Base64Helper.decode(fileContentAsBase64);
		byte[] fileContentBytes = ZipUtils.decompress(fileContentAsZipBytes);
		return fileContentBytes;
	}

	public static FileContent normalize(IContent content) {
		if(content == null){
			return null;
		}
		
		if(content instanceof FileContent){
			FileContent entity = (FileContent)content;
			entity.refreshVersion();
			return entity;
		}else{
			Element fileContent = null;
			if(MeshNames.MESH_QNAME_FILE.equals(content.getPayload().getQName())){
				fileContent = content.getPayload();
			}else{
				fileContent = content.getPayload().element(MeshNames.MESH_QNAME_FILE);
			}
			if(fileContent == null){
				return null;
			}else{
				String fileName = fileContent.attributeValue(MeshNames.MESH_QNAME_FILE_ID);
				if(fileName == null){
					return null;
				}
				
				if(fileContent.element(MeshNames.MESH_QNAME_FILE_CONTENT) == null){
					return null;
				}
				
				return new FileContent(fileContent, fileName);
			}
		}
	}

	public static Element makeFileElement(String fileName, byte[] fileContentBytes){
		
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(fileContentBytes, "fileContentBytes");
		
		try{
			Element fileElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE);
			fileElement.addAttribute(MeshNames.MESH_QNAME_FILE_ID, fileName);
		
			byte[] fileContentAsZipBytes = ZipUtils.compress(fileContentBytes);
			String fileContentAsBase64 = Base64Helper.encode(fileContentAsZipBytes);
			Element fileContentElement = fileElement.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
			fileContentElement.setText(fileContentAsBase64);
			return fileElement;
		} catch (Exception e) {
			throw new MeshException(e);
		}		
	}
}
