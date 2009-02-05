package org.mesh4j.sync.ui.tasks;

import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.ui.KmlFrame;
import org.mesh4j.sync.ui.LogFrame;
import org.mesh4j.sync.ui.translator.KmlUITranslator;
import org.mesh4j.sync.validations.MeshException;

public class PrepareKMLToSyncTask extends AbstractKmlTask {

	public PrepareKMLToSyncTask(KmlFrame kmlFrame, String fileName){
		super(kmlFrame, fileName);
	}

	protected void basicProcess(){
		String fileName = getFileName();
		setStatusInProcess(KmlUITranslator.getMessagePrepareToSync(fileName));
		
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(getFileName(), getIdentityProvider());
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.prepareDOMToSync();
			
			setStatusOk(KmlUITranslator.getMessagePrepareToSyncCompleted(KmlUITranslator.getMessagePrepareToSyncSuccessfuly()));	
		} catch (MeshException e) {
			LogFrame.Logger.error(e.getMessage(), e);
			setStatusError(KmlUITranslator.getMessagePrepareToSyncCompleted(KmlUITranslator.getMessagePrepareToSyncFailed()));
		}
	}
}
