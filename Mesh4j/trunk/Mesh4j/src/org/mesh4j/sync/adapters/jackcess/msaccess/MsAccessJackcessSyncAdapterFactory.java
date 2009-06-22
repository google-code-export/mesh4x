package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

/**
 *  MsAccessSyncAdapterFactory is a factory if ISyncAdapter(SplitAdapter) for MsAccess 2000 files.
 *  Support GUI types.
 *  Does not support Auto numerics.
 *  PK is required (no auto numeric pk).
 */
public class MsAccessJackcessSyncAdapterFactory {
	
	public static SplitAdapter createSplitAdapter(MsAccess msAccess, IIdentityProvider identityProvider, IRDFSchema rdfSchema) {
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		
		if (!msAccess.fileExists() || msAccess.getTable(rdfSchema.getOntologyClassName()) == null) {
			try{
				// TODO (JMT) create mdb file: MsAccessRDFSchemaGenerator.createMsAccessFile(msAccess, rdfSchema);
			}catch (Exception e) {
				throw new MeshException(e);
			}
		}
		
		SplitAdapter splitAdapter = createSyncAdapter(msAccess, rdfSchema.getOntologyClassName(), identityProvider, rdfSchema.getOntologyBaseUri());
		IRDFSchema rdfSchemaAutoGenetated = (IRDFSchema)((MsAccessContentAdapter)splitAdapter.getContentAdapter()).getSchema();
		if(!rdfSchema.isCompatible(rdfSchemaAutoGenetated)){
			Guard.throwsException("INVALID_RDF_SCHEMA");
		}
		return splitAdapter;
	}

	public static SplitAdapter createSyncAdapter(MsAccess msAccess, String tableName, IIdentityProvider identityProvider, String rdfBaseURL) {
		Guard.argumentNotNull(msAccess, "msAccess");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		MsAccessSyncRepository syncRepo = new MsAccessSyncRepository(msAccess, tableName + "_sync", identityProvider, IdGenerator.INSTANCE);
		
		MsAccessToRDFMapping mapping = MsAccessRDFSchemaGenerator.extractRDFSchemaAndMappings(msAccess.getFileName(), tableName, rdfBaseURL);
		
		MsAccessContentAdapter contentAdapter = new MsAccessContentAdapter(msAccess, mapping, tableName);
		return new SplitAdapter(syncRepo, contentAdapter, identityProvider); 
	}
	
	public static CompositeSyncAdapter createSyncAdapterForMultiTables(MsAccess msAccess, Set<String> tables, IIdentityProvider identityProvider, ISyncAdapter adapterOpaque, String rdfBaseURL){
	
		try{
			IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[tables.size()];
			int i = 0;
			for (String tableName : tables) {
				SplitAdapter syncAdapter = createSyncAdapter(msAccess, tableName, identityProvider, rdfBaseURL);
				MsAccessContentAdapter contentAdapter = (MsAccessContentAdapter)syncAdapter.getContentAdapter();
				adapters[i] = new IdentifiableSyncAdapter(contentAdapter.getType(), syncAdapter);
				i = i +1;
			}
			return new CompositeSyncAdapter("MsAccess composite", adapterOpaque, identityProvider, adapters);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public static Set<String> getTableNames(String fileName) throws IOException {
		TreeSet<String> result = new TreeSet<String>();
		Set<String> tableNames = MsAccessHelper.getTableNames(fileName);
		for (String tableName : tableNames) {
			if(!isSyncTableName(tableName)){
				result.add(tableName);
			}
		}
		return result;
	}

	private static boolean isSyncTableName(String tableName) {
		return tableName.trim().toLowerCase().endsWith("_sync");
	}
}
