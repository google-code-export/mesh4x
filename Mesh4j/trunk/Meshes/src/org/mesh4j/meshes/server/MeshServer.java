package org.mesh4j.meshes.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.validations.MeshException;

import sun.misc.BASE64Encoder;

public class MeshServer implements IMeshServer {

	private String baseUrl;
	
	private static IMeshServer instance = new MeshServer();

	private MeshServer() {
		this("https://mesh.instedd.org");
		//this("http://localhost:3000");
	}

	private MeshServer(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public static IMeshServer getInstance() {
		return instance;
	}
	
	public boolean areValid(String email, String password) {
		try {
			get("/accounts/verify", email, password);
			return true;
		} catch (MeshException e) {
			return false;
		}
	}
	
	@Override
	public boolean createAccount(String email, String password) {
		try {
			get("/accounts/create?email=" + encode(email) + "&password=" + encode(password));
			return true;
		} catch (MeshException e) {
			return false;
		}
	}

	public void createMesh(Mesh mesh, String email, String password) {
		createMesh(mesh.getName(), email, password);
		
		for(DataSet dataSet : mesh.getDataSets()) {
			String secretUrl = createFeed(mesh.getName(), dataSet.getName(), email, password);
			dataSet.setServerFeedUrl(secretUrl);
		}
	}
	
	private void createMesh(String meshName, String email, String password) {
		post("/meshes/" + encode(meshName), email, password);
	}
	
	private String createFeed(String meshName, String feedName, String email, String password) {
		return post("/meshes/" + encode(meshName) + "/feeds/" + encode(feedName), email, password).trim();
	}
	
	@Override
	public boolean meshExists(String meshName, String email, String password) {
		try {
			get("/meshes/" + encode(meshName), email, password);
			return true;
		} catch (MeshException e) {
			return false;
		}
	}
	
	private String get(String urlString) {
		return get(urlString, null, null);
	}
	
	private String get(String urlString, String email, String password) {
		return method("GET", urlString, email, password);
	}
	
	private String post(String urlString, String email, String password) {
		return method("POST", urlString, email, password);
	}
	
	private String method(String verb, String urlString, String email, String password) {
		try {
			HttpURLConnection conn = null;
			URL url = new URL(baseUrl + urlString);
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(verb);
				if (email != null && password != null) {
					conn.setRequestProperty("Authorization", "Basic " + new BASE64Encoder().encode((email + ":" + password).getBytes()));
				}

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					response.append(line).append("\n");
				}
				in.close();
				return response.toString();
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
