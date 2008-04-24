package com.sun.syndication.feed.module.feedsync.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.WebContentSyndicationFormat;

public class SharingModule extends FeedSyncModule {

	// CONSTANTS
	private static final long serialVersionUID = -2219000881894049089L;

	// MODEL VARIABLES
	private String since;
	private String until;
	private Date expires;
	private List<Related> relatedElements = new ArrayList<Related>();

	// BUSINESS METHODS
	public String getSince() {
		return since;
	}

	private void setSince(String since) {
		this.since = since;
	}

	public String getUntil() {
		return until;
	}

	private void setUntil(String until) {
		this.until = until;
	}

	public Date getExpires() {
		return expires;
	}

	private void setExpires(Date expires) {
		this.expires = expires;
	}

	public List<Related> getRelatedElements() {
		return this.relatedElements;
	}

	public void addRelatedElement(Related related) {
		this.relatedElements.add(related);
	}

	// FeedSyncModule API
	@Override
	public void copyFrom(Object obj) {
		SharingModule sharing = (SharingModule) obj;
		this.setSince(sharing.getSince());
		this.setUntil(sharing.getUntil());
		this.setExpires(sharing.getExpires());
		this.relatedElements.addAll(sharing.getRelatedElements());
	}

	public static SharingModule parse(Element element, WebContentSyndicationFormat webContentSyndicationFormat) {
		SharingModule sharing = null;
		Element sharingChild = element.getChild("sharing", NAMESPACE);
		if (sharingChild != null) {
			Attribute sinceAttribute = sharingChild.getAttribute("since");
			Attribute untilAttribute = sharingChild.getAttribute("until");
			Attribute expiresAttribute = sharingChild.getAttribute("expires");
			
			sharing = new SharingModule();
			sharing.setSince(sinceAttribute == null ? null : sinceAttribute.getValue().trim());
			sharing.setUntil(untilAttribute == null ? null : untilAttribute.getValue().trim());
			sharing.setExpires(expiresAttribute == null ? null : webContentSyndicationFormat.parseDateTime(expiresAttribute.getValue().trim()));
			
			Related.parse(sharingChild, webContentSyndicationFormat, sharing);
		}
		return sharing;
	}

	public static void generate(Element elementRoot, WebContentSyndicationFormat webContentSyndicationFormat, SharingModule sharing) {
		elementRoot.addNamespaceDeclaration(NAMESPACE);
		Element sharingElement = new Element("sharing", NAMESPACE);
		sharingElement.setAttribute("since", sharing.getSince() == null ? "" : sharing.getSince());
		sharingElement.setAttribute("until",  sharing.getUntil() == null ? "" : sharing.getUntil());
		sharingElement.setAttribute("expires",  webContentSyndicationFormat.formatDateTime(sharing.getExpires()));
		elementRoot.addContent(0, sharingElement);
		
		
		for (Related related : sharing.getRelatedElements()) {
			Related.generate(sharingElement, webContentSyndicationFormat, related);
		}
	}

}
