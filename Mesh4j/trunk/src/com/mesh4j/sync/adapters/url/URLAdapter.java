package com.mesh4j.sync.adapters.url;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;

// TODO (JMT) This class is a spike
public class URLAdapter implements IRepositoryAdapter {

	private final static Log Logger = LogFactory.getLog(URLAdapter.class);
	private final static NullFilter<Item> NULL_FILTER = new NullFilter<Item>();
	private final static ConflictsFilter CONFLICTS_FILTER = new ConflictsFilter();
	
	// MODEL VARIABLEs
	private String baseUrl;
	private URL url;
	private FeedReader feedReader;
	private FeedWriter feedWriter;
	
	// BUSINESS METHODS
	public URLAdapter(String url, ISyndicationFormat syndicationFormat, ISecurity security) throws MalformedURLException {
		Guard.argumentNotNullOrEmptyString(url, "url");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(security, "security");
		this.baseUrl = url;
		this.url = new URL(url);
		this.feedReader = new FeedReader(syndicationFormat, security);
		this.feedWriter = new FeedWriter(syndicationFormat, security);

	}

	@Override
	public boolean supportsMerge() {
		return true;
	}
	
	@Override
	public List<Item> merge(List<Item> items) {
		Feed feed = new Feed(items);
		Document document = DocumentHelper.createDocument();
		try {
			feedWriter.write(document, feed);
			String xml = document.asXML();
			this.POST(xml);

		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);  // TODO (JMT) throws runtime exception
		}
		return new ArrayList<Item>();		  // TODO (JMT) obtain post result and translate to items
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
		Feed feed = this.readFeed();
		ArrayList<Item> result = new ArrayList<Item>();
		if(feed != null){
			for (Item item : feed.getItems()) {
				boolean dateOk = since == null || since.compareTo(item.getSync().getLastUpdate().getWhen()) <= 0; 
				if(filter.applies(item) && dateOk){
					result.add(item);
				}
			}
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
	
	private Feed readFeed() {
		Feed feed = null;
		try {
			feed = feedReader.read(this.url);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);	// TODO (JMT) throws runtime exception
		}
		return feed;
	}
	
	private String GET(String queryString){
		String result = null;
		URLConnection conn = null;
	    try{
	    	URL newUrl = new URL(this.baseUrl + queryString);
			conn = newUrl.openConnection();
	
			InputStream is = conn.getInputStream();
	
			StringBuffer putBackTogether = new StringBuffer();
			Reader r = new InputStreamReader(is, "UTF-8");
			char[] cb = new char[2048];
	
			int amtRead = r.read(cb);
			while (amtRead > 0) {
				putBackTogether.append(cb, 0, amtRead);
				amtRead = r.read(cb);
			}
			result = putBackTogether.toString();
	    } catch(Exception e){
	    	Logger.error(e.getMessage(), e);	// TODO (JMT) throws runtime exception
	    }		
		return result;
	}
	
	private void POST(String content){
	    HttpURLConnection conn = null;
	    int status = 0;
	    try{
	    	conn = (HttpURLConnection) url.openConnection();
		    String charEncoding = "UTF-8";		    
		    conn.setDoOutput(true);
		    conn.setRequestMethod("POST");
		    conn.setRequestProperty("Content-Length", Integer.toString(content.length()));
		    conn.setRequestProperty("Content-Type", "text/xml");
		    conn.getOutputStream().write(content.getBytes(charEncoding));
		    conn.connect();
		    status = conn.getResponseCode();	// TODO (JMT) analize status code for example 200=OK, 401/404 errors
	    } catch(Exception e){
	    	Logger.error(e.getMessage(), e);	// TODO (JMT) throws runtime exception
	    } finally{
	    	if(conn != null){
	    		conn.disconnect();
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
