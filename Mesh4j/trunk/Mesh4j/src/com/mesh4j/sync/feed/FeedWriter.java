package com.mesh4j.sync.feed;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.Security;

public class FeedWriter {

	// MODEL VARIABLES
	SyndicationFormat syndicationFormat;
	
	// BUSINESS METHODS

	public FeedWriter(SyndicationFormat syndicationFormat){
		super();
		this.syndicationFormat = syndicationFormat;
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
	public void writePayload(Element root, Element payload) throws DocumentException {
		List<Element> payloadElements = payload.elements(); 
		for (Element payloadElement : payloadElements) {
			root.add(payloadElement.detach());
		}
	}

	protected Element addRootElement(Document document) {
		return this.syndicationFormat.addRootElement(document);
	}

	public void write(Element root, Item item) throws DocumentException
	{
		Element itemElement = this.addFeedItemElement(root);
		
		this.writeContent(itemElement, item.getContent());

		History lastUpdate = item.getLastUpdate();
		if (lastUpdate != null && lastUpdate.getBy() != null)
		{
			String by = lastUpdate.getBy();
			itemElement.addElement("author", by);
		}
		else
		{
			itemElement.addElement("author", Security.getAuthenticatedUser());
		}
		
		if(item.getSync() != null){
			writeSync(itemElement, item.getSync());
		}
	}	
	
	private void writeContent(Element itemElement, Content content) throws DocumentException {
		Element xmlContent = ItemXMLContent.normalizeContent(content);
		writePayload(itemElement, xmlContent);
	}

	protected Element addFeedItemElement(Element root) {
		return this.syndicationFormat.addFeedItemElement(root);
	}

	protected String parseDate(Date date) {
		return this.syndicationFormat.formatDate(date);
	}

	public void writeSync(Element rootElement, Sync sync) throws DocumentException
	{		
		// <sx:sync>
		Element syncElement = rootElement.addElement("sx:sync");
		syncElement.addAttribute("id", sync.getId());
		syncElement.addAttribute("updates", String.valueOf(sync.getUpdates()));
		syncElement.addAttribute("deleted", String.valueOf(sync.isDeleted()));
		syncElement.addAttribute("noConflicts", String.valueOf(sync.isNoConflicts()));

		for(History history : sync.getUpdatesHistory())
		{
			writeHistory(syncElement, history);
		}
		
		if (sync.getConflicts().size() > 0) {
			Element conflictsElement = syncElement.addElement("sx:conflicts");
			for (Item item : sync.getConflicts()) {
				write(conflictsElement, item);
			}
		}
	}
		
	public void writeHistory(Element rootElement, History history)
	{
		Element historyElement = rootElement.addElement("sx:history");
		historyElement.addAttribute("sequence", String.valueOf(history.getSequence()));
		if (history.getWhen() != null){
			historyElement.addAttribute("when", this.parseDate(history.getWhen()));
		}
		historyElement.addAttribute("by", history.getBy());
	}
	

	protected void write(XMLWriter writer, Document document) throws IOException {
        writer.write( document );
        writer.close();
	}
	
}
