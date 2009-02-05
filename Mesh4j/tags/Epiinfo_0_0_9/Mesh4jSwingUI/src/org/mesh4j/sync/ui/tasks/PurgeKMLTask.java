package org.mesh4j.sync.ui.tasks;

import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.ui.KmlFrame;
import org.mesh4j.sync.ui.LogFrame;
import org.mesh4j.sync.ui.translator.KmlUITranslator;
import org.mesh4j.sync.validations.MeshException;


public class PurgeKMLTask extends AbstractKmlTask {
 
	public PurgeKMLTask(KmlFrame kmlFrame, String fileName){
		super(kmlFrame, fileName);
	}

	protected void basicProcess(){
		String fileName = getFileName();
		setStatusInProcess(KmlUITranslator.getMessagePurgueKML(fileName));
		
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(fileName, getIdentityProvider());
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.purgue();
			
			setStatusOk(KmlUITranslator.getMessagePurgueKMLCompleted(KmlUITranslator.getMessagePurgueKMLSuccessfuly()));	
		} catch (MeshException e) {
			LogFrame.Logger.error(e.getMessage(), e);
			setStatusError(KmlUITranslator.getMessagePurgueKMLCompleted(KmlUITranslator.getMessagePurgueKMLFailed()));
		}
	}
}
