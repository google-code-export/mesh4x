package org.mesh4j.ektoo.ui.mappings;

import java.awt.Cursor;
import java.awt.Window;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.ektoo.tasks.OpenFileTask;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.kml.exporter.KMLExporter;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class OpenMapTask extends SwingWorker<Void, Void> {

	private final static Log LOGGER = LogFactory.getLog(OpenMapTask.class);

	// MODEL VARIABLEs
	private IMapTaskListener taskListener;
	private Window frame;
	private AbstractUIController controller;
	private ISyncAdapter adapter;
	private IRDFSchema rdfSchema;
	private Mapping mappings;
	
	// BUSINESS METHODS
	public OpenMapTask(Window frame, IMapTaskListener taskListener, AbstractUIController controller, ISyncAdapter adapter, IRDFSchema rdfSchema, Mapping mappings) {
		super();
		this.taskListener = taskListener;
		this.frame = frame;
		this.controller = controller;
		this.adapter = adapter;
		this.rdfSchema = rdfSchema;
		this.mappings = mappings;
	}

	@Override
	public Void doInBackground() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {
			if(mappings != null){
				taskListener.notifyProgress("");
				String fileName = controller.getAdapterBuilder().makeTempFileName(rdfSchema.getOntologyClassName() + ".kml");
				taskListener.notifyProgress(EktooUITranslator.getMapConfigurationMessageKMLFileGeneration());
				KMLExporter.export(fileName, adapter, rdfSchema, mappings);
				taskListener.notifyProgress(EktooUITranslator.getMapConfigurationMessageOpenKMLFile());
				OpenFileTask.openFile(taskListener, fileName);
				taskListener.notifyProgress("");
			} else {
				taskListener.notifyError(EktooUITranslator.getMapConfigurationErrorRequiredFields());
			}
		} catch (Throwable e) {
			taskListener.notifyError(EktooUITranslator.getErrorOpenBrowser());
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void done() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
