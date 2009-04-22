package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.MsExcelModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.validations.Guard;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MsExcelUIController extends AbstractController implements IUIController
{
    private static final String WORKBOOK_NAME_PROPERTY 		= "WorkbookName";
    private static final String WORKSHEET_NAME_PROPERTY 	= "WorksheetName";    
    private static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnName";
    
    
	private ISyncAdapterBuilder adapterBuilder;
	private PropertiesProvider propertiesProvider;
	
	public MsExcelUIController(PropertiesProvider propertiesProvider) 
	{
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
		this.propertiesProvider = propertiesProvider;
	}

	public void changeWorkbookName(String workbookName)
	{
		setModelProperty( WORKBOOK_NAME_PROPERTY , workbookName);
	}

	public void changeWorksheetName(String worksheetName)
	{
		setModelProperty( WORKSHEET_NAME_PROPERTY, worksheetName);
	}

	public void changeUniqueColumnName(String uniqueColumnName)
	{
		setModelProperty( UNIQUE_COLUMN_NAME_PROPERTY, uniqueColumnName);
	}

	
	@Override
	public ISyncAdapter createAdapter() 
	{
		MsExcelModel model = (MsExcelModel)this.getModel();
		return adapterBuilder.createMsExcelAdapter(model.getWorkbookName(), model.getWorksheetName(), model.getUniqueColumnName());
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) 
	{
	}
}
