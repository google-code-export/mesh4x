package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.hibernate.IHibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import sun.jdbc.odbc.JdbcOdbcDriver;

/**
 * MsAccessHibernateSyncAdapterFactory is a factory of ISyncAdapter(SplitAdapter) for MsAccess file via odbc.
 * Using sun JdbcOdbcDriver.
 * Pk is required
 * No Supports GUID and Auto numeric fields
 */
public class MsAccessHibernateSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "MsAccess";
	private static final String MS_ACCESS = SOURCE_TYPE+":";		
	public static final String DEFAULT_SEPARATOR = "@";
	
	// MODEL VARIABLES
	private String baseDirectory;
	private String rdfBaseUri;

	// BUSINESS METHODS
	public MsAccessHibernateSyncAdapterFactory(String baseDirectory, String rdfBaseUri){
		super();
		Guard.argumentNotNull(baseDirectory, "baseDirectory");
		
		this.baseDirectory = baseDirectory;
		this.rdfBaseUri = rdfBaseUri;
	}	
	
	public SplitAdapter createSyncAdapterFromFile(String sourceAlias, String mdbFileName, String tableName, IIdentityProvider identityProvider) {
		return createHibernateAdapter(mdbFileName, tableName, this.rdfBaseUri, this.baseDirectory, identityProvider);
	}

	private static IRDFSchema getRDFSchema(String sourceAlias, String mdbFileName, String tableName, String rdfBaseUri) throws IOException {
		if(rdfBaseUri == null){
			return null;
		}			
		return MsAccessRDFSchemaGenerator.extractRDFSchema(mdbFileName, tableName, sourceAlias, rdfBaseUri+"/"+sourceAlias+"#");
	}

	public static SplitAdapter createSyncAdapterFromFile(String mdbFileName, String tableName, String contentMappingFileName, String syncMappingFileName, IRDFSchema rdfSchema, IIdentityProvider identityProvider){
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}"; 
		return createSplitAdapter(dbURL, tableName, "", "", contentMappingFileName, syncMappingFileName, rdfSchema, identityProvider);	
	}

	public SplitAdapter createSyncAdapterFromODBC(String odbcName, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName, IRDFSchema rdfSchema, IIdentityProvider identityProvider){
		String dbURL = "jdbc:odbc:"+odbcName;
		return createSplitAdapter(tableName, user, password, dbURL, contentMappingFileName, syncMappingFileName, rdfSchema, identityProvider);	
	}
	
	private static SplitAdapter createSplitAdapter(String dbURL, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName, IRDFSchema rdfSchema, IIdentityProvider identityProvider) {
		HibernateSessionFactoryBuilder builder = createHibernateSessionBuilder(dbURL, tableName, user, password, contentMappingFileName, syncMappingFileName, rdfSchema);
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, MsAccessHibernateMappingGenerator.getSyncTableName(tableName));
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builder, syncInfoParser);
		
		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builder, tableName);
		return new SplitAdapter(syncRepository, contentAdapter, identityProvider);
	}
	
	public static HibernateSessionFactoryBuilder createHibernateSessionBuilder(String dbURL, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName, IRDFSchema rdfSchema) {
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builder.setProperty("hibernate.connection.driver_class", JdbcOdbcDriver.class.getName());
		builder.setProperty("hibernate.connection.url", dbURL);
		builder.setProperty("hibernate.connection.username",user);
		builder.setProperty("hibernate.connection.password",password);
		builder.addMapping(new File(contentMappingFileName));
		builder.addMapping(new File(syncMappingFileName));
		builder.addRDFSchema(tableName, rdfSchema);
		return builder;
	}

	public static boolean isMsAccess(String sourceDefinition) {
		return sourceDefinition.startsWith(MS_ACCESS);
	}

	public static boolean isValidAccessTable(String mdbFileName, String mdbTableName) {
		return MsAccessHelper.existTable(mdbFileName, mdbTableName);
	}
	
	// ISyncAdapterFactry methods
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return sourceDefinition != null && isMsAccess(sourceDefinition);
	}

	@Override
	public SplitAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		String mdbFileName = getFileName(sourceDefinition);
		String tableName = getTableName(sourceDefinition);
		SplitAdapter msAccessAdapter = createSyncAdapterFromFile(sourceAlias, mdbFileName, tableName, identityProvider);
		return msAccessAdapter;
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}

	public static String createSourceDefinition(String mdbFileName, String mdbTableName) {
		return MS_ACCESS + mdbFileName + DEFAULT_SEPARATOR + mdbTableName;
	}

	private static String getDataSource(String sourceDefinition) {
		if(isMsAccess(sourceDefinition)){
			return sourceDefinition.substring(MS_ACCESS.length(), sourceDefinition.length());
		} else {
			return sourceDefinition;
		}
	}
	
	public static String getFileName(String sourceDefinition) {
		String source = MsAccessHibernateSyncAdapterFactory.getDataSource(sourceDefinition);
		String[] elements = source.split(DEFAULT_SEPARATOR);
		//String tableName = elements[1];
		String fileName= elements[0];
		return fileName;
	}

	public static String getTableName(String sourceDefinition) {
		String source = MsAccessHibernateSyncAdapterFactory.getDataSource(sourceDefinition);
		String[] elements = source.split(DEFAULT_SEPARATOR);
		String tableName = elements[1];
		//String fileName= elements[0];
		return tableName;
	}

	public void changeSourceDefinition(String sourceAlias, String sourceDefinition, ISyncAdapter syncAdapter) {
		try{
			SplitAdapter splitAdapter = (SplitAdapter) syncAdapter;
			
			String mdbFileName = getFileName(sourceDefinition);
			String tableName = getTableName(sourceDefinition);
			String contentMappingFileName = FileUtils.getFileName(this.baseDirectory , tableName + ".hbm.xml");
			String syncMappingFileName =  FileUtils.getFileName(this.baseDirectory , tableName + "_sync.hbm.xml");
			String user = "";
			String password = "";
	
			String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}"; 
			
			IRDFSchema rdfSchema = getRDFSchema(sourceAlias, mdbFileName, tableName, this.rdfBaseUri);
	
			IHibernateSessionFactoryBuilder builder = createHibernateSessionBuilder(dbURL, tableName, user, password, contentMappingFileName, syncMappingFileName, rdfSchema);
			
			HibernateContentAdapter contentAdapter = (HibernateContentAdapter) splitAdapter.getContentAdapter();
			contentAdapter.initializeSessionFactory(builder, tableName);
			
			HibernateSyncRepository syncRepo = (HibernateSyncRepository) splitAdapter.getSyncRepository();
			syncRepo.initializeSessionFactory(builder);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public static CompositeSyncAdapter createSyncAdapterForMultiTables(String mdbFileName, Set<String> tables, String rdfBaseUri, String baseDirectory, IIdentityProvider identityProvider, ISyncAdapter adapterOpaque){
		try{
			IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[tables.size()];
			int i = 0;
			for (String tableName : tables) {
				SplitAdapter syncAdapter = createHibernateAdapter(mdbFileName, tableName, rdfBaseUri, baseDirectory, identityProvider);
				HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)syncAdapter.getContentAdapter();
				adapters[i] = new IdentifiableSyncAdapter(hibernateContentAdapter.getType(), syncAdapter);
				i = i +1;
			}
			return new CompositeSyncAdapter("MsAccess composite", adapterOpaque, identityProvider, adapters);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public static Set<String> getTableNames(String fileName) throws IOException {
		TreeSet<String> result = new TreeSet<String>();
		Set<String> tableNames = MsAccessHelper.getTableNames(fileName);
		for (String tableName : tableNames) {
			if(!MsAccessHibernateMappingGenerator.isSyncTableName(tableName)){
				result.add(tableName);
			}
		}
		return result;
	}

	public static SplitAdapter createHibernateAdapter(String mdbFileName, String tableName, String rdfBaseUri, String baseDirectory, IIdentityProvider identityProvider) {
		
		if(mdbFileName == null || mdbFileName.length() == 0 || tableName == null || tableName.length() == 0){
			return null;
		}
		
		try{
			String contentMappingFileName = FileUtils.getFileName(baseDirectory , tableName + ".hbm.xml");
			String syncMappingFileName =  FileUtils.getFileName(baseDirectory , tableName + "_sync.hbm.xml");
			
			MsAccessHibernateMappingGenerator.forceCreateMappings(mdbFileName, tableName, contentMappingFileName, syncMappingFileName);
			
			String syncTableName = MsAccessHibernateMappingGenerator.getSyncTableName(tableName);
			MsAccessHelper.createSyncTableIfAbsent(mdbFileName, syncTableName);
			
			IRDFSchema rdfSchema = getRDFSchema(tableName, mdbFileName, tableName, rdfBaseUri);
			return createSyncAdapterFromFile(mdbFileName, tableName, contentMappingFileName, syncMappingFileName, rdfSchema, identityProvider);
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
