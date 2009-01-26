package org.mesh4j.sync.adapters.msaccess;

import java.io.File;

import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

import sun.jdbc.odbc.JdbcOdbcDriver;

public class MsAccessSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "MsAccess";
	private static final String MS_ACCESS = SOURCE_TYPE+":";	
	
	// MODEL VARIABLES
	private String baseDirectory;
	private IMsAccessSourceIdResolver sourceIdResolver;
		
	// BUSINESS METHODS
	public MsAccessSyncAdapterFactory(String baseDirectory, IMsAccessSourceIdResolver sourceIdResolver){
		Guard.argumentNotNull(baseDirectory,"baseDirectory");
		Guard.argumentNotNull(sourceIdResolver,"sourceIdResolver");
		
		this.baseDirectory = baseDirectory;
		this.sourceIdResolver = sourceIdResolver;
	}	
	
	public static SplitAdapter createSyncAdapterFromFile(String mdbFileName, String tableName, String mappingsDirectory) throws Exception{
		if(mdbFileName == null || mdbFileName.length() == 0 || tableName == null || tableName.length() == 0){
			return null;
		}
		
		String contentMappingFileName = mappingsDirectory + "/" + tableName + ".hbm.xml";
		String syncMappingFileName =  mappingsDirectory + "/" + tableName + "_sync.hbm.xml";
		
		MsAccessHibernateMappingGenerator.createMappingsIfAbsent(mdbFileName, tableName, contentMappingFileName, syncMappingFileName);
//		MsAccessHibernateMappingGenerator.createSyncTableIfAbsent(mdbFileName, tableName);
		return createSyncAdapterFromFile(mdbFileName, tableName, contentMappingFileName, syncMappingFileName);	
	}

	public static SplitAdapter createSyncAdapterFromFile(String mdbFileName, String tableName, String contentMappingFileName, String syncMappingFileName){
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}"; 
		return createSplitAdapter(dbURL, tableName, "", "", contentMappingFileName, syncMappingFileName);	
	}

	public static SplitAdapter createSyncAdapterFromODBC(String odbcName, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName){
		String dbURL = "jdbc:odbc:"+odbcName;
		return createSplitAdapter(tableName, user, password, dbURL, contentMappingFileName, syncMappingFileName);	
	}
	
	private static SplitAdapter createSplitAdapter(String dbURL, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName) {
		HibernateSessionFactoryBuilder builder = createHibernateSessionBuilder(dbURL, tableName, user, password, contentMappingFileName, syncMappingFileName);
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builder, syncInfoParser);
		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builder, tableName);
		return new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
	}
	
	public static HibernateSessionFactoryBuilder createHibernateSessionBuilder(String dbURL, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName) {
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builder.setProperty("hibernate.connection.driver_class", JdbcOdbcDriver.class.getName());
		builder.setProperty("hibernate.connection.url", dbURL);
		builder.setProperty("hibernate.connection.username",user);
		builder.setProperty("hibernate.connection.password",password);
		builder.addMapping(new File(contentMappingFileName));
		builder.addMapping(new File(syncMappingFileName));
		return builder;
	}

	public static boolean isMsAccess(String sourceId) {
		return sourceId.startsWith(MS_ACCESS);
	}

	// ISyncAdapterFactry methods
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		return isMsAccess(sourceId);
	}

	@Override
	public SplitAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		String mdbFileName = this.sourceIdResolver.getFileName(sourceId);
		String tableName = this.sourceIdResolver.getTableName(sourceId);
		SplitAdapter msAccessAdapter = createSyncAdapterFromFile(mdbFileName, tableName, this.baseDirectory);
		return msAccessAdapter;
	}

	public String getBaseDirectory() {
		return this.baseDirectory;
	}

	@Override
	public String getSourceName(String sourceId) {
		return this.sourceIdResolver.getSourceName(sourceId);
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}

	public static boolean isValidAccessTable(String mdbFileName, String mdbTableName) {
		return MsAccessHelper.existTable(mdbFileName, mdbTableName);
	}

	public static String createSourceId(String alias) {
		return MS_ACCESS + alias;
	}

	public static String getDataSource(String sourceId) {
		if(isMsAccess(sourceId)){
			return sourceId.substring(MS_ACCESS.length(), sourceId.length());
		} else {
			return sourceId;
		}
	}
}
