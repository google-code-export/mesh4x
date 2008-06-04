package com.mesh4j.sync.adapters.dom;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.security.IIdentityProvider;

public interface IMeshDOM {

	// ELEMENTS
	List<Element> getAllElements();
	Element getElement(String id);
	Element addElement(Element element);
	Element updateElement(Element element);
	void deleteElement(String id);	
	Element normalize(Element element);
	
	// SYNC
	List<SyncInfo> getAllSyncs();
	SyncInfo getSync(String syncId);
	void updateSync(SyncInfo syncInfo);

	void updateMeshStatus();
	
	IContent createContent(Element element, String syncID);
	IContent normalizeContent(IContent content);

	IIdentityProvider getIdentityProvider();
	String newID();
	
	// DOM
	Document toDocument();
	String asXML();
	void normalize();
	String getType();
	
	// ELEMENT ATTRIBUTES
	String getMeshSyncId(Element element);
	boolean isValid(Element element);
	Element getContentRepository(Document document);
	Element getSyncRepository(Document document);

	
}
