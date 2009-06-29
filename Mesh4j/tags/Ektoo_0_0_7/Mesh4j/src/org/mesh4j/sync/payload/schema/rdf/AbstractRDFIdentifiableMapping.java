package org.mesh4j.sync.payload.schema.rdf;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.validations.Guard;

public abstract class AbstractRDFIdentifiableMapping implements IIdentifiableMapping {

	// MODEL VARIABLES
	protected IRDFSchema rdfSchema;
	
	// BUSINESS METHODS
	public AbstractRDFIdentifiableMapping(IRDFSchema rdfSchema){
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		this.rdfSchema = rdfSchema;
	}
	
	@Override
	public String getId(Element payload) {
		Element typeElement = getTypeElement(payload);
		RDFInstance instance = this.rdfSchema.createNewInstanceFromRDFXML(typeElement.asXML());
		return instance.getId();
	}

	@Override
	public String getType() {
		return this.rdfSchema.getOntologyClassName();
	}

	@Override
	public Element getTypeElement(Element payload) {
		Element element = null;
		if(getType().equals(payload.getName())){
			element = payload;
		}else{
			element = payload.element(getType());
		}
		
		if(RDFSchema.isRDF(payload)){
			element = payload;
		}
		return element;
	}

	public IRDFSchema getSchema() {
		return rdfSchema;
	}
	
	public static String makeId(List<String> idValues){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < idValues.size(); i++) {
			String idValue = idValues.get(i);
			sb.append(idValue);
			if(i < idValues.size() - 1){
				sb.append(IIdentifiableMapping.ID_SEPARATOR);
			}
		}
		return sb.toString();
	}

	protected String[] getIds(String id) {
		return id.split(IIdentifiableMapping.ID_SEPARATOR);
	}
}
