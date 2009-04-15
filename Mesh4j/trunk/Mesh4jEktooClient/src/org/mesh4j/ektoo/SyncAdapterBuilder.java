package org.mesh4j.ektoo;

import java.io.File;

import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.ISpreadSheetToXMLMapper;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.SpreadSheetToXMLMapper;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MSExcelToPlainXMLMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SyncAdapterBuilder implements ISyncAdapterBuilder{

	
	@Override
	public ISyncAdapter createMsExcelAdapter(String sheetName,
			String idColumnName, String contentFileName, String syncFileName,
			IIdentityProvider identityProvider, IdGenerator idGenerator) {
		
		
		
		MsExcel contentExcel = null;
		MsExcel syncExcel = null;
		//TODO if file doesn't exist then create file with the help of schema
		//request the client to provide the schema
		if(contentFileName.equals(syncFileName)){
			File file = getFile(contentFileName);
			contentExcel = new MsExcel(file.getAbsolutePath());
			syncExcel = contentExcel;
		} else {
			File contentData = getFile(contentFileName);
			File syncData = getFile(syncFileName);
			
			contentExcel = new MsExcel(contentData.getAbsolutePath());
			syncExcel = new MsExcel(syncData.getAbsolutePath());
		}
		
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(syncExcel, identityProvider, idGenerator);
		MSExcelToPlainXMLMapping mapper = new MSExcelToPlainXMLMapping(idColumnName, null);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(contentExcel, mapper, sheetName);

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		
		return splitAdapter;
	}


	@Override
	public ISyncAdapter createMsAccessAdapter(String baseDirectory,String rdfUrl,String sourceAlias,
										String mdbFileName, String tableName) {

		MsAccessSyncAdapterFactory msAccesSyncAdapter  = new MsAccessSyncAdapterFactory(baseDirectory,rdfUrl);
		try {
			return msAccesSyncAdapter.createSyncAdapterFromFile(sourceAlias, mdbFileName, tableName);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@Override
	public ISyncAdapter createGoogleSpreadSheetAdapter(GoogleSpreadSheetInfo spreadSheetInfo){
		
		String idColumName = spreadSheetInfo.getIdColumnName();
		int lastUpdateColumnPosition = spreadSheetInfo.getLastUpdateColumnPosition();
		int idColumnPosition = spreadSheetInfo.getIdColumnPosition();
		String userName = spreadSheetInfo.getUserName();
		String passWord = spreadSheetInfo.getPassWord();
		String GOOGLE_SPREADSHEET_FIELD = spreadSheetInfo.getGOOGLE_SPREADSHEET_FIELD();
		
		ISpreadSheetToXMLMapper mapper = new SpreadSheetToXMLMapper(idColumName,idColumnPosition,lastUpdateColumnPosition);
		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		
		GSWorksheet contentWorkSheet = spreadsheet.getGSWorksheet(spreadSheetInfo.getContentWorkSheetIndex());
		GSWorksheet syncWorkSheet = spreadsheet.getGSWorksheet(spreadSheetInfo.getSyncWorkSheetIndex()); 
	
		SplitAdapter spreadSheetAdapter = GoogleSpreadsheetUtils.createGoogleSpreadSheetAdapter(spreadsheet,mapper,contentWorkSheet,
				syncWorkSheet,spreadSheetInfo.getIdentityProvider(),spreadSheetInfo.getIdGenerator());
		
			
		return spreadSheetAdapter;
	}
	private File getFile(String fileName) {
		File file = new File(fileName);
		if(!file.exists()){
			Guard.throwsArgumentException(fileName);
		}
		return file;
	}
	
}
