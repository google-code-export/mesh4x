package org.mesh4j.sync.web;

import java.util.Date;
import java.util.List;

import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.servlet.Format;

public interface IFeedRepository {

	String readFeed(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate) throws Exception;

	String synchronize(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, String feedXml) throws Exception;

	List<Item> getAll(String sourceID, String link, Date sinceDate);
	
	ISchema getSchema(String sourceID, String link) throws Exception;
	
	IMapping getMappings(String sourceID, String link, IGeoCoder geoCoder) throws Exception;

	boolean isAddNewFeedAction(String sourceID);

	boolean existsFeed(String sourceID);

	void addNewFeed(String newSourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by);
	
	void updateFeed(String sourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by);

	void deleteFeed(String sourceID, String link, String by);

	void cleanFeed(String sourceID);

	void addNewItemFromRawContent(String sourceID, String link, String rawXml, String by);

	String getHistory(String sourceID, String link, ISyndicationFormat syndicationFormat, String syncId);
}