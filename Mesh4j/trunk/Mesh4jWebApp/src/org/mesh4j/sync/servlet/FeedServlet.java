package org.mesh4j.sync.servlet;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.kml.exporter.KMLExporter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMappingResolver;
import org.mesh4j.sync.payload.schema.ISchemaResolver;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.web.FeedRepositoryFactory;
import org.mesh4j.sync.web.IFeedRepository;
import org.mesh4j.sync.web.geo.coder.GeoCoderFactory;

public class FeedServlet extends HttpServlet {

	private static final String USER_ADMIN = "admin";

	private static final String PARAM_SOURCE_ID = "sourceID";
	private static final String PARAM_DESCRIPTION = "description";
	private static final String PARAM_NEW_SOURCE_ID = "newSourceID";
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_FORMAT = "format";
	private static final String PARAM_MAPPINGS = "mappings";
	private static final String PARAM_SCHEMA = "schema";

	private static final String ACTION_DELETE = "delete";
	private static final String ACTION_UPLOAD_MESH_DEFINITION = "uploadMeshDefinition";
	private static final String ACTION_CLEAN = "clean";

	private static final String FORMAT_PLAIN = "plain";
	private static final String FORMAT_KML = "kml";

	private static final long serialVersionUID = 8932466869419169112L;
	
	// MODEL VARIABLES
	private IFeedRepository feedRepository;
	private IGeoCoder geoCoder;
	
	// BUSINESS METHODS

	public FeedServlet(){
		super();
	}

	public void init() throws ServletException{
		this.log("Mesh4x is starting up.....");
		
		super.init();		
		
		try{
			Properties prop = getProperties();

			this.feedRepository = FeedRepositoryFactory.createFeedRepository(this, prop); 
			this.geoCoder = GeoCoderFactory.createGeoCoder(this, prop);
		} catch(Exception e){
			this.log(e.getMessage());
			throw new ServletException(e);
		}
		this.log("Mesh4x is running.....");
	}

	private Properties getProperties() throws Exception {
		String fileName = this.getServletContext().getRealPath("/properties/mesh4j.properties");
		FileReader reader = new FileReader(fileName);
		Properties prop = new Properties();
		prop.load(reader);
		return prop;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sourceID = this.getSourceID(request);
		String link = getFeedLink(request);

		if(sourceID != null && sourceID.endsWith(PARAM_SCHEMA)){
			processGetSchema(response, sourceID, link);
		} else if(sourceID != null && sourceID.endsWith(PARAM_MAPPINGS)){
			processGetMappings(response, sourceID, link);
		} else {
			if(sourceID != null && !this.feedRepository.existsFeed(sourceID)){  // sourceID == null ==> Get all feeds
				response.sendError(404, sourceID);
			} else {
				String format = request.getParameter(PARAM_FORMAT);     // format=rss20/atom10/kml
				if(FORMAT_KML.equals(format)){					
					processGetKML(request, response, sourceID, link);
				}else {
					processGetFeed(request, response, sourceID, link, format);
				}
			}
		}
	}

