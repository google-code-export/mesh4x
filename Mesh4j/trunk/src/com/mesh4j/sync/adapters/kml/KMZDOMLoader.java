package com.mesh4j.sync.adapters.kml;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.ZipUtils;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMZDOMLoader extends KMLMeshDOMLoader {

	public KMZDOMLoader(String fileName, IIdentityProvider identityProvider,
			XMLView xmlView) {
		super(fileName, identityProvider, xmlView);
		
		String localFileName = fileName.trim(); 
		if(!localFileName.toUpperCase().endsWith(".KMZ")){
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
		}
	}

	@Override
	protected void flush() {
		try {
			ZipUtils.write(this.getFile(), KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, this.getDocument().asXML());
		} catch (IOException e) {
			throw new MeshException(e);
		}		
	}

	@Override
	protected IKMLMeshDocument load() {
		try {
			String kml = ZipUtils.getTextEntryContent(this.getFile(), KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
			Document document = DocumentHelper.parseText(kml);
			return new KMLMeshDocument(document, getIdentityProvider(), getXMLView());
		} catch (DocumentException e) {
			throw new MeshException(e);
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}

	@Override
	protected IKMLMeshDocument createDocument(String name) {
		return new KMLMeshDocument(name, getIdentityProvider(), getXMLView());
	}	
}
