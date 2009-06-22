package org.mesh4j.sync.adapters.http;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class HttpSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "HTTP";
	
	public static final HttpSyncAdapterFactory INSTANCE = new HttpSyncAdapterFactory();
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return sourceDefinition != null && sourceDefinition.toUpperCase().startsWith("HTTP://") && isValidURL(sourceDefinition);
	}

	@Override
	public HttpSyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		return new HttpSyncAdapter(sourceDefinition, RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, ContentWriter.INSTANCE, ContentReader.INSTANCE);
	}
	
	public static HttpSyncAdapter createSyncAdapter(String url, IIdentityProvider identityProvider){
		return new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, ContentWriter.INSTANCE, ContentReader.INSTANCE);
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}
	
	
	public static boolean isValidURL(String url){
		URL newURL;
		try {
			newURL = new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}
			
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) newURL.openConnection();
			conn.connect();
		} catch (Exception e) {
			return false;
		}finally{
	    	if(conn != null){
	    		conn.disconnect();
	    	}
		}
		return true;
	}

	public static boolean isURL(String url) {
		return url != null && url.toUpperCase().startsWith("HTTP://");
	}

	public static boolean isMalformedURL(String url) {
		try {
			new URL(url);
			return false;
		} catch (MalformedURLException e) {
			return true;
		}
	}

	public static HttpSyncAdapter createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(String serverUrl, String meshGroup, String dataSetId, IIdentityProvider identityProvider, ISchema schema) {
		String url = serverUrl + "/" + meshGroup + "/" + dataSetId;
		HttpSyncAdapter adapter = createSyncAdapter(url, identityProvider);
		
		//TODO: need to come up with better strategy for automatic creation of mesh/feed
		//if not available
		ISchema cloudSchema = adapter.getSchema();
		if(cloudSchema == null){
			HttpSyncAdapter.uploadMeshDefinition(
					serverUrl, 
					meshGroup,
					RssSyndicationFormat.NAME, 
					"", 
					null, 
					null,
					identityProvider.getAuthenticatedUser());
			
			HttpSyncAdapter.uploadMeshDefinition(
					serverUrl, 
					meshGroup + "/" + dataSetId,
					RssSyndicationFormat.NAME, 
					"", 
					schema,
					null, 
					identityProvider.getAuthenticatedUser());
		} else {
			if(!cloudSchema.isCompatible(schema)){
				Guard.throwsException("INCOMPATIBLE_SCHEMA");
			}
		}
		return adapter;
	}
	
	public static HttpSyncAdapter createSyncAdapterForMultiDataset(String serverUrl, String meshGroup, IIdentityProvider identityProvider, List<ISchema> schemas) {
		
		// create mesh group
		if (!HttpSyncAdapter.isMeshDefinitionAvailable(serverUrl+"/"+meshGroup)){
			HttpSyncAdapter.uploadMeshDefinition(serverUrl, meshGroup,
					RssSyndicationFormat.NAME, "", null, null, identityProvider
							.getAuthenticatedUser());
		}
		
		// create mesh data sets
		for (ISchema schema : schemas) {
			String feedName = schema.getName();
			
			String tmpUrl = serverUrl + "/" + meshGroup + "/" + feedName;
			HttpSyncAdapter adapter = createSyncAdapter(tmpUrl, identityProvider);
			ISchema cloudSchema = adapter.getSchema();
			
			if (cloudSchema == null) {
				HttpSyncAdapter.uploadMeshDefinition(serverUrl, meshGroup + "/"
						+ feedName, RssSyndicationFormat.NAME, "", schema,
						null, identityProvider.getAuthenticatedUser());
			} else {
				if(!cloudSchema.isCompatible(schema)){
					Guard.throwsException("INCOMPATIBLE_SCHEMA");
				}
			}
		}
				
		// create http sync adapter
		String url = HttpSyncAdapter.makeMeshGroupURLToSync(serverUrl + "/" + meshGroup);
		return createSyncAdapter(url, identityProvider);
	}

}
