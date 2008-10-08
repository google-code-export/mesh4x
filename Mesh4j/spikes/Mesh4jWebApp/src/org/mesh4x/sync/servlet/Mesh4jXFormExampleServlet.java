package org.mesh4x.sync.servlet;

import java.io.IOException;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class Mesh4jXFormExampleServlet extends Mesh4jServlet {

	private static final long serialVersionUID = -2001777749439484226L;
	
	public Mesh4jXFormExampleServlet() throws IOException {
		super();
	}

	@Override
	protected String getFileName() {
		String feedName = "C:\\Clarius\\temp\\xform3.xml";
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
