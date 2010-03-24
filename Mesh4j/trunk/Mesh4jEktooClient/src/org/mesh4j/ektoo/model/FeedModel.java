package org.mesh4j.ektoo.model;

import java.io.File;

import org.mesh4j.ektoo.controller.FeedUIController;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class FeedModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String fileName = null;
	private ISyndicationFormat syndicationFormat = RssSyndicationFormat.INSTANCE;
	private String baseUrl = null;
	

	// BUSINESS METHODS
	public FeedModel(String fileName, ISyndicationFormat syndicationFormat, String baseURL) {
		super();
		this.fileName = fileName;
		this.syndicationFormat = syndicationFormat;
		this.baseUrl = baseURL;
	}
	
	
	public void setFileName(String fileName) {
		firePropertyChange(FeedUIController.FILE_NAME_PROPERTY, this.fileName, this.fileName = fileName);
	}
	
	 
	


	public String getFileName() {
		return this.fileName;
	}
	
	 public String toString()
	 {
	    return "FEED | " + getFileName();
	 }

	

	public String getFeedTile() {
		return getFeedName();
	}

	public ISyndicationFormat getSyndicationFormat() {
		return this.syndicationFormat;
	}

	public String getFeedLink() {
		return this.baseUrl + "/" + getFeedName();
	}

	public String getFeedDescription() {
		return getFeedName();
	}
	
	private String getFeedName() {
		File file = new File(this.fileName);
		return file.getName();
	}
}