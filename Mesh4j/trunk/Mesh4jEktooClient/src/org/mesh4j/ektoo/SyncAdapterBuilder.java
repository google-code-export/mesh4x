package org.mesh4j.ektoo;

import java.io.File;

import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadSheetSyncRepository;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.GoogleSpreadsheetToPlainXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SyncAdapterBuilder implements ISyncAdapterBuilder{
	
	// MODEL VARIABLEs
	private PropertiesProvider propertiesProvider;
	
	// BUSINESS METHODS

	public SyncAdapterBuilder(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.propertiesProvider = propertiesProvider;
	}


	@Override
	public ISyncAdapter createMsExcelAdapter(String contentFileName, String sheetName, String idColumnName) {
		
		//TODO if file doesn't exist then create file with the help of schema
		//request the client to provide the schema
		
		IIdentityProvider identityProvider = getIdentityProvider();
		
		File file = getFile(contentFileName);
		MsExcel  excelFile = new MsExcel(file.getAbsolutePath());
		
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(excelFile, getIdentityProvider(), getIdGenerator());
		
		//implement the MsExcelToRDFMapping instead of MSExcelToPlainXMLMapping
		RDFSchema schema;
		try {
			schema = MsExcelToRDFMapping.extractRDFSchema(excelFile, sheetName);
			System.out.println(schema.asXML());
		} catch (Exception e) {
			throw new MeshException();
		}
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(schema,idColumnName);
		
//		MSExcelToPlainXMLMapping mapper = new MSExcelToPlainXMLMapping(idColumnName, null);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excelFile, mapper, sheetName);

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		
		return splitAdapter;
	}

	@Override
	public ISyncAdapter createMsAccessAdapter(String mdbFileName, String tableName) {

		MsAccessSyncAdapterFactory msAccesSyncAdapter  = new MsAccessSyncAdapterFactory(this.getBaseDirectory(), this.getBaseRDFUrl());
		try {
			return msAccesSyncAdapter.createSyncAdapterFromFile(tableName, mdbFileName, tableName);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISyncAdapter createGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo){
		
		String idColumName = spreadSheetInfo.getIdColumnName();
		int lastUpdateColumnPosition = spreadSheetInfo.getLastUpdateColumnPosition();
		int idColumnPosition = spreadSheetInfo.getIdColumnPosition();
		String userName = spreadSheetInfo.getUserName();
		String passWord = spreadSheetInfo.getPassWord();
		String googleSpreadSheetId = spreadSheetInfo.getGoogleSpreadSheetId();
		String type = spreadSheetInfo.getType();
		
		// create google spread sheet
		IGoogleSpreadsheetToXMLMapping mapper = new GoogleSpreadsheetToPlainXMLMapping(type,idColumName,
				idColumnPosition,lastUpdateColumnPosition);
		IGoogleSpreadSheet gSpreadSheet = new GoogleSpreadsheet(googleSpreadSheetId, userName, passWord);
		
		// TODO (Sharif) create sync sheet automatically
		GSWorksheet<GSRow<GSCell>> contentWorkSheet = gSpreadSheet.getGSWorksheet(spreadSheetInfo.getSheetName());
		String syncWorkSheetName = spreadSheetInfo.getSheetName() + "_sync";
		GSWorksheet<GSRow<GSCell>> syncWorkSheet = gSpreadSheet.getGSWorksheet(syncWorkSheetName); 

		// adapter creation
		IIdentityProvider identityProvider = getIdentityProvider();
		GoogleSpreadSheetContentAdapter contentRepo = new GoogleSpreadSheetContentAdapter(gSpreadSheet, contentWorkSheet, mapper);
		GoogleSpreadSheetSyncRepository  syncRepo = new GoogleSpreadSheetSyncRepository(gSpreadSheet, identityProvider, getIdGenerator(), syncWorkSheet.getName());
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentRepo, identityProvider);
		
		return splitAdapter;
	}
	
	public ISyncAdapter createHttpSyncAdapter(String meshid, String datasetId){
		String url = getSyncUrl(meshid, datasetId);
		HttpSyncAdapter adapter = new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, getIdentityProvider(), getIdGenerator(), ContentWriter.INSTANCE, ContentReader.INSTANCE);
		return adapter;
	}
	
	// ACCESSORS
	
	private File getFile(String fileName) {
		File file = new File(fileName);
		if(!file.exists()){
			Guard.throwsArgumentException(fileName);
		}
		return file;
	}
	
	private String getSyncUrl(String meshid, String datasetId) {
		return this.propertiesProvider.getMeshURL(meshid + "/" +  datasetId);
	}
	
	private IIdentityProvider getIdentityProvider() {
		return this.propertiesProvider.getIdentityProvider();
	}
	
	private IIdGenerator getIdGenerator() {
		return IdGenerator.INSTANCE;
	}
	
	private String getBaseDirectory() {
		return this.propertiesProvider.getBaseDirectory();
	}


	private String getBaseRDFUrl() {
		// TODO (JMT) review
		return this.propertiesProvider.getMeshSyncServerURL();
	}


  @Override
  public ISyncAdapter createMySQLAdapter(String userName,String password,String hostName, int portNo,
      String databaseName, String tableName)
  {
  	  //  Example 
	  return HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///"+databaseName, 
			userName,									// TODO db user  
			password, 									// TODO db password
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tableName, 
			tableName+"_sync_info", 
			getBaseRDFUrl()+tableName+"#",
			getBaseDirectory());
  }

}
