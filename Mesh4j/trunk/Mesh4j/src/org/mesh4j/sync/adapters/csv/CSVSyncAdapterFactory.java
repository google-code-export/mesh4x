package org.mesh4j.sync.adapters.csv;

import java.io.File;

import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class CSVSyncAdapterFactory{

	public static SplitAdapter createSyncAdapter(String fileName, String[] idColumnNames, String lastUpdateColumnName, IIdentityProvider identityProvider, String rdfBaseURL) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		
		String syncFileName = fileName.substring(0, fileName.length() - 3) + "_sync.csv";
		CSVSyncRepository syncRepo =  new CSVSyncRepository(syncFileName, identityProvider, IdGenerator.INSTANCE);
		CSVContentAdapter contentAdapter = createContentAdapter(fileName, idColumnNames, lastUpdateColumnName, rdfBaseURL);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		return splitAdapter;
	}
	
	public static SplitAdapter createSyncAdapter(String fileName, IIdentityProvider identityProvider, IRDFSchema rdfSchema) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		
		String syncFileName = fileName.substring(0, fileName.length() - 3) + "_sync.csv";
	
		CSVToRDFMapping mappings = new CSVToRDFMapping(rdfSchema);
		CSVSyncRepository syncRepo =  new CSVSyncRepository(syncFileName, identityProvider, IdGenerator.INSTANCE);
		CSVContentAdapter contentAdapter = new CSVContentAdapter(fileName, mappings);
		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		
	}
	
	protected static CSVContentAdapter createContentAdapter(String fileName, String[] idColumnNames, String lastUpdateColumnName, String rdfBaseURL) {
		Guard.argumentNotNull(idColumnNames, "idColumnNames");
		if(idColumnNames.length == 0){
			Guard.throwsArgumentException("idColumnNames");
		}

		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		if(!fileName.trim().toLowerCase().endsWith(".csv")){
			Guard.throwsArgumentException("fileName", fileName);
		}
		
		File file = new File(fileName);
		if(!file.exists()){
			Guard.throwsArgumentException("fileName");	
		}
		IRDFSchema rdfSchema = CSVToRDFMapping.extractRDFSchema(fileName, idColumnNames, lastUpdateColumnName, rdfBaseURL);
		CSVToRDFMapping mappings = new CSVToRDFMapping(rdfSchema);
		return new CSVContentAdapter(fileName, mappings);
	}
}
