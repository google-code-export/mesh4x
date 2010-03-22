package org.mesh4j.sync.adapters.feed.pfif.model;

import java.io.File;

import org.mesh4j.sync.adapters.feed.Feed;

public class PfifModel {
	private String entityName;
	private Feed feed;
	private File file;
	
	
	public PfifModel(String entityName, Feed feed, File file) {
		super();
		this.entityName = entityName;
		this.feed = feed;
		this.file = file;
	}


	public String getEntityName() {
		return entityName;
	}


	public Feed getFeed() {
		return feed;
	}


	public File getFile() {
		return file;
	}
	
	
}
