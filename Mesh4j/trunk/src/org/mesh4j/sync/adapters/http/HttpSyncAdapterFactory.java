package org.mesh4j.sync.adapters.http;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.security.IIdentityProvider;

public class HttpSyncAdapterFactory implements ISyncAdapterFactory {

	public static final HttpSyncAdapterFactory INSTANCE = new HttpSyncAdapterFactory();
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		return sourceId.toUpperCase().startsWith("HTTP://") && isValidURL(sourceId);
	}

	@Override
	public HttpSyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		return new HttpSyncAdapter(sourceId, RssSyndicationFormat.INSTANCE, identityProvider);
	}

	@Override
	public String getSourceName(String sourceId) {
		return sourceId;
	}

	@Override
	public String getSourceType(String sourceId) {
		return "http";
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


}
