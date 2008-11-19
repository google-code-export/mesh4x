package org.mesh4j.sync.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.web.FeedRepositoryFactory;
import org.mesh4j.sync.web.IFeedRepository;

public class FeedServlet extends HttpServlet {

	private static final long serialVersionUID = 8932466869419169112L;
	
	// MODEL VARIABLES
	private IFeedRepository feedRepository;
	
	// BUSINESS METHODS

	public FeedServlet(){
		super();
	}

	public void init() throws ServletException{
		this.log("Mesh4x is starting up.....");
		super.init();		
		this.feedRepository = FeedRepositoryFactory.createFeedRepository(this); 
		this.log("Mesh4x is running.....");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sourceID = this.getSourceID(request);
		if(sourceID != null && !this.feedRepository.existsFeed(sourceID)){  // sourceID == null ==> Get all feeds
			response.sendError(404, sourceID);
		} else {
			String format = request.getParameter("format");     // format=rss20/atom10
			ISyndicationFormat syndicationFormat = this.feedRepository.getSyndicationFormat(format);
			if(syndicationFormat == null){
				response.sendError(404, format);
			} else {

				Date sinceDate = this.getSinceDate(request);
				String link = getLink(request, sourceID);
				String responseContent = this.feedRepository.readFeed(sourceID, link, sinceDate, syndicationFormat);
				responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) issue from XFROMS (MIDP demo)
				responseContent = responseContent.replaceAll("&gt;", ">");
				
				response.setContentType("text/plain");
				response.setContentLength(responseContent.length());
				PrintWriter out = response.getWriter();
				out.println(responseContent);
			}
		}
	}

	private String getLink(HttpServletRequest request, String sourceID) {
		if(sourceID == null){
			return request.getRequestURI();
		} else {
			return request.getRequestURI()+ "/" + sourceID;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String sourceID = this.getSourceID(request);				
		if(this.feedRepository.isAddNewFeedAction(sourceID)){
			addNewFeed(request, response);
		} else {
			synchronizeFeed(request, response, sourceID);
		}
	}

	private void synchronizeFeed(HttpServletRequest request, HttpServletResponse response, String sourceID) throws IOException {
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			String format = request.getParameter("format");  		// format=rss20/atom10
			ISyndicationFormat syndicationFormat = this.feedRepository.getSyndicationFormat(format);	
			if(syndicationFormat == null){
				response.sendError(404, format);	
			} else {
				String feedXml = this.readXML(request);
				if(feedXml == null){
					response.sendError(404, sourceID);
				} else {
					String link = getLink(request, sourceID);
					String responseContent = this.feedRepository.synchronize(sourceID, link, feedXml, syndicationFormat);
					
					response.setContentType("text/plain");
					response.setContentLength(responseContent.length());
					PrintWriter out = response.getWriter();
					out.println(responseContent);
				}
			}
		}
	}

	private void addNewFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String newSourceID = request.getParameter("newSourceID");
		if(this.feedRepository.existsFeed(newSourceID)){
			response.sendError(404, newSourceID);
		} else {
			String format = request.getParameter("format");
			String description = request.getParameter("description");
			String schema = request.getParameter("schema");
			
			ISyndicationFormat syndicationFormat = this.feedRepository.getSyndicationFormat(format);	
			if(syndicationFormat == null){
				response.sendError(404, format);	
			}
			
			String link = getLink(request, newSourceID);
			this.feedRepository.addNewFeed(newSourceID, syndicationFormat, link, description, schema);
			
			response.sendRedirect(request.getRequestURI()+"/"+newSourceID);
		}
	}

	private String readXML(HttpServletRequest request) throws IOException{
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
		} finally {
			if (reader != null){
				reader.close();
			}
		}		
	}

	private String getSourceID(HttpServletRequest request) {
		
		String servletPath = request.getServletPath();
		String requestUri = request.getRequestURI();
		
		int index = requestUri.indexOf(servletPath);
		if(index + servletPath.length() + 1 <= requestUri.length()){
			String sourceID = requestUri.substring(index + servletPath.length() + 1, requestUri.length());
			if(!sourceID.isEmpty()){
				return sourceID;	
			}
		} 
		return null;
	}
	
	private Date getSinceDate(HttpServletRequest request){
		String modifiedSince = request.getHeader("If-Modified-Since");
		if(modifiedSince != null && modifiedSince.trim().length() > 0){
			return DateHelper.parseDateYYYYMMDDHHMMSS(modifiedSince, TimeZone.getTimeZone("GMT"));
		} else {
			return null;
		}
	}
}
