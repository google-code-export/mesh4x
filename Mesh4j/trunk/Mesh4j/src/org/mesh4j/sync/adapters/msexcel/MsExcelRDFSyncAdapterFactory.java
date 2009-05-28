package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.util.Map;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelRDFSyncAdapterFactory extends MsExcelSyncAdapterFactory{
	
	// MODEL VARIABLES
	private String rdfBaseURL;
	
	// BUSINESS METHODS
	public MsExcelRDFSyncAdapterFactory(String rdfBaseURL){
		super();
		Guard.argumentNotNullOrEmptyString(rdfBaseURL, "rdfBaseURL");
		this.rdfBaseURL = rdfBaseURL;
	}
	
	protected MsExcelContentAdapter createContentAdapter(String sheetName, String idColumnName, IMsExcel excel) {
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		File file = new File(excel.getFileName());
		if(!file.exists()){
			Guard.argumentNotNull(excel.getFileName(), "fileName");	
		}
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, sheetName, this.rdfBaseURL);
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema, idColumnName);
		return new MsExcelContentAdapter(excel, mappings, sheetName);
	}

	public SplitAdapter createSyncAdapter(String excelFileName, String sheetName, String idColumnName, IIdentityProvider identityProvider, IRDFSchema rdfSchema) {
		Guard.argumentNotNullOrEmptyString(excelFileName, "excelFileName");
		MsExcel excel = new MsExcel(excelFileName);
		return createSyncAdapter(excel, sheetName, idColumnName, identityProvider, rdfSchema); 
	}
	
	public SplitAdapter createSyncAdapter(IMsExcel excel, String sheetName, String idColumnName, IIdentityProvider identityProvider, IRDFSchema rdfSchema) {
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		
		if (!excel.fileExists() || excel.getSheet(sheetName) == null) {
			MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema, idColumnName);
			try{
				mappings.createDataSource(excel);
			}catch (Exception e) {
				throw new MeshException(e);
			}
		
			MsExcelSyncRepository syncRepo = createSyncRepository(sheetName, identityProvider, excel);
			MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings, sheetName);
			return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		} else {
			SplitAdapter splitAdapter = super.createSyncAdapter(excel, sheetName, idColumnName, identityProvider);
			IRDFSchema rdfSchemaAutoGenetated = (IRDFSchema)((MsExcelContentAdapter)splitAdapter.getContentAdapter()).getSchema();
			if(!rdfSchema.isCompatible(rdfSchemaAutoGenetated)){
				Guard.throwsException("INVALID_RDF_SCHEMA");
			}
			return splitAdapter;
		}
	}	

	public ISyncAdapter createSyncAdapterForMultiSheets(String excelFileName, IIdentityProvider identityProvider, ISyncAdapter opaqueAdapter, Map<IRDFSchema, String> sheets) {
		MsExcel excel = new MsExcel(excelFileName);
		
		IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[sheets.size()];
		
		int i = 0;
		for (IRDFSchema rdfSchema : sheets.keySet()) {
			String sheetName = rdfSchema.getOntologyClassName();
			String idColumnName = sheets.get(rdfSchema);
			SplitAdapter splitAdapter = this.createSyncAdapter(excel, sheetName, idColumnName, identityProvider, rdfSchema);
			IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(sheetName, splitAdapter);
			adapters[i] = adapter;
			i = i +1;
		}
		
		return new CompositeSyncAdapter("MsExcel composite", opaqueAdapter, identityProvider, adapters);
	}
}