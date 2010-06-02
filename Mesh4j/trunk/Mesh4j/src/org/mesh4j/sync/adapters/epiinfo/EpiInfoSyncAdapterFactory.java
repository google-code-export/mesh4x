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
		List<String> dataTableNames = getTableNames(mdbFileName);
		List<List<String>> columnIds = new ArrayList<List<String>>(dataTableNames.size());
		for (int i = 0; i < columnIds.size(); i++)
			columnIds.add(uniqueKey());
		
		return MsAccessHibernateSyncAdapterFactory.createSyncAdapterForMultiTables(mdbFileName, dataTableNames, columnIds, rdfBaseUri, baseDirectory, identityProvider, adapterOpaque);
	}
	
	public static ISyncAdapter createSyncAdapter(String mdbFileName, String tableName, String rdfBaseUri, String baseDirectory, IIdentityProvider identityProvider) {
		return MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(mdbFileName, tableName, uniqueKey(), rdfBaseUri, baseDirectory, identityProvider);
	}
	
	public static List<String> getTableNames(String mdbFileName) {
		try {
			Set<String> tableNames = MsAccessHibernateSyncAdapterFactory.getTableNames(mdbFileName);
			
			List<String> dataTableNames = new ArrayList<String>(tableNames.size());
			for(String tableName : tableNames) {
				if (tableNames.contains("view" + tableName)) {
					dataTableNames.add(tableName);
				}
			}
			return dataTableNames;
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}
	
	private static List<String> uniqueKey() {
		List<String> columnIds = new ArrayList<String>(1);
		columnIds.add("UniqueKey");
		return columnIds;
	}

}
