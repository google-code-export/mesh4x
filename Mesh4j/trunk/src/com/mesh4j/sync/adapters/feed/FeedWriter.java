package com.mesh4j.sync.adapters.feed;

import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_UPDATES;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ELEMENT_AUTHOR;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ELEMENT_NAME;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_CONFLICTS;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_HISTORY;
import static com.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_SYNC;

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

import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

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

		History lastUpdate = item.getLastUpdate();
		if (lastUpdate != null && lastUpdate.getBy() != null)
		{
			Element author = itemElement.addElement(SX_ELEMENT_AUTHOR);
			Element name = author.addElement(SX_ELEMENT_NAME);
			name.setText(lastUpdate.getBy());
		}
		else
		{
			Element author = itemElement.addElement(SX_ELEMENT_AUTHOR);
			Element name = author.addElement(SX_ELEMENT_NAME);
			name.setText(this.getAuthenticatedUser());
		}
		
		if(item.getSync() != null){
			writeSync(itemElement, item.getSync());
		}
	}	
	
	private void writeContent(Element itemElement, IContent content) {
		Element xmlContent = XMLContent.normalizeContent(content);
		writePayload(itemElement, xmlContent);
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
