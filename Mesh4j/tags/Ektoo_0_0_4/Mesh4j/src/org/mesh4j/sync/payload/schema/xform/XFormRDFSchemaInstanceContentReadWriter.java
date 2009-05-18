package org.mesh4j.sync.payload.schema.xform;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.payload.schema.SchemaInstanceContentReadWriter;
import org.mesh4j.sync.payload.schema.SchemaTypeFormat;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class XFormRDFSchemaInstanceContentReadWriter extends SchemaInstanceContentReadWriter{

	private static Map<String, ISchemaTypeFormat> TYPE_FORMATS = new HashMap<String, ISchemaTypeFormat>();
	
	static {
		TYPE_FORMATS.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		TYPE_FORMATS.put(IRDFSchema.XLS_BOOLEAN, XFormBooleanFormat.INSTANCE);
	}
	
	public XFormRDFSchemaInstanceContentReadWriter(ISchema schema, IMapping mapping, boolean mustWriteSync) {
		super(schema, mapping, mustWriteSync);
	}
	
	protected Element asInstanceXML(Item item) {
		return this.getSchema().asInstancePlainXML(item.getContent().getPayload(), TYPE_FORMATS);
	}
	
	protected Element asInstanceXMLForMappingResolution(Element payloadToWrite, Item item) {
		return payloadToWrite;
	}
	
	protected Element getInstanceFromXML(String id, Element contentElement) {
		return this.getSchema().getInstanceFromPlainXML(id, contentElement, TYPE_FORMATS);
	}

}
