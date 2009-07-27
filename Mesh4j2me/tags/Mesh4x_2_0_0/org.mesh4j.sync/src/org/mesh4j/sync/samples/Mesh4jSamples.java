package org.mesh4j.sync.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.Base64;

import de.enough.polish.util.ZipUtil;
import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;

public class Mesh4jSamples {

	public static final String DEFAUT_PLACEMARK = "<Placemark xmlns=\"http://earth.google.com/kml/2.2\"><name>my favorite placemark</name><visibility>0</visibility><LookAt><longitude>-95.26548319399998</longitude><latitude>38.95938957099998</latitude><altitude>0</altitude><range>6000264.254089176</range><tilt>0</tilt><heading>-9.382636310317375e-014</heading></LookAt><styleUrl>#msn_ylw-pushpin</styleUrl><Point><coordinates>-95.265483194,38.95938957099998,0</coordinates></Point></Placemark>";

	public static String synchronizeInMemory() {
		StringBuffer result = new StringBuffer();
		result.append("Start synchronization: ");
		result.append(new Date());
		try {
			Vector<Item> itemsA = new Vector<Item>();

			String id = "1";
			IContent content = new XMLContent(id, "title", "desc", "", DEFAUT_PLACEMARK);
			Sync sync = new Sync(id, "jmt", new Date(), false);
			Item item = new Item(content, sync);

			itemsA.addElement(item);

			Vector<Item> itemsB = new Vector<Item>();

			InMemorySyncAdapter source = new InMemorySyncAdapter("Adapter A",
					NullIdentityProvider.INSTANCE, itemsA);
			InMemorySyncAdapter target = new InMemorySyncAdapter("Adapter B",
					NullIdentityProvider.INSTANCE, itemsB);

			SyncEngine syncEngine = new SyncEngine(source, target);
			Vector<Item> conflicts = syncEngine.synchronize();

			if (conflicts.isEmpty()) {
				result.append("\n\nNo Conflicts.");
			} else {
				result.append("\n\nConflicts: ");
				for (Item conflictItem : conflicts) {
					asString(result, conflictItem);
				}
			}

			result.append("\n\nSource: ");
			for (Item sourceItem : source.getAll()) {
				asString(result, sourceItem);
			}

			result.append("\n\nTarget: ");
			for (Item targetItem : target.getAll()) {
				asString(result, targetItem);
			}

			result.append("\n\nEnd synchronization: ");
			result.append(new Date());
			return result.toString();
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static void asString(StringBuffer sb, Item item) {
		sb.append("\n Item syncId:");
		sb.append(item.getSyncId());
		sb.append("\n  deleted: ");
		sb.append(item.isDeleted());
		sb.append("\n  has conflicts: ");
		sb.append(item.hasSyncConflicts());
		sb.append("\n  payload: ");
		sb.append(new String(item.getContent().getPayload()));
		sb.append("\n  last update:");
		sb.append("\n     by:");
		sb.append(item.getLastUpdate().getBy());
		sb.append("\n     sequence:");
		sb.append(item.getLastUpdate().getSequence());
		sb.append("\n     when:");
		sb.append(item.getLastUpdate().getWhen());
	}

	public static String readXML(InputStream is) throws IOException {
		XmlDomNode root = XmlDomParser.parseTree(is);

		StringBuffer sb = new StringBuffer();
		asXml(root, 0, sb);
		return sb.toString();
	}

	private static void asXml(XmlDomNode root, int level, StringBuffer sb) {
		sb.append("\n");

		int localLevel = level + 1;
		for (int i = 0; i < localLevel; i++) {
			sb.append("-");
		}
		sb.append(root.getName());
		sb.append(" ");

		if (root.getAttributes() != null) {
			Enumeration attrs = root.getAttributes().keys();
			while (attrs.hasMoreElements()) {
				String attr = (String) attrs.nextElement();
				sb.append("<");
				sb.append(attr);
				sb.append("=");
				sb.append(root.getAttribute(attr));
				sb.append(">");
			}
		}

		if (root.getText() != null && root.getText().trim().length() > 0) {
			sb.append("<text=");
			sb.append(root.getText());
			sb.append(">");
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			XmlDomNode node = root.getChild(i);
			asXml(node, localLevel, sb);
		}
	}

	public static String readText(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		int chr = 0;
		while ((chr = is.read()) != -1) {
			sb.append((char) chr);
		}
		String s = sb.toString();
		return s;

	}

	public static String encodeMessage(String message) throws IOException {
		byte[] data = message.getBytes();
		byte[] compressedData = ZipUtil.compress(data);
		byte[] encodedData = Base64.encode(compressedData);
		String encodedString = new String(encodedData);
		return encodedString;
	}

	public static String decodeMessage(String message) throws IOException {
		byte[] encodedData = message.getBytes();
		byte[] decodedData = Base64.decode(encodedData);
		byte[] decompressedData = ZipUtil.decompress(decodedData);
		String decodedString = new String(decompressedData);
		return decodedString;
	}

	public static String readRssFeed(InputStream is) {
		try {
			Reader reader = new InputStreamReader(is);

			FeedReader feedReader = new FeedReader(
					RssSyndicationFormat.INSTANCE,
					NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
			Feed feed = feedReader.read(reader);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(baos);

			FeedWriter feedWriter = new FeedWriter(
					RssSyndicationFormat.INSTANCE,
					NullIdentityProvider.INSTANCE);
			feedWriter.write(writer, feed);

			writer.flush();

			writer.close();
			baos.close();
			return new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public static String readAtomFeed(InputStream is){
		try {
			Reader reader = new InputStreamReader(is);

			FeedReader feedReader = new FeedReader(
					AtomSyndicationFormat.INSTANCE,
					NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
			Feed feed = feedReader.read(reader);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(baos);

			FeedWriter feedWriter = new FeedWriter(
					AtomSyndicationFormat.INSTANCE,
					NullIdentityProvider.INSTANCE);
			feedWriter.write(writer, feed);

			writer.flush();

			writer.close();
			baos.close();
			return new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

}
