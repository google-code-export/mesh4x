package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.FeedUIController;
import org.mesh4j.ektoo.controller.PFIFUIController;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class PFIFModel extends FeedModel {

	public String entityNames[] = null;
	private String fileName = null;
	private ISyndicationFormat syndicationFormat = RssSyndicationFormat.INSTANCE;
	

	

	public PFIFModel(String fileName, 
			ISyndicationFormat syndicationFormat,
			String baseURL) {
		super(fileName, syndicationFormat, baseURL);

	}
	
	public String getFileName() {
		return fileName;
	}

	public void setSyndicationFormat(Boolean isAtom) {
		if(isAtom){
			this.syndicationFormat = AtomSyndicationFormat.INSTANCE;	
		} else {
			this.syndicationFormat = RssSyndicationFormat.INSTANCE;
		}
		
	}
	
	public void setFileName(String fileName) {
		firePropertyChange(FeedUIController.FILE_NAME_PROPERTY, this.fileName, this.fileName = fileName);
	}
	
	public String[] getEntityNames() {
		return entityNames;
	}

	public void setEntityNames(String[] entityNames) {
		firePropertyChange( PFIFUIController.ENTITY_NAMES, this.entityNames,
				this.entityNames = entityNames);
	}
	
	public ISyndicationFormat getSyndicationFormat() {
		return syndicationFormat;
	}

	public String toString(){
	     return "PFIF | " + getFileName();
	}	

}
