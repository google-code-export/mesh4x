package org.mesh4j.sync.web;

import java.io.StringReader;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.IContentReader;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.filter.CompoundFilter;
import org.mesh4j.sync.filter.FilterQuery;
import org.mesh4j.sync.filter.NonDeletedFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.filter.XMLContentLinkFilter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.payload.schema.SchemaInstanceContentReadWriter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchemaInstanceContentReadWriter;
import org.mesh4j.sync.payload.schema.xform.XFormRDFSchemaContentWriter;
import org.mesh4j.sync.payload.schema.xform.XFormRDFSchemaInstanceContentReadWriter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.IdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.servlet.Format;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

public abstract class AbstractFeedRepository implements IFeedRepository{

	// BUSINESS METHODS
	public AbstractFeedRepository(){
		super();
	}
	
	protected abstract ISyncAdapter getSyncMeshGroupAdapter(String sourceID, IIdentityProvider identityProvider, ISyndicationFormat syndicationFormat, Format contentFormat);
	protected abstract ISyncAdapter getSyncAdapter(String sourceID, IIdentityProvider identityProvider);
	protected abstract ISyncAdapter getParentSyncAdapter(String sourceID, IIdentityProvider identityProvider);
	protected abstract void addNewFeed(String sourceID, Feed feed, ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider);

	@Override
	public List<Item> getAll(String sourceID, String link, Date sinceDate, String filterQuery, ISchema schema, IMapping mapping) {
		ISyncAdapter adapter = getSyncAdapter(sourceID, NullIdentityProvider.INSTANCE);
		
		IFilter<Item> filter = new FilterQuery(filterQuery, schema);
		if(filter == null){
			return adapter.getAllSince(sinceDate);
		} else {
			return adapter.getAllSince(sinceDate, filter);
		}
	}
	
	@Override
	public String readFeedGroup(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate, String filterQuery) throws Exception {
		ISyncAdapter adapter = getSyncMeshGroupAdapter(sourceID, NullIdentityProvider.INSTANCE, syndicationFormat, contentFormat);
		return readFeed(adapter, sourceID, link, syndicationFormat, contentFormat, geoCoder, sinceDate, filterQuery);
	}
	

	@Override
	public String readFeed(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate, String filterQuery) throws Exception {
		ISyncAdapter adapter = getSyncAdapter(sourceID, NullIdentityProvider.INSTANCE);
		return readFeed(adapter, sourceID, link, syndicationFormat, contentFormat, geoCoder, sinceDate, filterQuery);
	}
	
	@SuppressWarnings("unchecked")
	private String readFeed(ISyncAdapter adapter, String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate, String filterQuery) throws Exception {

		ISchema schema = this.getSchema(sourceID, link);
		IMapping mapping = this.getMappings(sourceID, link, geoCoder);

		List<Item> items;
		IFilter<Item> filter = new FilterQuery(filterQuery, schema);
		if(contentFormat != null && contentFormat.isPlainXML()){
			CompoundFilter compoundFilter;
			if(sinceDate == null){
				compoundFilter = new CompoundFilter(NonDeletedFilter.INSTANCE, filter);
			}else {
				SinceLastUpdateFilter sinceFilter = new SinceLastUpdateFilter(sinceDate);
				compoundFilter = new CompoundFilter(NonDeletedFilter.INSTANCE, sinceFilter, filter);
			}
			items = adapter.getAll(compoundFilter);
		} else {
			items = adapter.getAllSince(sinceDate, filter);	
		}
		
		String title = getFeedTitle(sourceID);
		Feed feed = new Feed(title, title, link);
		feed.addItems(items);
		
		String xml = writeFeedAsXml(sourceID, feed, syndicationFormat, contentFormat, schema, mapping, NullIdentityProvider.INSTANCE);
		return xml;
	}
	
