package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class OpenMySqlFeedTask extends SwingWorker<Void, Void> {

	private final static Log LOGGER = LogFactory.getLog(OpenMySqlFeedTask.class);

	// MODEL VARIABLEs
	private IErrorListener errorListener;
	private JFrame frame;
	private MySQLUIController controller;

	// BUSINESS METHODS
	public OpenMySqlFeedTask(JFrame frame, IErrorListener errorListener, MySQLUIController controller) {
		super();
		this.errorListener = errorListener;
		this.frame = frame;
		this.controller = controller;
	}

	@Override
	public Void doInBackground() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {
			String fileName = this.controller.generateFeed();
			File file = new File(fileName);
			if (file.exists()) {
				OpenFileTask.openFile(this.errorListener, file);
			} else {
				errorListener
						.notifyError(EktooUITranslator
								.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
			}
		} catch (Throwable e) {
			errorListener.notifyError(EktooUITranslator.getErrorOpenBrowser());
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void done() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
