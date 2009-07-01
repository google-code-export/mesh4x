package org.mesh4j.ektoo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.folder.FolderSyncAdapterFactory;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SyncAdapterBuilder implements ISyncAdapterBuilder {

	// MODEL VARIABLEs
	private PropertiesProvider propertiesProvider;
	private MsAccessHibernateSyncAdapterFactory msAccesSyncAdapter;
	private MsExcelSyncAdapterFactory excelSyncFactory; 
	private GoogleSpreadSheetSyncAdapterFactory googleSpreadSheetSyncAdapterFactory;
	private GoogleSpreadSheetRDFSyncAdapterFactory googleSpreadSheetRDFSyncAdapterFactory;
	
	// BUSINESS METHODS

	public SyncAdapterBuilder(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.propertiesProvider = propertiesProvider;
		this.msAccesSyncAdapter = new MsAccessHibernateSyncAdapterFactory(this.getBaseDirectory(), this.getBaseRDFUrl());
		this.excelSyncFactory = new MsExcelSyncAdapterFactory();
		this.googleSpreadSheetSyncAdapterFactory = new GoogleSpreadSheetSyncAdapterFactory();
		this.googleSpreadSheetRDFSyncAdapterFactory = new GoogleSpreadSheetRDFSyncAdapterFactory(this.getBaseRDFUrl());
	}

	@Override
	public ISyncAdapter createMsAccessAdapter(String mdbFileName, String tableName) {
		try {
			return this.msAccesSyncAdapter.createSyncAdapterFromFile(tableName, mdbFileName, tableName, this.getIdentityProvider());
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@Override
	public ISyncAdapter createMsAccessMultiTablesAdapter(String mdbFileName, String[] selectedTables){
		Set<String> tables = new TreeSet<String>();			
		for (String tableName : selectedTables) {
			tables.add(tableName);
		}
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", getIdentityProvider());
		return this.msAccesSyncAdapter.createSyncAdapterForMultiTables(mdbFileName, tables, getIdentityProvider(), adapterOpaque);
	}

	/**
	 * google spreadsheet adapter without rdf
	 */
	@Override
	public ISyncAdapter createPlainXMLBasedGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo) {
		String idColumName = spreadSheetInfo.getIdColumnName();
		String userName = spreadSheetInfo.getUserName();
		String passWord = spreadSheetInfo.getPassWord();
		String spreadsheetName = spreadSheetInfo.getGoogleSpreadSheetName();
		String type = spreadSheetInfo.getType();
		String sheetName = spreadSheetInfo.getSheetName();
		
		IIdentityProvider identityProvider = getIdentityProvider();
		SplitAdapter splitAdapter = googleSpreadSheetSyncAdapterFactory
				.createSyncAdapter(userName, passWord, spreadsheetName,
						sheetName, idColumName, null, identityProvider, type);

		return splitAdapter;
	}

	/**
	 * google spreadsheet adapter with rdf
	 * 
	 * when googlespreadsheet adapter works as source, no need to supply rdf schema
	 */
	@Override
	public SplitAdapter createRdfBasedGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo, IRDFSchema sourceSchema) {
		String idColumnName = spreadSheetInfo.getIdColumnName();
		String username = spreadSheetInfo.getUserName();
		String password = spreadSheetInfo.getPassWord();
		String spreadsheetName = spreadSheetInfo.getGoogleSpreadSheetName();
		String sourceAlias = spreadSheetInfo.getType();
		String cotentSheetName = spreadSheetInfo.getSheetName();
		String lastUpdateColumnName = null;
		
		IIdentityProvider identityProvider = getIdentityProvider();

		if(sourceSchema == null){
			return googleSpreadSheetRDFSyncAdapterFactory
			.createSyncAdapter(username, password, spreadsheetName,
					cotentSheetName, idColumnName, lastUpdateColumnName, identityProvider, sourceAlias);
		}else{
			return this.googleSpreadSheetRDFSyncAdapterFactory.createSyncAdapter(
				username, password, spreadsheetName, cotentSheetName,
				idColumnName, lastUpdateColumnName, identityProvider, sourceSchema, sourceAlias);
		}
	}	

	@Override
	public ISyncAdapter createHttpSyncAdapter(String serverUrl, String meshGroup, String dataSetId, IRDFSchema rdfSchema) {
		return HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, getIdentityProvider(), rdfSchema);
	}	
	
	@Override
	public ISyncAdapter createHttpSyncAdapterForMultiDataset(String serverUrl, String meshGroup, List<IRDFSchema> rdfSchemas){
		List<ISchema> schemas = new ArrayList<ISchema>();
		for (IRDFSchema rdfschema : rdfSchemas) {
			schemas.add(rdfschema);
		}
		return HttpSyncAdapterFactory.createSyncAdapterForMultiDataset(serverUrl, meshGroup, getIdentityProvider(), schemas);
	}
	
	@Override
	public SplitAdapter createMySQLAdapter(String userName, String password,
			String hostName, int portNo, String databaseName, String tableName) {

		String baseDirectory = getBaseDirectory();
		//create directory for keeping mapping file,mapping file directory will be the name of the
		//provided database name
		File mappingDirectory = new File(baseDirectory + File.separator + databaseName);
		
		String connectionUri = "jdbc:mysql://" + hostName + ":" + portNo + "/"
				+ databaseName;
		
		return HibernateSyncAdapterFactory.createHibernateAdapter(
				connectionUri,
				userName,
				password,
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class, 
				tableName, 
				getBaseRDFUrl(),
				mappingDirectory.getAbsolutePath(),
				this.getIdentityProvider(),
				null);
	}

	@Override
	public ISyncAdapter createMySQLAdapterForMultiTables(String userName, String password,
			String hostName, int portNo, String databaseName, String[] tables) {

		String baseDirectory = getBaseDirectory();
		//create directory for keeping mapping file,mapping file directory will be the name of the
		//provided database name
		File mappingDirectory = new File(baseDirectory + File.separator + databaseName);
		
		String connectionUri = "jdbc:mysql://" + hostName + ":" + portNo + "/" + databaseName;
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		TreeSet<String> syncTables = new TreeSet<String>(Arrays.asList(tables));
		
		return HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
				connectionUri,
				userName,
				password,
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class, 
				syncTables, 
				getBaseRDFUrl(),
				mappingDirectory.getAbsolutePath(),
				this.getIdentityProvider(),
				adapterOpaque,
				null);
	}
	
	@Override
	public SplitAdapter createMsExcelAdapter(String contentFileName, String sheetName, String[] idColumnNames, boolean isRDF) {
		Guard.argumentNotNullOrEmptyString(contentFileName, "contentFileName");
		Guard.argumentNotNull(isRDF, "isRDF");
		File file = getFile(contentFileName);
		if (file == null || !file.exists()) {
			Guard.argumentNotNullOrEmptyString(contentFileName, "contentFileName");
		}
		if(isRDF){
			return MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(file.getAbsolutePath()), sheetName, idColumnNames, null, getIdentityProvider(), getBaseRDFUrl());	
		}
		return this.excelSyncFactory.createSyncAdapter(file.getAbsolutePath(), sheetName, idColumnNames[0], getIdentityProvider());
	}
	
	@Override
	public ISyncAdapter createMsExcelAdapter(String contentFileName, IRDFSchema sourceSchema){
		return MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(contentFileName), getIdentityProvider(), sourceSchema);
	}	
	
	@Override
	public ISyncAdapter createMsExcelAdapterForMultiSheets(String contentFileName, List<IRDFSchema> sheets) {
		Guard.argumentNotNullOrEmptyString(contentFileName, "contentFileName");
		
		File file = getFile(contentFileName);
		if (file == null || !file.exists()) {
			Guard.argumentNotNullOrEmptyString(contentFileName, "contentFileName");
		}
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", getIdentityProvider());
		return MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(file.getAbsolutePath(), getIdentityProvider(), adapterOpaque, sheets);
	}
	
	@Override
	public ISyncAdapter createKMLAdapter(String kmlFileName) {
		return KMLDOMLoaderFactory.createKMLAdapter(kmlFileName, getIdentityProvider());
	}
	
	@Override
	public ISyncAdapter createFeedAdapter(String title, String description, String link, String fileName, ISyndicationFormat syndicationFormat) {
		Feed feed = new Feed(title, description, link);
		FeedAdapter adapter = new FeedAdapter(fileName, getIdentityProvider(), IdGenerator.INSTANCE, syndicationFormat, feed);
		return adapter;
	}
	
	@Override
	public ISyncAdapter createZipFeedAdapter(String zipFileName) {
		return FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, getIdentityProvider(), getBaseDirectory());
	}
	
	@Override
	public ISyncAdapter createFolderAdapter(String folderName) {
		return FolderSyncAdapterFactory.createFolderAdapter(folderName, getIdentityProvider(), getIdGenerator());
	}
	
	@Override
	public String generateMySqlFeed(String userName, String password, String hostName, int portNo, String databaseName, String tableName) {
		
		String fileName = (userName+"_"+databaseName+"_"+tableName+"_"+IdGenerator.INSTANCE.newID()+".xml").replace(" ", "");
		
		String fullFileName = FileUtils.getFileName(this.propertiesProvider.getBaseDirectory() + File.separator + "temp", fileName);
		
		ISyncAdapter adapter;
		
		if(tableName == null || tableName.length() == 0){
			adapter = createMySQLTableDiscoveryAdapter(userName, password, hostName, portNo, databaseName);
		} else {
			adapter = createMySQLAdapter(userName, password, hostName, portNo, databaseName, tableName);
		}

		List<Item> items = adapter.getAll();
		Feed feed = new Feed();
		feed.addItems(items);
		
		FeedAdapter feedAdapter = new FeedAdapter(fullFileName, this.propertiesProvider.getIdentityProvider(), IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		feedAdapter.flush();
		return fullFileName;
	}
	
	// ACCESSORS
	private ISyncAdapter createMySQLTableDiscoveryAdapter(String userName, String password, String hostName, int portNo, String databaseName) {
		
		IIdentityProvider identityProvider = this.propertiesProvider.getIdentityProvider();
		Set<String> tableNames = HibernateSyncAdapterFactory.getMySqlTableNames(hostName, portNo, databaseName, userName, password);
		
		List<Item> items = new ArrayList<Item>();
		for (String tableName : tableNames) {
			String sycnId = IdGenerator.INSTANCE.newID();
			XMLContent content = new XMLContent(sycnId, "table name: "+ tableName, "table name: " + tableName, "", NullContent.PAYLOAD);
			Sync sync = new Sync(sycnId, identityProvider.getAuthenticatedUser(), new Date(), false);
			Item item = new Item(content, sync);
			items.add(item);
		}
		
		InMemorySyncAdapter adapter = new InMemorySyncAdapter(databaseName, identityProvider, items);
		return adapter;
	}

	private File getFile(String fileName) {
		File file = new File(fileName);
		return file;
	}

	private IIdentityProvider getIdentityProvider() {
		return this.propertiesProvider.getIdentityProvider();
	}

	private IIdGenerator getIdGenerator() {
		return IdGenerator.INSTANCE;
	}

	private String getBaseDirectory() {
		return this.propertiesProvider.getBaseDirectory();
	}

	public String getBaseRDFUrl() {
		return this.propertiesProvider.getMeshSyncServerURL();
	}	
}
