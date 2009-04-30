package org.mesh4j.ektoo;

import java.io.File;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.mesh4j.sync.adapters.msexcel.IMsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
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
	public ISyncAdapter createMsExcelAdapter(IRDFSchema schema,String contentFileName, String sheetName, String idColumnName) {
		
		System.out.println("provided schema is:" + schema.asXML());
		SplitAdapter splitAdapter = null;
		MsExcel  excel = null;
		IRDFSchema extractedSchema = null;
		MsExcelToRDFMapping mapper = new MsExcelToRDFMapping(schema,idColumnName);
		File file = new File(contentFileName);
		
		if(file == null || !file.exists()){
			//create the Msexcel file with the help of provided schema
			 try {
				mapper.createDataSource(contentFileName);
			} catch (Exception e) {
				throw new MeshException(e);
			}
			excel = new MsExcel(file.getAbsolutePath()); 
			splitAdapter = getExcelAdapter(excel,mapper,sheetName);
		}else{
			//extract schema from excel file sheet and compare the schema 
			//with the provided schema.
			//if provided schema and extracted schema are same then continue otherwise throw exception.
			 excel = new MsExcel(file.getAbsolutePath());
			try {
				extractedSchema = extractMsExcelRDFSchema(excel,sheetName);
				System.out.println("MsExcel schema is:" + extractedSchema.asXML());
			} catch (Exception e) {
				throw new MeshException();
			}
			//Right now we guess both RDF schema is same
			//TODO, in future implement the RDF comparison 

//			if(extractedSchema.equals(schema)){//if both schema same
//				excel = new MsExcel(file.getAbsolutePath()); 
//				mapper = new MsExcelToRDFMapping(schema,idColumnName);
//				splitAdapter = getExcelAdapter(excel,mapper,sheetName);
//			}else{
//				//TODO ,implement feature for mapping the source and target schema in future
//				throw new MeshException("source and target schema is not equal");
//			}
			
			mapper = new MsExcelToRDFMapping(schema,idColumnName);
			splitAdapter = getExcelAdapter(excel,mapper,sheetName);
			
		}
		return splitAdapter;
	}

	private SplitAdapter getExcelAdapter(MsExcel excel,MsExcelToRDFMapping mapper,String sheetName){
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(excel, getIdentityProvider(), getIdGenerator());
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mapper, sheetName);
		return new SplitAdapter(syncRepo, contentAdapter, getIdentityProvider());
	}
	
	private RDFSchema extractMsExcelRDFSchema(IMsExcel excel,String sheetName){
	
		RDFSchema rdfSchema = new RDFSchema(sheetName, "http://mesh4x/MeshSyncExample/"+sheetName+"#", sheetName);
		
		String cellName;
		HSSFWorkbook workbook = excel.getWorkbook();
		HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, sheetName);
		HSSFCell cell;

		HSSFRow headerRow = sheet.getRow(sheet.getFirstRowNum());
		HSSFRow dataRow = sheet.getRow(sheet.getLastRowNum());
		int cellType;
		for (Iterator<HSSFCell> iterator = dataRow.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			
			cellName = headerRow.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
			cellType = cell.getCellType();
			if(HSSFCell.CELL_TYPE_STRING == cellType){
				rdfSchema.addStringProperty(cellName, cellName, "en");
			} else if(HSSFCell.CELL_TYPE_BOOLEAN == cellType){
				rdfSchema.addBooleanProperty(cellName, cellName, "en");
			} else if(HSSFCell.CELL_TYPE_NUMERIC == cellType){
				if(HSSFDateUtil.isCellDateFormatted(cell)) {
					rdfSchema.addDateTimeProperty(cellName, cellName, "en");
				} else {
					rdfSchema.addDoubleProperty(cellName, cellName, "en");
		        }
			}
		}
		
		return rdfSchema;
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
  public ISyncAdapter createMySQLAdapter(String userName,String password,String connectionURL, String tableName){
  
	  return HibernateSyncAdapterFactory.createHibernateAdapter(
			connectionURL,
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
