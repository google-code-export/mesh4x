package org.mesh4j.ektoo;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;

public interface ISyncAdapterBuilder {

	/**
	 * creates adapter for excel repository
	 * 
	 * @param sheetName, the physical sheet name of the excel file 
	 * @param idColumnName, the name of the identity column of entity
	 * @param contentFileName,the content file which actually contains the provided sheet
	 * @param syncFileName,the sync file which actually contains the
	 * @param identityProvider
	 * @param idGenerator
	 * @return ISyncAdapter   
	 */
	public ISyncAdapter createMsExcelAdapter(String sheetName, String idColumnName, 
										String contentFileName, String syncFileName, 
										IIdentityProvider identityProvider, 
										IdGenerator idGenerator); 
	
	
	/**
	 * Creates adapter for access adapter
	 * 
	 * @param sourceAlias, the alias for source 
	 * @param mdbFileName,the name of the ms access database file name.
	 * @param tableName,the table name of the ms access database to be applied for sync.
	 * @return ISyncAdapter
	 */
	public ISyncAdapter createMsAccessAdapter(String baseDirectory,String rdfUrl,String sourceAlias,
											 String mdbFileName,String tableName);
	
	/**
	 * 
	 * @param spreadSheetInfo
	 * @return
	 */
	public ISyncAdapter createGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo);
	
}
