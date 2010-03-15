package org.mesh4j.sync.adapters.feed.pfif.mapping;

import java.io.File;
import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.pfif.model.IPfif;
import org.mesh4j.sync.adapters.feed.pfif.model.PFIFModel;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.AbstractPlainXmlIdentifiableMapping;

public class PfifToPlainXmlMapping extends AbstractPlainXmlIdentifiableMapping implements IPfifToPlainXmlMapping{

	
	private List<PFIFModel> models = null;
//	private IPFIFSchema pfifSchema;
//	private String pifiFeedSourceFile;
	private static final String  UTC_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private IPfif pfif = null; 
	
	public PfifToPlainXmlMapping(String type, String idColumnName,
			String lastUpdateColumnName, String lastUpdateColumnDateTimeFormat,IPfif pfif) {
		super(type, idColumnName, lastUpdateColumnName, lastUpdateColumnDateTimeFormat);
//		this.pfifSchema = pfifSchema;

		
		
//		this.pfifSchema = pfifSchema;
		this.pfif = pfif;
		
		File pfifFile = new File(this.pfif.getSourceFile());
		if(!pfifFile.exists() || 
				pfifFile.length() == 0){
			return ;
		}
//		try {
//			models = PFIFUtil.getOrCreatePersonAndNoteFileIfNecessary(this.pfif.getSourceFile(),
//					pfif.getSyndicationFormat());
//		} catch (IOException e) {
//			throw new MeshException(e);
//		}
	}

	@Override
	public Element convertPfifToXML(Element pfifPayload) {
//		Element rootElement = DocumentHelper.createElement(this.getType());
//		rootElement.add(pfifPayload);
		return pfifPayload;
	}

	@Override
	public Element convertXMLToPfif(Element xmlPayload) {
		return xmlPayload;
	}

	@Override
	public List<Item> getNonParticipantItems() {
		return this.pfif.getNonParticipantItems();
	}

	@Override
	public String getPfifFeedSourceFile() {
		return this.pfif.getPfifFeedSourceFile();
	}

	@Override
	public List<PFIFModel> getPfifModels() {
		return this.pfif.getPfifModels();
	}

	@Override
	public String getSourceFile() {
		return this.pfif.getSourceFile();
	}

}
