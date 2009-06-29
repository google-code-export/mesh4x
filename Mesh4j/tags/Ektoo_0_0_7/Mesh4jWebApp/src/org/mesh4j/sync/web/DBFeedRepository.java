package org.mesh4j.sync.web;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.hibernate.dialect.Dialect;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.IContentReader;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.filter.CompoundFilter;
import org.mesh4j.sync.filter.FilterQuery;
import org.mesh4j.sync.filter.NonDeletedFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.SchemaInstanceContentReadWriter;
import org.mesh4j.sync.payload.schema.xform.XFormRDFSchemaContentWriter;
import org.mesh4j.sync.payload.schema.xform.XFormRDFSchemaInstanceContentReadWriter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.IdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.servlet.Format;
import org.mesh4j.sync.utils.SqlDBUtils;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

public class DBFeedRepository implements IFeedRepository {
	
	// MODEL VARIABLES
	private Class<Driver> driverClass;
	private Class<Dialect> dialectClass;
	private String connectionUri;
	private String dbSchema;
	private String userName;
	private String password;
	private String baseDirectory;
	private String syncInfoSubfixed;
	
	// BUSINESS METHODS
	
	public DBFeedRepository(String baseDirectory, String connectionUri,
			Class<Dialect> dialectClass, Class<Driver> driverClass, String dbSchema, String userName, String password,
			String syncInfoSubfixed) {
		super();
		this.baseDirectory = baseDirectory;
		this.connectionUri = connectionUri;
		this.dialectClass = dialectClass;
		this.driverClass = driverClass;
		this.password = password;
		this.userName = userName;
		this.dbSchema = dbSchema;
		this.syncInfoSubfixed = syncInfoSubfixed;
	}
	
	@Override
	public void addNewItemFromRawContent(String sourceID, String link, String rawXml, String by) {
		IIdentityProvider identityProvider = new IdentityProvider(by);
		String databaseName = getDBName(sourceID);
		String tableName = getDBTableName(sourceID);
		ISyncAdapter adapter = getSyncAdapter(link, databaseName, tableName, identityProvider);
		
		if(adapter instanceof ISyncAware){
			((ISyncAware)adapter).beginSync();
		}
		
		String syncID = IdGenerator.INSTANCE.newID();
		
		Element payload = XMLHelper.parseElement(rawXml);
		XMLContent content = new XMLContent(syncID, null, null, null, payload);
		Item item = new Item(content, new Sync(syncID, by, new Date(), false));
		adapter.add(item);
		
		if(adapter instanceof ISyncAware){
			((ISyncAware)adapter).endSync();
		}
	}

	@Override
	public boolean existsFeed(String sourceID) {
		if(sourceID == null){
			return false;
		}
		
		String databaseName = getDBName(sourceID);
		if(databaseName == null){
			return false;
		} 
		
		List<String> dbNames = getDBNames();
		if(!dbNames.contains(databaseName)){
			return false;
		}
		
		String tableName = getDBTableName(sourceID);
		if(tableName != null){
			Set<String> tableNames = getTableNames(databaseName);	
			return tableNames.contains(tableName);
		} 
		return true;
	}

	@Override
	public List<Item> getAll(String sourceID, String link, Date sinceDate, String filterQuery, ISchema schema, IMapping mapping){
		String databaseName = getDBName(sourceID);
		String tableName = getDBTableName(sourceID);
		ISyncAdapter adapter = getSyncAdapter(link, databaseName, tableName, NullIdentityProvider.INSTANCE);
		
		IFilter<Item> filter = new FilterQuery(filterQuery, schema);
		if(filter == null){
			return adapter.getAllSince(sinceDate);
		} else {
			return adapter.getAllSince(sinceDate, filter);
		}
	}

	@Override
	public ISchema getSchema(String sourceID, String link) throws Exception {
		String databaseName = getDBName(sourceID);
		String tableName = getDBTableName(sourceID);
		SplitAdapter adapter = getSyncAdapter(link, databaseName, tableName, NullIdentityProvider.INSTANCE);
		ISchema schema = ((HibernateContentAdapter)adapter.getContentAdapter()).getMapping().getSchema();
		return schema;
	}
	
	
	@Override
	public String readFeedGroup(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate, String filterQuery) throws Exception {
		ISyncAdapter adapter = this.getSyncMeshGroupAdapter(link, sourceID, NullIdentityProvider.INSTANCE, syndicationFormat, contentFormat);
		return readFeed(adapter, null, sourceID, link, syndicationFormat, contentFormat, geoCoder, sinceDate, filterQuery);	
	}
	
