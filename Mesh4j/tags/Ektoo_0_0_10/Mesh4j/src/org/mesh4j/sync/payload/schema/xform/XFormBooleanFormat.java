package org.mesh4j.sync.payload.schema.xform;

import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;

public class XFormBooleanFormat implements ISchemaTypeFormat{

	// CONSTANTS
	public static final XFormBooleanFormat INSTANCE = new XFormBooleanFormat();
	
	public static final String OPTION_YES = "true";
	public static final String OPTION_NO = "false";

	// BUSINESS METHODS
	private XFormBooleanFormat(){
		super();
	}
	
	@Override
	public Object format(Object fieldValue) {
		if(fieldValue instanceof Boolean){
			Boolean bool = (Boolean) fieldValue;
			return bool ? OPTION_YES : OPTION_NO;
		} else {
			return null;
		}
	}

	@Override
	public Object parseObject(String fieldValue) {
		return OPTION_YES.equals(fieldValue);
	}

}
