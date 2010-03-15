package org.mesh4j.sync.adapters.feed.pfif.mapping;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.adapters.feed.pfif.model.PFIFModel;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IPfifToPlainXmlMapping extends IIdentifiableMapping {
	
	public Element convertPfifToXML(Element pfifPayload);
	public Element convertXMLToPfif(Element xmlPayload);
	public ISchema getSchema();
	
	public String getSourceFile();
	public List<Item> getNonParticipantItems();
	public String getPfifFeedSourceFile();
	public List<PFIFModel> getPfifModels();
}
