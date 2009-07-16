package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class OpenFileTask extends SwingWorker<Void, Void> {

	private final static Log LOGGER = LogFactory.getLog(OpenFileTask.class);

	// MODEL VARIABLEs
	private String fileName;
	private IErrorListener errorListener;
	private JFrame frame;

	// BUSINESS METHODS
	public OpenFileTask(JFrame frame, IErrorListener errorListener,
			String fileName) {
		super();
		this.fileName = fileName;
		this.errorListener = errorListener;
		this.frame = frame;
	}

	@Override
	public Void doInBackground() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {
			File file = new File(fileName);
			if (file.exists()) {
				openFile(this.errorListener, file);
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

	public static void openFile(IErrorListener errorListener, String fileName)
			throws IOException {
		openFile(errorListener, new File(fileName));
	}

	public static void openFile(IErrorListener errorListener, File file)
			throws IOException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.OPEN)) {
				if (file.exists()) {
					desktop.open(file.getCanonicalFile());
				} else {
					errorListener
							.notifyError(EktooUITranslator
									.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
				}
			} else {
				errorListener.notifyError(EktooUITranslator
						.getErrorOpenFileActionNotSupported());
			}
		} else {
			errorListener.notifyError(EktooUITranslator
					.getErrorOpenFileActionNotSupported());
		}
	}

	@Override
	public void done() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
