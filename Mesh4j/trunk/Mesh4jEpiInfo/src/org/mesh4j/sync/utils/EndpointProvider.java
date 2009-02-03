package org.mesh4j.sync.utils;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.validations.MeshException;

public class EndpointProvider {

	private final static Log Logger = LogFactory.getLog(EndpointProvider.class);

	
	public static EndpointMapping[] getEndpointMappings(PropertiesProvider propertiesProvider) {
		Map<String, String> myEndpoints = getEndpoints(propertiesProvider);
		EndpointMapping[] result = new EndpointMapping[myEndpoints.size()];
		int i = 0;
		for (String alias : myEndpoints.keySet()) {
			result[i] = new EndpointMapping(alias, myEndpoints.get(alias));
			i = i + 1;
		}
		return result;
	}

	public static EndpointMapping getEndpointMapping(String endpointId, PropertiesProvider propertiesProvider) {
		Map<String, String> myEndpoints = getEndpoints(propertiesProvider);
		for (String alias : myEndpoints.keySet()) {
			if(endpointId.equals(myEndpoints.get(alias))){
				return new EndpointMapping(alias, endpointId);
			}
		}
		return null;
	}
	
	public static EndpointMapping getEndpointMappingByAlias(String endpointAlias, PropertiesProvider propertiesProvider) {
		Map<String, String> myEndpoints = getEndpoints(propertiesProvider);
		for (String alias : myEndpoints.keySet()) {
			if(endpointAlias.equals(alias)){
				return new EndpointMapping(alias, myEndpoints.get(alias));
			}
		}
		return null;
	}
	
	public static void deleteEndpointMapping(EndpointMapping endpoint, PropertiesProvider propertiesProvider) {
		Map<String, String> myEndpoints = getEndpoints(propertiesProvider);
		String result = myEndpoints.remove(endpoint.getAlias());
		if(result != null){
			PropertiesUtils.store(getEndpointsFileName(propertiesProvider), myEndpoints);
		}		
	}
	
	public static void saveOrUpdateEndpointMapping(String alias, EndpointMapping endpoint, PropertiesProvider propertiesProvider) {
		Map<String, String> myEndpoints = getEndpoints(propertiesProvider);
		myEndpoints.remove(alias);
		
		myEndpoints.put(endpoint.getAlias(), endpoint.getEndpoint());
		PropertiesUtils.store(getEndpointsFileName(propertiesProvider), myEndpoints);		
		
	}
	
	public static EndpointMapping createNewEndpointMappingIfAbsent(String alias, String endpointId, PropertiesProvider propertiesProvider){
		EndpointMapping endpoint = null;
		try{
			endpoint = getEndpointMapping(endpointId, propertiesProvider);
			if(endpoint == null){
				
				endpoint = getEndpointMappingByAlias(alias, propertiesProvider);
				
				String aliasToAdd = alias;
				int i = 0;
				while(endpoint != null){
					i = i +1;
					aliasToAdd = alias + "_" + i;
					endpoint = getEndpointMappingByAlias(aliasToAdd, propertiesProvider);
				}
				
				endpoint = new EndpointMapping(aliasToAdd, endpointId);
				saveOrUpdateEndpointMapping(endpointId, endpoint, propertiesProvider);
				return endpoint;
				
			} else {
				return null;
			}
		} catch(Throwable e){
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private static Map<String, String> getEndpoints(PropertiesProvider propertiesProvider) {
		String fileName = getEndpointsFileName(propertiesProvider);
		File file = new File(fileName);
		if(!file.exists()){
			try{
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		}
		
		Map<String, String> myEndpoints = PropertiesUtils.getProperties(fileName);
		return myEndpoints;
	}

	private static String getEndpointsFileName(PropertiesProvider propertiesProvider) {
		String baseDirectory = propertiesProvider.getBaseDirectory();
		String fileName = baseDirectory+"/myEndpoints.properties";
		return fileName;
	}
}
