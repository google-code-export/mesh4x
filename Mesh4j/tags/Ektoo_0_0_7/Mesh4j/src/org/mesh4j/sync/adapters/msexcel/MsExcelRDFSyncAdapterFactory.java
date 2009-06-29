package org.mesh4j.sync.adapters.msexcel;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelRDFSyncAdapterFactory{
	
	protected static MsExcelContentAdapter createContentAdapter(String sheetName, String[] idColumnNames, String lastUpdateColumnName, IMsExcel excel, String rdfBaseURL) {
		Guard.argumentNotNull(idColumnNames, "idColumnNames");
		if(idColumnNames.length == 0){
			Guard.throwsArgumentException("idColumnNames");
		}
		
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		File file = new File(excel.getFileName());
		if(!file.exists()){
			Guard.throwsArgumentException("fileName", excel.getFileName());	
		}
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, sheetName, idColumnNames, lastUpdateColumnName, rdfBaseURL);
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema);
		return new MsExcelContentAdapter(excel, mappings);
	}

	public static SplitAdapter createSyncAdapter(IMsExcel excel, String sheetName, String[] idColumnNames, String lastUpdateColumnName, IIdentityProvider identityProvider, String rdfBaseURL) {
		Guard.argumentNotNull(excel, "excel");
		
		MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel, sheetName+"_sync", identityProvider, IdGenerator.INSTANCE);
		MsExcelContentAdapter contentAdapter = createContentAdapter(sheetName, idColumnNames, lastUpdateColumnName, excel, rdfBaseURL);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		return splitAdapter;
	}
	
	public static SplitAdapter createSyncAdapter(IMsExcel excel, IIdentityProvider identityProvider, IRDFSchema rdfSchema) {
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		
		String sheetName = rdfSchema.getOntologyClassName();
		if (!excel.fileExists() || excel.getSheet(sheetName) == null) {
			MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema);
			try{
				mappings.createDataSource(excel);
			}catch (Exception e) {
				throw new MeshException(e);
			}
		
			MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel, sheetName+"_sync", identityProvider, IdGenerator.INSTANCE);
			MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings);
			return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		} else {
			MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel, sheetName+"_sync", identityProvider, IdGenerator.INSTANCE);
			MsExcelContentAdapter contentAdapter = createContentAdapter(sheetName, rdfSchema.getIdentifiablePropertyNames().toArray(new String[0]), rdfSchema.getVersionPropertyName(), excel, rdfSchema.getBaseRDFURL());
			SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
			
			if(rdfSchema != null){
				IRDFSchema rdfSchemaAutoGenetated = (IRDFSchema)((MsExcelContentAdapter)splitAdapter.getContentAdapter()).getSchema();
				if(!rdfSchema.isCompatible(rdfSchemaAutoGenetated)){
					Guard.throwsException("INVALID_RDF_SCHEMA");
				}
			}
			return splitAdapter;
		}
	}	
	
	public static CompositeSyncAdapter createSyncAdapterForMultiSheets(String excelFileName, IIdentityProvider identityProvider, Map<String, String[]> sheets, ISyncAdapter opaqueAdapter, String rdfBaseURL) {
		MsExcel excel = new MsExcel(excelFileName);
		
		IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[sheets.size()];
		
		int i = 0;
		for (String sheetName : sheets.keySet()) {
			String[] idColumnNames = sheets.get(sheetName);
			SplitAdapter syncAdapter = createSyncAdapter(excel, sheetName, idColumnNames, null, identityProvider, rdfBaseURL);
			IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(sheetName, syncAdapter);
			adapters[i] = adapter;
			i = i +1;
		}
		
		return new CompositeSyncAdapter("MsExcel composite", opaqueAdapter, identityProvider, adapters);
	}
	
	public static ISyncAdapter createSyncAdapterForMultiSheets(String excelFileName, IIdentityProvider identityProvider, ISyncAdapter opaqueAdapter, List<IRDFSchema> sheets) {
		MsExcel excel = new MsExcel(excelFileName);
		
		IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[sheets.size()];
		
		int i = 0;
		for (IRDFSchema rdfSchema : sheets) {
			String sheetName = rdfSchema.getOntologyClassName();
			SplitAdapter splitAdapter = createSyncAdapter(excel, identityProvider, rdfSchema);
			IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(sheetName, splitAdapter);
			adapters[i] = adapter;
			i = i +1;
		}
		
		return new CompositeSyncAdapter("MsExcel composite", opaqueAdapter, identityProvider, adapters);
	}
}