	@Override
	public String readFeed(String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate, String filterQuery) throws Exception {
		
		String databaseName = getDBName(sourceID);
		String tableName = getDBTableName(sourceID);
		
		ISyncAdapter adapter = null;
		ISchema schema = null;
		if(databaseName == null){
			adapter = getSchemaAdapter(link, NullIdentityProvider.INSTANCE);			
		} else {
			if(tableName == null){
				adapter = getTableAdapter(link, databaseName, NullIdentityProvider.INSTANCE);			
			} else {
				adapter = getSyncAdapter(link, databaseName, tableName, NullIdentityProvider.INSTANCE);
				schema = ((HibernateContentAdapter)((SplitAdapter)adapter).getContentAdapter()).getMapping().getSchema();
			}
		}
		
		return readFeed(adapter, schema, sourceID, link, syndicationFormat, contentFormat, geoCoder, sinceDate, filterQuery); 
	}
	
	@SuppressWarnings("unchecked")
	public String readFeed(ISyncAdapter adapter, ISchema schema, String sourceID, String link, ISyndicationFormat syndicationFormat, Format contentFormat, IGeoCoder geoCoder, Date sinceDate, String filterQuery) throws Exception {
			
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
		
		IMapping mapping = this.getMappings(sourceID, link, geoCoder);
		String xml = writeFeedAsXml(feed, syndicationFormat, contentFormat, schema, mapping, NullIdentityProvider.INSTANCE);
		return xml;
	}

	@Override
	public String synchronize(String sourceID, String link,
			ISyndicationFormat syndicationFormat, Format contentFormat,
			IGeoCoder geoCoder, String feedXml) throws Exception {
		
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		if(isMeshGroup(sourceID)){
			Feed feedLoaded = this.readFeedFromXml(feedXml, syndicationFormat, contentFormat, null, null);		
			InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter(sourceID, identityProvider, feedLoaded.getItems());
			
			ISyncAdapter adapter = getSyncMeshGroupAdapter(link, sourceID, identityProvider, syndicationFormat, contentFormat);
			SyncEngine syncEngine = new SyncEngine(adapter, inMemoryAdapter);
			List<Item> conflicts = syncEngine.synchronize();
		
			String title = getFeedTitle(sourceID);
			Feed feedResult = new Feed(title, "conflicts", link);
			feedResult.addItems(conflicts);
			return this.writeFeedAsXml(feedResult, syndicationFormat, contentFormat, null, null, identityProvider);
		} else {
			String databaseName = getDBName(sourceID);
			String tableName = getDBTableName(sourceID);
			
			SplitAdapter adapter = getSyncAdapter(link, databaseName, tableName, identityProvider);
			ISchema schema = ((HibernateContentAdapter)adapter.getContentAdapter()).getMapping().getSchema();
			
			IMapping mapping = this.getMappings(sourceID, link, geoCoder);
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
	
	protected ISyncAdapter getSyncMeshGroupAdapter(String link, String sourceID, IIdentityProvider identityProvider, ISyndicationFormat syndicationFormat, Format contentFormat){
		String databaseName = getDBName(sourceID);
		ISyncAdapter tableAdapter = getTableAdapter(link, databaseName, identityProvider);
		
		List<Item> items = tableAdapter.getAll();
		
		IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[items.size()];
		
		int i = 0;
		for (Item item : items) {
			String tableName = ((XMLContent)item.getContent()).getTitle();
			String tableLink = ((XMLContent)item.getContent()).getLink();
			ISyncAdapter feedDataSetAdapter = getSyncAdapter(tableLink, databaseName, tableName, identityProvider);
			IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(tableName, feedDataSetAdapter);
			adapters[i] = adapter;
			i = i +1;
		}
		FeedAdapter opaqueAdapter = createOpaqueFeedAdapter(sourceID, identityProvider);
		return new CompositeSyncAdapter("Feed file composite", opaqueAdapter, identityProvider, adapters);
	}

	protected ISyncAdapter getTableAdapter(String link, String databaseName, IIdentityProvider identityProvider) {
		
		Set<String> tableNames = getTableNames(databaseName);
		
		List<Item> items = new ArrayList<Item>();
		for (String tableName : tableNames) {
			if(!tableName.endsWith(this.syncInfoSubfixed)){
				String sycnId = IdGenerator.INSTANCE.newID();
				String linkTable = link + "/" + tableName;
				XMLContent content = new XMLContent(sycnId, tableName, "table name: " + tableName, linkTable, NullContent.PAYLOAD);
				Sync sync = new Sync(sycnId, identityProvider.getAuthenticatedUser(), new Date(), false);
				Item item = new Item(content, sync);
				items.add(item);
			}
		}
		
		InMemorySyncAdapter adapter = new InMemorySyncAdapter(databaseName, identityProvider, items);
		return adapter;
	}
	
	protected ISyncAdapter getSchemaAdapter(String link, IIdentityProvider identityProvider) {

		List<String> dbNames = getDBNames();
		
		List<Item> items = new ArrayList<Item>();
		for (String dbName : dbNames) {
			String sycnId = IdGenerator.INSTANCE.newID();
			String linkDB = link + "/" + dbName;
			XMLContent content = new XMLContent(sycnId, "db name: "+ dbName, "db name: " + dbName, linkDB, NullContent.PAYLOAD);
			Sync sync = new Sync(sycnId, identityProvider.getAuthenticatedUser(), new Date(), false);
			Item item = new Item(content, sync);
			items.add(item);
		}
		
		InMemorySyncAdapter adapter = new InMemorySyncAdapter("root", identityProvider, items);
		return adapter;
	}

	protected SplitAdapter getSyncAdapter(String link, String databaseName, String tableName, IIdentityProvider identityProvider) {
		String rdfLink = link.substring(0, link.length() - ("/"+tableName).length());
		return HibernateSyncAdapterFactory.createHibernateAdapter(
				this.connectionUri + databaseName,
				this.userName,
				this.password,
				this.driverClass,
				this.dialectClass, 
				tableName, 
				rdfLink,
				this.baseDirectory,
				identityProvider);	
	}
	
	
	private String getDBName(String sourceID) {
		if(sourceID == null){
			return null;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				String normalizedSourceID = sourceID.replaceAll("/", "");
				return normalizedSourceID;
			} else {
				String[] sourceIds = sourceID.split("/");
				return sourceIds[0];
			}
		}
	}

	private String getDBTableName(String sourceID) {
		if(sourceID == null){
			return null;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				return null;
			} else {
				String[] sourceIds = sourceID.split("/");
				return sourceIds[1];
			}
		}
	}
	
