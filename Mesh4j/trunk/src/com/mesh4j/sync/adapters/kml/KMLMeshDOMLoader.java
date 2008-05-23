package com.mesh4j.sync.adapters.kml;

import java.io.File;

import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public abstract class KMLMeshDOMLoader implements IKMLMeshDomLoader {

	// MODEL VARIABLES
	private File file;
	private IKMLMeshDocument kmlDocument;
	private IIdentityProvider identityProvider;
	private XMLView xmlView;
	
	// BUSINESS METHODS

	public KMLMeshDOMLoader(String fileName, IIdentityProvider identityProvider, XMLView xmlView) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(xmlView, "xmlView");
		
		this.file = new File(fileName);
		this.identityProvider = identityProvider;
		this.xmlView = xmlView;
	}

	public void read() {
		if(!file.exists()){
			this.kmlDocument = this.createDocument(this.file.getName());
		}else{
			this.kmlDocument = this.load();
		}
		this.kmlDocument.updateMeshStatus();
	}

	protected abstract IKMLMeshDocument createDocument(String name);
	

	public void write() {
		this.kmlDocument.normalize();
		this.flush();
	}
	
	protected abstract void flush();
	
	protected abstract IKMLMeshDocument load();

	public IKMLMeshDocument getDocument() {
		return this.kmlDocument;
	}
	
	protected File getFile(){
		return this.file;
	}
	
	public IIdentityProvider getIdentityProvider(){
		return identityProvider;
	}
	
	protected XMLView getXMLView(){
		return xmlView;
	}
	

}
