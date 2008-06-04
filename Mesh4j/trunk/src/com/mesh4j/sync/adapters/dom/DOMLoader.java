package com.mesh4j.sync.adapters.dom;

import java.io.File;

import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public abstract class DOMLoader implements IDOMLoader {

	// MODEL VARIABLES
	private File file;
	private IMeshDOM dom;
	private IIdentityProvider identityProvider;
	private IXMLView xmlView;
	
	// BUSINESS METHODS

	public DOMLoader(String fileName, IIdentityProvider identityProvider, IXMLView xmlView) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(xmlView, "xmlView");
		
		this.file = new File(fileName);
		this.identityProvider = identityProvider;
		this.xmlView = xmlView;
	}

	public void read() {
		if(!file.exists()){
			this.dom = this.createDocument(this.file.getName());
		}else{
			this.dom = this.load();
		}
		this.dom.updateMeshStatus();
	}

	protected abstract IMeshDOM createDocument(String name);
	

	public void write() {
		this.flush();
	}
	
	protected abstract void flush();
	
	protected abstract IMeshDOM load();

	public IMeshDOM getDOM() {
		return this.dom;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public IIdentityProvider getIdentityProvider(){
		return identityProvider;
	}
	
	public IXMLView getXMLView(){
		return xmlView;
	}
	

}
