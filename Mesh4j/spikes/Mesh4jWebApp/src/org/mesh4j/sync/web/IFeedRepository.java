package org.mesh4j.sync.web;

import java.util.Date;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;

public interface IFeedRepository {

	boolean existsFeed(String sourceID);

	String readFeed(String sourceID, String link, Date sinceDate, ISyndicationFormat syndicationFormat);

	boolean isAddNewFeedAction(String sourceID);

	void addNewFeed(String newSourceID, ISyndicationFormat syndicationFormat, String link, String description);

	ISyndicationFormat getSyndicationFormat(String format);

	String synchronize(String sourceID, String link, String feedXml, ISyndicationFormat syndicationFormat);
}