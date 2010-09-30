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
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.SchemaInstanceContentReadWriter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchemaInstanceContentReadWriter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class HttpSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "HTTP";
	
	public static final HttpSyncAdapterFactory INSTANCE = new HttpSyncAdapterFactory();
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return sourceDefinition != null && sourceDefinition.toUpperCase().startsWith("HTTP://") && isValidURL(sourceDefinition);
	}

	@Override
	public HttpSyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		return createSyncAdapter(sourceDefinition, identityProvider);
	}
	
	public static HttpSyncAdapter createSyncAdapter(String url, IIdentityProvider identityProvider){
		try{
			ISchema schema = HttpSyncAdapter.getSchema(new URL(url));
			return createSyncAdapter(url, identityProvider, schema, null);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	
	public static HttpSyncAdapter createSyncAdapter(String serverUrl, String meshGroup, String dataSetId, IIdentityProvider identityProvider){
		ISchema schema = HttpSyncAdapter.getSchema(serverUrl, meshGroup, dataSetId);
		String url = serverUrl + "/" + meshGroup + "/" + dataSetId;
		return createSyncAdapter(url, identityProvider, schema, null);
	}

	public static HttpSyncAdapter createSyncAdapter(String url, IIdentityProvider identityProvider, ISchema schema, IMapping mapping) {
		if(schema == null){
			return new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, ContentWriter.INSTANCE, ContentReader.INSTANCE);
		} else {
			if(schema instanceof IRDFSchema){
				RDFSchemaInstanceContentReadWriter readWriter = new RDFSchemaInstanceContentReadWriter((IRDFSchema)schema, (mapping == null ? new Mapping(null) : mapping), true);
				return new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, readWriter, readWriter);
			} else {
				SchemaInstanceContentReadWriter readWriter = new SchemaInstanceContentReadWriter(schema, (mapping == null ? new Mapping(null) : mapping), true);
				return new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, readWriter, readWriter);	
			}
			
		}
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

	public static HttpSyncAdapter createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(String serverUrl, String meshGroup, String dataSetId, IIdentityProvider identityProvider, ISchema schema, IMapping mapping) {
		String url = serverUrl + "/" + meshGroup + "/" + dataSetId;
		
		//TODO: need to come up with better strategy for automatic creation of mesh/feed
		//if not available
		ISchema cloudSchema = HttpSyncAdapter.getSchema(serverUrl, meshGroup, dataSetId);
		if(cloudSchema == null){
			HttpSyncAdapter.uploadMeshDefinition(
					serverUrl, 
					meshGroup,
					RssSyndicationFormat.NAME, 
					meshGroup, 
					null, 
					null,
					identityProvider.getAuthenticatedUser());
			
			HttpSyncAdapter.uploadMeshDefinition(
					serverUrl, 
					meshGroup + "/" + dataSetId,
					RssSyndicationFormat.NAME, 
					dataSetId, 
					schema,
					mapping, 
					identityProvider.getAuthenticatedUser());
			return createSyncAdapter(url, identityProvider, schema, mapping);
		} else {
			if(!cloudSchema.isCompatible(schema)){
				Guard.throwsException("INCOMPATIBLE_SCHEMA");
			}
			
			if(mapping != null){
				IMapping cloudMappings = HttpSyncAdapter.getMappings(serverUrl, meshGroup, dataSetId);
				if(cloudMappings == null || (cloudMappings != null && !cloudMappings.asXML().equals(mapping.asXML()))){
					HttpSyncAdapter.uploadMeshDefinition(
						serverUrl, 
						meshGroup + "/" + dataSetId,
						RssSyndicationFormat.NAME, 
						"", 
						schema,
						mapping, 
						identityProvider.getAuthenticatedUser());
				}
			}
			return createSyncAdapter(url, identityProvider, cloudSchema, mapping);
		}
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

			ISchema cloudSchema = HttpSyncAdapter.getSchema(serverUrl, meshGroup, feedName);
			
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
		return createSyncAdapter(url, identityProvider, null, null);
	}

}
