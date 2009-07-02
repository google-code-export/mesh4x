package org.mesh4j.sync.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.rms.storage.RmsStorage;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageContentAdapter;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageContentWithSyncAdapter;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageSyncRepository;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;

public class Mesh4jSamplesHttpAdapter {

	public static final String DEFAULT_URL_ATOM = "http://vcc-pc:8080/Mesh4j/atomFeed";
	public static final String DEFAULT_URL_RSS = "http://vcc-pc:8080/Mesh4j/rssFeed";
	public static final String DEFAULT_URL = "http://sync.instedd.org/Service.svc/feeds/XFormDemo2";

	public static String synchronizeRMSStorageAndHttpAdapterAtom() {
		return synchronize(DEFAULT_URL_ATOM, AtomSyndicationFormat.INSTANCE, AtomSyndicationFormat.INSTANCE.getName(), true);
	}
	
	public static String synchronizeRMSStorageAndHttpAdapterRss() {
		return synchronize(DEFAULT_URL_RSS, RssSyndicationFormat.INSTANCE, RssSyndicationFormat.INSTANCE.getName(), true);
	}
	
	public static String synchronizeInstedd() {
		return synchronize(DEFAULT_URL, RssSyndicationFormat.INSTANCE, "Instedd", false);
	}

	private static String synchronize(String url, ISyndicationFormat syndicationFormat, String name, boolean mustCreateItems) {
		RmsStorageContentAdapter contentAdapter = new RmsStorageContentAdapter("contentHttp"+name);
		contentAdapter.deleteAll();
		String result = synchronize(url, syndicationFormat, name, mustCreateItems, contentAdapter);
		contentAdapter.deleteRecordStorage();
		return result;
	}
	
	private static String synchronize(String url, ISyndicationFormat syndicationFormat, String name, boolean mustCreateItems, IContentAdapter contentAdapter) {
		RmsStorageSyncRepository syncRepo = new RmsStorageSyncRepository("syncInfoHttp"+name, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		syncRepo.deleteAll();
		
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, NullIdentityProvider.INSTANCE);
		
		if(mustCreateItems){
			splitAdapter.add(makeNewItem());
			splitAdapter.add(makeNewItem());
			splitAdapter.add(makeNewItem());
		}
		
		HttpSyncAdapter httpAdapter = new HttpSyncAdapter(url, syndicationFormat, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapter, httpAdapter);
		syncEngine.synchronize();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		FeedWriter feedWriter = new FeedWriter(syndicationFormat, NullIdentityProvider.INSTANCE);

		try {
			Feed feed = new Feed("", "", "", splitAdapter.getAll());

			writer.write("\n\nResult: ");
			writer.write(String.valueOf(feed.getItems().size()));
			writer.write("\n");
			feedWriter.write(writer, feed);

			writer.flush();
			baos.flush();

			syncRepo.deleteRecordStorage();

			return new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				writer.close();
				baos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public static String getAllRSS(){
		return getAll(DEFAULT_URL_RSS, RssSyndicationFormat.INSTANCE);
	}
	
	public static String getAllAtom(){
		return getAll(DEFAULT_URL_ATOM, AtomSyndicationFormat.INSTANCE);
	}

	private static String getAll(String url, ISyndicationFormat syndicationFormat) {
		HttpSyncAdapter adapter = new HttpSyncAdapter(url, syndicationFormat, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		FeedWriter feedWriter = new FeedWriter(syndicationFormat, NullIdentityProvider.INSTANCE);

		try {
			Feed feed = new Feed("", "", "", adapter.getAll());

			writer.write("\n\nHttp adapter: ");
			writer.write(String.valueOf(feed.getItems().size()));
			writer.write("\n");
			feedWriter.write(writer, feed);

			writer.flush();
			baos.flush();

			return new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				writer.close();
				baos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private static Item makeNewItem() {
		String syncID = IdGenerator.INSTANCE.newID();
		Content content = new XMLContent(syncID, "title", "desc", "", "<foo>bar" + syncID + "</foo>");
		
		Sync sync = new Sync(syncID, "jmt", new Date(), false);
		return new Item(content, sync);
	}

	public static String synchronizeRMSStorageWithSyncAndHttpAdapterRss() {
		String name = "contentandsync";
		RmsStorage contentStorage =  new RmsStorage(new PayloadObjectParser(), "contentHttp"+name);
		RmsStorageContentWithSyncAdapter contentAdapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		contentAdapter.deleteAll();
		String result = synchronize(DEFAULT_URL_RSS, RssSyndicationFormat.INSTANCE, RssSyndicationFormat.INSTANCE.getName(), true, contentAdapter);
		contentAdapter.deleteRecordStorage();
		return result;
	}
}
