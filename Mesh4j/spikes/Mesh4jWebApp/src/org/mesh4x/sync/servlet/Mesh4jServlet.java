package org.mesh4x.sync.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;

/**
 * Servlet implementation class Mesh4jServlet
 */
public class Mesh4jServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// MODEL VARIABLES
	private FeedAdapter adapter;
	
	// BUSINESS METHODS

	public Mesh4jServlet() throws IOException {
		this.initializeFeed();
	}

	private void initializeFeed() throws IOException {
		String feedName = this.getClass().getResource("feed.xml").getFile();
		File file = new File(feedName);
		if(!file.exists()){
			file.createNewFile();
		}
		
		this.adapter = new FeedAdapter(file, AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		if(this.adapter.getAll().isEmpty()){
			this.adapter.add(makeNewItem());
			this.adapter.add(makeNewItem());
			this.adapter.add(makeNewItem());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String modifiedSince = request.getHeader("If-Modified-Since");
		Feed feed = null;
		if(modifiedSince == null || modifiedSince.trim().length() == 0){
			List<Item> items = this.adapter.getAll();
			feed = new Feed(items);
		} else {
			Date since = DateHelper.parseDateYYYYMMDDHHMMSS(modifiedSince, TimeZone.getTimeZone("GMT"));
			List<Item> items = this.adapter.getAllSince(since);
			feed = new Feed(items);
		}

		String message = null;
		try{
			message = this.adapter.getFeedWriter().writeAsXml(feed);
		} catch(Exception e){
			e.printStackTrace();
			message = "ERROR Get: " + modifiedSince;
		}
		
		response.setContentType("text/plain");
		response.setContentLength(message.length());
		PrintWriter out = response.getWriter();
		out.println(message);
	}

	private Item makeNewItem() {
		String syncID = this.adapter.getFeedReader().getIdGenereator().newID();
		
		Element element = XMLHelper.parseElement("<payload><foo>bar" + syncID + "</foo></payload>"); 
			
		XMLContent content = new XMLContent(syncID, syncID, syncID, element);
		Sync sync = new Sync(syncID, "jmt", new Date(), false);
		return new Item(content, sync);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String message;
		String xml = this.readXML(request);
		System.out.println(xml);
		if(xml != null){
			try{
				Feed feedLoaded = this.adapter.getFeedReader().read(xml);
				InMemorySyncAdapter inMemoryAdapter = new InMemorySyncAdapter("feed", NullIdentityProvider.INSTANCE, feedLoaded.getItems());
				
				SyncEngine syncEngine = new SyncEngine(this.adapter, inMemoryAdapter);
				List<Item> conflicts = syncEngine.synchronize();
				
				Feed feedResult = new Feed(conflicts);
				message = this.adapter.getFeedWriter().writeAsXml(feedResult);
			} catch(Exception e){
				e.printStackTrace();
				message = "ERROR feed";
			}
		} else {
			message = "ERROR http";
		}
		System.out.println(message);
		
		response.setContentType("text/plain");
		response.setContentLength(message.length());
		PrintWriter out = response.getWriter();
		out.println(message);
	}

	private String readXML(HttpServletRequest request) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(request.getInputStream());
			StringBuffer result = new StringBuffer();
			char[] cb = new char[2048];
			int amtRead = reader.read(cb);
			while (amtRead > 0) {
				result.append(cb, 0, amtRead);
				amtRead = reader.read(cb);
			}
			return result.toString();
		} catch(IOException e){
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}		
	}

}
