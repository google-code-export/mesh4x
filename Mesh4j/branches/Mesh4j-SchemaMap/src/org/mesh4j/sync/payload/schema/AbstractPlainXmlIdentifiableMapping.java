package org.mesh4j.sync.payload.schema;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.validations.Guard;

public class AbstractPlainXmlIdentifiableMapping implements IIdentifiableMapping{

	// MODEL VARIABLES
	protected String type;
	protected String idColumnName;
	protected String lastUpdateColumnName;
	private String lastUpdateColumnDateTimeFormat;
	
	// BUSINESS METHODS
	public AbstractPlainXmlIdentifiableMapping(String type, String idColumnName, String lastUpdateColumnName, String lastUpdateColumnDateTimeFormat) {
		Guard.argumentNotNullOrEmptyString(type, "type");
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		if(lastUpdateColumnName != null){
			Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
			Guard.argumentNotNullOrEmptyString(lastUpdateColumnDateTimeFormat, "lastUpdateColumnDateTimeFormat");
		}		

		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
		this.lastUpdateColumnDateTimeFormat = lastUpdateColumnDateTimeFormat;
		this.type = type;
	}
	
	@Override
	public String getId(Element payload) {
		Element typeElement = getTypeElement(payload);
		Element idElement = typeElement.element(this.idColumnName);
		return idElement == null ? null : idElement.getText();
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public Element getTypeElement(Element payload) {
		Element element = null;
		if(getType().equals(payload.getName())){
			element = payload;
		}else{
			element = payload.element(getType());
		}
		return element;
	}
	
	public String getIdColumnName() {
		return this.idColumnName;
	}

	public String getLastUpdateColumnName() {
		return this.lastUpdateColumnName;
	}
	
	public String getLastUpdateColumnDateTimeFormat() {
		return this.lastUpdateColumnDateTimeFormat;
	}
	
	public ISchema getSchema() {
		return null;
	}
}
