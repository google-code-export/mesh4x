package org.mesh4j.ektoo;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public interface ISyncAdapterBuilder {

	/**
	 * creates adapter for excel repository
	 * 
	 * @param contentFileName,the content file which actually contains the provided sheet
	 * @param sheetName, the physical sheet name of the excel file 
	 * @param idColumnName, the name of the identity column of entity
	 *  
	 */
	public ISyncAdapter createMsExcelAdapter(IRDFSchema schema,String contentFileName, String sheetName, String idColumnName); 
  
	@Deprecated
	public ISyncAdapter createMsExcelAdapter(String contentFileName, String sheetName, String idColumnName);
	
	/**
	 * Create adapter for access adapter
	 * 
	 * @param sourceAlias, the alias for source 
	 * @param mdbFileName,the name of the ms access database file name.
	 * @param tableName,the table name of the ms access database to be applied for sync.
	 * @return ISyncAdapter
	 */
	public ISyncAdapter createMsAccessAdapter(String mdbFileName, String tableName);
	
	/**
	 * TODO create documentation (raju)
	 * 
	 * @param spreadSheetInfo
	 * @return
	 */
	public ISyncAdapter createGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo);
	
	/** TODO create documentation (raju)
	 * 
	 * @param rootUrl
	 * @param meshId
	 * @param dataSetId
	 * @return
	 */
	public ISyncAdapter createHttpSyncAdapter(String meshId, String dataSetId);

	/**
	 * TODO create documentation (raju)
	 * 
	 * @param userName
	 * @param password
	 * @param hostName
	 * @param portNo
	 * @param databaseName
	 * @param tableName
	 * @return
	 */
	public ISyncAdapter createMySQLAdapter(String userName,String password, String hostName, int portNo, String databseName, String tableName);
}
