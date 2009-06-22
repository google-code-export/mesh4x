package org.mesh4j.sync.adapters.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.IContentReader;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.filter.ConflictsFilter;
import org.mesh4j.sync.filter.FilterQuery;
import org.mesh4j.sync.filter.NullFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class HttpSyncAdapter implements ISyncAdapter, ISupportMerge {

	private final static Log Logger = LogFactory.getLog(HttpSyncAdapter.class);
	private final static NullFilter<Item> NULL_FILTER = new NullFilter<Item>();
	private final static ConflictsFilter CONFLICTS_FILTER = new ConflictsFilter();
	
	// MODEL VARIABLEs
	private URL url;
	private FeedReader feedReader;
	private FeedWriter feedWriter;
	private IIdentityProvider identityProvider;
	
	// BUSINESS METHODS
	public HttpSyncAdapter(String url, ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, 
			IIdGenerator idGenerator, IContentWriter contentWriter, IContentReader contentReader){
		Guard.argumentNotNullOrEmptyString(url, "url");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNull(contentReader, "contentReader");
		Guard.argumentNotNull(contentWriter, "contentWriter");
		
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new MeshException(e);
		}
		this.identityProvider = identityProvider;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator, contentReader);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider, contentWriter);
	}

	@Override
	public List<Item> merge(List<Item> items) {
		try {
			Feed feed = new Feed(items);
			String xml = feedWriter.writeAsXml(feed);
			
			String result = doPOST(this.url, xml, "text/xml");
			
			feed = feedReader.read(result);
			return feed.getItems();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e); 
			throw new MeshException(e);
		}
	}
	
	public List<Item> getAll()
	{
		return getAllSince(null, NULL_FILTER);
	}

	public List<Item> getAll(IFilter<Item> filter)
	{
		return getAllSince(null, filter);
	}

	public List<Item> getAllSince(Date since)
	{
		return getAllSince(since, NULL_FILTER);
	}

	public List<Item> getAllSince(Date since, IFilter<Item> filter)
	{
		Guard.argumentNotNull(filter, "filter");
		return getAll(since == null ? since : DateHelper.normalize(since), filter);
	}
	
	protected List<Item> getAll(Date since, IFilter<Item> filter){
		ArrayList<Item> result = new ArrayList<Item>();
		try {
			String filterCondition = null;
			if(filter instanceof FilterQuery){
				filterCondition = ((FilterQuery) filter).getConditionExpression();
			}
			
			URL getURL = new URL(makeGetURL(this.url.toExternalForm(), filterCondition));
			
			Feed feed = null;
			if(since == null){
				feed = feedReader.read(getURL);
			} else {
				String xml = doGET(getURL, since);
				if(xml == null || xml.trim().length() == 0){
					return result;
				}
				Document documentFeed = DocumentHelper.parseText(xml);
				feed = feedReader.read(documentFeed);
			}
			if(feed != null){
				for (Item item : feed.getItems()) {
					boolean dateOk = SinceLastUpdateFilter.applies(item, since);
					if(filter.applies(item) && dateOk){
						result.add(item);
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}
		return result;
	}
	
	public List<Item> getConflicts()
	{
		return getAllSince(null, CONFLICTS_FILTER);
	}

	
	@Override
	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}
	
	protected String doGET(URL getURL, Date since){
		String result = null;
		HttpURLConnection conn = null;
	    try{
			conn = (HttpURLConnection) getURL.openConnection();

			if(since != null){
				conn.setIfModifiedSince(since.getTime());
			}
			result = readData(conn);
	    } catch(Exception e){
			if(conn != null){
				try {
					int responseCode = conn.getResponseCode();
					if(responseCode != HttpURLConnection.HTTP_OK){
						return null;
					}
				} catch (IOException e1) {
					Logger.error(e1.getMessage(), e1);
					Logger.error(e.getMessage(), e);
					throw new MeshException(e);
				}
			} else {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
	    }		
		return result;
	}

	private static String readData(HttpURLConnection conn) throws Exception {
		InputStream is = null;
	
		try{
			is = conn.getInputStream();
		} catch(Exception e){
			InputStream es = conn.getErrorStream();
			
			StringBuffer result = new StringBuffer();
			Reader reader = new InputStreamReader(es, "UTF-8");
			char[] cb = new char[2048];

			int amtRead = reader.read(cb);
			while (amtRead > 0) {
				result.append(cb, 0, amtRead);
				amtRead = reader.read(cb);
			}
			reader.close();
			es.close();
			
			if(is != null){
				is.close();
			}
			
			Logger.error(result.toString());
			throw e;
		} 

		StringBuffer result = new StringBuffer();
		Reader reader = new InputStreamReader(is, "UTF-8");
		char[] cb = new char[2048];

		int amtRead = reader.read(cb);
		while (amtRead > 0) {
			result.append(cb, 0, amtRead);
			amtRead = reader.read(cb);
		}
		reader.close();
		is.close();
		return result.toString();
	}
	
	public static String doPOST(URL url, String content, String contentType){
	    HttpURLConnection conn = null;
	    String result = null;
	    try{ 
	    	conn = (HttpURLConnection) url.openConnection();
			writeData(content, contentType, conn);		    
		    result = readData(conn);
	    } catch(Exception e){
	    	Logger.error(e.getMessage(), e);
	    	throw new MeshException(e);
	    } finally{
	    	if(conn != null){
	    		conn.disconnect();
	    	}
	    }
	    return result;
	}

	private static void writeData(String content, String contentType, HttpURLConnection conn) throws Exception {
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Length", Integer.toString(content.length()));
		conn.setRequestProperty("Content-Type", contentType);
						
		OutputStreamWriter out = null;
		try{
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(content);
		} finally {
			if(out != null){
				out.close();
			}
		}
	}
	
	// NOT SUPPORTED 
	@Override
	public void add(Item item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Item get(String id) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void update(Item item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(Item item, boolean resolveConflicts) {
		throw new UnsupportedOperationException();
	}

	public static String getSchema(String url) {
		try{
			URL baseURL = new URL(url);		
			return getSchema(baseURL);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	
	private static String getSchema(URL url) {
		String result = null;
		HttpURLConnection conn = null;
	    try{

	    	String urlSchemaString = url.getProtocol() + "://"+ url.getHost() +":"+ url.getPort()+ url.getPath()+ "/" + "schema";

	    	URL urlSchema = new URL(urlSchemaString);
			conn = (HttpURLConnection) urlSchema.openConnection();
			
			result = readData(conn);
	    } catch(Exception e){
			if(conn != null){
				try {
					int responseCode = conn.getResponseCode();
					if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
						return null;
					}
				} catch (IOException e1) {
					Logger.error(e1.getMessage(), e1);
					Logger.error(e.getMessage(), e);
					throw new MeshException(e);
				}
			} else {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
	    }		
		return result;
	}

	public static String getMappings(String url) {
		String result = null;
		HttpURLConnection conn = null;
	    try{
	    	URL baseURL = new URL(url);
	    	String urlMappingsString = baseURL.getProtocol() + "://"+ baseURL.getHost() +":"+ baseURL.getPort()+ baseURL.getPath()+ "/" + "mappings";
	    	
	    	URL urlMappings = new URL(urlMappingsString);
			conn = (HttpURLConnection) urlMappings.openConnection();
			
			result = readData(conn);
	    } catch(Exception e){
			if(conn != null){
				try {
					int responseCode = conn.getResponseCode();
					if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
						return null;
					}
				} catch (IOException e1) {
					Logger.error(e1.getMessage(), e1);
					Logger.error(e.getMessage(), e);
					throw new MeshException(e);
				}
			} else {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
	    }		
		return result;
	}

	public static String makeMeshGroupURLToSync(String url) {
		return addQueryParamater(url, "viewALLGroupMeshItems");
	}
	
	public static String makeMappingsURL(String url) {
		return addSubdomain(url, "mappings");
	}

	public static String makeSchemaURL(String url) {
		return addSubdomain(url, "schema");
	}
	
	public static String makeAddItemFromRawDataURL(String url) {
		return addSubdomain(url, "add");
	}
	
	public static String makeGetURL(String url, String conditionExpression) {
		if(conditionExpression == null || conditionExpression.length() == 0){
			return url;
		} else {
			return addQueryParamater(url, "filter=" + conditionExpression);
		}
	}
	
	private static String addSubdomain(String url, String subDomain) {
		try{
			if(url.contains("?")){
				URL fullURL = new URL(url);
				String query = fullURL.getQuery();
				String baseURL = url.substring(0, url.length() - (query.length() + 1));
				if(baseURL.endsWith("/")){
					return baseURL + subDomain + "?" + query;
				} else {
					return baseURL + "/" + subDomain + "?" + query;
				}
			} else {
				if(url.endsWith("/")){
					return url + subDomain;
				} else {
					return url + "/" + subDomain;
				}
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private static String addQueryParamater(String url, String queryParamater) {
		if(queryParamater == null || queryParamater.length() == 0){
			return url;
		} else {
			if(url.contains("?")){
				return url + "&" + queryParamater;
			} else {
				return url + "?" + queryParamater;
			}
		}
	}

	public boolean isMeshDefinitionAvailable(String meshName){
		String result=null;
		URL meshChkUrl;
		try {
			meshChkUrl = new URL(url.toString()+"/"+meshName+ "?format=atom10");
		} catch (MalformedURLException e1) {
			Logger.error(e1.getMessage(), e1); 
			throw new MeshException(e1);
		}
		try{
			result = doGET(meshChkUrl, new Date());
		}catch(Exception e){
			Logger.error(e.getMessage(), e); 
			throw new MeshException(e);
		}
		
		return result !=null ? true : false;
	}	

	public static void uploadMeshDefinition(String url, String sourceId, String format, String description, ISchema schema, IMapping mappings, String by) {
		try{
			URL baseURL = new URL(url);
			
			String content = makeMeshDefinitionContent(
					sourceId, 
					format, 
					description, 
					schema == null ? "" : schema.asXML(), 
					mappings == null ? "" : mappings.asXML(),
					by);
			doPOST(baseURL, content, "application/x-www-form-urlencoded");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e); 
			throw new MeshException(e);
		}
	}

	private static String makeMeshDefinitionContent(String sourceId,
			String format, String description, String schema, String mappings, String by) throws UnsupportedEncodingException {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(URLEncoder.encode("action", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode("uploadMeshDefinition", "UTF-8"));
		
		sb.append("&");
		
		sb.append(URLEncoder.encode("newSourceID", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode(sourceId, "UTF-8"));
		
		sb.append("&");

		sb.append(URLEncoder.encode("format", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode(format, "UTF-8"));
		
		sb.append("&");

		sb.append(URLEncoder.encode("description", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode(description, "UTF-8"));

		sb.append("&");

		sb.append(URLEncoder.encode("by", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode(by, "UTF-8"));
		
		sb.append("&");

		sb.append(URLEncoder.encode("schema", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode(schema, "UTF-8"));
		
		sb.append("&");

		sb.append(URLEncoder.encode("mappings", "UTF-8"));
		sb.append("=");
		sb.append(URLEncoder.encode(mappings, "UTF-8"));
		
		sb.append("\r\n");
		return sb.toString();
	}

	public void addItemFromRowData(String xml) {
		try{
			URL urlAddItem = new URL(makeAddItemFromRawDataURL(this.url.toString()));
//			doPOST(urlAddItem, xml, "text/xml");
			
			StringBuffer sb = new StringBuffer();
			sb.append(URLEncoder.encode("by", "UTF-8"));
			sb.append("=");
			sb.append(URLEncoder.encode(this.identityProvider.getAuthenticatedUser(), "UTF-8"));
			sb.append("&");
			sb.append(URLEncoder.encode("data", "UTF-8"));
			sb.append("=");
			sb.append(xml);
			
			doPOST(urlAddItem, sb.toString(), "application/x-www-form-urlencoded");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e); 
			throw new MeshException(e);
		}
	}

	public ISchema getSchema() {
		ISchema schema= null;
		String schemaXML = getSchema(this.url);
		if(schemaXML != null && schemaXML.length() != 0){
			if(schemaXML.trim().equals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")){
				return null;
			}
			
			if(schemaXML.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><rdf") || schemaXML.startsWith("<rdf")){
				StringReader xmlReader = new StringReader(schemaXML);
				schema =  new RDFSchema(xmlReader);
			} else {
				schema = new Schema(XMLHelper.parseElement(schemaXML));
			}
		}
		return schema;
	}
}
