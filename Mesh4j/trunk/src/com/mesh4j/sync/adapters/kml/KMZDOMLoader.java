package com.mesh4j.sync.adapters.kml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.mesh4j.sync.adapters.dom.DOMLoader;
import com.mesh4j.sync.adapters.dom.IMeshDOM;
import com.mesh4j.sync.adapters.dom.parsers.IFileManager;
import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.ZipUtils;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMZDOMLoader extends DOMLoader {

	private IFileManager fileManager;
	
	public KMZDOMLoader(String fileName, IIdentityProvider identityProvider,
			IXMLView xmlView, IFileManager fileManager) {
		super(fileName, identityProvider, xmlView);
		
		String localFileName = fileName.trim(); 
		if(!localFileName.toUpperCase().endsWith(".KMZ")){
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
		}
		
		Guard.argumentNotNull(fileManager, "fileManager");
		this.fileManager = fileManager;
	}
	
	@Override
	protected void flush() {
		try {
			Map<String, byte[]> zipEntries = new HashMap<String, byte[]>(); 
			zipEntries.put(KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, this.getDOM().asXML().getBytes());
			
			for (String entryName : fileManager.getFileContents().keySet()) {
				byte[] decodedBytes = Base64Helper.decode(fileManager.getFileContent(entryName));
				zipEntries.put(entryName, decodedBytes);
			}	
			ZipUtils.write(this.getFile(), zipEntries);
		} catch (IOException e) {
			throw new MeshException(e);
		}		
	}

	@Override
	protected IMeshDOM load() {
		try {
			String kml = null;
			Map<String, byte[]> zipEntries = ZipUtils.getEntries(this.getFile());
			for (String entryName : zipEntries.keySet()) {
				if(KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML.equals(entryName)){
					kml = new String(zipEntries.get(entryName));
				} else {
					String contentAsBase64 = Base64Helper.encode(zipEntries.get(entryName));	
					fileManager.setFileContent(entryName, contentAsBase64);
				}
			}
			Document document = DocumentHelper.parseText(kml);
			return new KMLDOM(document, getIdentityProvider(), getXMLView());
		} catch (DocumentException e) {
			throw new MeshException(e);
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}

	@Override
	protected IMeshDOM createDocument(String name) {
		return new KMLDOM(name, getIdentityProvider(), getXMLView());
	}	
	
	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}
}
