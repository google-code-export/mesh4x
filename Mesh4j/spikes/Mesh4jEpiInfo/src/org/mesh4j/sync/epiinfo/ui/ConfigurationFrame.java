package org.mesh4j.sync.epiinfo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

public class ConfigurationFrame extends JFrame {

	private static final long serialVersionUID = 1688018089793859404L;

	// MODEL VARIABLES
	private EpiinfoCompactUI owner;
	
	// BUSINESS METHODS
	
	public ConfigurationFrame(EpiinfoCompactUI owner) {
		super();
		this.owner = owner;
		createUI();
	}

	public void createUI(){
				
		getContentPane().setBackground(Color.WHITE);
		setIconImage(SwingResourceManager.getImage(ConfigurationFrame.class, "/cdc.gif"));
		setResizable(false);
		setTitle(EpiInfoCompactUITranslator.getConfigurationWindowTitle());
		setBounds(100, 100, 273, 375);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("125dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("185dlu"),
				RowSpec.decode("14dlu"),
				RowSpec.decode("10dlu")}));

		// Contacts
		JPanel panelContacts = new JPanel(false);
		panelContacts.setBackground(Color.WHITE);
		panelContacts.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("114dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("132dlu")}));

		final JPanel panelEditContact = new JPanel();
		panelEditContact.setBackground(Color.WHITE);
		panelEditContact.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("54dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("54dlu")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("2dlu"),
				FormFactory.DEFAULT_ROWSPEC}));
		panelContacts.add(panelEditContact, new CellConstraints(2, 2));

		DefaultListModel listModelContacts = new DefaultListModel();
		final JList listContacts = new JList(listModelContacts);
		
		final JTextField textFieldContactAlias = new JTextField();
		panelEditContact.add(textFieldContactAlias, new CellConstraints());

		final JTextField textFieldContactNumber = new JTextField();
		panelEditContact.add(textFieldContactNumber, new CellConstraints(3, 1));

		final JPanel panelContactButtons = new JPanel();
		panelContactButtons.setBackground(Color.WHITE);
		panelContactButtons.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		panelEditContact.add(panelContactButtons, new CellConstraints(1, 3, 3, 1));

		ActionListener saveContactActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String alias = textFieldContactAlias.getText();
				String endpointNumber = textFieldContactNumber.getText();
				
				if(alias == null || alias.length() == 0 || endpointNumber == null || endpointNumber.length() == 0){
					JOptionPane.showMessageDialog(
							ConfigurationFrame.this,
							EpiInfoCompactUITranslator.getMessageEditContactRequiredFields(),
							EpiInfoCompactUITranslator.getTitle(),
							JOptionPane.ERROR_MESSAGE);
				} else {
					
					int index = listContacts.getSelectedIndex();
					if (index == -1) {	// save
						
						EndpointMapping endpoint = new EndpointMapping(alias, endpointNumber);
						SyncEngineUtil.saveOrUpdateEndpointMapping(alias, endpoint);
						
						DefaultListModel listModel = (DefaultListModel)listContacts.getModel();
						listModel.addElement(endpoint);
					
					} else { 			// update
						EndpointMapping endpoint = (EndpointMapping) listContacts.getSelectedValue();
						String oldAlias = endpoint.getAlias();
						
						endpoint.setAlias(alias);
						endpoint.setEndpoint(endpointNumber);
						
						SyncEngineUtil.saveOrUpdateEndpointMapping(oldAlias, endpoint);		
						
					}	
					listContacts.setSelectedIndex(-1);
					listContacts.repaint();
					owner.notifyEndpointMappingListsChanges();
				}
			}
		};		
		final JButton buttonSaveContact = new JButton();
		panelContactButtons.add(buttonSaveContact, new CellConstraints());
		buttonSaveContact.setBorderPainted(false);
		buttonSaveContact.setContentAreaFilled(false);
		buttonSaveContact.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSaveContact.setBackground(Color.WHITE);
		buttonSaveContact.setFont(new Font("Calibri", Font.BOLD, 10));
		buttonSaveContact.setText(EpiInfoCompactUITranslator.getLabelSave());
		buttonSaveContact.addActionListener(saveContactActionListener);

		ActionListener deleteContactActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int index = listContacts.getSelectedIndex();
				if (index != -1) {
					EndpointMapping endpoint = (EndpointMapping) listContacts.getSelectedValue();
					SyncEngineUtil.deleteEndpointMapping(endpoint);				
					
					DefaultListModel listModel = (DefaultListModel)listContacts.getModel();
					listModel.remove(index);

					listContacts.setSelectedIndex(-1);
					listContacts.repaint();
					
					owner.notifyEndpointMappingListsChanges();
				}
			}
		};			
		final JButton buttonDeleteContact = new JButton();
		panelContactButtons.add(buttonDeleteContact, new CellConstraints(3, 1));
		buttonDeleteContact.setBorderPainted(false);
		buttonDeleteContact.setContentAreaFilled(false);
		buttonDeleteContact.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonDeleteContact.setBackground(Color.WHITE);
		buttonDeleteContact.setFont(new Font("Calibri", Font.BOLD, 10));
		buttonDeleteContact.setText(EpiInfoCompactUITranslator.getLabelDelete());
		buttonDeleteContact.setEnabled(false);
		buttonDeleteContact.addActionListener(deleteContactActionListener);
		
		EndpointMapping[] endpoints = SyncEngineUtil.getEndpointMappings();
		for (int i = 0; i < endpoints.length; i++) {
			listModelContacts.addElement(endpoints[i]);			
		}
	
		ListSelectionListener contactsListSelectionListener = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
			    if (e.getValueIsAdjusting() == false) {
			        if (listContacts.getSelectedIndex() != -1) {
			        	EndpointMapping endpoint = (EndpointMapping) listContacts.getSelectedValue();
			        	textFieldContactAlias.setText(endpoint.getAlias());
			        	textFieldContactNumber.setText(endpoint.getEndpoint());
			        	
			        	buttonDeleteContact.setEnabled(true);
			        } else {
			        	buttonDeleteContact.setEnabled(false);
			        }
			    }
			}
		};
		
		listContacts.setFont(new Font("Calibri", Font.PLAIN, 12));
		listContacts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listContacts.setLayoutOrientation(JList.VERTICAL_WRAP);
		listContacts.setVisibleRowCount(-1);
		listContacts.addListSelectionListener(contactsListSelectionListener);
		
		final JScrollPane scrollPaneContacts = new JScrollPane();
		scrollPaneContacts.setBorder(new BevelBorder(BevelBorder.LOWERED));
		scrollPaneContacts.setViewportView(listContacts);
		panelContacts.add(scrollPaneContacts, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		
		// DataSource		
		JPanel panelDataSources = new JPanel(false);
		panelDataSources.setBackground(Color.WHITE);
		panelDataSources.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("114dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("99dlu")}));
		
		final JPanel panelEditDataSource = new JPanel();
		panelEditDataSource.setBackground(Color.WHITE);
		panelEditDataSource.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("108dlu"),
				ColumnSpec.decode("5dlu")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("2dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("2dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("2dlu"),
				FormFactory.DEFAULT_ROWSPEC}));
		panelDataSources.add(panelEditDataSource, new CellConstraints(2, 2));

		DefaultListModel listModelDataSources = new DefaultListModel();
		final JList listDataSources = new JList(listModelDataSources);
		
		final JTextField textFieldDataSourceFileName = new JTextField();
		textFieldDataSourceFileName.setEditable(false);
		textFieldDataSourceFileName.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditDataSource.add(textFieldDataSourceFileName, new CellConstraints(1, 3));

		final JTextField textFieldDataSourceAlias = new JTextField();
		textFieldDataSourceAlias.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditDataSource.add(textFieldDataSourceAlias, new CellConstraints());
		
		final JComboBox comboBoxTableName = new JComboBox();
		comboBoxTableName.setBackground(Color.WHITE);
		comboBoxTableName.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditDataSource.add(comboBoxTableName, new CellConstraints(1, 5));
		
		final JPanel panelDataSourceButtons = new JPanel();
		panelDataSourceButtons.setBackground(Color.WHITE);
		panelDataSourceButtons.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		panelEditDataSource.add(panelDataSourceButtons, new CellConstraints(1, 7, 2, 1));

		ActionListener saveDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int index = listDataSources.getSelectedIndex();
				
				String newAlias = textFieldDataSourceAlias.getText();
				String fileName = textFieldDataSourceFileName.getText();
				String mdbName = SyncEngineUtil.getMDBName(fileName);
				String tableName = (String)comboBoxTableName.getSelectedItem();
				
				if(newAlias == null || newAlias.length() == 0 || fileName == null || fileName.length() == 0 || tableName == null || tableName.length() == 0){
					JOptionPane.showMessageDialog(
							ConfigurationFrame.this,
							EpiInfoCompactUITranslator.getMessageEditDataSourceRequiredFields(),
							EpiInfoCompactUITranslator.getTitle(),
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (index == -1) {	// save
	
						DataSourceMapping dataSourceMapping = new DataSourceMapping(newAlias, mdbName, tableName, fileName);
						owner.getSourceIdResolver().saveDataSourceMapping(dataSourceMapping);
						
						DefaultListModel listModel = (DefaultListModel)listDataSources.getModel();
						listModel.addElement(dataSourceMapping);
					
					} else { 			// update
						DataSourceMapping dataSourceMapping = (DataSourceMapping) listDataSources.getSelectedValue();
						DataSourceMapping oldDataSourceMapping = new DataSourceMapping(
								dataSourceMapping.getAlias(),
								dataSourceMapping.getMDBName(),
								dataSourceMapping.getTableName(),
								dataSourceMapping.getFileName());
						
						dataSourceMapping.setAlias(newAlias);
						dataSourceMapping.setMDBName(mdbName);
						dataSourceMapping.setTableName(tableName);
						dataSourceMapping.setFileName(fileName);
						
						owner.getSourceIdResolver().updateDataSourceMapping(oldDataSourceMapping, dataSourceMapping);		
						
					}	
					listDataSources.setSelectedIndex(-1);
					listDataSources.repaint();
					
					owner.notifyDataSourceMappingListsChanges();
				}
			}
		};	
		final JButton buttonSaveDataSource = new JButton();
		buttonSaveDataSource.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSaveDataSource.setContentAreaFilled(false);
		buttonSaveDataSource.setBorderPainted(false);
		buttonSaveDataSource.setFont(new Font("Calibri", Font.BOLD, 10));
		panelDataSourceButtons.add(buttonSaveDataSource, new CellConstraints());
		buttonSaveDataSource.setText(EpiInfoCompactUITranslator.getLabelSave());
		buttonSaveDataSource.addActionListener(saveDataSourceActionListener);

		ActionListener deleteDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int index = listDataSources.getSelectedIndex();
				if (index != -1) {
					DataSourceMapping dataSource = (DataSourceMapping) listDataSources.getSelectedValue();
					owner.getSourceIdResolver().deleteDataSourceMapping(dataSource);				
					
					DefaultListModel listModel = (DefaultListModel)listDataSources.getModel();
					listModel.remove(index);

					listDataSources.setSelectedIndex(-1);
					listDataSources.repaint();
					
					owner.notifyDataSourceMappingListsChanges();
				}
			}
		};	
		final JButton buttonDeleteDataSource = new JButton();
		buttonDeleteDataSource.setContentAreaFilled(false);
		buttonDeleteDataSource.setBorderPainted(false);
		buttonDeleteDataSource.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonDeleteDataSource.setFont(new Font("Calibri", Font.BOLD, 10));
		panelDataSourceButtons.add(buttonDeleteDataSource, new CellConstraints(3, 1));
		buttonDeleteDataSource.setText(EpiInfoCompactUITranslator.getLabelDelete());
		buttonDeleteDataSource.setEnabled(false);
		buttonDeleteDataSource.addActionListener(deleteDataSourceActionListener);

		ActionListener fileChooserFileActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = openFileDialog(
					textFieldDataSourceFileName.getText(), 
					new FileNameExtensionFilter(EpiInfoUITranslator.getLabelDataSourceFileExtensions(), "mdb"));
				if(selectedFileName != null){
					textFieldDataSourceFileName.setText(selectedFileName);
					Set<String> tableNames = SyncEngineUtil.getTableNames(selectedFileName);
					ComboBoxModel tableNameModel = new DefaultComboBoxModel(tableNames.toArray());
					comboBoxTableName.setModel(tableNameModel);
				}
			}
		};
		
		final JButton buttonOpenDataSourceFileChooser = new JButton();
		buttonOpenDataSourceFileChooser.setContentAreaFilled(false);
		buttonOpenDataSourceFileChooser.setBorderPainted(false);
		buttonOpenDataSourceFileChooser.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonOpenDataSourceFileChooser.setToolTipText(EpiInfoUITranslator.getToolTipFileChooser());
		buttonOpenDataSourceFileChooser.setText(EpiInfoUITranslator.getLabelFileChooser());
		buttonOpenDataSourceFileChooser.addActionListener(fileChooserFileActionListener);

		panelEditDataSource.add(buttonOpenDataSourceFileChooser, new CellConstraints(2, 3));

		Iterator<DataSourceMapping> sources = owner.getSourceIdResolver().getDataSourceMappings().iterator();
		while(sources.hasNext()) {
			listModelDataSources.addElement(sources.next());			
		}
		
		ListSelectionListener dataSourceListSelectionListener = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
			    if (e.getValueIsAdjusting() == false) {
			        if (listDataSources.getSelectedIndex() != -1) {
			        	DataSourceMapping dataSourceMapping = (DataSourceMapping) listDataSources.getSelectedValue();
			        	textFieldDataSourceAlias.setText(dataSourceMapping.getAlias());
			        	textFieldDataSourceFileName.setText(dataSourceMapping.getFileName());
			        	
						Set<String> tableNames = SyncEngineUtil.getTableNames(dataSourceMapping.getFileName());
						ComboBoxModel tableNameModel = new DefaultComboBoxModel(tableNames.toArray());
						tableNameModel.setSelectedItem(dataSourceMapping.getTableName());						
						comboBoxTableName.setModel(tableNameModel);
						
						buttonDeleteDataSource.setEnabled(true);
			        } else {
			        	buttonDeleteDataSource.setEnabled(false);
			        }
			    }
			}
		};
		
		listDataSources.setFont(new Font("Calibri", Font.PLAIN, 12));
		listDataSources.setBorder(new EmptyBorder(0, 0, 0, 0));
		listDataSources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDataSources.setLayoutOrientation(JList.VERTICAL);
		listDataSources.setVisibleRowCount(-1);
		listDataSources.addListSelectionListener(dataSourceListSelectionListener);

		final JScrollPane scrollPaneDataSources = new JScrollPane();
		scrollPaneDataSources.setBorder(new BevelBorder(BevelBorder.LOWERED));
		scrollPaneDataSources.setViewportView(listDataSources);
		panelDataSources.add(scrollPaneDataSources, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		
		// Modem		
		JPanel panelModem = new JPanel(false);
		panelModem.setBackground(Color.WHITE);
		panelModem.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("114dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("163dlu")}));
		
		// Tabbed Panel
	    JTabbedPane tabbedPane = new JTabbedPane();
	    tabbedPane.addTab(EpiInfoCompactUITranslator.getLabelTabContacts(), panelContacts);
	    tabbedPane.addTab(EpiInfoCompactUITranslator.getLabelTabDataSources(), panelDataSources);
	    tabbedPane.addTab(EpiInfoCompactUITranslator.getLabelTabModem(), panelModem);
		getContentPane().add(tabbedPane, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		
		// Buttons panel
		final JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Color.WHITE);
		panelButtons.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		getContentPane().add(panelButtons, new CellConstraints(2, 3));

		final JButton buttonClose = new JButton();
		buttonClose.setContentAreaFilled(false);
		buttonClose.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClose.setBorderPainted(false);
		buttonClose.setOpaque(false);
		buttonClose.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonClose.setText(EpiInfoCompactUITranslator.getConfigurationWindowLabelClose());
		buttonClose.setToolTipText(EpiInfoCompactUITranslator.getConfigurationWindowToolTipClose());
		ActionListener closeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ConfigurationFrame.this.setVisible(false);
			}
		};
		
		buttonClose.addActionListener(closeActionListener);
		
		panelButtons.add(buttonClose, new CellConstraints(1, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
	}

	private String openFileDialog(String fileName, FileNameExtensionFilter filter){
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		
		if(fileName != null && fileName.trim().length() > 0){
			File file = new File(fileName);
			chooser.setSelectedFile(file);
		}
		
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else{
			return null;
		}
	}
}
