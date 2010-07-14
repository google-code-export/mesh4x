package org.mesh4j.meshes.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class MeshServer {

	private String baseUrl;
	
	private static MeshServer instance = new MeshServer();

	private MeshServer() {
		this("https://mesh.instedd.org");
	}

	private MeshServer(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public static MeshServer getInstance() {
		return instance;
	}

	public void createMesh(Mesh mesh) {
		post(baseUrl + "/feeds", 
				"action=uploadMeshDefinition"
				+ "&by=" + encode(LoggedInIdentityProvider.getUserName())
				+ "&newSourceID=" + encode(mesh.getName())
				+ "&title=" + encode(mesh.getName())
				+ "&description=" + encode(mesh.getDescription())
				+ "&format=rss20");
		
		for(DataSet dataSet : mesh.getDataSets()) {
			createFeed(mesh, dataSet);
		}
	}
	
	private void createFeed(Mesh mesh, DataSet dataSet) {
		post(baseUrl + "/feeds/" + mesh.getName(), 
				"action=uploadMeshDefinition"
				+ "&by=" + encode(LoggedInIdentityProvider.getUserName())
				+ "&newSourceID=" + encode(mesh.getName() + "/" + dataSet.getName())
				+ "&title=" + encode(dataSet.getName())
				+ "&description=" + encode(mesh.getDescription())
				+ "&format=rss20");
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
			throw new MeshException(e);
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
				((HttpURLConnection) conn).disconnect();
			}
		}
	}
	
	private void post(String urlString, String data) {
		try {
			HttpURLConnection conn = null;
			URL url = new URL(baseUrl + "/feeds");
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);

				OutputStreamWriter out = new OutputStreamWriter(conn
						.getOutputStream());
				out.write(data);
				out.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while (in.readLine() != null) {
				}
				in.close();
			} finally {
				if (conn != null && (conn instanceof HttpURLConnection)) {
					((HttpURLConnection) conn).disconnect();
				}
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private static String encode(String data) {
		try {
			if (data == null) return "";
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return data;
		}
	}

}
