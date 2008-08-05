package org.mesh4j.sync.adapters.feed;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_UPDATES;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ELEMENT_AUTHOR;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ELEMENT_ITEM_TITLE;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ELEMENT_NAME;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_CONFLICTS;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_HISTORY;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_SYNC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


public class FeedWriter {

	// MODEL VARIABLES
	ISyndicationFormat syndicationFormat;
	IIdentityProvider identityProvider;
	
	// BUSINESS METHODS

	public FeedWriter(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.syndicationFormat = syndicationFormat;
		this.identityProvider = identityProvider;
	}
	
	public void write(XMLWriter writer, Feed feed) throws IOException, DocumentException{
        Document document = DocumentHelper.createDocument();
        write(document, feed);        
        write(writer, document);
	}

	public void write(Document document, Feed feed) throws DocumentException {
		Element root = this.addRootElement(document);
		
		if(feed.getPayload() != null){
			this.writePayload(root, feed.getPayload());
		}
		
		for (Item item : feed.getItems()) {
			write(root, item);
		}
	}

	@SuppressWarnings("unchecked")
	public void writePayload(Element root, Element payload) {
		List<Element> payloadElements = payload.elements(); 
		for (Element payloadElement : payloadElements) {
			root.add(payloadElement.createCopy());
		}
	}

	protected Element addRootElement(Document document) {
		return this.syndicationFormat.addRootElement(document);
	}

	public void write(Element root, Item item) 
	{
		Element itemElement = this.addFeedItemElement(root);
		
		this.writeContent(itemElement, item.getContent());
		
		String by = this.getAuthenticatedUser();
		History lastUpdate = item.getLastUpdate();
		if (lastUpdate != null && lastUpdate.getBy() != null){
			by = lastUpdate.getBy();
		}
		
		Element author = itemElement.element(SX_ELEMENT_AUTHOR);
		if(author == null){
			author = itemElement.addElement(SX_ELEMENT_AUTHOR);
			Element name = author.addElement(SX_ELEMENT_NAME);
			name.setText(by);
		} else {
			Element name = author.element(SX_ELEMENT_NAME);
			if(name == null){
				name = author.addElement(SX_ELEMENT_NAME);
				name.setText(by);
			} else if(!name.getTextTrim().equals(by.trim())){
				name.setText(by);
			}
		}
		
		if(item.getSync() != null){
			writeSync(itemElement, item.getSync());
		}
	}	
	
	private void writeContent(Element itemElement, IContent content) {
		Element xmlContent = XMLContent.normalizeContent(content);
		writePayload(itemElement, xmlContent);
		
		if(content instanceof XMLContent){
			XMLContent contextAsXMLContent = (XMLContent) content;
			if(contextAsXMLContent.getTitle() != null && contextAsXMLContent.getTitle().trim().length() > 0){
				String title = itemElement.elementText(SX_ELEMENT_ITEM_TITLE);
				if(title == null){
					Element titleElement = DocumentHelper.createElement(SX_ELEMENT_ITEM_TITLE);
					titleElement.setText(contextAsXMLContent.getTitle());
					itemElement.add(titleElement);
				} else {
					Element titleElement = itemElement.element(SX_ELEMENT_ITEM_TITLE);
					titleElement.setText(contextAsXMLContent.getTitle());
				}
			}else{
				Element titleElement = itemElement.element(SX_ELEMENT_ITEM_TITLE);
				if(titleElement != null){
					itemElement.remove(titleElement);
				}
			}
			
			if(contextAsXMLContent.getDescription() != null && contextAsXMLContent.getDescription().trim().length() > 0){
				String description = itemElement.elementText(SX_ELEMENT_ITEM_DESCRIPTION);
				if(description == null){
					Element descriptionElement = DocumentHelper.createElement(SX_ELEMENT_ITEM_DESCRIPTION);
					descriptionElement.setText(contextAsXMLContent.getDescription());
					itemElement.add(descriptionElement);
				} else {
					Element descriptionElement = itemElement.element(SX_ELEMENT_ITEM_DESCRIPTION);
					descriptionElement.setText(contextAsXMLContent.getDescription());
				}
			}else{
				Element descriptionElement = itemElement.element(SX_ELEMENT_ITEM_DESCRIPTION);
				if(descriptionElement != null){
					itemElement.remove(descriptionElement);
				}
			}
		}
	}

	protected Element addFeedItemElement(Element root) {
		return this.syndicationFormat.addFeedItemElement(root);
	}

	protected String parseDate(Date date) {
		return this.syndicationFormat.formatDate(date);
	}

	public void writeSync(Element rootElement, Sync sync) {		
		// <sx:sync>
		Element syncElement = rootElement.addElement(SX_QNAME_SYNC);
		syncElement.addAttribute(SX_ATTRIBUTE_SYNC_ID, sync.getId());
		syncElement.addAttribute(SX_ATTRIBUTE_SYNC_UPDATES, String.valueOf(sync.getUpdates()));
		syncElement.addAttribute(SX_ATTRIBUTE_SYNC_DELETED, String.valueOf(sync.isDeleted()));
		syncElement.addAttribute(SX_ATTRIBUTE_SYNC_NO_CONFLICTS, String.valueOf(sync.isNoConflicts()));

		ArrayList<History> allUpdatesHistories = new ArrayList<History>(sync.getUpdatesHistory());
		Collections.reverse(allUpdatesHistories);
		
		for(History history : allUpdatesHistories)
		{
			writeHistory(syncElement, history);
		}
		
		if (sync.getConflicts().size() > 0) {
			Element conflictsElement = syncElement.addElement(SX_QNAME_CONFLICTS);
			for (Item item : sync.getConflicts()) {
				write(conflictsElement, item);
			}
		}
	}
		
	public void writeHistory(Element rootElement, History history)
	{
		Element historyElement = rootElement.addElement(SX_QNAME_HISTORY);
		historyElement.addAttribute(SX_ATTRIBUTE_HISTORY_SEQUENCE, String.valueOf(history.getSequence()));
		if (history.getWhen() != null){
			historyElement.addAttribute(SX_ATTRIBUTE_HISTORY_WHEN, this.parseDate(history.getWhen()));
		}
		historyElement.addAttribute(SX_ATTRIBUTE_HISTORY_BY, history.getBy());
	}
	

	protected void write(XMLWriter writer, Document document) throws IOException {
        writer.write( document );
        writer.close();
	}
	
	private String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}
	
}