	protected String getFeedTitle(String sourceID) {
		if(sourceID == null){
			return "Mesh";
		} else {
			String[] sourceIds = sourceID.split("/");
			return sourceIds[sourceIds.length -1];
		}
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
	
	private IContentReader getContentReader(Format contentFormat,
			ISchema meshSchema, IMapping mapping) {
		IContentReader feedItemReader;
		if(meshSchema == null){
			feedItemReader = ContentReader.INSTANCE;
		} else {
			if(contentFormat != null && contentFormat.isXForm()){
				feedItemReader = new XFormRDFSchemaInstanceContentReadWriter(meshSchema, mapping, !contentFormat.isPlainXML());
			} else {
				feedItemReader = new SchemaInstanceContentReadWriter(meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
			}
		}
		return feedItemReader;
	}
	
	private IContentWriter getContentWriter(Format contentFormat, ISchema meshSchema, IMapping mapping) {
		IContentWriter feedItemWriter;
		if(meshSchema == null){
			if(contentFormat != null && contentFormat.isXForm()){
				feedItemWriter = new XFormRDFSchemaContentWriter(contentFormat.isPlainXML());
			} else {
				feedItemWriter = ContentWriter.INSTANCE;
			}
		} else {
			if(contentFormat != null && contentFormat.isXForm()){
				feedItemWriter = new XFormRDFSchemaInstanceContentReadWriter(meshSchema, mapping, !contentFormat.isPlainXML());
			} else {
				feedItemWriter = new SchemaInstanceContentReadWriter(meshSchema, mapping, contentFormat == null ? true : !contentFormat.isPlainXML());
			}
		}
		return feedItemWriter;
	}

	@Override
	public IMapping getMappings(String sourceID, String link, IGeoCoder geoCoder)throws Exception {
		if(geoCoder != null){
			GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
			GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
			GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
			return new Mapping(null, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
		} else {
			return new Mapping(null);
		}
	}
	
	private List<String> getDBNames() {
		//TODO (JMT) return SqlDBUtils.getDBNames(this.driverClass, this.connectionUri, this.userName, this.password);
	
		List<String> dbNames = new ArrayList<String>();
		dbNames.add(this.dbSchema);
		return dbNames;
	}

	private Set<String> getTableNames(String databaseName) {
		return SqlDBUtils.getTableNames(this.driverClass, this.connectionUri + databaseName, this.userName, this.password);
	}

	@Override
	public boolean isMeshGroup(String sourceID) {
		return sourceID == null || (sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length());
	}
	
	
	// UNSUPPORTED OPERATIONS
	
	@Override
	public void updateFeed(String sourceID,
			ISyndicationFormat syndicationFormat, String link,
			String description, String schema, String mappings, String by) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addNewFeed(String newSourceID, ISyndicationFormat syndicationFormat, String link, String description, String schema, String mappings, String by) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void deleteFeed(String sourceID, String link, String by) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHistory(String sourceID, String link, ISyndicationFormat syndicationFormat, String syncId) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void cleanFeed(String sourceID) {
		throw new UnsupportedOperationException();
	}

	private FeedAdapter createOpaqueFeedAdapter(String sourceID, IIdentityProvider identityProvider) {
		String fileName = getFeedOpaqueFileName(sourceID);
		if(fileName == null){
			return null;
		} else {
			Feed defaultFeed = new Feed(sourceID, sourceID, "");
			FeedAdapter adapter = new FeedAdapter(fileName, identityProvider, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, defaultFeed);
			adapter.refresh();
			return adapter;
		}
	}
	
	private String getFeedOpaqueFileName(String sourceID) {
		if(sourceID == null){
			return null;
		} else {
			if(sourceID.indexOf("/") == -1 || sourceID.indexOf("/") == sourceID.length()){
				return this.baseDirectory + "mesh_" + sourceID + "_opaque.xml";
			} else {
				return null;
			}
		}
	}
}
