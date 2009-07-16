package org.mesh4j.sync.adapters.feed;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_SEQUENCE;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_DELETED;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_ATTRIBUTE_SYNC_NO_CONFLICTS;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_CONFLICTS;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_HISTORY;
import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.SX_QNAME_SYNC;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.xml.sax.InputSource;


public class FeedReader {
	
	// MODEL VARIABLES
	private ISyndicationFormat syndicationFormat;
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	private IContentReader contentReader;
	
	// BUSINESS METHODS

	public FeedReader(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, IIdGenerator idGenerator, IContentReader contentReader){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNull(contentReader, "contentReader");
		
		this.syndicationFormat = syndicationFormat;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator; 
		this.contentReader = contentReader;
	}
	
	public Feed read(URL url) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(url);
		return read(document);
	}
	
	public Feed read(Reader reader) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(reader);
		return read(document);
	}
	
	public Feed read(InputStream inputStream) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(inputStream);
		return read(document);
	}

	public Feed read(InputSource inputSource) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(inputSource);
		return read(document);
	}
		
	public Feed read(File file) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		return read(document);
	}

	public Feed read(Document document) throws Exception {
		
		Element payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		
		String title = "";
		String description = "";
		String link = "";
		ArrayList<Item> items = new ArrayList<Item>();
		
		Element root = document.getRootElement();
		List<Element> elements = getRootElements(root);
		for (Element element : elements) {
			if(isFeedItem(element)){
				Item item = readItem(element);
				items.add(item);
			}else if(this.syndicationFormat.isFeedTitle(element)){
				title = this.syndicationFormat.getFeedTitle(element);
			} else if(this.syndicationFormat.isFeedDescription(element)){
				description = this.syndicationFormat.getFeedDescription(element);
			}else if(this.syndicationFormat.isFeedLink(element)){
				link = this.syndicationFormat.getFeedLink(element);
			} else if(this.syndicationFormat.isAditionalFeedPayload(element)){
				payload.add(element.detach());
			}				
		}

		Feed feed = new Feed(title, description, link);
		feed.addItems(items);
		feed.setPayload(payload);
		return feed;
	}

	@SuppressWarnings("unchecked")
	public Item readItem(Element itemElement) {
		Element payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		
		Sync sync = null;
		String title = null;
		String description = null;
		String link = null;
		
		List<Element> elements = itemElement.elements();
		for (Element element : elements) {
			if(SX_QNAME_SYNC.getName().equals(element.getName())){
				sync = readSync(element);
			} else {
				if(this.syndicationFormat.isFeedItemTitle(element)){
					title = this.syndicationFormat.getFeedItemTitle(element);
				} else if(this.syndicationFormat.isFeedItemDescription(element)){
					description = this.syndicationFormat.getFeedItemDescription(element);
				}else if(this.syndicationFormat.isFeedItemLink(element)){
					link = this.syndicationFormat.getFeedItemLink(element);
				} else if(this.syndicationFormat.isAditionalFeedItemPayload(element)){
					payload.add(element.detach());
				}				
			}
		}
		
		if(sync == null){
			sync = new Sync(makeNewSyncID(), this.getAuthenticatedUser(), new Date(), false); 
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
			
			Element contentElement = this.syndicationFormat.getFeedItemPayloadElement(itemElement);
			if(contentElement != null){
				this.contentReader.readContent(sync.getId(), payload, contentElement);
			}
			
			if(payload.elements().size() == 1){
				payload = (Element)payload.elements().get(0);
			}
			XMLContent modelItem = new XMLContent(sync.getId(), title, description, link, payload);
			return new Item(modelItem, sync);
		}
	}

	@SuppressWarnings("unchecked")
	public Sync readSync(Element syncElement) {
		
		String syncID = syncElement.attributeValue(SX_ATTRIBUTE_SYNC_ID);
		//int updates = Integer.valueOf(syncElement.attributeValue(SX_ATTRIBUTE_SYNC_UPDATES));
		boolean deleted = Boolean.parseBoolean(syncElement.attributeValue(SX_ATTRIBUTE_SYNC_DELETED));
		boolean noConflicts = Boolean.parseBoolean(syncElement.attributeValue(SX_ATTRIBUTE_SYNC_NO_CONFLICTS));
		
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
		sync.setUpdatesWithLastUpdateSequence();
		
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
		return this.idGenerator.newID();
	}

	public Feed read(String xml) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		return read(document);
	}

	public IIdGenerator getIdGenereator() {
		return this.idGenerator;
	}
}