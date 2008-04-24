package com.sun.syndication.feed.module.feedsync;

import java.util.Set;

import org.jdom.Element;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.feedsync.modules.FeedSyncModule;
import com.sun.syndication.feed.module.feedsync.modules.SharingModule;
import com.sun.syndication.feed.module.feedsync.modules.SyncModule;
import com.sun.syndication.feed.synd.impl.Converters;
import com.sun.syndication.io.DelegatingModuleGenerator;
import com.sun.syndication.io.DelegatingModuleParser;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;

public abstract class FeedSyncParser implements DelegatingModuleParser, DelegatingModuleGenerator, WebContentSyndicationFormat {

	protected static final Converters CONVERTERS = new Converters();

	
	// DelegatingModuleParser API
	public abstract void setFeedParser(WireFeedParser feedParser);

	public Module parse(Element element) {
		if (this.isSharingRoot(element)) {
			return SharingModule.parse(element, this.getWebContentSyndicationFormat());
		} else if (this.isSyncRoot(element)){
			return SyncModule.parse(element, this.getWebContentSyndicationFormat());
		}else{
			return null;
		}
	}

	public WebContentSyndicationFormat getWebContentSyndicationFormat(){
		return this;
	}
	public abstract boolean isSharingRoot(Element element);
	public abstract boolean isSyncRoot(Element element);

	
	// DelegatingModuleGenerator API
	public abstract void setFeedGenerator(WireFeedGenerator feedGenerator);

	public void generate(Module module, Element element) {
		if (module instanceof SharingModule) {
			SharingModule sharing = (SharingModule) module;
			SharingModule.generate(element, this.getWebContentSyndicationFormat(), sharing);
		}else if(module instanceof SyncModule) {
			SyncModule sync = (SyncModule) module;
			SyncModule.generate(element, this.getWebContentSyndicationFormat(), sync);			
		}
	}
	public String getNamespaceUri() {
		return FeedSyncModule.SCHEMA_URI;
	}

	public Set getNamespaces() {
		return FeedSyncModule.NAMESPACES;
	}
	
}