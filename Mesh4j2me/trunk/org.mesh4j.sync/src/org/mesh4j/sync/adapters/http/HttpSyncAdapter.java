package org.mesh4j.sync.adapters.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.filter.ConflictsFilter;
import org.mesh4j.sync.filter.NullFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class HttpSyncAdapter implements ISyncAdapter, ISupportMerge {

	private final static NullFilter<Item> NULL_FILTER = new NullFilter<Item>();
	private final static ConflictsFilter CONFLICTS_FILTER = new ConflictsFilter();

	// MODEL VARIABLEs
	private String url;
	private FeedReader feedReader;
	private FeedWriter feedWriter;

	// BUSINESS METHODS
	public HttpSyncAdapter(String url, ISyndicationFormat syndicationFormat,
			IIdentityProvider identityProvider, IdGenerator idGenerator) {
		Guard.argumentNotNullOrEmptyString(url, "url");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");

		this.url = url;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider,
				idGenerator);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider);
	}

	public Vector<Item> merge(Vector<Item> items) {

		try {
			Feed feed = new Feed("", "", "", items);
			String xml = feedWriter.writeAsXml(feed);
System.out.println("POST: " + xml);
			byte[] result = this.doPOST(xml);
			String resultString = new String(result);
System.out.println("RESULT: " + resultString);
			if(resultString == null || resultString.length() == 0){
				return new Vector<Item>();
			} else {
				feed = feedReader.read(result);
				return feed.getItems();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	protected Vector<Item> getAll(Date since, IFilter<Item> filter) {

		try {
			Vector<Item> result = new Vector<Item>();
			byte[] data = doGET(since);
System.out.println("GET: " + new String(data));
			Feed feed = feedReader.read(data);
			if (feed != null) {
				for (Item item : feed.getItems()) {
					boolean dateOk = SinceLastUpdateFilter.applies(item, since);
					if (filter.applies(item) && dateOk) {
						result.addElement(item);
					}
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public Vector<Item> getAll() {
		return getAllSince(null, NULL_FILTER);
	}

	public Vector<Item> getAll(IFilter<Item> filter) {
		return getAllSince(null, filter);
	}

	public Vector<Item> getAllSince(Date since) {
		return getAllSince(since, NULL_FILTER);
	}

	public Vector<Item> getAllSince(Date since, IFilter<Item> filter) {
		Guard.argumentNotNull(filter, "filter");
		return getAll(since == null ? since : DateHelper.normalize(since),
				filter);
	}

	public Vector<Item> getConflicts() {
		return getAllSince(null, CONFLICTS_FILTER);
	}

	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}

	public byte[] doGET(Date since) {
		HttpConnection hc = null;
		InputStream in = null;
		try {
			String url = this.url;
			hc = (HttpConnection) Connector.open(url);
			hc.setRequestMethod(HttpConnection.GET);
			//hc.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
			//hc.setRequestProperty("Content-Language", "en-US");

			if (since != null) {
				hc.setRequestProperty("If-Modified-Since", DateHelper
						.formatDateYYYYMMDDHHMMSS(since, "/", " ", null, null));
			}

			in = hc.openInputStream();
			return readData(in);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
				if (hc != null)
					hc.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public byte[] doPOST(String content) {
		HttpConnection hc = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			String url = this.url;
			hc = (HttpConnection) Connector.open(url);

			hc.setRequestMethod(HttpConnection.POST);
			//hc.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
			//hc.setRequestProperty("Content-Language", "en-US");
			hc.setRequestProperty("Content-Type", "text/xml");
			hc.setRequestProperty("Content-Length", Integer.toString(content.length()));
			
			out = hc.openOutputStream();
			out.write(content.getBytes());

			in = hc.openInputStream();
			return readData(in);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (hc != null)
					hc.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private byte[] readData(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);		
		Reader reader = null;
		
		try{
			reader = new InputStreamReader(is, "UTF-8");
			
			char[] cb = new char[2048];
	
			int amtRead = reader.read(cb);
			while (amtRead > 0) {
				writer.write(cb, 0, amtRead);
				amtRead = reader.read(cb);
			}
			writer.flush();
			baos.flush();
			return baos.toByteArray();
		}finally{
			writer.close();
			baos.close();
			if(reader != null){reader.close();}
		}
		
	}
	
	
	// NOT SUPPORTED

	public void add(Item item) {
		Guard.throwsException("UnsupportedOperation");
	}

	public void delete(String id) {
		Guard.throwsException("UnsupportedOperation");
	}

	public Item get(String id) {
		Guard.throwsException("UnsupportedOperation");
		return null;
	}

	public void update(Item item) {
		Guard.throwsException("UnsupportedOperation");
	}

	public void update(Item item, boolean resolveConflicts) {
		Guard.throwsException("UnsupportedOperation");
	}
}
