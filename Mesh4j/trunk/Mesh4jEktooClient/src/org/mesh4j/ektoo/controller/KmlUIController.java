package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class KmlUIController implements IUIController
{
	ISyncAdapterBuilder adapterBuilder;

	public KmlUIController(PropertiesProvider propertiesProvider) 
	{
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	@Override
	public ISyncAdapter createAdapter() 
	{
		return null;
	}

  @Override
  public IRDFSchema createSchema()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISyncAdapter createAdapter(IRDFSchema schema)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
