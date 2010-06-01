package org.mesh4j.sync.adapters.epiinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class EpiInfoSyncAdapterFactory {
	
	public static CompositeSyncAdapter createSyncAdapter(String mdbFileName, String rdfBaseUri, String baseDirectory, IIdentityProvider identityProvider, ISyncAdapter adapterOpaque) {
		try {
			Set<String> tableNames = MsAccessHibernateSyncAdapterFactory.getTableNames(mdbFileName);
			
			List<String> dataTableNames = new ArrayList<String>(tableNames.size());
			List<List<String>> columnIds = new ArrayList<List<String>>();
			
			for(String tableName : tableNames) {
				if (tableNames.contains("view" + tableName)) {
					dataTableNames.add(tableName);
					
					List<String> ids = new ArrayList<String>(1);
					ids.add("UniqueKey");
					columnIds.add(ids);
				}
			}
			
			return MsAccessHibernateSyncAdapterFactory.createSyncAdapterForMultiTables(mdbFileName, dataTableNames, columnIds, rdfBaseUri, baseDirectory, identityProvider, adapterOpaque);
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}

}
