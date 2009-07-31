package org.mesh4j.sync.payload.schema.xform;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.payload.schema.SchemaTypeFormat;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchemaInstanceContentReadWriter;

public class XFormRDFSchemaInstanceContentReadWriter extends RDFSchemaInstanceContentReadWriter{

	private static Map<String, ISchemaTypeFormat> TYPE_FORMATS = new HashMap<String, ISchemaTypeFormat>();
	
	static {
		TYPE_FORMATS.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		TYPE_FORMATS.put(IRDFSchema.XLS_BOOLEAN, XFormBooleanFormat.INSTANCE);
	}
	
	public XFormRDFSchemaInstanceContentReadWriter(IRDFSchema schema, IMapping mapping, boolean mustWriteSync) {
		super(schema, mapping, mustWriteSync);
	}
	
	@Override
	protected Element getInstanceAsXML(Item item) {
		return this.getSchema().asInstancePlainXML(item.getContent().getPayload(), TYPE_FORMATS);
	}
		
	@Override
	protected Element readInstanceFromXML(String id, Element contentElement) {
		String idRDF = getIdFromPayload(this.getRDFSchema(), contentElement, id);
		return this.getSchema().getInstanceFromPlainXML(idRDF, contentElement, TYPE_FORMATS);
	}

	@Override
	public boolean encapsulateContentInCDATA() {
		return true;
	}
	
	@Override
	public void addNameSpace(Element nsElement){
		// nothing to do
	}
}
