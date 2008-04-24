package com.sun.syndication.feed.module.feedsync.modules;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.WebContentSyndicationFormat;

public class SyncModule extends FeedSyncModule {

	// CONSTANTS
	private static final long serialVersionUID = 7197492869035937855L;
	
	// MODEL VARIABLES
	private String id;
	private int updates = 0 ;
	private boolean deleted = false;
	private boolean noConflicts = false;
	private List<History> histories = new ArrayList<History>();
	private List<ConflictItem> conflicts = new ArrayList<ConflictItem>();
	
	// BUSINESS METHODS
	private String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	private int getUpdates() {
		return updates;
	}

	private void setUpdates(int updates) {
		this.updates = updates;
	}

	private boolean isDeleted() {
		return deleted;
	}

	private void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	private boolean isNoConflicts() {
		return noConflicts;
	}

	private void setNoConflicts(boolean noConflicts) {
		this.noConflicts = noConflicts;
	}
	
	public List<History> getHistories() {
		return histories;
	}
	
	public void addHistory(History history){
		this.histories.add(history);		
	}
 
	public List<ConflictItem> getConflicts() {
		return conflicts;
	}
	
	public void addConflict(ConflictItem conflict){
		this.conflicts.add(conflict);		
	}
	
	// FeedSyncModule protocol
	@Override
	public void copyFrom(Object obj) {
		SyncModule sync = (SyncModule) obj;
		this.setDeleted(sync.isDeleted());
		this.setNoConflicts(sync.isNoConflicts());
		this.setId(sync.getId());
		this.setUpdates(sync.getUpdates());
		this.histories.addAll(sync.getHistories());
		this.conflicts.addAll(sync.getConflicts());
	}

	public static SyncModule parse(Element element, WebContentSyndicationFormat webContentSyndicationFormat) {
		SyncModule sync = null;
		Element syncChild = element.getChild("sync", NAMESPACE);
		if (syncChild != null) {
			Attribute idAttribute = syncChild.getAttribute("id");
			Attribute updatesAttribute = syncChild.getAttribute("updates");
			Attribute deletedAttribute = syncChild.getAttribute("deleted");
			Attribute noConflictsAttribute = syncChild.getAttribute("noConflicts");

			sync = new SyncModule();
			sync.setId(idAttribute == null ? null : idAttribute.getValue().trim());
			sync.setUpdates(updatesAttribute == null ? 0 : Integer.parseInt(updatesAttribute.getValue().trim()));
			sync.setDeleted(deletedAttribute == null ? false : Boolean.parseBoolean(deletedAttribute.getValue().trim()));
			sync.setNoConflicts(noConflictsAttribute == null ? false : Boolean.parseBoolean(noConflictsAttribute.getValue().trim()));
		
			History.parse(syncChild, webContentSyndicationFormat, sync);
			ConflictItem.parse(syncChild, webContentSyndicationFormat, sync);
		}
		return sync;
	}

	public static void generate(Element elementRoot, WebContentSyndicationFormat webContentSyndicationFormat, SyncModule sync) {
		elementRoot.addNamespaceDeclaration(NAMESPACE);
		Element syncElement = new Element("sync", NAMESPACE);
		syncElement.setAttribute("id", sync.getId() == null ? "" : sync.getId());
		syncElement.setAttribute("updates",  String.valueOf(sync.getUpdates()));
		syncElement.setAttribute("deleted",  String.valueOf(sync.isDeleted()));
		syncElement.setAttribute("noConflicts",  String.valueOf(sync.isNoConflicts()));
		
		elementRoot.addContent(0, syncElement);
				
		for (History history : sync.getHistories()) {
			History.generate(syncElement, webContentSyndicationFormat, history);
		}
         
		if(sync.getConflicts().size() > 0){
			Element conflictsElement = new Element("conflicts", NAMESPACE);
			for (ConflictItem conflictItem : sync.getConflicts()) {
				ConflictItem.generate(conflictsElement, webContentSyndicationFormat, conflictItem);
			}
			elementRoot.addContent(conflictsElement);
		}
		
	}
}
