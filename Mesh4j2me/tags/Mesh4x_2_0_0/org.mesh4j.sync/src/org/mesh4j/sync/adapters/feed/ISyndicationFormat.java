package org.mesh4j.sync.adapters.feed;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.mesh4j.sync.model.Item;

public interface ISyndicationFormat {

	String getName();
	
	String getContentType();
	
	boolean isFeedTitle(Element element);
	boolean isFeedDescription(Element element);
	boolean isFeedLink(Element element);
	boolean isAditionalFeedPayload(Element element);
	
	boolean isFeedItem(Element element);
	boolean isFeedItemTitle(Element element);
	boolean isFeedItemDescription(Element element);
	boolean isFeedItemLink(Element element);
	boolean isFeedItemAuthor(Element element);
	boolean isFeedItemPayload(Element element);
	boolean isAditionalFeedItemPayload(Element element);
	

	Element getBaseElement(Document document);

	Date parseDate(String dateAsString);
	String formatDate(Date date);

	void writeStartDocument(Writer writer) throws Exception;
	void writeStartItem(Writer writer, Item item) throws Exception;
	void writeEndItem(Writer writer) throws Exception;
	void writeEndDocument(Writer writer) throws Exception;
	
	Element getFeedItemPayloadElement(Element itemElement);

	void addFeedInformation(Writer writer, String title, String description, String link, Date lastUpdate) throws IOException;
	void addAuthorElement(Writer writer, String by) throws IOException;
	void addFeedItemTitleElement(Writer writer, String title) throws IOException;
	void addFeedItemDescriptionElement(Writer writer, String description) throws IOException;
	void addFeedItemLinkElement(Writer writer, String link) throws IOException;
	void addFeedItemPayloadElement(Writer writer, String payload) throws IOException;
	
	String getFeedTitle(Element element);
	String getFeedDescription(Element element);
	String getFeedLink(Element element);
	
	String getFeedItemTitle(Element element);
	String getFeedItemDescription(Element element);
	String getFeedItemLink(Element element);

	// / <summary>
	// / Namespace of the FeedSync elements.
	// / </summary>
	public static final String SX_NAMESPACE = "http://feedsync.org/2007/feedsync";

	// / <summary>
	// / Default prefix used for FeedSync elements.
	// / </summary>
	public static final String SX_PREFIX = "sx";

	public static final String SX_ELEMENT_AUTHOR = "author";
	public static final String SX_ELEMENT_NAME = "name";

	// sx:elements
	public static final String SX_ELEMENT_SYNC = "sx:sync";
	public static final String SX_ELEMENT_SHARING = "sx:sharing";
	public static final String SX_ELEMENT_RELATED = "sx:related";
	public static final String SX_ELEMENT_HISTORY = "sx:history";
	public static final String SX_ELEMENT_CONFLICTS = "sx:conflicts";
	
	// sx:sharing
	public static final String SX_ATTRIBUTE_SHARING_SINCE = "since";
	public static final String SX_ATTRIBUTE_SHARING_UNTIL = "until";
	public static final String SX_ATTRIBUTE_SHARING_VERSION = "version";
	public static final String SX_ATTRIBUTE_SHARING_EXPIRES = "expires";
	// sx:related
	public static final String SX_ATTRIBUTE_RELATED_LINK = "link";
	public static final String SX_ATTRIBUTE_RELATED_TITLE = "title";
	public static final String SX_ATTRIBUTE_RELATED_TYPE = "type";
	// sx:sync
	public static final String SX_ATTRIBUTE_SYNC_ID = "id";
	public static final String SX_ATTRIBUTE_SYNC_UPDATES = "updates";
	public static final String SX_ATTRIBUTE_SYNC_DELETED = "deleted";
	public static final String SX_ATTRIBUTE_SYNC_NO_CONFLICTS = "noconflicts";
	
	// sx:history
	public static final String SX_ATTRIBUTE_HISTORY_SEQUENCE = "sequence";
	public static final String SX_ATTRIBUTE_HISTORY_WHEN = "when";
	public static final String SX_ATTRIBUTE_HISTORY_BY = "by";

	
	public static final String ELEMENT_PAYLOAD = "payload";
}