	@Override
	public String synchronize(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, String feedXml) throws Exception {
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;

		if(isMeshGroup(sourceID)){
			Feed feedLoaded = this.readFeedFromXml(feedXml, syndicationFormat, contentFormat, null, null);		
			InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(sourceID, identityProvider, feedLoaded.getItems());
			
			ISyncAdapter adapter = getSyncMeshGroupAdapter(sourceID, identityProvider, syndicationFormat, contentFormat);
			SyncEngine syncEngine = new SyncEngine(adapter, inMemoryAdapter);
			List<Item> conflicts = syncEngine.synchronize();
		
			String title = getFeedTitle(sourceID);
			Feed feedResult = new Feed(title, "conflicts", link);
			feedResult.addItems(conflicts);
			return this.writeFeedAsXml(feedResult, syndicationFormat, contentFormat, null, null, identityProvider);

		} else {
			ISchema schema = this.getSchema(sourceID, link);
			IMapping mapping = this.getMappings(sourceID, link, geoCoder);
			ISyncAdapter adapter = getSyncAdapter(sourceID, identityProvider);
			Feed feedLoaded = this.readFeedFromXml(feedXml, syndicationFormat, contentFormat, schema, mapping);		
			InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(sourceID, identityProvider, feedLoaded.getItems());
		
			SyncEngine syncEngine = new SyncEngine(adapter, inMemoryAdapter);
			List<Item> conflicts = syncEngine.synchronize();
		
			String title = getFeedTitle(sourceID);
			Feed feedResult = new Feed(title, "conflicts", link);
			feedResult.addItems(conflicts);
			return this.writeFeedAsXml(feedResult, syndicationFormat, contentFormat, schema, mapping, identityProvider);
		}
	}
	

	@Override
	public void addNewFeed(String sourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by) {
		IIdentityProvider identityProvider = new IdentityProvider(by);
		ISyncAdapter parentAdapter = this.getParentSyncAdapter(sourceID, identityProvider);
		
		if(parentAdapter instanceof ISyncAware){
			((ISyncAware)parentAdapter).beginSync();
		}
		
		basicAddNewFeed(parentAdapter, sourceID, syndicationFormat, link, description, schema, mappings, identityProvider);
		
		if(parentAdapter instanceof ISyncAware){
			((ISyncAware)parentAdapter).endSync();
		}

	}
	
	public void basicAddNewFeed(ISyncAdapter parentAdapter, String sourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, IIdentityProvider identityProvider) {
		String title = getFeedTitle(sourceID);
		Feed feed = new Feed(title, description, link);
		
		this.addNewFeed(sourceID, feed, syndicationFormat, identityProvider);
		
		Element parentPayload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		
		Element schemaElement = parentPayload.addElement(ISchema.ELEMENT_SCHEMA);		
		if(schema != null && schema.trim().length() >0){				
			schemaElement.setText(schema);	// TODO (JMT) validate schema
		}
		
		Element mappingsElement = parentPayload.addElement(IMapping.ELEMENT_MAPPING);
		if(mappings != null && mappings.trim().length() >0){				
			mappingsElement.setText(mappings);  // TODO (JMT) validate mappings
		}
		
		String sycnId = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(sycnId, title, description, link, parentPayload);
		Sync sync = new Sync(sycnId, identityProvider.getAuthenticatedUser(), new Date(), false);
		Item item = new Item(content, sync);
		
		parentAdapter.add(item);
		
	}

	protected String getFeedTitle(String sourceID) {
		if(sourceID == null){
			return "Mesh";
		} else {
			String[] sourceIds = sourceID.split("/");
			return sourceIds[sourceIds.length -1];
		}
	}

	public boolean isMeshGroup(String sourceID) {
		return sourceID == null || (sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length());
	}
	
