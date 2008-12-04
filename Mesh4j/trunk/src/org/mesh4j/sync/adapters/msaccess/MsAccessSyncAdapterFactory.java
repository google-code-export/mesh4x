package org.mesh4j.sync.adapters.msaccess;

import java.io.File;

import org.mesh4j.sync.adapters.ISourceIdResolver;
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

	// MODEL VARIABLES
	private String baseDirectory;
	private ISourceIdResolver fileMappings;
		
	// BUSINESS METHODS
	public MsAccessSyncAdapterFactory(String baseDirectory, ISourceIdResolver fileMappings){
		Guard.argumentNotNull(baseDirectory,"baseDirectory");
		Guard.argumentNotNull(fileMappings,"fileMappings");
		
		this.baseDirectory = baseDirectory;
		this.fileMappings = fileMappings;
	}	
	
	public static SplitAdapter createSyncAdapterFromFile(String mdbFileName, String tableName, String mappingsDirectory) throws Exception{
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
		return sourceId.startsWith("access:");
	}

	// ISyncAdapterFactry methods
	
	public static String createSourceId(String mdbFileName, String mdbTableName){
		File file = new File(mdbFileName);
		String sourceID = "access:" + file.getName() + "@" + mdbTableName;
		return sourceID;
	}
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		return isMsAccess(sourceId);
	}

	@Override
	public SplitAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		String[] elements = sourceId.substring("access:".length(), sourceId.length()).split("@");
		String mdbFileName = this.baseDirectory+"/"+ elements[0];
		String tableName = elements[1];
		
		if(this.fileMappings != null){
			String fileName = (String) this.fileMappings.getSource(elements[0]);
			if(fileName != null){
				mdbFileName = fileName;
			}
		}
		SplitAdapter msAccessAdapter = createSyncAdapterFromFile(mdbFileName, tableName, this.baseDirectory);
		return msAccessAdapter;
	}

	public static String getTableName(String sourceId) {
		String[] elements = sourceId.substring("access:".length(), sourceId.length()).split("@");
		return elements[1];
	}

	public static String getFileName(String sourceId) {
		String[] elements = sourceId.substring("access:".length(), sourceId.length()).split("@");
		return elements[0];
	}

	public String getBaseDirectory() {
		return this.baseDirectory;
	}

	@Override
	public String getSourceName(String sourceId) {
		return getTableName(sourceId);
	}

	@Override
	public String getSourceType(String sourceId) {
		return "MsAccess";
	}
}