	private String getFeedLink(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	private void processGetFeed(HttpServletRequest request, HttpServletResponse response, String sourceID, String link, String format) throws IOException {
		ISyndicationFormat syndicationFormat = this.feedRepository.getSyndicationFormat(format);
		if(syndicationFormat == null){
			response.sendError(404, format);
		} else {
			boolean plainMode = request.getParameter(FORMAT_PLAIN) != null;     // plain  ==> remove deleted items and sync information
			
			Date sinceDate = this.getSinceDate(request);
			String responseContent = this.feedRepository.readFeed(sourceID, link, sinceDate, syndicationFormat, plainMode);
			responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
			responseContent = responseContent.replaceAll("&gt;", ">");
			
			response.setContentType(syndicationFormat.getContentType());
			response.setContentLength(responseContent.length());
			PrintWriter out = response.getWriter();
			out.println(responseContent);
		}
	}

	private void processGetKML(HttpServletRequest request, HttpServletResponse response, String sourceID, String link)throws ServletException, IOException {
		Date sinceDate = this.getSinceDate(request);
		
		List<Item> items = this.feedRepository.getAll(sourceID, sinceDate);
		
		IMappingResolver mappingResolver;
		try {
			mappingResolver = this.feedRepository.getMappings(sourceID, link, this.geoCoder);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		String responseContent = KMLExporter.generateKML(sourceID, items, mappingResolver);
		
		response.setContentType("text/plain");
		response.setContentLength(responseContent.length());
		PrintWriter out = response.getWriter();
		out.println(responseContent);
	}

	private void processGetSchema(HttpServletResponse response, String sourceID, String link) throws IOException, ServletException {
		link = link.substring(0, link.length() - "/schema".length());
		sourceID = sourceID.substring(0, sourceID.length() - "/schema".length());

		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {
			ISchemaResolver propertyResolver;
			try {
				propertyResolver = this.feedRepository.getSchema(sourceID, link);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			String responseContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+ propertyResolver.getSchema().asXML();
			responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
			responseContent = responseContent.replaceAll("&gt;", ">");
			
			response.setContentType("text/plain");
			response.setContentLength(responseContent.length());
			PrintWriter out = response.getWriter();
			out.println(responseContent);
		}
	}
	
	private void processGetMappings(HttpServletResponse response, String sourceID, String link) throws IOException, ServletException {
		link = link.substring(0, link.length() - "/mappings".length());
		sourceID = sourceID.substring(0, sourceID.length() - "/mappings".length());

		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {
			IMappingResolver mappingsResolver;
			try {
				mappingsResolver = this.feedRepository.getMappings(sourceID, link, this.geoCoder);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			String responseContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+ mappingsResolver.getMappings().asXML();
			responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
			responseContent = responseContent.replaceAll("&gt;", ">");
			
			response.setContentType("text/plain");
			response.setContentLength(responseContent.length());
			PrintWriter out = response.getWriter();
			out.println(responseContent);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String action = request.getParameter(PARAM_ACTION);
		if(ACTION_CLEAN.equals(action)){
			cleanFeed(request, response);
		} else if (ACTION_UPLOAD_MESH_DEFINITION.equals(action)){
			uploadMeshDefinition(request, response);
		} else if(ACTION_DELETE.equals(action)){
			deleteFeed(request, response);
		}else {
			synchronizeFeed(request, response);
		}
	}
	
	private void cleanFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sourceID = request.getParameter(PARAM_SOURCE_ID);
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			this.feedRepository.cleanFeed(sourceID);
			String link = getFeedLink(request)+ "/" + sourceID;
			response.sendRedirect(link);
		}
	}
	
	private void deleteFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sourceID = request.getParameter(PARAM_SOURCE_ID);
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			String link = getFeedLink(request)+ "/" + sourceID;
			this.feedRepository.deleteFeed(sourceID, link, USER_ADMIN);
			response.sendRedirect(getFeedLink(request));
		}
	}

	private void synchronizeFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sourceID = this.getSourceID(request);
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			String format = request.getParameter(PARAM_FORMAT);  		// format=rss20/atom10
			ISyndicationFormat syndicationFormat = this.feedRepository.getSyndicationFormat(format);	
			if(syndicationFormat == null){
				response.sendError(404, format);	
			} else {
				String feedXml = this.readXML(request);
				if(feedXml == null){
					response.sendError(404, sourceID);
				} else {
					String link = getFeedLink(request);
					String responseContent = this.feedRepository.synchronize(sourceID, link, feedXml, syndicationFormat);

					response.setContentType(syndicationFormat.getContentType());
					response.setContentLength(responseContent.length());
					PrintWriter out = response.getWriter();
					out.println(responseContent);
				}
			}
		}
	}

	private void uploadMeshDefinition(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String newSourceID = request.getParameter(PARAM_NEW_SOURCE_ID);
		if(newSourceID == null){
			response.sendError(404, newSourceID);
		} else {
			String format = request.getParameter(PARAM_FORMAT);
			String description = request.getParameter(PARAM_DESCRIPTION);
			String schema = request.getParameter(PARAM_SCHEMA);
			String mappings = request.getParameter(PARAM_MAPPINGS);
			
			ISyndicationFormat syndicationFormat = this.feedRepository.getSyndicationFormat(format);	
			if(syndicationFormat == null){
				response.sendError(404, format);	
			}
			
			String link = getFeedLink(request)+ "/" + newSourceID;
			
			if(this.feedRepository.existsFeed(newSourceID)){
				this.feedRepository.updateFeed(newSourceID, syndicationFormat, link, description, schema, mappings, USER_ADMIN);
			} else {
				this.feedRepository.addNewFeed(newSourceID, syndicationFormat, link, description, schema, mappings, USER_ADMIN);
			}
			
			response.sendRedirect(link);
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