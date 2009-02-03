package org.mesh4j.geo.coder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class GoogleGeoCoder implements IGeoCoder {

	private static String GOOGLE_URL = "http://maps.google.com/maps/geo?q={0}&output=csv&key={1}";

	// MODEL VARIABLES
	private String googleKey;

	// BUSINESS METHODS
	public GoogleGeoCoder(String googleKey) {
		Guard.argumentNotNullOrEmptyString(googleKey, "googleKey");
		this.googleKey = googleKey;
	}

	public GeoLocation getLocation(String address) {
		String url = MessageFormat.format(GOOGLE_URL, address, googleKey);
		String data = doGET(url);
		if(data != null){
			String[] parts = data.split(",");
			if ("200".equals(parts[0])) {
				return GeoLocation.parse(address, parts[2], parts[3]);
			}
		} 
		return null;
	}

	protected String doGET(String urlString) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString.replace(" ", "%20"));
			conn = (HttpURLConnection) url.openConnection();
			result = readData(conn);
		} catch (Exception e) {
			if (conn != null) {
				try {
					int responseCode = conn.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
						return null;
					}
				} catch (IOException e1) {
					throw new MeshException(e);
				}
			} else {
				throw new MeshException(e);
			}
		}
		return result;
	}

	private String readData(HttpURLConnection conn) throws Exception {
		InputStream is = null;

		try {
			is = conn.getInputStream();
		} catch (Exception e) {
			StringBuffer result = new StringBuffer();
			Reader reader = new InputStreamReader(conn.getErrorStream());
			char[] cb = new char[2048];

			int amtRead = reader.read(cb);
			while (amtRead > 0) {
				result.append(cb, 0, amtRead);
				amtRead = reader.read(cb);
			}
			reader.close();
			throw e;
		}

		StringBuffer result = new StringBuffer();
		Reader reader = new InputStreamReader(is);
		char[] cb = new char[2048];

		int amtRead = reader.read(cb);
		while (amtRead > 0) {
			result.append(cb, 0, amtRead);
			amtRead = reader.read(cb);
		}
		reader.close();
		return result.toString();
	}

}
