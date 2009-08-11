package org.mesh4j.ektoo.ui.settings;

import java.util.List;

import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
//TODO (raju) must come up with a better solution.change the AbstractUIController with no
//implementation of IUIController,subclass of AbstractUIController will implement the IUIController
//AbstractUIController will be working as the propagation of flow between model and view.

public class AbstractSettingsController extends AbstractUIController{

	AbstractSettingsController(){
	}
	@Override
	public ISyncAdapter createAdapter() {
		try {
			throw new NoSuchMethodError("Not supported");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		try {
			throw new NoSuchMethodError("Not supported");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		try {
			throw new NoSuchMethodError("Not supported");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
