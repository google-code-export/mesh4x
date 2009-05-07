package org.mesh4j.sync.adapters.msexcel;

import java.io.File;

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
		
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, sheetName, this.rdfBaseURL);
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema, idColumnName);
		return new MsExcelContentAdapter(excel, mappings, sheetName);
	}

	public SplitAdapter createSyncAdapter(String excelFileName, String sheetName, String idColumnName, IIdentityProvider identityProvider, IRDFSchema rdfSchema) {
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNullOrEmptyString(excelFileName, "excelFileName");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		
		File file = new File(excelFileName);
		
//		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema, idColumnName);
//		if (!file.exists()) {
//			try{
//				mappings.createDataSource(excelFileName);
//			}catch (Exception e) {
//				throw new MeshException(e);
//			}
//		}
//		
//		MsExcel excel = new MsExcel(excelFileName);
//		MsExcelSyncRepository syncRepo = createSyncRepository(identityProvider, excel);
//		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings, sheetName);
//		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		if (!file.exists()) {
			MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema, idColumnName);
			try{
				mappings.createDataSource(excelFileName);
			}catch (Exception e) {
				throw new MeshException(e);
			}
		
			MsExcel excel = new MsExcel(excelFileName);
			MsExcelSyncRepository syncRepo = createSyncRepository(identityProvider, excel);
			MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings, sheetName);
			return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		} else {
			SplitAdapter splitAdapter = super.createSyncAdapter(excelFileName, sheetName, idColumnName, identityProvider);
			IRDFSchema rdfSchemaAutoGenetated = (IRDFSchema)((MsExcelContentAdapter)splitAdapter.getContentAdapter()).getSchema();
			if(!rdfSchema.asXML().equals(rdfSchemaAutoGenetated.asXML())){
			//if(!rdfSchema.equals(rdfSchemaAutoGenetated)){
				Guard.throwsException("INVALID_RDF_SCHEMA");
			}
			return splitAdapter;
		}
	}	
}