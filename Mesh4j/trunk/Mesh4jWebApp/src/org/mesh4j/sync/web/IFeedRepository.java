package org.mesh4j.sync.web;

import java.util.Date;
import java.util.List;

import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMappingResolver;
import org.mesh4j.sync.payload.schema.ISchemaResolver;

public interface IFeedRepository {

	boolean existsFeed(String sourceID);

	String readFeed(String sourceID, String link, Date sinceDate, ISyndicationFormat syndicationFormat, boolean plainMode);

	boolean isAddNewFeedAction(String sourceID);

	void addNewFeed(String newSourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by);

	ISyndicationFormat getSyndicationFormat(String format);

	String synchronize(String sourceID, String link, String feedXml, ISyndicationFormat syndicationFormat);

	List<Item> getAll(String sourceID, Date sinceDate);
	
	ISchemaResolver getSchema(String sourceID, String link) throws Exception;
	
	IMappingResolver getMappings(String sourceID, String link, IGeoCoder geoCoder) throws Exception;

	void cleanFeed(String sourceID);

	void updateFeed(String sourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by);

//	void removeFeed(String sourceID, String link, String by);


}