package org.mesh4j.sync.utils;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.Mesh4jUI;
import org.mesh4j.sync.ui.translator.Mesh4jUITranslator;
import org.mesh4j.sync.validations.MeshException;

public class SyncEngineUtil {

	private static final String FIELD_SEPARATOR = "~";
	private final static Log Logger = LogFactory.getLog(Mesh4jUI.class);
	
	public static String synchronizeItems(String endpoint1, String endpoint2, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		try{
			ISyncAdapter sourceRepo = makeRepositoryAdapter(endpoint1, identityProvider, idGenerator);
			ISyncAdapter targetRepo = makeRepositoryAdapter(endpoint2, identityProvider, idGenerator);
			
			SyncEngine syncEngine = new SyncEngine(sourceRepo, targetRepo);
			List<Item> conflicts = syncEngine.synchronize();
			if(conflicts.isEmpty()){
				return Mesh4jUITranslator.getMessageSyncSuccessfully();
			} else {
				return Mesh4jUITranslator.getMessageSyncCompletedWithConflicts(conflicts.size());
			}
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessageSyncFailed();
		}
	}
	
	public static ISyncAdapter makeRepositoryAdapter(String endpoint, IIdentityProvider identityProvider, IIdGenerator idGenerator) {
		
		// TODO (JMT) refactoring: change for ISyncAdapterFactory
		
		if(isURL(endpoint)){
			return new HttpSyncAdapter(endpoint, RssSyndicationFormat.INSTANCE, identityProvider);
		} else if(isFeed(endpoint)){
			File file = new File(endpoint);
			if(file.exists()){
				return new FeedAdapter(endpoint, identityProvider, idGenerator);
			} else {
				Feed feed = new Feed(endpoint, endpoint, "", RssSyndicationFormat.INSTANCE);
				return new FeedAdapter(endpoint, identityProvider, idGenerator, RssSyndicationFormat.INSTANCE, feed);
			}
		}else if(isKML(endpoint)){
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(endpoint, identityProvider);
			return new DOMAdapter(loader);
		} else if(isExcel(endpoint)){
			String[] elements = endpoint.split(FIELD_SEPARATOR);
			String excelFile = elements[1];
			String entity = elements[2];
			String entityID = elements[3];
			
			MsExcel excel = new MsExcel(excelFile);
			MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(excel, identityProvider, idGenerator);
			MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, entity, entityID);
			return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		}else if(isAccess(endpoint)){
			String[] elements = endpoint.split(FIELD_SEPARATOR);
			String odbc = elements[1];
			String entity = elements[2];
			String user = elements[3];
			String pass = elements[4];
			
			HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
			builder.setProperty("hibernate.dialect","org.mesh4j.sync.adapters.hibernate.MSAccessDialect");
			builder.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
			builder.setProperty("hibernate.connection.url","jdbc:odbc:"+odbc);
			builder.setProperty("hibernate.connection.username",user);
			builder.setProperty("hibernate.connection.password",pass);
			builder.addMapping(new File(entity+".hbm.xml"));
			builder.addMapping(new File("SyncInfo.hbm.xml"));

			SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
			
			HibernateSyncRepository syncRepository = new HibernateSyncRepository(builder, syncInfoParser);
			HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builder, entity);
			return new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);		
		} else if(isMySQL(endpoint)){
			String[] elements = endpoint.split(FIELD_SEPARATOR);
			String entity = elements[1];
			
			HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
			builder.setPropertiesFile(new File("test_MySQL_hibernate.properties"));
			builder.addMapping(new File(entity+".hbm.xml"));
			builder.addMapping(new File("SyncInfo.hbm.xml"));

			return new HibernateAdapter(builder, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
				
		} else {
			return null;
		}
	}
	
	public static String purgueKML(String kmlFile, IIdentityProvider identityProvider){
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(kmlFile, identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.purgue();
			return Mesh4jUITranslator.getMessagePurgueKMLSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessagePurgueKMLFailed();
		}
	}

	public static String cleanKML(String kmlFile, IIdentityProvider identityProvider){
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(kmlFile, identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.clean();
			return Mesh4jUITranslator.getMessageCleanKMLSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessageCleanKMLFailed();
		}
	}

	public static String prepareKMLToSync(String kmlFile, IIdentityProvider identityProvider){
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(kmlFile, identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.prepareDOMToSync();
			return Mesh4jUITranslator.getMessagePrepareToSyncSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessagePrepareToSyncFailed();
		}
	}
	
	public static boolean isFile(String endpointValue) {
		return !isURL(endpointValue) && !isSMS(endpointValue);
	}
	
	public static boolean isURL(String endpointValue) {
		return endpointValue.toUpperCase().startsWith("HTTP://");
	}
	
	public static boolean isFeed(String endpointValue) {
		return endpointValue.toUpperCase().endsWith("XML");
	}

	public static boolean isSMS(String endpointValue) {
		return endpointValue.toUpperCase().startsWith("SMS:");
	}
	
	public static boolean isKML(String endpointValue) {
		return endpointValue.toUpperCase().endsWith(".KML") || endpointValue.toUpperCase().endsWith(".KMZ");
	}
	
	public static boolean isExcel(String endpointValue) {
		String[] elements = endpointValue.split(FIELD_SEPARATOR);
		return endpointValue.toUpperCase().startsWith("EXCEL") && elements.length == 4 
			&& (elements[1].toUpperCase().endsWith(".XLS") || elements[1].toUpperCase().endsWith(".XLSX"));
	}
	
	public static boolean isAccess(String endpointValue) {
		String[] elements = endpointValue.split(FIELD_SEPARATOR);
		return endpointValue.toUpperCase().startsWith("ACCESS") && elements.length == 5; 
	}
	
	public static boolean isMySQL(String endpointValue) {
		String[] elements = endpointValue.split(FIELD_SEPARATOR);
		return endpointValue.toUpperCase().startsWith("MYSQL") && elements.length == 2; 
	}
}
