package org.mesh4j.sync.adapters.msexcel;

import org.mesh4j.sync.payload.schema.rdf.RDFSchema;

public class MsExcelRDFSyncAdapterFactory extends MsExcelSyncAdapterFactory{
	
	private RDFSchema schema;
	
	public MsExcelRDFSyncAdapterFactory(RDFSchema schema){
		super();
		this.schema = schema;
	}
	
	protected MsExcelContentAdapter createContentAdapter(String sheetName, String idColumnName, IMsExcel excel) {
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(this.schema, idColumnName);
		return new MsExcelContentAdapter(excel, mappings, sheetName);
	}
	
}
