package org.mesh4j.meshes.server;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


public class MeshServer {
	
	private String baseUrl;
	
	public MeshServer() {
		this("http://mesh.instedd.org");
	}
	
	public MeshServer(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@SuppressWarnings("unchecked")
	public String[] getMeshNames() {
		try {
			SyndFeed feed = readFeed(baseUrl + "/feeds");
			List entries = feed.getEntries();
			String[] names = new String[entries.size()];
			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = (SyndEntry) entries.get(i);
				names[i] = entry.getTitle();
			}
			return names;
		} catch (Exception e) {
			return new String[0];
		}
	}
	
	private SyndFeed readFeed(String urlString) throws Exception {
		URLConnection conn = null;
		URL url = new URL(urlString);
		try {
			conn = url.openConnection();
		
			SyndFeedInput input = new SyndFeedInput();
			return input.build(new XmlReader(conn.getInputStream()));
		} finally {
			if (conn != null && (conn instanceof HttpURLConnection)) {
				((HttpURLConnection)conn).disconnect();
			}
		}
	}

}