	private Feed readFeedFromXml(String xml, ISyndicationFormat syndicationFormat, Format contentFormat, ISchema meshSchema, IMapping mapping){
		IContentReader feedItemReader = getContentReader(contentFormat, meshSchema, mapping);		
		
		FeedReader reader = new FeedReader(syndicationFormat, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feedItemReader);
		try {
			return reader.read(xml);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private String writeFeedAsXml(Feed feed, ISyndicationFormat syndicationFormat, Format contentFormat, ISchema meshSchema, IMapping mapping, IIdentityProvider identityProvider){
		IContentWriter feedItemWriter = getContentWriter(contentFormat, meshSchema, mapping);
		
		FeedWriter writer = new FeedWriter(syndicationFormat, identityProvider, feedItemWriter);
		try {
			return writer.writeAsXml(feed);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private IContentWriter getContentWriter(Format contentFormat, ISchema meshSchema, IMapping mapping) {
		IContentWriter feedItemWriter;
		if(meshSchema == null){
			if(mapping == null){
				feedItemWriter = ContentWriter.INSTANCE;
			}else{
				feedItemWriter = new ContentWriter(mapping);
			}	
		} else {
			if(contentFormat != null && contentFormat.isXForm()){
				feedItemWriter = new XFormRDFSchemaInstanceContentReadWriter((IRDFSchema)meshSchema, mapping, !contentFormat.isPlainXML());
			} else {
				if(meshSchema instanceof IRDFSchema){
					feedItemWriter = new RDFSchemaInstanceContentReadWriter((IRDFSchema)meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
				} else {
					feedItemWriter = new SchemaInstanceContentReadWriter(meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
				}
			}
		}
		return feedItemWriter;
	}

	
	private String writeFeedAsXml(String sourceID, Feed feed, ISyndicationFormat syndicationFormat, Format contentFormat, ISchema meshSchema, IMapping mapping, IIdentityProvider identityProvider){
		IContentWriter feedItemWriter = getContentWriter(sourceID, contentFormat, meshSchema, mapping);
		
		FeedWriter writer = new FeedWriter(syndicationFormat, identityProvider, feedItemWriter);
		try {
			return writer.writeAsXml(feed);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	private IContentWriter getContentWriter(String sourceID, Format contentFormat, ISchema meshSchema, IMapping mapping) {
		IContentWriter feedItemWriter;
		if(meshSchema == null){
			if(contentFormat != null && contentFormat.isXForm() && isMeshGroup(sourceID)){
				feedItemWriter = new XFormRDFSchemaContentWriter(contentFormat.isPlainXML());
			} else {
				if(mapping == null){
					feedItemWriter = ContentWriter.INSTANCE;
				}else{
					feedItemWriter = new ContentWriter(mapping);
				}
			}
		} else {
			if(contentFormat != null && contentFormat.isXForm()){
				feedItemWriter = new XFormRDFSchemaInstanceContentReadWriter((IRDFSchema)meshSchema, mapping, !contentFormat.isPlainXML());
			} else {
				if(meshSchema instanceof IRDFSchema){
					feedItemWriter = new RDFSchemaInstanceContentReadWriter((IRDFSchema)meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
				} else {
					feedItemWriter = new SchemaInstanceContentReadWriter(meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
				}
			}
		}
		return feedItemWriter;
	}
	
	private IContentReader getContentReader(Format contentFormat, ISchema meshSchema, IMapping mapping) {
		IContentReader feedItemReader;
		if(meshSchema == null){
			feedItemReader = ContentReader.INSTANCE;
		} else {
			if(contentFormat != null && contentFormat.isXForm()){
				feedItemReader = new XFormRDFSchemaInstanceContentReadWriter((IRDFSchema)meshSchema, mapping, !contentFormat.isPlainXML());
			} else {
				if(meshSchema instanceof IRDFSchema){
					feedItemReader = new RDFSchemaInstanceContentReadWriter((IRDFSchema)meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
				} else {
					feedItemReader = new SchemaInstanceContentReadWriter(meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
				}
			}
		}
		return feedItemReader;
	}
	
	@Override
	public ISchema getSchema(String sourceID, String link) throws Exception{
		if(sourceID == null){
			return null;
		}
		
		ISyncAdapter syncAdapter = this.getParentSyncAdapter(sourceID, NullIdentityProvider.INSTANCE);
		List<Item> items = syncAdapter.getAll(new XMLContentLinkFilter(link));
				
		if(!items.isEmpty()){
			Item item = items.get(0);
			
			String xml = item.getContent().getPayload().asXML();
//			xml = xml.replaceAll("&lt;", "<");						// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
//			xml = xml.replaceAll("&gt;", ">");
			
			Element schema = DocumentHelper.parseText(xml).getRootElement();
			if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(schema.getName())){
				schema = schema.element(ISchema.ELEMENT_SCHEMA);
			}
			
			if(schema == null || !schema.getName().equals(ISchema.ELEMENT_SCHEMA)){
				return null;
			} else {
				String schemaXML = schema.getText();
				if(schemaXML == null || schemaXML.isEmpty()){
					return null;
				}
				Element schemaElement = XMLHelper.parseElement(schemaXML);
				if(RDFSchema.isRDF(schemaElement)){
					StringReader xmlReader = new StringReader(schemaXML);
					return new RDFSchema(xmlReader);
				} else {
					return new Schema(schemaElement);
				}
			}
		} else {
			return null;
		}
	}
	
	@Override
	public IMapping getMappings(String sourceID, String link, IGeoCoder geoCoder) throws Exception{
		if(sourceID == null){
			return null;
		}
		ISyncAdapter syncAdapter = this.getParentSyncAdapter(sourceID, NullIdentityProvider.INSTANCE);
		List<Item> items = syncAdapter.getAll(new XMLContentLinkFilter(link));
				
		Element mappings = null;
		if(!items.isEmpty()){
			Item item = items.get(0);
			
			String xml = item.getContent().getPayload().asXML();
			xml = xml.replaceAll("&lt;", "<");						// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
			xml = xml.replaceAll("&gt;", ">");
			
			mappings = DocumentHelper.parseText(xml).getRootElement();
			if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(mappings.getName())){
				mappings = mappings.element(IMapping.ELEMENT_MAPPING);
			}
			if(mappings != null && !mappings.getName().equals(IMapping.ELEMENT_MAPPING)){
				mappings = null;
			}
		}
		
		if(geoCoder != null){
			GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
			GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
			GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
			return new Mapping(mappings, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
		} else {
			return new Mapping(mappings);
		}
	}

	@Override
	public void deleteFeed(String sourceID, String link, String by) {
		ISyncAdapter parentAdapter = this.getParentSyncAdapter(sourceID, new IdentityProvider(by));
		
		if(parentAdapter instanceof ISyncAware){
			((ISyncAware)parentAdapter).beginSync();
		}
		
		List<Item> items = parentAdapter.getAll(new XMLContentLinkFilter(link));
		if(!items.isEmpty()){
			Item item = items.get(0);
			parentAdapter.delete(item.getSyncId());
			this.basicDeleteFeed(sourceID);
		}
		
		if(parentAdapter instanceof ISyncAware){
			((ISyncAware)parentAdapter).endSync();
		}
	}

	protected abstract void basicDeleteFeed(String sourceID);

	@Override
	public void updateFeed(String sourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by) {
		
		IIdentityProvider identityProvider = new IdentityProvider(by);
		ISyncAdapter parentAdapter = this.getParentSyncAdapter(sourceID, identityProvider);
				
		if(parentAdapter instanceof ISyncAware){
			((ISyncAware)parentAdapter).beginSync();
		}
		
		List<Item> items = parentAdapter.getAll(new XMLContentLinkFilter(link));
		if(items.isEmpty()){
			
			basicAddNewFeed(parentAdapter, sourceID, syndicationFormat, link, description, schema, mappings, identityProvider);
			
		} else{
			
			Item item =items.get(0);
			
			item.getSync().update(by, new Date());
			
			Element payload = item.getContent().getPayload();
			if(schema != null && schema.trim().length() >0){
				Element schemaElement = payload.element(ISchema.ELEMENT_SCHEMA);
				if(schemaElement == null){
					schemaElement = payload.addElement(ISchema.ELEMENT_SCHEMA);
				}
				schemaElement.setText(schema);	// TODO (JMT) validate schema
			}
			
			if(mappings != null && mappings.trim().length() >0){
				Element mappingsElement = payload.element(IMapping.ELEMENT_MAPPING);
				if(mappingsElement == null){	
					mappingsElement = payload.addElement(IMapping.ELEMENT_MAPPING);
				}
				mappingsElement.setText(mappings);  // TODO (JMT) validate mappings
			}
			
			((XMLContent)item.getContent()).setDescription(description);
			
			parentAdapter.update(item);
			
		}
		
		if(parentAdapter instanceof ISyncAware){
			((ISyncAware)parentAdapter).endSync();
		}
	}
	
	@Override
	public void addNewItemFromRawContent(String sourceID, String link, String xml, String by){
		IIdentityProvider identityProvider = new IdentityProvider(by);
		ISyncAdapter adapter = getSyncAdapter(sourceID, identityProvider);
		
		if(adapter instanceof ISyncAware){
			((ISyncAware)adapter).beginSync();
		}
		
		String syncID = IdGenerator.INSTANCE.newID();
		
		Element payload = XMLHelper.parseElement(xml);
		XMLContent content = new XMLContent(syncID, null, null, null, payload);
		Item item = new Item(content, new Sync(syncID, by, new Date(), false));
		adapter.add(item);
		
		if(adapter instanceof ISyncAware){
			((ISyncAware)adapter).endSync();
		}
	}
	
}
