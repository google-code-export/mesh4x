package org.mesh4x.sync.servlet;

import java.io.IOException;
import java.util.Date;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.utils.XMLHelper;

public class Mesh4jUserServlet extends Mesh4jServlet{

	private static final long serialVersionUID = 4903484981707116915L;

	public Mesh4jUserServlet() throws IOException {
		super();
	}

	@Override
	protected String getFileName() {
		//String feedName = this.getClass().getResource("user.xml").getFile();
		String feedName = "c:\\user.xml";
		return feedName;
	}

	@Override
	protected ISyndicationFormat getSyndicationFormat() {
		return RssSyndicationFormat.INSTANCE;
	}
	
	protected Item makeNewItem() {
		String syncID = this.newID();	
		String entityID = this.newID();
		
		Element element = XMLHelper.parseElement("<payload><user><type>TLP</type><name>"+entityID+"</name><password>"+syncID+"</password></user></payload>"); 
System.out.println(element.asXML());
		XMLContent content = new XMLContent(entityID, syncID, syncID, element);
		Sync sync = new Sync(syncID, "jmt", new Date(), false);
		return new Item(content, sync);
	}

	
	@Override
	protected boolean mustCreateItems() {
		return true;
	}
}
