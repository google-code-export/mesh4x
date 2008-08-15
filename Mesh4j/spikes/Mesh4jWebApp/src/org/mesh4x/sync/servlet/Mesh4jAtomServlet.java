package org.mesh4x.sync.servlet;

import java.io.IOException;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;

public class Mesh4jAtomServlet extends Mesh4jServlet{

	private static final long serialVersionUID = -4113163235672230359L;

	public Mesh4jAtomServlet() throws IOException {
		super();
	}

	@Override
	protected String getFileName() {
		//String feedName = this.getClass().getResource("atom.xml").getFile();
		String feedName = "c:\\atom.xml";
		return feedName;
	}

	@Override
	protected ISyndicationFormat getSyndicationFormat() {
		return AtomSyndicationFormat.INSTANCE;
	}
	
}
