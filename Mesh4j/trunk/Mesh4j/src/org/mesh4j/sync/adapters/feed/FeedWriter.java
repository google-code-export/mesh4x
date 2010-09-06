package org.mesh4j.sync.adapters.feed;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_UPDATES;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_CONFLICTS;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_HISTORY;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_SYNC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


public class FeedWriter {

	// MODEL VARIABLES
	private ISyndicationFormat syndicationFormat;
	private IIdentityProvider identityProvider;
	private IContentWriter contentWriter;
		
	// BUSINESS METHODS

	public FeedWriter(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, IContentWriter contentWriter){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(contentWriter, "contentWriter");
				
		this.syndicationFormat = syndicationFormat;
		this.identityProvider = identityProvider;
		this.contentWriter = contentWriter;
	}
	
	public void write(XMLWriter writer, Feed feed) throws IOException {
        Document document = DocumentHelper.createDocument();
        write(document, feed);
        write(writer, document);
	}

	public void write(Document document, Feed feed) {
		Element root = this.addRootElement(document);
		addAdditionalNS(root);
		this.syndicationFormat.addFeedInformation(root, feed.getTitle(), feed.getDescription(), feed.getLink(), feed.getLastUpdate());
		
		if(feed.getPayload() != null){
			this.writePayload(root, feed.getPayload());
		}
		
		for (Item item : feed.getItems()) {
			write(document.getRootElement(), root, item);
		}
	}
	
	public String writeAsXml(Feed feed) throws Exception {
		Document document = DocumentHelper.createDocument();
		this.write(document, feed);
		String xml = document.asXML();
		return xml;
	}
	
	public String writeAsXml(Item item) throws Exception {
		Element root = DocumentHelper.createElement("items");
		this.write(root, root, item);
		String xml = ((Element)root.elements().get(0)).asXML();
		return xml;
	}

	private void addAdditionalNS(Element root){
		if(this.contentWriter instanceof  INamespace){
			INamespace iNamespace = (INamespace)this.contentWriter;
			for(Namespace namespace : iNamespace.getNamespaceList()){
				root.add(namespace);
			}
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
		return this.syndicationFormat.addRootElement(document, this.contentWriter.encapsulateContentInCDATA());
	}

	public void write(Element nsElement, Element root, Item item) {
		Element itemElement = this.addFeedItemElement(root, item);
		
		this.contentWriter.writeContent(this.syndicationFormat, nsElement, itemElement, item);
		
		String by = this.getAuthenticatedUser();
		History lastUpdate = item.getLastUpdate();
		if (lastUpdate != null && lastUpdate.getBy() != null){
			by = lastUpdate.getBy();
		}
		
		this.syndicationFormat.addAuthorElement(itemElement, by);
		
		if(item.getSync() != null && this.contentWriter.mustWriteSync(item)){
			writeSync(nsElement, itemElement, item.getSync());
		}
	}	
	
	protected Element addFeedItemElement(Element root, Item item) {
		return this.syndicationFormat.addFeedItemElement(root, item);
	}

	protected String parseDate(Date date) {
		return this.syndicationFormat.formatDate(date);
	}

	public void writeSync(Element rootElement, Sync sync) {
		writeSync(rootElement, rootElement, sync);
	}
	public void writeSync(Element nsElement, Element rootElement, Sync sync) {
		
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
				write(nsElement, conflictsElement, item);
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
	
	public static void writeHistory(Element rootElement, String sequence, String when, String by){
		Element historyElement = rootElement.addElement(SX_QNAME_HISTORY);
		historyElement.addAttribute(SX_ATTRIBUTE_HISTORY_SEQUENCE, sequence);
		if (when != null){
			historyElement.addAttribute(SX_ATTRIBUTE_HISTORY_WHEN, when);
		}
		historyElement.addAttribute(SX_ATTRIBUTE_HISTORY_BY, by);
	}
	

	protected void write(XMLWriter writer, Document document) throws IOException {
        writer.write( document );
        writer.close();
	}
	
	private String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}
}
