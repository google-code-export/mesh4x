package org.mesh4j.sync.adapters.googlespreadsheet;

import java.util.Arrays;

import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

/**
 * @author sharif
 * @version 1.0, 12/5/2009
 */
public class GoogleSpreadSheetRDFSyncAdapterFactory {
	
	// MODEL VARIABLES
	private String rdfBaseURL;
	
	static final String DEFAULT_NEW_SPREADSHEET_FILENAME = "new spreadsheet";
	static final Byte SPREADSHEET_STATUS_SPREADSHEET_NONE = 0;
	static final Byte SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_YES = 1;
	static final Byte SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO = 2;
	static final Byte SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_YES = 3;

	// BUSINESS METHODS
	public GoogleSpreadSheetRDFSyncAdapterFactory(String rdfBaseURL){
		super();
		Guard.argumentNotNullOrEmptyString(rdfBaseURL, "rdfBaseURL");
		this.rdfBaseURL = rdfBaseURL;
	}
	
	
	protected GoogleSpreadSheetContentAdapter createContentAdapter(IGoogleSpreadSheet spreadSheet, String[] idColumnNames, String lastUpdateColumnName, String sheetName, String type) {
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
//		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		if(idColumnNames.length == 0){
			Guard.throwsArgumentException("idColumnNames");
		}
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(type, "type");
		
		IRDFSchema rdfSchema;
		try {
//			ArrayList<String> pks = new ArrayList<String>();
//			pks.add(idColumnName);
			rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(spreadSheet, sheetName, Arrays.asList(idColumnNames), this.rdfBaseURL);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		GoogleSpreadsheetToRDFMapping mappings = new GoogleSpreadsheetToRDFMapping(rdfSchema);
		
		return new GoogleSpreadSheetContentAdapter(spreadSheet, mappings);
	}

	public SplitAdapter createSyncAdapter(String username, String password,
			String spreadsheetName, String cotentSheetName, String[] idColumnNames, String lastUpdateColumnName,
			IIdentityProvider identityProvider, IRDFSchema rdfSchema, String sourceAlias) {		
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");
		Guard.argumentNotNullOrEmptyString(spreadsheetName, "spreadsheetName");	
//		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		if(idColumnNames.length == 0){
			Guard.throwsArgumentException("idColumnNames");
		}
		Guard.argumentNotNullOrEmptyString(cotentSheetName, "cotentSheetName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		
		int spreadStatus;
		try {
			spreadStatus = GoogleSpreadsheetUtils.getSpreadsheetStatus(username, password, spreadsheetName, cotentSheetName);
		} catch (Exception ex) {
			throw new MeshException(ex);
		}
		
		switch(spreadStatus){
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_SPREADSHEET_NONE:
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_NO:	
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_YES:{
				//spreadsheet doesn't exists
				GoogleSpreadsheetToRDFMapping mappings = new GoogleSpreadsheetToRDFMapping(rdfSchema);
				IGoogleSpreadSheet spreadSheet = null;
				
				if(spreadStatus == GoogleSpreadsheetUtils.SPREADSHEET_STATUS_SPREADSHEET_NONE){
					//create new spreadsheet
					try{
						spreadSheet = mappings.createDataSource(spreadsheetName, username, password);
					}catch (Exception e) {
						throw new MeshException(e);
					}
				}
				
				//load the new spreadsheet
				//IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadsheetName,	username, password);
				
				//TODO:need to review whether keep it or let it handle by the Guard in adapter constructor
				if(spreadSheet.getGSSpreadsheet() == null ) return null;
				
//				GoogleSpreadSheetSyncRepository syncRepo = createSyncRepository(
//						spreadSheet, cotentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX, identityProvider);
				GoogleSpreadSheetSyncRepository syncRepo = new GoogleSpreadSheetSyncRepository(
					spreadSheet, identityProvider, IdGenerator.INSTANCE,
					cotentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX);
				
				GoogleSpreadSheetContentAdapter contentAdapter = new GoogleSpreadSheetContentAdapter(spreadSheet, mappings);
				
				return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
			}
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO:
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_YES:{
				//spreadsheet already exists with a valid content worksheet
				SplitAdapter splitAdapter = createSyncAdapter(username, password, spreadsheetName, cotentSheetName, idColumnNames,  
						lastUpdateColumnName, identityProvider, sourceAlias);
					
				//createSyncAdapter(excelFileName, sheetName, idColumnName, identityProvider);
				IRDFSchema rdfSchemaAutoGenetated = (IRDFSchema)((GoogleSpreadSheetContentAdapter)splitAdapter.getContentAdapter()).getSchema();
				//if(!rdfSchema.asXML().equals(rdfSchemaAutoGenetated.asXML())){
				if(!rdfSchema.isCompatible(rdfSchemaAutoGenetated)){
					Guard.throwsException("INVALID_RDF_SCHEMA");
				}
				return splitAdapter;	
			}	
		}
		return null;	
	}

