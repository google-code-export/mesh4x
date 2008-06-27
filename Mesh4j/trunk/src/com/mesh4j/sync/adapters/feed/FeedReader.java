package com.mesh4j.sync.adapters.feed;

import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_ITEM_DESCRIPTION;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_ITEM_TITLE;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_CONFLICTS;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_HISTORY;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_SYNC;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class FeedReader {
	
	// MODEL VARIABLES
	ISyndicationFormat syndicationFormat;
	IIdentityProvider identityProvider;
	
	// BUSINESS METHODS

	public FeedReader(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.syndicationFormat = syndicationFormat;
		this.identityProvider = identityProvider;
	}
	
	public Feed read(URL url) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(url);
		return read(document);
	}
	
	public Feed read(Reader reader) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(reader);
		return read(document);
	}
	
	public Feed read(InputStream inputStream) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(inputStream);
		return read(document);
	}

	public Feed read(InputSource inputSource) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(inputSource);
		return read(document);
	}
		
	public Feed read(File file) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		return read(document);
	}

	public Feed read(Document document) {
		Feed feed = new Feed();
		Element payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		
		Element root = document.getRootElement();
		List<Element> elements = getRootElements(root);
		for (Element element : elements) {
			if(isFeedItem(element)){
				Item item = readItem(element);
				feed.addItem(item);
			} else {
				payload.add(element.detach());
			}
		}

		feed.setPayload(payload);
		return feed;
	}

	@SuppressWarnings("unchecked")
	public Item readItem(Element itemElement) {
		Element payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		
		Sync sync = null;
		
		List<Element> elements = itemElement.elements();
		for (Element element : elements) {
			if(SX_QNAME_SYNC.getName().equals(element.getName())){
				sync = readSync(element);
			} else {
				payload.add(element.detach());
			}
		}
		
		if(sync == null){
			sync = new Sync(makeNewSyncID(), this.getAuthenticatedUser(), new Date(), false); 
		}
		
		if(sync.isDeleted()){
			return new Item(new NullContent(sync.getId()), sync);
		} else {
			String title = itemElement.elementText(SX_ATTRIBUTE_ITEM_TITLE);
			String description = itemElement.elementText(SX_ATTRIBUTE_ITEM_DESCRIPTION);
			XMLContent modelItem = new XMLContent(sync.getId(), title, description, payload);
			return new Item(modelItem, sync);
		}
	}

	@SuppressWarnings("unchecked")
	public Sync readSync(Element syncElement) {
		String syncID = syncElement.attributeValue(SX_ATTRIBUTE_SYNC_ID);
		//int updates = Integer.valueOf(syncElement.attributeValue(SX_ATTRIBUTE_SYNC_UPDATES));
		boolean deleted = Boolean.parseBoolean(syncElement.attributeValue(SX_ATTRIBUTE_SYNC_DELETED));
		boolean noConflicts = Boolean.parseBoolean(syncElement.attributeValue(SX_ATTRIBUTE_SYNC_NO_CONFLICTS));
		//syncElement.asXML()
		
		Sync sync = new Sync(syncID);
		sync.setDeleted(deleted);
		if(noConflicts){
			sync.markWithoutConflicts();
		} else {
			sync.markWithConflicts();
		}
		
		List<Element> elements = syncElement.elements();
		ArrayList<Element> historyElements = new ArrayList<Element>();
		for (Element historyElement : elements) {
			if(SX_QNAME_HISTORY.getName().equals(historyElement.getName())){
				historyElements.add(historyElement);
			} 
		}

		Collections.reverse(historyElements);
		for (Element historyElement : historyElements) {
			int sequence = Integer.valueOf(historyElement.attributeValue(SX_ATTRIBUTE_HISTORY_SEQUENCE));
			Date when = this.parseDate(historyElement.attributeValue(SX_ATTRIBUTE_HISTORY_WHEN));
			String by = historyElement.attributeValue(SX_ATTRIBUTE_HISTORY_BY);
			sync.update(by, when, sequence);
		}
		
		
		
		Element conflicts = syncElement.element(SX_QNAME_CONFLICTS);
		if(conflicts != null){
			List<Element> conflicItems = conflicts.elements();
			for (Element itemElement : conflicItems) {
				Item item = readItem(itemElement);
				sync.addConflict(item);
			}
		}
		return sync;
	}
	
	protected boolean isFeedItem(Element element){
		return this.syndicationFormat.isFeedItem(element);
	}
	protected List<Element> getRootElements(Element root){
		return this.syndicationFormat.getRootElements(root);
	}
	
	protected Date parseDate(String dateAsString){
		return this.syndicationFormat.parseDate(dateAsString);
	}
	
	private String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

	protected String makeNewSyncID() {
		return IdGenerator.newID();
	}
}