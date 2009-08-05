package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.FolderUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenFileTask;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.FolderUIValidator;

public class FolderUI extends AbstractUI {

	private static final long serialVersionUID = 8670884881480486083L;

	private static final Log LOGGER = LogFactory.getLog(FolderUI.class);

	// MODEL VARIABLES
	private JLabel labelFileName = null;
	private JTextField txtFileName = null;
	private JButton btnFile = null;

	private JFileChooser fileChooser = null;

	// BUSINESS METHODS
	public FolderUI(String fileName, FolderUIController controller) {
		super(controller);
		this.initialize();
		this.txtFileName.setText(fileName);
		this.txtFileName.setToolTipText(fileName);
	}

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		this.add(getFileNameLabel(), null);
		this.add(getFileNameText(), null);
		this.add(getBtnFile(), null);
		this.add(getBtnView(), null);
		this.add(getConflictsButton());
		this.add(getMessagesText(), null);
	}

	private JLabel getFileNameLabel() {
		if (labelFileName == null) {
			labelFileName = new JLabel();
			labelFileName.setText(EktooUITranslator.getFolderFileNameLabel());
			labelFileName.setSize(new Dimension(85, 16));
			labelFileName.setPreferredSize(new Dimension(85, 16));
			labelFileName.setLocation(new Point(8, 9));
		}
		return labelFileName;
	}

	public JTextField getFileNameText() {
		if (txtFileName == null) {
			txtFileName = new JTextField();
			txtFileName.setBounds(new Rectangle(99, 8, 149, 20));
			txtFileName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					fileNameChanged(txtFileName.getText());
				}
			});
			txtFileName.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					fileNameChanged(txtFileName.getText());		
				}
			});		
		}
		return txtFileName;
	}

	public JButton getBtnFile() {
		if (btnFile == null) {
			btnFile = new JButton();
			btnFile.setText(EktooUITranslator.getBrowseButtonLabel());
			btnFile.setBounds(new Rectangle(259, 8, 34, 20));
			btnFile.setToolTipText(EktooUITranslator
					.getTooltipFolderSeleceFile());
			btnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getFileChooser().setSelectedFile(new File(txtFileName.getText()));
					int returnVal = getFileChooser().showOpenDialog(btnFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = getFileChooser().getSelectedFile();
						if (selectedFile != null) {
							try {
								txtFileName.setText(selectedFile.getCanonicalPath());
								fileNameChanged(txtFileName.getText());								
							} catch (Exception ex) {
								LOGGER.error(ex.getMessage(), ex);
							}
						}
					}
				}
			});
		}
		return btnFile;
	}

	private JButton getBtnView() {
		getViewButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = FolderUI.this.getRootFrame();
					OpenFileTask task = new OpenFileTask(frame, (IErrorListener) frame, txtFileName.getText());
					task.execute();
				}
			});
		return getViewButton();
	}
	
	public FolderUIController getController() {
		return (FolderUIController)controller;
	}

	public JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return fileChooser;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				FolderUIController.FOLDER_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!getFileNameText().getText().equals(newStringValue))
				getFileNameText().setText(newStringValue);
		}
	}

	@Override
	public boolean verify() {
		boolean valid = (new FolderUIValidator(this, controller.getModel(), null)).verify();
		return valid;
	}

	protected void fileNameChanged(String fileName) {
		txtFileName.setToolTipText(fileName);
		getController().changeFileName(fileName);
		
		File file = new File(fileName);
		if(!file.exists()){
			this.setMessageText(EktooUITranslator.getMessageNewFolder());
		} else {
			this.setMessageText(EktooUITranslator.getMessageUpdateFolder());
		}
	}
	
}