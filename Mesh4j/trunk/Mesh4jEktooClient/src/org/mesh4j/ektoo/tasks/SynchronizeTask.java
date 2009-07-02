package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.EktooController;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.GSSheetUI;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.component.statusbar.Statusbar;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class SynchronizeTask extends SwingWorker<String, Void> {

	private final static Log LOGGER = LogFactory.getLog(SynchronizeTask.class);

	// MODEL VARIABLEs
	private EktooFrame ui;
	private String result = null;
	private ISynchronizeTaskListener synchronizeTaskListener = null;

	// BUSINESS METHODS
	public SynchronizeTask(EktooFrame ui,
			ISynchronizeTaskListener synchronizeTaskListener) {
		super();
		this.ui = ui;
		this.synchronizeTaskListener = synchronizeTaskListener;
	}

	@Override
	public String doInBackground() {
		ui.getBtnSync().setIcon(ImageManager.getSyncIcon(true));
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ui.setStatusbarText(EktooUITranslator.getMessageStartSync(ui
				.getSourceItem().toString(), ui.getTargetItem().toString(),
				new Date()), Statusbar.PROGRESS_STATUS);

		try {
			SyncItemUI sourceItem = ui.getSourceItem();
			SyncItemUI targetItem = ui.getTargetItem();
			result = ui.getController().sync(sourceItem, targetItem);

			return result;
		} catch (Throwable t) {
			ui.getBtnSync().setIcon(ImageManager.getSyncIcon(false));
			ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			LOGGER.error(t.getMessage(), t);
			MessageDialog.showErrorMessage(ui, t.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public void done() {
		ui.getBtnSync().setIcon(ImageManager.getSyncIcon(false));
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		try {
			result = get();
			if (result != null) {
				if (result
						.equals(EktooController.SYNCHRONIZATION_ERROR_CREATING_ADAPTER)) {
					synchronizeTaskListener
					.notifySynchronizeTaskError(EktooUITranslator
							.getMessageSyncErrorInAdapterCreation(ui
									.getSourceItem().toString(), ui
									.getTargetItem().toString(),
									new Date()));
				} else if (result
						.equals(EktooController.SYNCHRONIZATION_SUCCEED)) {
					synchronizeTaskListener
							.notifySynchronizeTaskSuccess(EktooUITranslator
									.getMessageSyncSyccessfuly(ui
											.getSourceItem().toString(), ui
											.getTargetItem().toString(),
											new Date()));
					ui.getSourceItem().cleanMessaged();
					ui.getTargetItem().cleanMessaged();
					
					// this is to handle an exceptional situation when user
					// choose to create a new spreadsheet on target 
					if (ui.getTargetItem().getCurrentView() instanceof GSSheetUI){
						GSSheetUI gsUI = (GSSheetUI) ui.getTargetItem().getCurrentView();
						if( gsUI.getNewSpreadsheetNameIndex() != -1){
							Object oldSelection = gsUI.getNameList().getSelectedItem();
							gsUI.getConnectButton().doClick();
							//TODO: need to make current thread waiting till the click event finish
							gsUI.getNameList().setSelectedItem(oldSelection);
						}
					}
					
				} else if (result
						.equals(EktooController.SYNCHRONIZATION_CONFLICTED)) {
					synchronizeTaskListener
							.notifySynchronizeTaskConflict(EktooUITranslator
									.getMessageSyncConflicts(ui.getSourceItem()
											.toString(), ui.getTargetItem()
											.toString(), new Date()));
				} else if (result
						.equals(EktooController.SYNCHRONIZATION_FAILED)) {
					synchronizeTaskListener
							.notifySynchronizeTaskError(EktooUITranslator
									.getMessageSyncFailed(ui.getSourceItem()
											.toString(), ui.getTargetItem()
											.toString(), new Date()));
				}
			} else {
				synchronizeTaskListener
						.notifySynchronizeTaskError(EktooUITranslator
								.getMessageSyncFailed(ui.getSourceItem()
										.toString(), ui.getTargetItem()
										.toString(), new Date()));
			}
		} catch (InterruptedException e) {
			synchronizeTaskListener
					.notifySynchronizeTaskError(EktooUITranslator
							.getMessageSyncFailed(
									ui.getSourceItem().toString(), ui
											.getTargetItem().toString(),
									new Date()));

			LOGGER.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			synchronizeTaskListener
					.notifySynchronizeTaskError(EktooUITranslator
							.getMessageSyncFailed(
									ui.getSourceItem().toString(), ui
											.getTargetItem().toString(),
									new Date()));
			LOGGER.error(e.getMessage(), e);
		}
		//ui.showSyncImageLabel(false);
	}

}
