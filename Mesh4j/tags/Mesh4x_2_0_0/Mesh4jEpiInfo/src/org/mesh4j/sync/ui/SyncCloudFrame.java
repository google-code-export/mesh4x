package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.tasks.DownloadSchemaAndMappingsTask;
import org.mesh4j.sync.ui.tasks.IDownloadListener;
import org.mesh4j.sync.ui.tasks.IErrorListener;
import org.mesh4j.sync.ui.tasks.OpenFileTask;
import org.mesh4j.sync.ui.tasks.OpenURLTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.mesh4j.sync.ui.utils.IconManager;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SyncCloudFrame extends JFrame implements IErrorListener, IDownloadListener{

	private static final long serialVersionUID = 7380206163750504752L;

	// MODEL VARIABLES
	private JLabel labelURL;
	private JButton buttonSync;
	private JButton buttonOpenDataSource;
	private JComboBox comboBoxSyncMode;
	private JComboBox comboBoxMappingDataSource;
	private JTextArea textAreaStatus;
	private JTextField textFieldURL;
	private JLabel imageStatus;
	
	private PropertiesProvider propertiesProvider;
	private SourceIdMapper sourceIdMapper;
	private SyncSessionsFrame owner;
	
	// BUSINESS METHODS
	public SyncCloudFrame(PropertiesProvider propertiesProvider, SourceIdMapper sourceIdMapper, SyncSessionsFrame owner){

		super();
		
		this.propertiesProvider = propertiesProvider;
		this.sourceIdMapper = sourceIdMapper;
		this.owner = owner;
		
//		setAlwaysOnTop(true);
		setIconImage(IconManager.getCDCImage());
		getContentPane().setBackground(Color.WHITE);
		setTitle(MeshCompactUITranslator.getSyncWindowTitle());
		setResizable(false);
		setBounds(100, 100, 630, 178);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("285dlu"),
				ColumnSpec.decode("14dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("34dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("6dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JPanel panelWeb = new JPanel();
		panelWeb.setBackground(Color.WHITE);
		panelWeb.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("218dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("24dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu")}));
		getContentPane().add(panelWeb, new CellConstraints(2, 2));

		panelWeb.add(getLabelURL(), new CellConstraints());

		textFieldURL = new JTextField();
		textFieldURL.setText("");
		panelWeb.add(textFieldURL, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener openFeedActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				OpenURLTask task = new OpenURLTask(SyncCloudFrame.this, SyncCloudFrame.this, textFieldURL.getText());
				task.execute();
			}
		};	
		
		final JButton buttonOpenFeed = new JButton();
		buttonOpenFeed.setContentAreaFilled(false);
		buttonOpenFeed.setBorderPainted(false);
		buttonOpenFeed.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenFeed.setBackground(Color.WHITE);
		buttonOpenFeed.setText("");
		buttonOpenFeed.setToolTipText(MeshCompactUITranslator.getSyncWindowTooltipViewFeed());
		buttonOpenFeed.setIcon(IconManager.getViewCloudIcon());
		buttonOpenFeed.addActionListener(openFeedActionListener);
		panelWeb.add(buttonOpenFeed, new CellConstraints(7, 1, CellConstraints.CENTER, CellConstraints.FILL));

		ActionListener downloadMappingsActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				DownloadSchemaAndMappingsTask task = new DownloadSchemaAndMappingsTask(
					SyncCloudFrame.this, SyncCloudFrame.this, SyncCloudFrame.this.propertiesProvider, textFieldURL.getText(), (MSAccessDataSourceMapping)comboBoxMappingDataSource.getSelectedItem()
				);
				task.execute();
			}
		};	
		final JButton buttonDownload = new JButton();
		buttonDownload.setContentAreaFilled(false);
		buttonDownload.setBorderPainted(false);
		buttonDownload.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonDownload.setBackground(Color.WHITE);
		buttonDownload.setText("");
		buttonDownload.setToolTipText(MeshCompactUITranslator.getTooltipDownloadSchemaAndMappings());
		buttonDownload.setIcon(IconManager.getDownloadImage());
		buttonDownload.addActionListener(downloadMappingsActionListener);
		panelWeb.add(buttonDownload, new CellConstraints(5, 1));

		final JPanel panelSync = new JPanel();
		panelSync.setBackground(Color.WHITE);
		panelSync.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("86dlu"),
				ColumnSpec.decode("2dlu"),
				ColumnSpec.decode("17dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("117dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("53dlu")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC}));
		getContentPane().add(panelSync, new CellConstraints(2, 4));

		panelSync.add(getComboBoxMappingDataSource(), new CellConstraints(1, 1));
		panelSync.add(getComboBoxSyncMode(), new CellConstraints(5, 1));
		panelSync.add(getButtonSync(), new CellConstraints(7, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener openDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping) getComboBoxMappingDataSource().getSelectedItem();
				if(dataSource != null){
					OpenFileTask task = new OpenFileTask(SyncCloudFrame.this, SyncCloudFrame.this, dataSource.getFileName());
					task.execute();
				}
			}
		};	
		
		buttonOpenDataSource = new JButton();
		buttonOpenDataSource.setContentAreaFilled(false);
		buttonOpenDataSource.setBorderPainted(false);
		buttonOpenDataSource.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenDataSource.setBackground(Color.WHITE);
		buttonOpenDataSource.setText("");
		buttonOpenDataSource.setToolTipText(MeshCompactUITranslator.getTooltipViewDataSource());
		buttonOpenDataSource.setIcon(IconManager.getViewDataSource());
		buttonOpenDataSource.addActionListener(openDataSourceActionListener);
		panelSync.add(buttonOpenDataSource, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));

		final JPanel panelStatus = new JPanel();
		panelStatus.setBackground(Color.WHITE);
		panelStatus.setBorder(new EmptyBorder(0, 0, 0, 0));
		panelStatus.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("254dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("17dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu")}));
		getContentPane().add(panelStatus, new CellConstraints(2, 6, CellConstraints.FILL, CellConstraints.FILL));

		textAreaStatus = new JTextArea();
		textAreaStatus.setFont(new Font("Calibri", Font.BOLD, 12));
		textAreaStatus.setLineWrap(true);
		textAreaStatus.setWrapStyleWord(true);
		textAreaStatus.setOpaque(true);
		textAreaStatus.setEditable(false);
		textAreaStatus.setText(MeshCompactUITranslator.getMessageWelcome(this.propertiesProvider.getLoggedUserName()));

		panelStatus.add(textAreaStatus, new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.FILL));
		panelStatus.add(getImageStatus(), new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));
	}
	
	protected JButton getButtonSync() {
		if (buttonSync == null) {
			buttonSync = new JButton();
			buttonSync.setFont(new Font("Arial", Font.PLAIN, 16));
			buttonSync.setText(MeshCompactUITranslator.getLabelSync());
			buttonSync.setToolTipText(MeshCompactUITranslator.getToolTipSync());
			//buttonSync.setBorder(new EmptyBorder(0, 0, 0, 0));
			//buttonSync.setBorderPainted(false);
			//buttonSync.setContentAreaFilled(false);
			//buttonSync.setIcon(IconManager.getStatusProcessingIcon());
			
			ActionListener synchronizeActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SynchronizeCloudTask task = new SynchronizeCloudTask();
					task.execute();
				}
			};	
			buttonSync.addActionListener(synchronizeActionListener);
		}
		return buttonSync;
	}
	
	public JComboBox getComboBoxMappingDataSource() {
		if (comboBoxMappingDataSource == null) {
			comboBoxMappingDataSource = new JComboBox();
			comboBoxMappingDataSource.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxMappingDataSource.setToolTipText(MeshCompactUITranslator.getToolTipDataSources());
			
			comboBoxMappingDataSource.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					setURLFromSelectedDataSource();
				}
			});
		}
		return comboBoxMappingDataSource;
	}

	public JComboBox getComboBoxSyncMode() {
		if (comboBoxSyncMode == null) {
			comboBoxSyncMode = new JComboBox();
			comboBoxSyncMode.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxSyncMode.setToolTipText(MeshCompactUITranslator.getToolTipSyncMode());

			DefaultComboBoxModel syncTypesTableModel = new DefaultComboBoxModel(new SyncMode[]{SyncMode.SendAndReceiveChanges, SyncMode.SendChangesOnly, SyncMode.ReceiveChangesOnly});
			comboBoxSyncMode.setModel(syncTypesTableModel);
		}
		return comboBoxSyncMode;
	}
	
	protected JLabel getLabelURL() {
		if (labelURL == null) {
			labelURL = new JLabel();
			labelURL.setFont(new Font("Calibri", Font.BOLD, 14));
			labelURL.setText(MeshCompactUITranslator.getLabelURL());
		}
		return labelURL;
	}
	
	protected JLabel getImageStatus() {
		if (imageStatus == null) {
			imageStatus = new JLabel();
			imageStatus.setIcon(IconManager.getStatusReadyIcon());
			imageStatus.setText("");
		}
		return imageStatus;
	}
	
	private class SynchronizeCloudTask extends SwingWorker<Void, Void> {
		 
		public SynchronizeCloudTask(){
			super();
		}
		
		@Override
	    public Void doInBackground() {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			String url = textFieldURL.getText();
			if(!HttpSyncAdapterFactory.isValidURL(url)){
				setError(MeshUITranslator.getErrorInvalidURL());
				return null;
			}
			
			MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping) getComboBoxMappingDataSource().getSelectedItem();
			if(dataSource == null || !MsAccessHibernateSyncAdapterFactory.isValidAccessTable(dataSource.getFileName(), dataSource.getTableName())){
    			setError(MeshCompactUITranslator.getErrorInvalidMSAccessTable());
				return null;
			}
			
			try{
				owner.notifyOwnerWorking();
				setInProcess(MeshUITranslator.getLabelStart());
				List<Item> conflicts = SyncEngineUtil.synchronize(url, dataSource.getAlias(), propertiesProvider, sourceIdMapper, getSelectedSyncMode());
				if(conflicts.isEmpty()){
					setOk(MeshUITranslator.getLabelSuccess());
				} else {
					setError(MeshUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));	
				}
				
				owner.updateSessions();
				owner.notifyOwnerNotWorking();
			} catch(Throwable e){
				LogFrame.Logger.error(e.getMessage(), e);
				setError(MeshUITranslator.getLabelFailed());
			}

			return null;
	    }

		@Override
	    public void done() {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}
	
	public void notifyDataSourceMappingListsChanges(ArrayList<DataSourceMapping> sources) {
		Iterator<DataSourceMapping> sourcesIt = sources.iterator();
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		while(sourcesIt.hasNext()) {
			model.addElement(sourcesIt.next());			
		}
		comboBoxMappingDataSource.setModel(model);
		
		setURLFromSelectedDataSource();
	}
	
	private void setURLFromSelectedDataSource() {
		DataSourceMapping selectedDataSourceMapping = (DataSourceMapping)comboBoxMappingDataSource.getSelectedItem();
		if(selectedDataSourceMapping != null){
			String selectedAlias = selectedDataSourceMapping.getAlias();
			textFieldURL.setText(this.propertiesProvider.getMeshURL(selectedAlias));
		}
	}
	
	private void setStatusText(String text) {
		this.textAreaStatus.setText(text);
		this.textAreaStatus.setToolTipText(text);
	}

	public void setReady(String msg){
		this.textAreaStatus.setForeground(Color.BLACK);
		this.setStatusText(msg);
		this.imageStatus.setIcon(null);
		this.imageStatus.setToolTipText(msg);
	}

	public void setInProcess(String msg){
		this.textAreaStatus.setForeground(Color.BLACK);
		this.setStatusText(msg);
		this.imageStatus.setIcon(IconManager.getStatusInProgressIcon());
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setOk(String msg){
		this.textAreaStatus.setForeground(Color.BLACK);
		this.setStatusText(msg);
		this.imageStatus.setIcon(IconManager.getStatusOkIcon());
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setError(String error){
		this.textAreaStatus.setForeground(Color.RED);
		this.setStatusText(error);
		this.imageStatus.setIcon(IconManager.getStatusErrorIcon());
		this.imageStatus.setToolTipText(error);
	}
	
	public void notifyOwnerWorking(){
		this.buttonSync.setEnabled(false);
		this.buttonOpenDataSource.setEnabled(false);
	}
	
	public void notifyOwnerNotWorking(){
		this.buttonSync.setEnabled(true);
		this.buttonOpenDataSource.setEnabled(true);
	}

	public void viewCloudSession(String url, String sourceId, String syncMode) {
		this.textFieldURL.setText(url);
		this.selectDataSource(sourceId);
		this.comboBoxSyncMode.setSelectedItem(SyncMode.valueOf(syncMode));
	}
	
	private void selectDataSource(String sourceId) {
		int size = this.comboBoxMappingDataSource.getModel().getSize();
		int i = 0;
		while(i< size){
			DataSourceMapping dataSource = (DataSourceMapping)this.comboBoxMappingDataSource.getModel().getElementAt(i);
			if(dataSource.getAlias().equals(sourceId)){
				this.comboBoxMappingDataSource.setSelectedIndex(i);
				return;
			}
			i = i +1;
		}		
	}
	
	private SyncMode getSelectedSyncMode() {
		return (SyncMode)this.comboBoxSyncMode.getSelectedItem();
	}

	@Override
	public void notifyError(String error) {
		setError(error);
	}

}