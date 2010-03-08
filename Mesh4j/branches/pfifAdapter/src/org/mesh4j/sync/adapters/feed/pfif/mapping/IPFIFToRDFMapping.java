package org.mesh4j.sync.adapters.feed.pfif.mapping;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IPFIFToRDFMapping extends IIdentifiableMapping{

	public Element convertPfifToRdfElement(Element pfifPayload);
	public Element convertRdfToPfifElement(Element rdfPayload);
	public ISchema getSchema();
	public String getId(Element rdfPayload);
	
	public String getSourceFile();
	public List<Item> getNonParticipantItems();
	public String getPfifFeedSourceFile();
	public List<PFIFModel> getPfifModels();
	
}
