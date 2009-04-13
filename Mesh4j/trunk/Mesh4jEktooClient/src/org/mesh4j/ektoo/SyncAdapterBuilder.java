package org.mesh4j.ektoo;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.msexcel.MSExcelToPlainXMLMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class SyncAdapterBuilder implements ISyncAdapterBuilder{

	
	@Override
	public ISyncAdapter createMsExcelAdapter(String sheetName,
			String idColumnName,String lastUpdateColName, String contentFileName, String syncFileName,
			IIdentityProvider identityProvider, IdGenerator idGenerator) {
		
		
		MsExcel contentExcel = null;
		MsExcel syncExcel = null;
		if(contentFileName.equals(syncFileName)){
			File file = getFile(contentFileName);
			contentExcel = new MsExcel(file.getAbsolutePath());
			syncExcel = contentExcel;
		} else {
			File fileData = getFile(contentFileName);
			File fileSync = getFile(syncFileName);
			
			contentExcel = new MsExcel(fileData.getAbsolutePath());
			syncExcel = new MsExcel(fileSync.getAbsolutePath());
		}
		
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(syncExcel, identityProvider, idGenerator);
		MSExcelToPlainXMLMapping mapper = new MSExcelToPlainXMLMapping(idColumnName, lastUpdateColName);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(contentExcel, mapper, sheetName);

		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		
		return splitAdapter;
	}

	private File getFile(String fileName) {
		File file = new File(fileName);
		if(!file.exists()){
			Guard.throwsArgumentException(fileName);
		}
		return file;
	}

}
