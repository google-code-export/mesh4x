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
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.PFIFUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenFileTask;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.IValidationStatus;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.PfifUtil;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIF_ENTITY;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class PfifFeedUI extends AbstractUI  implements IValidationStatus {

	private static final long serialVersionUID = 2457237653577593698L;

	private static final Log LOGGER = LogFactory.getLog(PfifFeedUI.class);

	// MODEL VARIABLES
	private JLabel labelFileName = null;
	private JTextField txtFileName = null;
	
	private JButton btnFile = null;

	private JFileChooser fileChooser = null;
	private JScrollPane listTableScroller =null;
	private JList listTable = null;

	// BUSINESS METHODS
	public PfifFeedUI(String fileName, PFIFUIController controller) {
		super(controller);
		this.initialize();
		this.txtFileName.setText(fileName);
		this.txtFileName.setToolTipText(fileName);
		this.setMessageText("");
		setList(fileName);
	}

	

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		this.add(getFileNameLabel(), null);
		this.add(getFileNameText(), null);
		this.add(getBtnFile(), null);
		this.add(getViewButton(), null);
		this.add(getConflictsButton());
		this.add(getMessagesText(), null);
		this.add(getMappingsButton());
		this.add(getSchemaViewButton(), null);
		this.add(getListTableScroller(), null);
	}
	
	
	private void setList(String fileName) {
		File feedFile = new File(fileName);
		JList tableList = getTableList();
		tableList.removeAll();
		
		if(!feedFile.exists() || feedFile.length() <= 0){
			return ;
		}
		ISyndicationFormat syndicationFormat = PfifUtil.getPfifSyndicationFormat(feedFile);
		getController().changeSyndicationFormat(syndicationFormat);
		Set<String> entityNames = PfifUtil.getFeedNames(feedFile, syndicationFormat);
		tableList.setListData(entityNames.toArray());
	}
	
	private JScrollPane getListTableScroller(){		
		if(listTableScroller == null){
			listTableScroller = new JScrollPane(getTableList());
			listTableScroller.setBounds(new Rectangle(99, 36, 183, 60));
			listTableScroller.setPreferredSize(new Dimension(183, 60));
		}
		return listTableScroller;
	}
	
	public JList getTableList() {
		if (listTable == null) {
			listTable = new JList();
			listTable.setToolTipText(EktooUITranslator.getTooltipSelectSingleTable());

			listTable.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent listselectionevent) {
							String[] str = new String[listTable.getSelectedValues().length];
							Arrays.asList(listTable.getSelectedValues()).toArray(str);
							getController().changeEntityNames(str);
						}
					});
		}

		listTable.setSelectionMode(EktooFrame.multiModeSync ? 
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

		return listTable;
	}	
	
	private JLabel getFileNameLabel() {
		if (labelFileName == null) {
			labelFileName = new JLabel();
			labelFileName.setText(EktooUITranslator.getFeedFileNameLabel());
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
			btnFile.setToolTipText(EktooUITranslator.getTooltipSeleceDataFile("Feed"));
			btnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getFileChooser().setSelectedFile(new File(txtFileName.getText()));
					int returnVal = getFileChooser().showOpenDialog(btnFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = getFileChooser().getSelectedFile();
						if (selectedFile != null) {
							try{
								txtFileName.setText(selectedFile.getCanonicalPath());
								fileNameChanged(txtFileName.getText());
								setList(txtFileName.getText());
							}catch (Exception ex) {
								LOGGER.debug(ex.getMessage(), ex);
							}	
							
						}
					}
				}
			});
		}
		return btnFile;
	}

	@Override
	protected void viewItems() {
		JFrame frame = PfifFeedUI.this.getRootFrame();
		OpenFileTask task = new OpenFileTask(frame, (IErrorListener)frame, txtFileName.getText());
		task.execute();
	}

	public PFIFUIController getController() {
		return super.getController(PFIFUIController.class);
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(new FileNameExtensionFilter(
					EktooUITranslator.getXMLFileSelectorTitle(), "xml", "XML"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		return fileChooser;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PFIFUIController.FILE_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			
//			if (!getFileNameText().getText().equals(newStringValue)){
				getFileNameText().setText(newStringValue);
				setList(newStringValue);
//			}
				
		} else if (evt.getPropertyName().equals(PFIFUIController.ENTITY_NAMES)) {
			String newStringValue = evt.getNewValue().toString();
			if (!getTableList().getSelectedValue().toString().equals(newStringValue))
				getTableList().setSelectedValue(newStringValue, true);
		}
	}

	@Override
	public boolean verify() {
		//boolean valid = (new FeedUIValidator(this, controller.getModel(), null)).verify();
		return true;
	}
	
	protected void fileNameChanged(String fileName) {
		txtFileName.setToolTipText(fileName);
		getController().changeFileName(fileName);
		
		File file = new File(fileName);
		if(!file.exists()){
			if(this.getController().acceptsCreateDataset()){
				this.setMessageText(EktooUITranslator.getMessageNewFile());
			} else {
				this.setMessageText(EktooUITranslator.getMessageUpdateFile());	
			}
		} else {
			this.setMessageText(EktooUITranslator.getMessageUpdateFile());
		}
	}
}