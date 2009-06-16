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
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsAccessMultiTableUIController;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenFileTask;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsAccessMultiTableUIValidator;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;

public class MsAccessMultiTableUI extends AbstractUI{

	private static final long serialVersionUID = 4708875346159085594L;
	private static final Log LOGGER = LogFactory.getLog(MsAccessMultiTableUI.class);

	// MODEL VARIABLES
	private JFileChooser fileChooser = new JFileChooser();

	private JLabel labelFile = null;
	private JTextField txtFileName = null;

	private JLabel labelTables = null;
	private JList listTables = null;

	private JButton btnFile = null;
	private JButton btnView = null;
	
	private MsAccessMultiTableUIController controller;

	// BUSINESS METHODS
	public MsAccessMultiTableUI(String fileName, MsAccessMultiTableUIController controller) {
		super();
		initialize();
		
		this.controller = controller;
		this.controller.addView(this);

		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().setFileFilter(
				new FileNameExtensionFilter(EktooUITranslator
						.getMSAccessFileSelectorTitle(), "mdb", "MDB"));
		this.getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

		getTxtFile().setText(fileName);
		getTxtFile().setToolTipText(fileName);
		changeDatabaseName(fileName);
	}
	
	protected void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		this.add(getLabelFile(), null);
		this.add(getTxtFile(), null);
		this.add(getBtnFile(), null);
		this.add(getBtnView(), null);

		this.add(getlabelTables(), null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		scrollPane.setViewportView(getListTables());
		scrollPane.setBounds(new Rectangle(99, 36, 194, 90));
		
		this.add(scrollPane, null);
		this.add(getMessagesText(), null);
	}

	public void changeDatabaseName(String fileName) {
		this.getListTables().setSelectedIndex(-1);
		
		DefaultListModel listModel = (DefaultListModel)this.getListTables().getModel();
		listModel.removeAllElements();

		try {
			File file = new File(fileName);
			if(file.exists()){
				Set<String> tableNames = MsAccessSyncAdapterFactory.getTableNames(fileName);
				for (String tableName : tableNames) {
					listModel.addElement(tableName);
				}
			} else {
				((SyncItemUI)this.getParent().getParent()).openErrorPopUp(EktooUITranslator.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
			}
			this.getListTables().repaint();
			this.controller.changeDatabaseName(fileName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void changeTablesSelections() {
		try {
			this.controller.changeTableNames(getListTables().getSelectedValues());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void setController(MsAccessMultiTableUIController controller) {
		this.controller = controller;
	}

	public MsAccessMultiTableUIController getController() {
		return controller;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				MsAccessUIController.DATABASE_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!getTxtFile().getText().equals(newStringValue))
				getTxtFile().setText(newStringValue);
		} else if (evt.getPropertyName().equals(
				MsAccessUIController.TABLE_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!isSelected(newStringValue)){
				getListTables().setSelectedValue(newStringValue, true);
			}
		}
	}

	private boolean isSelected(String tableName) {
		Object[] selected = getListTables().getSelectedValues();
		for (Object selectedValue : selected) {
			if(((String) selectedValue).equals(tableName)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean verify() {
		boolean valid = (new MsAccessMultiTableUIValidator(this, controller.getModel(),
				null)).verify();
		return valid;
	}

	private JLabel getLabelFile() {
		if (labelFile == null) {
			labelFile = new JLabel();
			labelFile.setText(EktooUITranslator.getFileLabel());
			labelFile.setSize(new Dimension(85, 16));
			labelFile.setPreferredSize(new Dimension(85, 16));
			labelFile.setLocation(new Point(8, 11));
		}
		return labelFile;
	}

	public void setLabelFile(String label) {
		if (labelFile != null) {
			labelFile.setText(label);
		}
	}

	public String getFilePath() {
		return getTxtFile().getText().trim();
	}

	public JTextField getTxtFile() {
		if (txtFileName == null) {
			txtFileName = new JTextField();
			txtFileName.setBounds(new Rectangle(99, 8, 149, 20));
			txtFileName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						txtFileName.setToolTipText(txtFileName.getText());
						changeDatabaseName(txtFileName.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtFileName.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						txtFileName.setToolTipText(txtFileName.getText());
						changeDatabaseName(txtFileName.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtFileName;
	}

	public JButton getBtnFile() {
		if (btnFile == null) {
			btnFile = new JButton();
			btnFile.setText(EktooUITranslator.getBrowseButtonLabel());
			btnFile.setToolTipText(EktooUITranslator.getTooltipSeleceDataFile("Access"));

			btnFile.setBounds(new Rectangle(259, 8, 34, 20));
			btnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getFileChooser().setSelectedFile(
							new File(txtFileName.getText()));
					int returnVal = getFileChooser().showOpenDialog(btnFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = getFileChooser().getSelectedFile();
						if (selectedFile != null) {
							try {
								txtFileName.setText(selectedFile
										.getCanonicalPath());
								txtFileName.setToolTipText(txtFileName
										.getText());
								changeDatabaseName(txtFileName.getText());
							} catch (Exception ex) {
								LOGGER.debug(ex.getMessage(), ex);
							}
						}
					}
				}
			});
		}
		return btnFile;
	}

	public JButton getBtnView() {
		if (btnView == null) {
			btnView = new JButton();
			btnView.setIcon(ImageManager.getViewIcon());
			btnView.setContentAreaFilled(false);
			btnView.setBorderPainted(false);
			btnView.setBorder(new EmptyBorder(0, 0, 0, 0));
			btnView.setBackground(Color.WHITE);
			btnView.setText("");
			btnView.setToolTipText(EktooUITranslator.getTooltipView());
			btnView.setBounds(new Rectangle(299, 8, 34, 40));
			btnView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = MsAccessMultiTableUI.this.getRootFrame();
					IErrorListener errorListener = MsAccessMultiTableUI.this
							.getErrorListener();
					OpenFileTask task = new OpenFileTask(frame, errorListener,
							txtFileName.getText());
					task.execute();
				}
			});
		}
		return btnView;
	}

	protected IErrorListener getErrorListener() {
		return (IErrorListener) getRootFrame();
	}

	private JLabel getlabelTables() {
		if (labelTables == null) {
			labelTables = new JLabel();
			labelTables.setText(EktooUITranslator.getTableLabel());
			labelTables.setLocation(new Point(8, 38));
			labelTables.setSize(new Dimension(85, 16));
			labelTables.setPreferredSize(new Dimension(85, 16));
		}
		return labelTables;
	}

	public void setLabelTable(String label) {
		if (labelTables != null) {
			labelTables.setText(label);
		}
	}

	public JList getListTables() {
		if (listTables == null) {
			listTables = new JList(new DefaultListModel());
			listTables.setToolTipText(EktooUITranslator.getTooltipSelectTable());
			listTables.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			listTables.setLayoutOrientation(JList.VERTICAL);
			listTables.setAutoscrolls(true);
			listTables.setVisibleRowCount(-1);
			
			ListSelectionListener selectionListener = new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e) {
				    if (e.getValueIsAdjusting() == false) {
				        changeTablesSelections();
				    }
				}
			};
			listTables.addListSelectionListener(selectionListener);
		}
		return listTables;
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public JFileChooser getFileChooser() {
		if (fileChooser == null)
			fileChooser = new JFileChooser();
		return fileChooser;
	}

}