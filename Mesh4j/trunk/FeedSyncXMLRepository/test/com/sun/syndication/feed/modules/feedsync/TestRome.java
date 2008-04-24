package com.sun.syndication.feed.modules.feedsync;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sun.syndication.feed.module.feedsync.modules.FeedSyncModule;
import com.sun.syndication.feed.module.feedsync.modules.SharingModule;
import com.sun.syndication.feed.module.feedsync.modules.SyncModule;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

public class TestRome {

	@Test
	public void testRssConflicts() throws IllegalArgumentException,
			FeedException, IOException {

		// READ
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed syndfeed = input.build(new XmlReader(
				getFile("myFeedRSSConflicts.xml")));

		SyndEntry entry = (SyndEntry) syndfeed.getEntries().get(0);
		SyncModule sync = (SyncModule) entry.getModule(FeedSyncModule.SCHEMA_URI);
		sync.getConflicts();

		// PUBLISH
		Writer writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(syndfeed, writer);
		writer.close();
		System.out.println(writer.toString());
	}

	@Test
	public void testSpikeAtomConflicts() throws IllegalArgumentException,
			FeedException, IOException {

		// READ
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed syndfeed = input.build(new XmlReader(
				getFile("myFeedAtomConflicts.xml")));

		SyndEntry entry = (SyndEntry) syndfeed.getEntries().get(0);
		SyncModule sync = (SyncModule) entry.getModule(FeedSyncModule.SCHEMA_URI);
		sync.getConflicts();

		// PUBLISH
		Writer writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(syndfeed, writer);
		writer.close();
		System.out.println(writer.toString());
	}

	@Test
	public void testSpikeSSERSS() throws IllegalArgumentException,
			FeedException, IOException {

		// READ
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed syndfeed = input.build(new XmlReader(
				getFile("myFeedSSERss.xml")));

		SharingModule sharing = (SharingModule) syndfeed
				.getModule(FeedSyncModule.SCHEMA_URI);

		// PUBLISH
		Writer writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(syndfeed, writer);
		writer.close();
		System.out.println(writer.toString());
	}

	@Test
	public void testSpikeSSEAtom() throws IllegalArgumentException,
			FeedException, IOException {

		// READ
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed syndfeed = input.build(new XmlReader(
				getFile("myFeedSSEAtom.xml")));

		SharingModule sharing = (SharingModule) syndfeed
				.getModule(FeedSyncModule.SCHEMA_URI);

		// PUBLISH
		Writer writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(syndfeed, writer);
		writer.close();

		System.out.println(writer.toString());
	}

	@Test
	public void testRome() throws IOException, FeedException {
		// FEED
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_1.0");
		feed.setTitle("MyProject Build Results");
		feed.setLink("http://myproject.mycompany.com/continuum");
		feed
				.setDescription("Continuous build results for the MyProject project");
		// feed.setCategory("MyProject");

		// ENTRIES
		SyndEntry entry = new SyndEntryImpl();
		entry.setTitle("BUILD SUCCESSFUL");
		entry
				.setLink("http://myproject.mycompany.com/continuum/build-results-1");
		entry.setPublishedDate(new Date());
		// DESCRITION
		SyndContent description = new SyndContentImpl();
		description.setType("text/html");
		description.setValue("The build was successful!");
		//
		entry.setDescription(description);
		// CATEGORIES
		List<SyndCategory> categories = new ArrayList<SyndCategory>();
		SyndCategory category = new SyndCategoryImpl();
		category.setName("MyProject");
		categories.add(category);
		entry.setCategories(categories);
		// PUBLISH
		Writer writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);
		writer.close();
		
		// READ
		InputStream is = new StringBufferInputStream(writer.toString());
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feedLoaded = input.build(new XmlReader(is));
		
		System.out.print(writer.toString());
		System.out.print(feedLoaded);

	}

	private File getFile(String fileName) {
		return new File(this.getClass().getResource(fileName).getFile());
	}
}