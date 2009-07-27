package org.mesh4j.sync.adapters.feed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class FeedReader {
	
	// MODEL VARIABLES
	ISyndicationFormat syndicationFormat;
	IIdentityProvider identityProvider;
	IIdGenerator idGenerator;
	
	// BUSINESS METHODS

	public FeedReader(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		
		this.syndicationFormat = syndicationFormat;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
	}
	
	public Feed read(Reader reader) throws Exception{
		XmlPullParser xmlPullParser = new KXmlParser();
		xmlPullParser.setInput(reader);
		
		Document document = new Document();
		document.parse(xmlPullParser);
		return read(document);
	}

	public Feed read(Document document) throws Exception {
		
		StringBuffer payload = new StringBuffer();
		
		String title = "";
		String description = "";
		String link = "";
		Vector<Item> items = new Vector<Item>();

		Element baseElement = this.syndicationFormat.getBaseElement(document);
		if(baseElement != null){
			int elementCount = baseElement.getChildCount();
			Element element;
			for (int i = 0; i < elementCount; i++) {
				element = baseElement.getElement(i);
				if(element != null){
					if(this.syndicationFormat.isFeedItem(element)){
						Item item = readItem(element);
						items.addElement(item);
					}else if(this.syndicationFormat.isFeedTitle(element)){
						title = this.syndicationFormat.getFeedTitle(element);
					} else if(this.syndicationFormat.isFeedDescription(element)){
						description = this.syndicationFormat.getFeedDescription(element);
					}else if(this.syndicationFormat.isFeedLink(element)){
						link = this.syndicationFormat.getFeedLink(element);
					} else if(this.syndicationFormat.isAditionalFeedPayload(element)){
						payload.append(this.elementToXML(element));
					}	
				}
			}
		}
		
		Feed feed = new Feed(title, description, link, items);
		feed.setPayload(payload.toString());
		return feed;
	}

	public Item readItem(Element itemElement) throws Exception {
		StringBuffer payload = new StringBuffer();
		boolean multipayload = false;
		
		Sync sync = null;
		String title = "";
		String description = "";
		String link = "";
		
		int size = itemElement.getChildCount();
		Element element;
		for (int i = 0; i < size; i++) {
			element = itemElement.getElement(i);
			if(element != null){
				if(ISyndicationFormat.SX_ELEMENT_SYNC.equals(element.getName())){
					sync = readSync(element);
				} else if(this.syndicationFormat.isFeedItemTitle(element)){
					title = this.syndicationFormat.getFeedItemTitle(element);
				} else if(this.syndicationFormat.isFeedItemDescription(element)){
					description = this.syndicationFormat.getFeedItemDescription(element);
				} else if(this.syndicationFormat.isFeedItemLink(element)){
					link = this.syndicationFormat.getFeedItemLink(element);
				} else if(this.syndicationFormat.isAditionalFeedItemPayload(element)){
					payload.append(this.elementToXML(element));
					multipayload = true;
				}
			}
		}
		
		Element contentElement = this.syndicationFormat.getFeedItemPayloadElement(itemElement);
		if(contentElement != null){
			if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(contentElement.getName())){
				multipayload = true;
				int sizeContentElementChildren = contentElement.getChildCount();
				Element elementContentElementChild;
				for (int i = 0; i < sizeContentElementChildren; i++) {
					elementContentElementChild = contentElement.getElement(i);
					if(elementContentElementChild != null){
						payload.append(this.elementToXML(elementContentElementChild));
					}
				}
			} else {
				payload.append(this.elementToXML(contentElement));
			}
		}
		
		String payloadAsString = "";
		if(multipayload){
			StringBuffer sb = new StringBuffer();
			sb.append("<");
			sb.append(ISyndicationFormat.ELEMENT_PAYLOAD);
			sb.append(">");
			sb.append(payload.toString());
			sb.append("</");
			sb.append(ISyndicationFormat.ELEMENT_PAYLOAD);
			sb.append(">");
			payloadAsString = sb.toString();
		} else {
			payloadAsString = payload.toString();
		}
		
		if(sync == null){
			sync = new Sync(this.idGenerator.newID(), this.identityProvider.getAuthenticatedUser(), new Date(), false); 
		}
		
		if(sync.isDeleted()){
			return new Item(new NullContent(sync.getId()), sync);
		} else {
			if(title == null){
				title = sync.getId();
			}
			
			if(description == null){
				description = sync.getId();
			}
			
			if(link == null){
				link = "";
			}
			
			XMLContent modelItem = new XMLContent(sync.getId(), title, description, link, payloadAsString);
			return new Item(modelItem, sync);
		}
	}

	private String elementToXML(Element payload) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		XmlSerializer xmlSerializer = new KXmlSerializer();
		xmlSerializer.setOutput(writer);
		payload.write(xmlSerializer);
		writer.flush();
		baos.flush();
		writer.close();
		baos.close();
		return new String(baos.toByteArray());
	}

	public Sync readSync(Element syncElement) throws Exception {
		String syncID = syncElement.getAttributeValue(null, ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID);
		
		String attrDeleted = syncElement.getAttributeValue(null, ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED);
		boolean deleted = attrDeleted != null && attrDeleted.equals("true") ? true : false;
		
		String attrNoConflicts = syncElement.getAttributeValue(null, ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS);
		boolean noConflicts = attrNoConflicts != null && attrNoConflicts.equals("true") ? true : false;
		
		Sync sync = new Sync(syncID);
		sync.setDeleted(deleted);
		if(noConflicts){
			sync.markWithoutConflicts();
		} else {
			sync.markWithConflicts();
		}
		
		int size = syncElement.getChildCount();
		Vector<Element> historyElements = new Vector<Element>();
		Element syncItemElement = null;
		for (int i = 0; i < size; i++) {
			syncItemElement = syncElement.getElement(i);
			if(syncItemElement != null){
				if(ISyndicationFormat.SX_ELEMENT_HISTORY.equals(syncItemElement.getName())){
					historyElements.addElement(syncItemElement);
				} else if(ISyndicationFormat.SX_ELEMENT_CONFLICTS.equals(syncItemElement.getName())){
					int conflictsCount = syncItemElement.getChildCount();
					Item item = null;
					for (int j = 0; j < conflictsCount; j++) {
						item = readItem(syncItemElement.getElement(j));
						sync.addConflict(item);
					}
				}
			}
		}

		size = historyElements.size();
		Element historyElement = null;
		for (int i = 0; i < size; i++) {
			historyElement = (Element)historyElements.elementAt(size - i -1);
			int sequence = Integer.valueOf(historyElement.getAttributeValue(null, ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE));
			Date when = this.syndicationFormat.parseDate(historyElement.getAttributeValue(null, ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN));
			String by = historyElement.getAttributeValue(null, ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY);
			sync.update(by, when, sequence);
		}
		sync.setUpdatesWithLastUpdateSequence();
		
		return sync;
	}

	public Sync readSync(String syncAsXml) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		
		try{
			this.syndicationFormat.writeStartDocument(writer);
			writer.write(syncAsXml);
			this.syndicationFormat.writeEndDocument(writer);
			writer.flush();
			baos.flush();
		}finally{
			baos.close();
			writer.close();
		}
				
		ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
		InputStreamReader reader = new InputStreamReader(is);
		Sync sync = null;
		try{
			XmlPullParser xmlPullParser = new KXmlParser();
			xmlPullParser.setInput(reader);
			
			Document document = new Document();
			document.parse(xmlPullParser);
			
			Element root = this.syndicationFormat.getBaseElement(document);
			sync = this.readSync(root.getElement(null, ISyndicationFormat.SX_ELEMENT_SYNC));
		} finally{
			is.close();
			reader.close();
		}		
		return sync;
	}

	public Feed read(byte[] data) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		Reader reader = new InputStreamReader(bais);
		try{
			return read(reader);
		} finally{
			reader.close();
			bais.close();
		}
	}
}