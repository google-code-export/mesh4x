/**
 *
 */
package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.tasks.SynchronizeTask;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class EktooUI extends JFrame {

	private static final long serialVersionUID = -8703829301086394863L;
	private final Log LOGGER = LogFactory.getLog(EktooUI.class);

	// MODEL VARIABLES
	private SyncItemUI sourceItem = null;
	private SyncItemUI targetItem = null;

	private JPanel panel = null;
	private JButton btnSync = null;

	private JLabel labelConsole = null;

	private EktooUIController controller;

	// BUSINESS METHODS
	public EktooUI(EktooUIController controller) {
		super();
		initialize();
		this.controller = controller;
	}

	private void initialize() {
		this.setSize(new Dimension(564, 511));
		this.setContentPane(getJPanel());
	}

	private JPanel getJPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(null);
			panel.add(getSourcePane(), null);
			panel.add(getTargetPane(), null);
			// panel.add(getViaPane(), null);
			// panel.add(getTypePane(), null);
			panel.add(getBtnSync(), null);
			panel.add(getConsole(), null);
		}
		return panel;
	}

	private JLabel getConsole() {
		if (labelConsole == null) {
			labelConsole = new JLabel();
			labelConsole.setSize(500, 16);
			labelConsole.setLocation(new Point(10, 400));
			labelConsole.setBackground(Color.red);
			// labelConsole.setText("Console....");
		}

		return labelConsole;
	}

	private JPanel getSourcePane() {
		if (getSourceItem() == null) {
			setSourceItem(new SyncItemUI(EktooUITranslator
					.getSourceSyncItemSelectorTitle()));
			getSourceItem().setSize(new Dimension(350, 190));
			getSourceItem().setLocation(new Point(10, 10));
		}

		return getSourceItem();
	}

	private JPanel getTargetPane() {
		if (getTargetItem() == null) {
			setTargetItem(new SyncItemUI(EktooUITranslator
					.getTargetSyncItemSelectorTitle()));
			getTargetItem().setSize(new Dimension(350, 190));
			getTargetItem().setLocation(new Point(10, 210));
		}

		return getTargetItem();
	}

	private JButton getBtnSync() {
		if (btnSync == null) {
			btnSync = new JButton();
			btnSync.setBounds(new Rectangle(315, 427, 127, 28));
			btnSync.setText(EktooUITranslator.getSyncLabel());
			btnSync.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					log("actionPerformed()");
					labelConsole.setText("");
					SwingWorker<String, Void> task = new SynchronizeTask(
							EktooUI.this);
					task.execute();
					log("Calling Sync...");
				}
			});
		}
		return btnSync;
	}


	private void log(String msg) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(msg);
		}
	}

	public void setController(EktooUIController controller) {
		this.controller = controller;
	}

	public EktooUIController getController() {
		return controller;
	}

	public void setSourceItem(SyncItemUI sourceItem) {
		this.sourceItem = sourceItem;
	}

	public SyncItemUI getSourceItem() {
		return sourceItem;
	}

	public void setTargetItem(SyncItemUI targetItem) {
		this.targetItem = targetItem;
	}

	public SyncItemUI getTargetItem() {
		return targetItem;
	}

	public void setConsole(String msg) {
		labelConsole.setText(msg);
	}
}
