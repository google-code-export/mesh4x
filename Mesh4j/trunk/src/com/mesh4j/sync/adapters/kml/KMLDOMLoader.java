package com.mesh4j.sync.adapters.kml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMLDOMLoader extends KMLMeshDOMLoader {

	public KMLDOMLoader(String fileName, IIdentityProvider identityProvider,
			XMLView xmlView) {
		super(fileName, identityProvider, xmlView);
		
		String localFileName = fileName.trim(); 
		if(!localFileName.toUpperCase().endsWith(".KML")){
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
		}
	}

	@Override
	protected void flush() {
		XMLHelper.write(this.getDocument().toDocument(), this.getFile());
	}

	@Override
	protected IKMLMeshDocument load() {
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(this.getFile());
			return new KMLMeshDocument(document, getIdentityProvider(), getXMLView());
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}

	@Override
	protected IKMLMeshDocument createDocument(String name) {
		return new KMLMeshDocument(name, getIdentityProvider(), getXMLView());
	}	
}
