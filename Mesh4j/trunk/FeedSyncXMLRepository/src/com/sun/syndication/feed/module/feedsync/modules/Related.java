package com.sun.syndication.feed.module.feedsync.modules;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.WebContentSyndicationFormat;

public class Related {

	// CONSTANTS
	private static final long serialVersionUID = 3137249170954893583L;

	// MODEL VARIABLES
	private String link;
	private String title;
	private RelatedType relatedType;

	// BUSINESS METHODS

	private String getLink() {
		return link;
	}

	private void setLink(String link) {
		this.link = link;
	}

	private String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	private RelatedType getRelatedType() {
		return relatedType;
	}

	private void setRelatedType(RelatedType relatedType) {
		this.relatedType = relatedType;
	}

	public static void parse(Element root,
			WebContentSyndicationFormat webContentSyndicationFormat,
			SharingModule sharing) {
		{
			List<Element> relatedElements = root.getChildren("related", FeedSyncModule.NAMESPACE);
			for (Element element : relatedElements) {
				Attribute linkAttribute = element.getAttribute("link");
				Attribute titleAttribute = element.getAttribute("title");
				Attribute typeAttribute = element.getAttribute("type");
				
				Related related = new Related();
				related.setLink(linkAttribute == null ? "" : linkAttribute.getValue().trim());
				related.setTitle(titleAttribute == null ? "" : titleAttribute.getValue().trim());
				related.setRelatedType(typeAttribute == null ? null : RelatedType.valueOf(typeAttribute.getValue().trim()));
				sharing.addRelatedElement(related);
			}
		}
	}
	
	public static void generate(Element elementRoot, WebContentSyndicationFormat webContentSyndicationFormat, Related related) {
		Element element = new Element("related", FeedSyncModule.NAMESPACE);
		element.setAttribute("link", related.getLink() == null ? "" : related.getLink());
		element.setAttribute("title",  related.getTitle() == null ? "" : related.getTitle());
		element.setAttribute("type",  related.getRelatedType() == null ? "" : related.getRelatedType().name());
		elementRoot.addContent(element);
	}
}
