package org.mesh4j.ektoo;

import java.util.List;

import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.IPropertyResolver;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
/**
 * The builder interface for creating adapter with the
 * minimum requirements
 */
public interface ISyncAdapterBuilder {

	/**
	 * creates adapter for excel repository
	 * 
	 * @param contentFileName
	 *            ,the content file which actually contains the provided sheet
	 * @param sheetName
	 *            , the physical sheet name of the excel file
	 * @param idColumnName
	 *            , the name of the identity column of entity
	 * @param isRDF
	 * 			,which actually represents if the adapter going to create is RDF based or without RDF.<l>
	 * 			if user make to true then created excel adapter will be based on RDF and it must have RDF schema<l>
	 * 			and user can easilly get the schema from its Content adapter.
	 */
	public SplitAdapter createMsExcelAdapter(String contentFileName, String sheetName, String[] idColumnNames, boolean isRDF);
	
	/**
	 * creates adapter for excel repository
	 * 
	 * @param contentFileName
	 *            ,the content file which actually contains the provided sheet.
	 * @param sheetName
	 *            , the physical sheet name of the excel file.
	 * @param idColumnName
	 *            , the name of the identity column of entity.
	 * @param  sourceSchema
	 * 			 ,the rdf schema of which excel repository should be.  
	 * @return ISyncAdapter,instance of the excel adapter         
	 */
	public ISyncAdapter createMsExcelAdapter(String contentFileName, IRDFSchema sourceSchema);
	
	/**
	 * Create adapter for access adapter
	 * 
	 * @param sourceAlias
	 *            , the alias for source
	 * @param mdbFileName
	 *            ,the name of the ms access database file name.
	 * @param tableName
	 *            ,the table name of the ms access database to be applied for
	 *            sync.
	 * @return ISyncAdapter
	 */
	public ISyncAdapter createMsAccessAdapter(String mdbFileName, String tableName);

	/**
	 * Creates MsAccess adapter for multi table
	 * 
	 * @param mdbFileName
	 * @param tables
	 * @return
	 */
	public ISyncAdapter createMsAccessMultiTablesAdapter(String mdbFileName, String[] tables);
	
	/**
	 * TODO create documentation (raju)
	 * 
	 * @param spreadSheetInfo
	 * @param rdfSchema
	 * @return
	 */
	public ISyncAdapter createPlainXMLBasedGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo);

	public ISyncAdapter createPlainXMLBasedGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo, IGoogleSpreadSheet gss);
	
	/**
	 * Basic method to create http adapter.
	 * @param serverUrl,the base feed server url
	 * @param meshGroup,the mesh name in the feed server
	 * @param dataSetId, the dataset name under provide meshGroup
	 * @return ISyncAdapter 
	 */
	public ISyncAdapter createHttpSyncAdapter(String serverUrl,String meshGroup,String dataSetId);
	/**
	 * TODO create documentation (raju)
	 * 
	 * @param rootUrl
	 * @param meshId
	 * @param dataSetId
	 * @return
	 */
	public ISyncAdapter createHttpSyncAdapter(String serverUrl, String meshGroup, String dataSetId, IRDFSchema rdfSchema, IMapping mapping);

	/**
	 * Create {@link HttpSyncAdapter} for multi dataset 
	 * 
	 * @param serverUrl
	 * @param meshGroup
	 * @param rdfSchemas
	 * @return
	 */
	public ISyncAdapter createHttpSyncAdapterForMultiDataset(String serverUrl, String meshGroup, List<IRDFSchema> rdfSchemas);
	
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
	public SplitAdapter createMySQLAdapter(String userName, String password,
			String hostName, int portNo, String databseName, String tableName);
	
	/**
	 * TODO create documentation (raju)
	 */
	public ISyncAdapter createKMLAdapter(String kmlFileName);
	
	
	/**
	 * TODO create documentation (raju)
	 */
	public ISyncAdapter createFeedAdapter(String title, String description,
			String link, String fileName, ISyndicationFormat syndicationFormat);


	public SplitAdapter createRdfBasedGoogleSpreadSheetAdapter(
			GoogleSpreadSheetInfo spreadSheetInfo, IRDFSchema sourceSchema);

	public SplitAdapter createRdfBasedGoogleSpreadSheetAdapter(
			GoogleSpreadSheetInfo spreadSheetInfo, IGoogleSpreadSheet gss,
			IRDFSchema sourceSchema);
	
	/**
	 * TODO create documentation (raju)
	 */
	public ISyncAdapter createFolderAdapter(String folderName);

	/**
	 * TODO create documentation (raju)
	 */
	public String generateMySqlFeed(String userName, String password, String hostName,
			int portNo, String databaseName, String tableName);

	/**
	 * This returns a composite adapter for sync multiple table using mysql/hibernate adapter 
	 * @param userName
	 * @param password
	 * @param hostName
	 * @param portNo
	 * @param databaseName
	 * @param tables
	 * @return
	 */
	public ISyncAdapter createMySQLAdapterForMultiTables(String userName,
			String password, String hostName, int portNo, String databaseName,
			String[] tables);

	/**
	 * This returns a composite adapter for sync multiple sheet using MsExcel adapter 
	 * @param contentFileName
	 * @param sheets
	 * @return
	 */
	public ISyncAdapter createMsExcelAdapterForMultiSheets(String contentFileName, List<IRDFSchema> sheets);

	public ISyncAdapter createZipFeedAdapter(String zipFileName);

	IIdentityProvider getIdentityProvider();

	IIdGenerator getIdGenerator();

	String getBaseDirectory();

	String getBaseRDFUrl();

	public IPropertyResolver[] getMappingPropertyResolvers();

	public PropertiesProvider getPropertiesProvider();

}
