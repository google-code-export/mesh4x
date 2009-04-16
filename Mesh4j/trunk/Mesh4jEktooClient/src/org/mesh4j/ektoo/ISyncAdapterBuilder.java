package org.mesh4j.ektoo;

import org.mesh4j.sync.ISyncAdapter;

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
	public ISyncAdapter createMsExcelAdapter(String sheetName, String idColumnName, String contentFileName); 
	
	
	/**
	 * Creates adapter for access adapter
	 * 
	 * @param sourceAlias, the alias for source 
	 * @param mdbFileName,the name of the ms access database file name.
	 * @param tableName,the table name of the ms access database to be applied for sync.
	 * @return ISyncAdapter
	 */
	public ISyncAdapter createMsAccessAdapter(String mdbFileName, String tableName);
	
	/**
	 * TODO create documentation
	 * 
	 * @param spreadSheetInfo
	 * @return
	 */
	public ISyncAdapter createGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo);
	
	/** TODO create documentation
	 * 
	 * @param rootUrl
	 * @param meshId
	 * @param dataSetId
	 * @return
	 */
	public ISyncAdapter createHttpSyncAdapter(String meshId, String dataSetId);
}
