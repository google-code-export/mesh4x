package org.mesh4x.sync.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;

/**
 * Servlet implementation class Mesh4jServlet
 */
public class Mesh4jServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// MODEL VARIABLES
	private FeedReader reader = new FeedReader(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	private FeedWriter writer = new FeedWriter(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
	private Feed feed = new Feed();
	private IdGenerator idGenerator = IdGenerator.INSTANCE;
	
	// BUSINESS METHODS

	public Mesh4jServlet() {
		this.initializeFeed();
	}

	private void initializeFeed() {
		if(this.feed.isEmpty()){
			this.feed.addItem(makeNewItem());
			this.feed.addItem(makeNewItem());
			this.feed.addItem(makeNewItem());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String modifiedSince = request.getHeader("If-Modified-Since");

		String message = "Get: " + modifiedSince;
		System.out.println(message);
		try{
			message = this.writer.writeAsXml(this.feed);
			System.out.println(message);
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
		String syncID = this.idGenerator.newID();
		
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
				Feed feedLoaded = this.reader.read(xml);
				message = this.writer.writeAsXml(feedLoaded);
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
