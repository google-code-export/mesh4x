package org.mesh4j.sync.adapters.feed.pfif;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.IContentReader;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIFSchema;
import org.mesh4j.sync.validations.MeshException;

public class PFIFContentReader implements IContentReader{

	
	private IPfifToPlainXmlMapping mapping;
	
	public PFIFContentReader(IPfifToPlainXmlMapping mapping){
		this.mapping = mapping;
	}
	
	@Override
	public void readContent(String syncId, Element payload,
							Element contentElement) {
//		if(payload == null || 
//				contentElement == null){
//			return;
//		}
		Element payLoadElement = null;
		if(payload == null){
			return ;
		} else {
			if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(payload.getName())){
				List<Element> elements = payload.elements();
				if(elements != null && elements.size()>0){
					String entityName = this.mapping.getType();
					if(entityName.equals(PFIFSchema.QNAME_PERSON.getName())){
						payLoadElement = payload.element(PFIFSchema.QNAME_PERSON);
					} else  if(entityName.equals(PFIFSchema.QNAME_NOTE.getName())){
						payLoadElement = payload.element(PFIFSchema.QNAME_NOTE);
					}
					
					for (Element element : elements) {
						payload.remove(element);
					}
					
					try {
						Element pfifElement = mapping.convertPfifToXML(payLoadElement);
						payload.add(pfifElement.createCopy());
					} catch (Exception e) {
						throw new MeshException(e);
					}
				}
			}
		}
	}
	
	

}
