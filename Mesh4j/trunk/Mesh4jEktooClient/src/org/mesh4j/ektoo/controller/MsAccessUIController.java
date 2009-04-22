package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.MsAccessModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.validations.Guard;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MsAccessUIController extends AbstractController implements IUIController
{
  private static final String DATABASE_NAME_PROPERTY 	= "DatabaseName";
  private static final String TABLE_NAME_PROPERTY 	= "TableName";    
    	
	private ISyncAdapterBuilder adapterBuilder;
	private PropertiesProvider propertiesProvider;
	
	public MsAccessUIController(PropertiesProvider propertiesProvider) 
	{
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.propertiesProvider = propertiesProvider;
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public void changeDatabaseName(String databaseName)
	{
		System.out.println("MsAccessUIController>changeDatabaseName");
		setModelProperty( DATABASE_NAME_PROPERTY, databaseName);
	}

	public void changeTableName(String tableName)
	{
		System.out.println("MsAccessUIController>changeTableName");
		setModelProperty( TABLE_NAME_PROPERTY, tableName);
	}
	
	@Override
	public ISyncAdapter createAdapter() 
	{
		MsAccessModel model = (MsAccessModel)this.getModel();
		return adapterBuilder.createMsAccessAdapter(model.getDatabaseName(), model.getTabletName());
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) 
	{
	}
}
