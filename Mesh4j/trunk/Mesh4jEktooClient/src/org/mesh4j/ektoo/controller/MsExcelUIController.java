package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.MsExcelModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsExcelUIController extends AbstractController 
{
	
	private static final String WORKBOOK_NAME_PROPERTY = "WorkbookName";
	private static final String WORKSHEET_NAME_PROPERTY = "WorksheetName";
	private static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnName";

	// MODEL VARIABLES
	private ISyncAdapterBuilder adapterBuilder;
	

	// BUSINESS METHODS
	public MsExcelUIController(PropertiesProvider propertiesProvider) 
	{
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public void changeWorkbookName(String workbookName) {
		setModelProperty(WORKBOOK_NAME_PROPERTY, workbookName);
	}

	public void changeWorksheetName(String worksheetName) {
		setModelProperty(WORKSHEET_NAME_PROPERTY, worksheetName);
	}

	public void changeUniqueColumnName(String uniqueColumnName) {
		setModelProperty(UNIQUE_COLUMN_NAME_PROPERTY, uniqueColumnName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		MsExcelModel model = (MsExcelModel) this.getModel();
		if (model == null){
			return null;
		}

		String workbookName = model.getWorkbookName();
		if (workbookName == null || workbookName.trim().length() == 0){
			return null;
		}

		String worksheetName = model.getWorksheetName();
		if (worksheetName == null || worksheetName.trim().length() == 0){
			return null;
		}

		String uniqueColumnName = model.getUniqueColumnName();
		if (uniqueColumnName == null || uniqueColumnName.trim().length() == 0){
			return null;
		}
		return adapterBuilder.createMsExcelAdapter(workbookName, worksheetName,
				uniqueColumnName);
	}

	@Override
	public ISyncAdapter createAdapter(IRDFSchema sourceSchema) {
		MsExcelModel model = (MsExcelModel) this.getModel();
		if (model == null){
			return null;
		}

		String workbookName = model.getWorkbookName();
		if (workbookName == null || workbookName.trim().length() == 0){
			return null;
		}

		String worksheetName = model.getWorksheetName();
		if (worksheetName == null || worksheetName.trim().length() == 0){
			return null;
		}

		String uniqueColumnName = model.getUniqueColumnName();
		if (uniqueColumnName == null || uniqueColumnName.trim().length() == 0){
			return null;
		}

		return adapterBuilder.createMsExcelAdapter(workbookName, worksheetName, uniqueColumnName, sourceSchema);
	}

	@Override
	public IRDFSchema fetchSchema() 
	{
	  MsExcelModel model = (MsExcelModel) this.getModel();
    if (model == null){
      return null;
    }

    String workbookName = model.getWorkbookName();
    if (workbookName == null || workbookName.trim().length() == 0){
      return null;
    }

    String worksheetName = model.getWorksheetName();
    if (worksheetName == null || worksheetName.trim().length() == 0){
      return null;
    }

    String uniqueColumnName = model.getUniqueColumnName();
    if (uniqueColumnName == null || uniqueColumnName.trim().length() == 0){
      return null;
    }
	  
	  MsExcel excel = new MsExcel(workbookName);
	  IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, worksheetName,  new PropertiesProvider().getMeshSyncServerURL());
	  return rdfSchema;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO (NBL) property Change
	}
}
