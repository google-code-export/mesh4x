package org.mesh4j.sync.adapters.msexcel;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class MsExcelSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "MS_EXCEL";
	
	// MODEL VARIABLES
	private String baseDirectory;
	
	// BUSINESS METHODS
	public MsExcelSyncAdapterFactory(String baseDirectory){
		Guard.argumentNotNull(baseDirectory, "baseDirectory");
		
		this.baseDirectory = baseDirectory;
	}
	
	public static String createSourceId(String excelFileName, String sheetName, String idColumn){
		File file = new File(excelFileName);
		String sourceID = "excel:" + file.getName() + "@" + sheetName + "@" + idColumn;
		return sourceID;
	}
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		String[] elements = sourceId.split("@");
		return sourceId.toUpperCase().startsWith("EXCEL:") && elements.length == 4 
			&& (elements[1].toUpperCase().endsWith(".XLS") || elements[1].toUpperCase().endsWith(".XLSX"));
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		
		String[] elements = sourceId.substring("excel:".length(), sourceId.length()).split("@");
		String excelFileName = elements[0];
		String sheetName = elements[1];
		String idColumnName = elements[2];
		
		MsExcel excel = new MsExcel(this.baseDirectory+"/" + excelFileName);
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(excel, identityProvider, IdGenerator.INSTANCE);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, sheetName, idColumnName);
		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
	}

	@Override
	public String getSourceName(String sourceId) {
		String[] elements = sourceId.substring("excel:".length(), sourceId.length()).split("@");
		//String excelFileName = elements[0];
		String sheetName = elements[1];
		return sheetName;
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}

}
