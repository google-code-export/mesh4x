package org.mesh4x.sync.servlet;

import java.io.IOException;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class Mesh4jXFormServlet extends Mesh4jServlet {

	private static final long serialVersionUID = -2001777749439484226L;
	
	public Mesh4jXFormServlet() throws IOException {
		super();
	}

	@Override
	protected String getFileName() {
		//String feedName = this.getClass().getResource("xform.xml").getFile();
		String feedName = "c:\\xform2.xml";
		return feedName;
	}

	@Override
	protected ISyndicationFormat getSyndicationFormat() {
		return RssSyndicationFormat.INSTANCE;
	}

	@Override
	protected boolean mustCreateItems() {
		return false;
	}
}
