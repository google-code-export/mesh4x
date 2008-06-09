package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.mesh4j.sync.adapters.dom.DOMLoader;
import com.mesh4j.sync.adapters.dom.IMeshDOM;
import com.mesh4j.sync.adapters.dom.parsers.IFileManager;
import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.FileUtils;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMLDOMLoader extends DOMLoader {

	private IFileManager fileManager;
	
	public KMLDOMLoader(String fileName, IIdentityProvider identityProvider,
			IXMLView xmlView, IFileManager fileManager) {
		super(fileName, identityProvider, xmlView);
		
		String localFileName = fileName.trim(); 
		if(!localFileName.toUpperCase().endsWith(".KML")){
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
		}
		
		Guard.argumentNotNull(fileManager, "fileManager");
		this.fileManager = fileManager;
	}

	@Override
	protected void flush() {
		try{
			File kmlFile = this.getFile();
			XMLHelper.write(this.getDOM().toDocument(), kmlFile);
			
			if(fileManager.getFileContents().size() > 0){
				File kmlFilesDirectory = new File(kmlFile.getParent() + "/files");
				if(!kmlFilesDirectory.exists()){
					kmlFilesDirectory.mkdir();
				}
			
				for (String entryName : fileManager.getFileContents().keySet()) {
					byte[] decodedBytes = Base64Helper.decode(fileManager.getFileContent(entryName));
					String fileName = kmlFile.getParent() + "/" + entryName;
					FileUtils.write(fileName, decodedBytes);
				}
			}
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}

	@Override
	protected IMeshDOM load() {
		try {
			File kmlFile = this.getFile();
			File kmlFilesDirectory = new File(kmlFile.getParent() + "/files");
			if(kmlFilesDirectory.exists()){
				File[] files = kmlFilesDirectory.listFiles();
				for (File file : files) {
					if(file.isFile()){
						String entryName = "files/" + file.getName();
						byte[] fileContet = FileUtils.read(file);
						String contentAsBase64 = Base64Helper.encode(fileContet);	
						fileManager.setFileContent(entryName, contentAsBase64);
					}
				}
			}
			
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(kmlFile);
			return new KMLDOM(document, getIdentityProvider(), getXMLView());
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	protected IMeshDOM createDocument(String name) {
		return new KMLDOM(name, getIdentityProvider(), getXMLView());
	}

	@Override
	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}	
}
