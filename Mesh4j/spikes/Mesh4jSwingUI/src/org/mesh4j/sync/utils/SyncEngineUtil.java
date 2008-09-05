package org.mesh4j.sync.utils;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.ui.Mesh4jUI;
import org.mesh4j.sync.ui.translator.Mesh4jUITranslator;
import org.mesh4j.sync.validations.MeshException;

public class SyncEngineUtil {

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
		if(isURL(endpoint)){
			return new HttpSyncAdapter(endpoint, RssSyndicationFormat.INSTANCE, identityProvider);
		} else {
			if(isFeed(endpoint)){
				return new FeedAdapter(endpoint, identityProvider, idGenerator);
			}else{
				IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(endpoint, identityProvider);
				return new DOMAdapter(loader);
			}
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
		return !isURL(endpointValue);
	}
	
	public static boolean isURL(String endpointValue) {
		return endpointValue.toUpperCase().startsWith("HTTP");
	}
	
	public static boolean isFeed(String endpointValue) {
		return endpointValue.toUpperCase().endsWith("XML");
	}
}
