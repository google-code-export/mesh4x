package com.mesh4j.sync.message.protocol;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.MeshException;

public class FeedItemEncoding implements IItemEncoding {

	// MODEL VARIABLES
	private FeedReader feedReader;
	private FeedWriter feedWriter;

	// METHODS
	public FeedItemEncoding(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider){
		super();
		this.feedReader = new FeedReader(syndicationFormat, identityProvider);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider);
	}
	
	@Override
	public String encode(Item item) {
		Element root = DocumentHelper.createElement("payload");
		this.feedWriter.write(root, item);
		Element itemElement = (Element) root.elements().get(0);

		String xml = XMLHelper.canonicalizeXML(itemElement);
		return xml;
	}

	@Override
	public Item decode(String encodingItem) {
		try {
			Document document = DocumentHelper.parseText(encodingItem);
			Element itemElement = document.getRootElement();

			Item item = this.feedReader.readItem(itemElement);
			return item;
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
}
