package org.mesh4j.sync.adapters.msexcel;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;

public class MsExcelSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "MsExcel";
	private static final String MS_EXCEL = SOURCE_TYPE+":";
	
	// BUSINESS METHODS
	public MsExcelSyncAdapterFactory(){
		super();
	}
	
	public static String createSourceDefinition(String excelFileName, String sheetName, String idColumn){
		File file = new File(excelFileName);
		String sourceDefinition = MS_EXCEL + file.getName() + "@" + sheetName + "@" + idColumn;
		return sourceDefinition;
	}
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		if(sourceDefinition == null){
			return false;
		}
		
		String[] elements = sourceDefinition.split("@");
		return sourceDefinition.toUpperCase().startsWith(MS_EXCEL.toUpperCase()) && elements.length == 4 
			&& (elements[1].toUpperCase().endsWith(".XLS") || elements[1].toUpperCase().endsWith(".XLSX"));
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		
		String[] elements = sourceDefinition.substring(MS_EXCEL.length(), sourceDefinition.length()).split("@");
		String excelFileName = elements[0];
		String sheetName = elements[1];
		String idColumnName = elements[2];
		
		return createSyncAdapter(excelFileName, sheetName, idColumnName, identityProvider);
	}

	public SplitAdapter createSyncAdapter(String excelFileName, String sheetName, String idColumnName, IIdentityProvider identityProvider) {
		MsExcel excel = new MsExcel(excelFileName);
		MsExcelSyncRepository syncRepo = createSyncRepository(identityProvider, excel);
		MsExcelContentAdapter contentAdapter = createContentAdapter(sheetName, idColumnName, excel);
		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
	}

	protected MsExcelContentAdapter createContentAdapter(String sheetName, String idColumnName, IMsExcel excel) {
		MSExcelToPlainXMLMapping mapping = new MSExcelToPlainXMLMapping(idColumnName, null);
		return new MsExcelContentAdapter(excel, mapping, sheetName);
	}

	protected MsExcelSyncRepository createSyncRepository(IIdentityProvider identityProvider, IMsExcel excel) {
		return new MsExcelSyncRepository(excel, identityProvider, IdGenerator.INSTANCE);
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}
}
