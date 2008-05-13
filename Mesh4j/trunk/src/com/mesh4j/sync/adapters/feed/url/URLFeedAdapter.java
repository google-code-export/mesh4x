package com.mesh4j.sync.adapters.feed.url;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.IRepositoryAdapter;
import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.filter.ConflictsFilter;
import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

// TODO (JMT) Rename to HttpFeedAdapter
public class URLFeedAdapter implements IRepositoryAdapter {

	private final static Log Logger = LogFactory.getLog(URLFeedAdapter.class);
	private final static NullFilter<Item> NULL_FILTER = new NullFilter<Item>();
	private final static ConflictsFilter CONFLICTS_FILTER = new ConflictsFilter();
	
	// MODEL VARIABLEs
	private URL url;
	private FeedReader feedReader;
	private FeedWriter feedWriter;
	
	// BUSINESS METHODS
	public URLFeedAdapter(String url, ISyndicationFormat syndicationFormat, ISecurity security){
		Guard.argumentNotNullOrEmptyString(url, "url");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(security, "security");
		
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new MeshException(e);
		}
		this.feedReader = new FeedReader(syndicationFormat, security);
		this.feedWriter = new FeedWriter(syndicationFormat, security);
	}

	@Override
	public boolean supportsMerge() {
		return true;
	}
	
	@Override
	public List<Item> merge(List<Item> items) {
		try {
			Feed feed = new Feed(items);
			Document document = DocumentHelper.createDocument();
			feedWriter.write(document, feed);
			String xml = document.asXML();
			
			String result = this.POST(xml);
			
			Document documentResult = DocumentHelper.parseText(result);
			feed = feedReader.read(documentResult);
			return feed.getItems();
		} catch (DocumentException e) {
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
			Feed feed = null;
			if(since == null){
				feed = feedReader.read(this.url);
			} else {
				String xml = GETSince(since);
				if(xml == null){
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
		} catch (DocumentException e) {
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
	
	protected String GETSince(Date since){
		String result = null;
		HttpURLConnection conn = null;
	    try{
			conn = (HttpURLConnection) this.url.openConnection();
			conn.setIfModifiedSince(since.getTime());
			result = readData(conn);
	    } catch(Exception e){
			if(conn != null){
				try {
					int responseCode = conn.getResponseCode();
					if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
						return null;
					}
				} catch (IOException e1) {
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

	private String readData(HttpURLConnection conn) throws UnsupportedEncodingException, IOException {
		InputStream is = conn.getInputStream();		
		StringBuffer result = new StringBuffer();
		Reader reader = new InputStreamReader(is, "UTF-8");
		char[] cb = new char[2048];

		int amtRead = reader.read(cb);
		while (amtRead > 0) {
			result.append(cb, 0, amtRead);
			amtRead = reader.read(cb);
		}
		reader.close();
		return result.toString();
	}
	
	private String POST(String content){
	    HttpURLConnection conn = null;
	    String result = null;
	    try{
	    	conn = (HttpURLConnection) this.url.openConnection();
		    writeData(content, conn);		    
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

	private void writeData(String content, HttpURLConnection conn) throws Exception {
	    conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Length", Integer.toString(content.length()));
		conn.setRequestProperty("Content-Type", "text/xml");
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
}