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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenFileTask;
import org.mesh4j.ektoo.tasks.SchemaViewTask;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsAccessUIValidator;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessJackcessSyncAdapterFactory;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class MsAccessUI extends AbstractUI{

	private static final long serialVersionUID = 4708875346159085594L;
	private static final Log LOGGER = LogFactory.getLog(MsAccessUI.class);

	// MODEL VARIABLES
	private JFileChooser fileChooser = new JFileChooser();

	private String table = null;

	private JLabel labelFile = null;
	private JTextField txtFileName = null;

	private JLabel labelTable = null;
	//private JComboBox listTable = null;
	private JList listTable = null;
	private JScrollPane listTableScroller =null;
	
	private JButton btnFile = null;
	private JButton btnView = null;
	
	private MsAccessUIController controller;

	// BUSINESS METHODS
	public MsAccessUI(String fileName, MsAccessUIController controller) {
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
		setList(fileName);
	}
	
	protected void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		this.add(getLabelFile(), null);
		this.add(getTxtFile(), null);
		this.add(getBtnFile(), null);
		this.add(getBtnView(), null);

		this.add(getlabelTable(), null);
		this.add(getListTableScroller(), null);
		this.add(getMessagesText(), null);
		this.add(getSchemaViewButton(), null);
	}


	private JButton getSchemaViewButton(){
		getSchemaButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EktooFrame ektooFrame = ((EktooFrame)MsAccessUI.this.getRootFrame());
				SchemaViewTask task = new SchemaViewTask(ektooFrame,MsAccessUI.this.controller,ektooFrame);
				task.execute();
			}
		});
		return getSchemaButton(); 
	}
	
	public void setList(String fileName) {
		JList tableList = getTableList();
		tableList.removeAll();
		
		try {
			File file = new File(fileName);
			if(file.exists()){
				Set<String> tableNames = MsAccessHibernateSyncAdapterFactory.getTableNames(fileName);
				tableList.setListData(tableNames.toArray());
//				for (String tableName : tableNames) {
//					tableList.addItem(tableName);
//				}
			} else {
				((SyncItemUI)this.getParent().getParent()).openErrorPopUp(EktooUITranslator.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
			}
			this.controller.changeDatabaseName(fileName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void setList(String fileName, String table) {
		try {
			String[] str = new String[getTableList().getSelectedValues().length];
			Arrays.asList(getTableList().getSelectedValues()).toArray(str);
			this.controller.changeTableNames(str);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void setController(MsAccessUIController controller) {
		this.controller = controller;
	}

	public MsAccessUIController getController() {
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
//			if (!((String) getTableList().getSelectedItem())
//					.equals(newStringValue)) {
//				getTableList().setSelectedItem((String) newStringValue);
//			}
			if (!getTableList().getSelectedValues().toString()
					.equals(newStringValue)){
				getTableList().setSelectedValue(newStringValue, true);
			}
		}
	}

	@Override
	public boolean verify() {
		boolean valid = (new MsAccessUIValidator(this, controller.getModel(),
				null)).verify();
		return valid;
	}

	private JLabel getLabelFile() {
		if (labelFile == null) {
			labelFile = new JLabel();
			labelFile.setText(EktooUITranslator.getAccessFileLabel());
			labelFile.setSize(new Dimension(85, 16));
			labelFile.setPreferredSize(new Dimension(85, 16));
			labelFile.setLocation(new Point(8, 11));
		}
		return labelFile;
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
						setList(txtFileName.getText());
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
						setList(txtFileName.getText());
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
								setList(txtFileName.getText());
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
					JFrame frame = MsAccessUI.this.getRootFrame();
					IErrorListener errorListener = MsAccessUI.this
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

	private JLabel getlabelTable() {
		if (labelTable == null) {
			labelTable = new JLabel();
			labelTable.setText(EktooUITranslator.getAccessTableLabel());
			labelTable.setLocation(new Point(8, 38));
			labelTable.setSize(new Dimension(85, 16));
			labelTable.setPreferredSize(new Dimension(85, 16));
		}
		return labelTable;
	}

/*	public JComboBox getTableList() {
		if (listTable == null) {
			listTable = new JComboBox();
			listTable.setBounds(new Rectangle(99, 36, 194, 20));
			listTable.setToolTipText(EktooUITranslator.getTooltipSelectTable());
			listTable.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (!listTable.isEnabled()) {
						return;
					}
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						int sheetIndex = listTable.getSelectedIndex();
						if (sheetIndex != -1) {
							table = (String) listTable.getSelectedItem();
							setList(txtFileName.getText(), table);
						}
					}
				}
			});
		}
		return listTable;
	}*/

	public JList getTableList() {
		if (listTable == null) {
			listTable = new JList();
			listTable.setToolTipText(EktooUITranslator.getTooltipSelectSingleTable());

			listTable.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent listselectionevent) {
							String[] str = new String[listTable.getSelectedValues().length];
							Arrays.asList(listTable.getSelectedValues()).toArray(str);
							getController().changeTableNames(str);
						}
					});
		}

		listTable.setSelectionMode(EktooFrame.multiModeSync ? 
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

		return listTable;
	}	
	
	private JScrollPane getListTableScroller(){		
		if(listTableScroller == null){
			listTableScroller = new JScrollPane(getTableList());
			listTableScroller.setBounds(new Rectangle(99, 36, 183, 60));
			listTableScroller.setPreferredSize(new Dimension(183, 60));
		}
		return listTableScroller;
	}
	
	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public JFileChooser getFileChooser() {
		if (fileChooser == null)
			fileChooser = new JFileChooser();
		return fileChooser;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTable() {
		return table;
	}


}