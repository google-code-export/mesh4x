package org.mesh4j.sync.adapters.googlespreadsheet;

import java.util.ArrayList;

import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

/**
 * @author sharif
 * @version 1.0, 12/5/2009
 *
 */
public class GoogleSpreadSheetRDFSyncAdapterFactory extends GoogleSpreadSheetSyncAdapterFactory{
	
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
	
	@Override
	protected GoogleSpreadSheetContentAdapter createContentAdapter(IGoogleSpreadSheet spreadSheet, String idColumnName, String lastUpdateColumnName, String sheetName, String type) {
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(type, "type");
		
		IRDFSchema rdfSchema;
		try {
			ArrayList<String> pks = new ArrayList<String>();
			pks.add(idColumnName);
			rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(spreadSheet, sheetName, pks, lastUpdateColumnName, this.rdfBaseURL);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		GoogleSpreadsheetToRDFMapping mappings = new GoogleSpreadsheetToRDFMapping(rdfSchema, spreadSheet.getDocsService());
		
		return new GoogleSpreadSheetContentAdapter(spreadSheet, mappings);
	}

	public SplitAdapter createSyncAdapter(String username, String password,
			String spreadsheetName, String cotentSheetName, String idColumnName, String lastUpdateColumnName,
			IIdentityProvider identityProvider, IRDFSchema rdfSchema, String sourceAlias) {		
		Guard.argumentNotNullOrEmptyString(username, "username");
		Guard.argumentNotNullOrEmptyString(password, "password");
		Guard.argumentNotNullOrEmptyString(spreadsheetName, "spreadsheetName");	
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
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
				GoogleSpreadsheetToRDFMapping mappings = new GoogleSpreadsheetToRDFMapping(rdfSchema, GoogleSpreadsheetUtils.getDocService(username, password));
				
				if(spreadStatus == GoogleSpreadsheetUtils.SPREADSHEET_STATUS_SPREADSHEET_NONE){
					//create new spreadsheet
					try{
						spreadsheetName = mappings.createDataSource(spreadsheetName);
					}catch (Exception e) {
						throw new MeshException(e);
					}
				}
				
				//load the new spreadsheet
				IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(spreadsheetName,	username, password);
				
				//TODO:need to review whether keep it or let it handle by the Guard in adapter constructor
				if(spreadSheet.getGSSpreadsheet() == null ) return null;
				
				GoogleSpreadSheetSyncRepository syncRepo = createSyncRepository(
						spreadSheet, cotentSheetName+DEFAULT_SYNCSHEET_POSTFIX, identityProvider);
							
				GoogleSpreadSheetContentAdapter contentAdapter = new GoogleSpreadSheetContentAdapter(spreadSheet, mappings);
				
				return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
			}
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO:
			case GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_YES:{
				//spreadsheet already exists with a valid content worksheet
				SplitAdapter splitAdapter = super.createSyncAdapter(username, password, spreadsheetName, cotentSheetName, idColumnName,  
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
}