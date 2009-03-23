package org.mesh4j.sync.adapters.msexcel;

import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class MsExcelRDFSyncAdapterFactory extends MsExcelSyncAdapterFactory{
	
	private IRDFSchema schema;
	
	public MsExcelRDFSyncAdapterFactory(IRDFSchema schema){
		super();
		this.schema = schema;
	}
	
	protected MsExcelContentAdapter createContentAdapter(String sheetName, String idColumnName, IMsExcel excel) {
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(this.schema, idColumnName);
		return new MsExcelContentAdapter(excel, mappings, sheetName);
	}
	
}
