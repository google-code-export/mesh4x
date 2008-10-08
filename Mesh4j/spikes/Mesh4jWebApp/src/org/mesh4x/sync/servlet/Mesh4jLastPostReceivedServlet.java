package org.mesh4x.sync.servlet;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.security.NullIdentityProvider;

public class Mesh4jLastPostReceivedServlet  extends HttpServlet {

	private static final long serialVersionUID = 8793342127977346028L;

	private static FeedWriter feedWriter = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		FileReader reader = new FileReader("C:\\Clarius\\temp\\lastPost.xml");
		String message = readData(reader);	
		if(message == null){
			message = "ERROR";
		}
		
		System.out.println("GET RESPONSE: " + message);	
						
		response.setContentType("text/plain");
		response.setContentLength(message.length());
		PrintWriter out = response.getWriter();
		out.println(message);
	}

	private String readData(Reader reader) {
		try {
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

	public static void writeFeed(Feed feed) throws IOException, DocumentException {	
		String xml = "<payload><title>lastPost</title><description>lastPost</description><link>/feeds/lastPost</link></payload>";
		Element payload = DocumentHelper.parseText(xml).getRootElement();
		feed.setPayload(payload);
		
		XMLWriter writer = new XMLWriter(new FileWriter("C:\\Clarius\\temp\\lastPost.xml"), OutputFormat.createPrettyPrint());
		Mesh4jLastPostReceivedServlet.feedWriter.write(writer, feed);
	}

}
