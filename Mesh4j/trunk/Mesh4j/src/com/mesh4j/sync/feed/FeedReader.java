package com.mesh4j.sync.feed;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.IdGenerator;

public class FeedReader {
	
	// MODEL VARIABLES
	SyndicationFormat syndicationFormat;
	
	// BUSINESS METHODS

	// TODO (JMT) delete harcodes and create enum for namespaces, elements and atributes 
	public FeedReader(SyndicationFormat syndicationFormat){
		super();
		this.syndicationFormat = syndicationFormat;
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
		Element payload = DocumentHelper.createElement("payload");
		
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
		Element payload = DocumentHelper.createElement("payload");
		
		Sync sync = null;
		
		List<Element> elements = itemElement.elements();
		for (Element element : elements) {
			if("sync".equals(element.getName())){
				sync = readSync(element);
		} else {
				payload.add(element.detach());
			}
		}
		
		if(sync == null){
			sync = new Sync(makeNewSyncID()); 
		}
		
		String title = itemElement.elementText("title");
		String description = itemElement.elementText("description");
		ItemXMLContent modelItem = new ItemXMLContent(sync.getId(), title, description, payload);
		return new Item(modelItem, sync);
	}

	protected String makeNewSyncID() {
		return IdGenerator.newID();
	}

	@SuppressWarnings("unchecked")
	public Sync readSync(Element syncElement) {
		String syncID = syncElement.attributeValue("id");
		int updates = Integer.valueOf(syncElement.attributeValue("updates"));
		
		Sync sync = new Sync(syncID, updates);
		
		List<Element> elements = syncElement.elements();
		for (Element element : elements) {
			if("history".equals(element.getName())){
				History history = readHistory(element);
				sync.addHistory(history);
			} 
		}
		return sync;
	}

	public History readHistory(Element historyElement) {
		int sequence = Integer.valueOf(historyElement.attributeValue("sequence"));
		Date when = this.parseDate(historyElement.attributeValue("when"));
		String by = historyElement.attributeValue("by");
		
		History history =  new History(by, when, sequence);
		return history;
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
}