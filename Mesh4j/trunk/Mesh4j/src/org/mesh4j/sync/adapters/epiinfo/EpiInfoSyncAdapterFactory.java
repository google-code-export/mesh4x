package org.mesh4j.sync.adapters.epiinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class EpiInfoSyncAdapterFactory {
		
	public static ISyncAdapter createSyncAdapter(String mdbFileName, String tableName, String rdfBaseUri, String baseDirectory, IIdentityProvider identityProvider, IRDFSchema schema) {
		return MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(mdbFileName, tableName, uniqueKey(mdbFileName, tableName), rdfBaseUri, baseDirectory, identityProvider, schema);
	}
	
	/**
	 * Returns a list of data table names for the given epi info file. Data table
	 * names are those which have a corresponding view* table.
	 */
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
	
	private static List<String> uniqueKey(String mdbFileName, String tableName) {
		String columnName;
		if (tableName.startsWith("code"))
			try {
				columnName = MsAccessHelper.getTableColumnNames(mdbFileName, tableName).iterator().next();
			} catch (IOException e) {
				throw new MeshException(e);
			}
		else if (tableName.startsWith("view"))
			columnName = "Name";
		else
			columnName = "UniqueKey";
			
		List<String> columnIds = new ArrayList<String>(1);
		columnIds.add(columnName);
		return columnIds;
	}

}
