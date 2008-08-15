package org.mesh4x.sync.servlet;

import java.io.IOException;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class Mesh4jRssServlet extends Mesh4jServlet{

	private static final long serialVersionUID = 4903484981707116915L;

	public Mesh4jRssServlet() throws IOException {
		super();
	}

	@Override
	protected String getFileName() {
		//String feedName = this.getClass().getResource("rss.xml").getFile();
		String feedName = "c:\\rss.xml";
		return feedName;
	}

	@Override
	protected ISyndicationFormat getSyndicationFormat() {
		return RssSyndicationFormat.INSTANCE;
	}

}
