package org.mesh4j.sync.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.rms.storage.EntityContent;
import org.mesh4j.sync.adapters.rms.storage.RmsRecordContent;
import org.mesh4j.sync.adapters.rms.storage.RmsStorage;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageContentAdapter;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageContentWithSyncAdapter;
import org.mesh4j.sync.adapters.rms.storage.RmsStorageSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;

public class Mesh4jSamplesRMSStorage {
	
	public static String synchronizeRMSStorages() {
		RmsStorageSyncRepository syncRepo = new RmsStorageSyncRepository(
				"syncInfo1", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);
		syncRepo.deleteAll();

		RmsStorageContentAdapter contentAdapter = new RmsStorageContentAdapter(
				"example1");
		contentAdapter.deleteAll();

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter,
				NullIdentityProvider.INSTANCE);

		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());

		RmsStorageSyncRepository syncRepo2 = new RmsStorageSyncRepository(
				"syncInfo2", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);
		syncRepo2.deleteAll();

		RmsStorageContentAdapter contentAdapter2 = new RmsStorageContentAdapter(
				"example2");
		contentAdapter2.deleteAll();

		SplitAdapter splitAdapter2 = new SplitAdapter(syncRepo2,
				contentAdapter2, NullIdentityProvider.INSTANCE);

		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());

		SyncEngine syncEngine = new SyncEngine(splitAdapter, splitAdapter2);
		syncEngine.synchronize();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		FeedWriter feedWriter = new FeedWriter(AtomSyndicationFormat.INSTANCE,
				NullIdentityProvider.INSTANCE);

		try {
			Feed feed = new Feed("", "", "", splitAdapter.getAll());

			writer.write("\n\nAdapter 1: ");
			writer.write(String.valueOf(feed.getItems().size()));
			writer.write("\n");
			feedWriter.write(writer, feed);

			Feed feed2 = new Feed("", "", "", splitAdapter2.getAll());

			writer.write("\n\nAdapter 2: ");
			writer.write(String.valueOf(feed2.getItems().size()));
			writer.write("\n");
			feedWriter.write(writer, feed2);

			writer.flush();
			baos.flush();

			contentAdapter.deleteRecordStorage();
			syncRepo.deleteRecordStorage();

			contentAdapter2.deleteRecordStorage();
			syncRepo2.deleteRecordStorage();

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
		IContent content = new XMLContent(syncID, "title", "desc", "", "<foo>bar" + syncID + "</foo>");
		Sync sync = new Sync(syncID, "jmt", new Date(), false);
		return new Item(content, sync);
	}
	
	public static String rmsStorageSplitAdapter() {
		RmsStorageSyncRepository syncRepo = new RmsStorageSyncRepository(
				"syncInfo1", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);
		syncRepo.deleteAll();

		RmsStorageContentAdapter contentAdapter = new RmsStorageContentAdapter(
				"example5");
		contentAdapter.deleteAll();

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter,
				NullIdentityProvider.INSTANCE);

		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());

		Vector<Item> items = splitAdapter.getAll();

		contentAdapter.deleteRecordStorage();
		syncRepo.deleteRecordStorage();

		StringBuffer sb = new StringBuffer();
		Item itemLoaded = null;
		for (int i = 0; i < items.size(); i++) {
			itemLoaded = (Item) items.elementAt(i);
			sb.append(itemLoaded.getContent().getPayload());
		}
		return sb.toString();
	}

	public static String rmsStorageContent() {
		RmsStorageContentAdapter adapter = new RmsStorageContentAdapter(
				"example5");
		adapter.deleteAll();

		String syncId1 = IdGenerator.INSTANCE.newID();
		IContent content1 = new EntityContent("hola", "example5", syncId1);
		adapter.save(content1);

		String syncId2 = IdGenerator.INSTANCE.newID();
		IContent content2 = new EntityContent("hola2", "example5", syncId2);
		adapter.save(content2);

		IContent content11 = adapter.get(syncId1);
		IContent content21 = adapter.get(syncId2);

		Vector<IContent> contents = adapter.getAll(new Date());

		StringBuffer sb = new StringBuffer();
		sb.append(content11.getPayload());
		sb.append(content21.getPayload());

		IContent content = null;
		for (int i = 0; i < contents.size(); i++) {
			content = (IContent) contents.elementAt(i);
			sb.append(content.getPayload());
		}
		adapter.deleteRecordStorage();
		return sb.toString();
	}

	public static String rmsStorageSyncInfo() {

		RmsStorageSyncRepository syncRepo = new RmsStorageSyncRepository(
				"syncInfo2", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);

		syncRepo.deleteAll();

		String syncId1 = IdGenerator.INSTANCE.newID();
		Sync sync1 = new Sync(syncId1, "jmt", new Date(), false);
		SyncInfo syncInfo1 = new SyncInfo(sync1, "example", "145", 1234);
		syncRepo.save(syncInfo1);

		String syncId2 = IdGenerator.INSTANCE.newID();
		Sync sync2 = new Sync(syncId2, "tmj", new Date(), false);
		SyncInfo syncInfo2 = new SyncInfo(sync2, "example", "145", 1234);
		syncRepo.save(syncInfo2);

		SyncInfo syncInfoLoaded1 = syncRepo.get(syncId1);
		SyncInfo syncInfoLoaded2 = syncRepo.get(syncId2);

		Vector<SyncInfo> syncInfos = syncRepo.getAll("example");

		StringBuffer sb = new StringBuffer();
		sb.append(syncInfoLoaded1.getSyncId());
		sb.append(syncInfoLoaded2.getSyncId());

		SyncInfo syncInfo = null;
		for (int i = 0; i < syncInfos.size(); i++) {
			syncInfo = (SyncInfo) syncInfos.elementAt(i);
			sb.append(syncInfo.getSyncId());
		}

		syncRepo.deleteRecordStorage();
		return sb.toString();
	}

	public static String rmsStorageContentWithSync() {
		
		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "example6Sync");
		RmsStorageContentWithSyncAdapter adapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		adapter.deleteAll();

		String syncId1 = IdGenerator.INSTANCE.newID();
		IContent content1 = new RmsRecordContent("hola", syncId1, -1, "example6Sync");
		adapter.save(content1);

		String syncId2 = IdGenerator.INSTANCE.newID();
		IContent content2 =  new RmsRecordContent("hola2", syncId2, -1, "example6Sync");
		adapter.save(content2);

		IContent content11 = adapter.get(syncId1);

		IContent content21 = adapter.get(syncId2);
		
		Vector<IContent> contents = adapter.getAll(null);
		
		StringBuffer sb = new StringBuffer();
		sb.append(content11.getPayload());
		sb.append(content21.getPayload());
		
		IContent content = null;
		for (int i = 0; i < contents.size(); i++) {
			content = (IContent) contents.elementAt(i);
			sb.append(content.getPayload());
		}
		adapter.deleteRecordStorage();

		return sb.toString();
	}

	public static String synchronizeRMSStorageContentWithSync() {
		RmsStorageSyncRepository syncRepo = new RmsStorageSyncRepository(
				"example1SyncInfo", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);
		syncRepo.deleteAll();

		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "example7ContentAndSync");
		RmsStorageContentWithSyncAdapter contentAdapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		contentAdapter.deleteAll();

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter,
				NullIdentityProvider.INSTANCE);

		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());
		splitAdapter.add(makeNewItem());

		RmsStorageSyncRepository syncRepo2 = new RmsStorageSyncRepository(
				"syncInfo2", NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE);
		syncRepo2.deleteAll();

		RmsStorageContentAdapter contentAdapter2 = new RmsStorageContentAdapter(
				"example2");
		contentAdapter2.deleteAll();

		SplitAdapter splitAdapter2 = new SplitAdapter(syncRepo2,
				contentAdapter2, NullIdentityProvider.INSTANCE);

		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());
		splitAdapter2.add(makeNewItem());

		SyncEngine syncEngine = new SyncEngine(splitAdapter, splitAdapter2);
		syncEngine.synchronize();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		FeedWriter feedWriter = new FeedWriter(AtomSyndicationFormat.INSTANCE,
				NullIdentityProvider.INSTANCE);

		try {
			Feed feed = new Feed("", "", "", splitAdapter.getAll());

			writer.write("\n\nAdapter 1: ");
			writer.write(String.valueOf(feed.getItems().size()));
			writer.write("\n");
			feedWriter.write(writer, feed);

			Feed feed2 = new Feed("", "", "", splitAdapter2.getAll());

			writer.write("\n\nAdapter 2: ");
			writer.write(String.valueOf(feed2.getItems().size()));
			writer.write("\n");
			feedWriter.write(writer, feed2);

			writer.flush();
			baos.flush();

			contentAdapter.deleteRecordStorage();
			syncRepo.deleteRecordStorage();

			contentAdapter2.deleteRecordStorage();
			syncRepo2.deleteRecordStorage();

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
}
