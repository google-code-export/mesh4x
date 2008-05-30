package com.mesh4j.sync.adapters.kml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.mesh4j.sync.adapters.dom.DOMLoader;
import com.mesh4j.sync.adapters.dom.IMeshDOM;
import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMLDOMLoader extends DOMLoader {

	public KMLDOMLoader(String fileName, IIdentityProvider identityProvider,
			IXMLView xmlView) {
		super(fileName, identityProvider, xmlView);
		
		String localFileName = fileName.trim(); 
		if(!localFileName.toUpperCase().endsWith(".KML")){
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
		}
	}

	@Override
	protected void flush() {
		XMLHelper.write(this.getDOM().toDocument(), this.getFile());
	}

	@Override
	protected IMeshDOM load() {
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(this.getFile());
			return new KMLDOM(document, getIdentityProvider(), getXMLView());
		} catch (DocumentException e) {
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