	/**
	 * This is only used when google spreadsheet is already loaded 
	 * 
	 * @param gss
	 * @param cotentSheetName
	 * @param idColumnName
	 * @param lastUpdateColumnName
	 * @param identityProvider
	 * @param sourceSchema
	 * @param sourceAlias
	 * @return
	 */
	public SplitAdapter createSyncAdapter(IGoogleSpreadSheet spreadSheet, String cotentSheetName, 
			String[] idColumnNames, String lastUpdateColumnName, IIdentityProvider 
			identityProvider, IRDFSchema sourceSchema, String sourceAlias) {
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
//		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		if(idColumnNames.length == 0){
			Guard.throwsArgumentException("idColumnNames");
		}
		Guard.argumentNotNullOrEmptyString(cotentSheetName, "cotentSheetName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(sourceSchema, "rdfSchema");
		
		int	spreadStatus = GoogleSpreadsheetUtils.getSpreadsheetStatus(spreadSheet, cotentSheetName);
		
		switch(spreadStatus){
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_SPREADSHEET_NONE:{
				throw new MeshException("No spreadsheet loaded...");
			}
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_NO:	
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_YES:{

				GoogleSpreadsheetToRDFMapping mappings = new GoogleSpreadsheetToRDFMapping(sourceSchema);
				
//				GoogleSpreadSheetSyncRepository syncRepo = createSyncRepository(
//						spreadSheet, cotentSheetName+GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX, identityProvider);
				GoogleSpreadSheetSyncRepository syncRepo = new GoogleSpreadSheetSyncRepository(
						spreadSheet, identityProvider, IdGenerator.INSTANCE,
						cotentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX);
				
				GoogleSpreadSheetContentAdapter contentAdapter = new GoogleSpreadSheetContentAdapter(spreadSheet, mappings);
				
				return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
			}
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO:
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_YES:{
				//spreadsheet already exists with a valid content worksheet
				SplitAdapter splitAdapter = createSyncAdapter(spreadSheet, cotentSheetName, idColumnNames,  
						lastUpdateColumnName, identityProvider, sourceAlias);
					
				//createSyncAdapter(excelFileName, sheetName, idColumnName, identityProvider);
				IRDFSchema rdfSchemaAutoGenetated = (IRDFSchema)((GoogleSpreadSheetContentAdapter)splitAdapter.getContentAdapter()).getSchema();
				//if(!rdfSchema.asXML().equals(rdfSchemaAutoGenetated.asXML())){
				if(!sourceSchema.isCompatible(rdfSchemaAutoGenetated)){
					Guard.throwsException("INVALID_RDF_SCHEMA");
				}
				return splitAdapter;	
			}	
		}
		return null;	
	}	
	
	public SplitAdapter createSyncAdapter(String username, String password,
			String spreadsheetName, String contentSheetName,
			String[] idColumnNames, String lastUpdateColumnName,
			IIdentityProvider identityProvider, String sourceAlias) {

		IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(
				spreadsheetName, username, password);
		
		//TODO:need to review whether keep it or let it handle by the Guard in adapter constructor
		if(spreadSheet.getGSSpreadsheet() == null ) return null;

//		GoogleSpreadSheetSyncRepository syncRepo = createSyncRepository(
//				spreadSheet, contentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX,
//				identityProvider);
		GoogleSpreadSheetSyncRepository syncRepo = new GoogleSpreadSheetSyncRepository(
				spreadSheet, identityProvider, IdGenerator.INSTANCE,
				contentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX);

		GoogleSpreadSheetContentAdapter contentAdapter = createContentAdapter(
				spreadSheet, idColumnNames, lastUpdateColumnName,
				contentSheetName, sourceAlias);

		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
	}

	/**
	 * this method should be used when spreadSheet is already loaded previously
	 * 
	 * @param spreadSheet
	 * @param contentSheetName
	 * @param idColumnName
	 * @param lastUpdateColumnName
	 * @param identityProvider
	 * @param sourceAlias
	 * @return
	 */
	public SplitAdapter createSyncAdapter(IGoogleSpreadSheet spreadSheet,
			String contentSheetName, String[] idColumnNames,
			String lastUpdateColumnName, IIdentityProvider identityProvider,
			String sourceAlias) {
		
		Guard.argumentNotNull(spreadSheet,"spreadSheet");
		Guard.argumentNotNull(spreadSheet.getGSSpreadsheet(),"gsSpreadsheet");

//		GoogleSpreadSheetSyncRepository syncRepo = createSyncRepository(
//				spreadSheet, contentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX,
//				identityProvider);
		GoogleSpreadSheetSyncRepository syncRepo = new GoogleSpreadSheetSyncRepository(
				spreadSheet, identityProvider, IdGenerator.INSTANCE,
				contentSheetName + GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX);

		GoogleSpreadSheetContentAdapter contentAdapter = createContentAdapter(
				spreadSheet, idColumnNames, lastUpdateColumnName,
				contentSheetName, sourceAlias);

		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);	
	}	
	
}