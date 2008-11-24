package org.mesh4j.sync.adapters.msaccess;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.NullIdentityProvider;

import sun.jdbc.odbc.JdbcOdbcDriver;

public class MsAccessSyncAdapterFactory {

	public static ISyncAdapter createSyncAdapterFromFile(String mdbFileName, String tableName, String mappingsDirectory) throws Exception{
		String contentMappingFileName = mappingsDirectory + "/" + tableName + ".hbm.xml";
		String syncMappingFileName =  mappingsDirectory + "/" + tableName + "_sync.hbm.xml";
		
		MsAccessHibernateMappingGenerator.createMappingsIfAbsent(mdbFileName, tableName, contentMappingFileName, syncMappingFileName);
//		MsAccessHibernateMappingGenerator.createSyncTableIfAbsent(mdbFileName, tableName);
		return createSyncAdapterFromFile(mdbFileName, tableName, contentMappingFileName, syncMappingFileName);	
	}

	public static ISyncAdapter createSyncAdapterFromFile(String mdbFileName, String tableName, String contentMappingFileName, String syncMappingFileName){
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}"; 
		return createSplitAdapter(dbURL, tableName, "", "", contentMappingFileName, syncMappingFileName);	
	}

	public static ISyncAdapter createSyncAdapterFromODBC(String odbcName, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName){
		String dbURL = "jdbc:odbc:"+odbcName;
		return createSplitAdapter(tableName, user, password, dbURL, contentMappingFileName, syncMappingFileName);	
	}
	
	private static ISyncAdapter createSplitAdapter(String dbURL, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName) {
		HibernateSessionFactoryBuilder builder = createHibernateSessionBuilder(dbURL, tableName, user, password, contentMappingFileName, syncMappingFileName);
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builder, syncInfoParser);
		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builder, tableName);
		return new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
	}
	
	private static HibernateSessionFactoryBuilder createHibernateSessionBuilder(String dbURL, String tableName, String user, String password, String contentMappingFileName, String syncMappingFileName) {
		
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
}
