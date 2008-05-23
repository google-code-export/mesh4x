package com.mesh4j.sync.adapters.kml;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.SyncInfo;

public interface IKMLMeshDocument {

	// ELEMENTS
	List<Element> getElementsToSync();
	Element getElement(String id);

	void addElement(Element createCopy, SyncInfo syncInfo);
	void updateElement(Element createCopy, SyncInfo syncInfo);
	void removeElement(String id);
	
	Element normalize(Element element);

	// SYNC
	List<SyncInfo> getAllSyncs();
	SyncInfo getSync(String syncId);
	void refreshSync(SyncInfo syncInfo);

	void updateMeshStatus();
	
	// DOM
	Document toDocument();
	String asXML();
	void normalize();
	String getType();
	
	// ELEMENT ATTRIBUTES
	String getMeshSyncId(Element element);
	String getMeshParentId(Element element);
}
