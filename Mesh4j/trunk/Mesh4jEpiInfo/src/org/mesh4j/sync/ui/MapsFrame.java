package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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

import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.ui.tasks.DownloadSchemaAndMappingsTask;
import org.mesh4j.sync.ui.tasks.IDownloadListener;
import org.mesh4j.sync.ui.tasks.IErrorListener;
import org.mesh4j.sync.ui.tasks.OpenFileTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.utils.IconManager;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MapsFrame extends JFrame implements IErrorListener, IDownloadListener{

	private static final long serialVersionUID = 7380206163750504752L;

	// MODEL VARIABLES
	private JLabel labelURL;
	private JButton buttonCreateMap;
	private JButton buttonOpenDataSource;
	private JComboBox comboBoxMappingDataSource;
	private JTextArea textAreaStatus;
	private JTextField textFieldURL;
	private JLabel imageStatus;
	
	private PropertiesProvider propertiesProvider;
	private SourceIdMapper sourceIdMapper;
	
	// BUSINESS METHODS
	public MapsFrame(PropertiesProvider propertiesProvider, SourceIdMapper sourceIdMapper){

		super();
		
		this.propertiesProvider = propertiesProvider;
		this.sourceIdMapper = sourceIdMapper;
		
//		setAlwaysOnTop(true);
		setIconImage(IconManager.getCDCImage());
		getContentPane().setBackground(Color.WHITE);
		setTitle(MeshCompactUITranslator.getMapsWindowTitle());
		setResizable(false);
		setBounds(100, 100, 545, 178);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("241dlu"),
				ColumnSpec.decode("14dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("28dlu"),
				RowSpec.decode("10dlu"),
				RowSpec.decode("19dlu"),
				RowSpec.decode("9dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JPanel panelWeb = new JPanel();
		panelWeb.setBackground(Color.WHITE);
		panelWeb.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("180dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("17dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("17dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu")}));
		getContentPane().add(panelWeb, new CellConstraints(2, 2));

		panelWeb.add(getLabelURL(), new CellConstraints());

		textFieldURL = new JTextField();
		textFieldURL.setText("");
		panelWeb.add(textFieldURL, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener downloadMappingsActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				DownloadSchemaAndMappingsTask task = new DownloadSchemaAndMappingsTask(
					MapsFrame.this, MapsFrame.this, MapsFrame.this.propertiesProvider, textFieldURL.getText(), (MSAccessDataSourceMapping)comboBoxMappingDataSource.getSelectedItem()
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
		panelWeb.add(buttonDownload, new CellConstraints(5, 1, CellConstraints.CENTER, CellConstraints.FILL));
		
		ActionListener openCloudMapActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				OpenCloudMapTask task = new OpenCloudMapTask();
				task.execute();
			}
		};	
		
		final JButton buttonOpenCloudMap = new JButton();
		buttonOpenCloudMap.setContentAreaFilled(false);
		buttonOpenCloudMap.setBorderPainted(false);
		buttonOpenCloudMap.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenCloudMap.setBackground(Color.WHITE);
		buttonOpenCloudMap.setText("");
		buttonOpenCloudMap.setToolTipText(MeshCompactUITranslator.getMapsWindowTooltipViewCloudMap());
		buttonOpenCloudMap.setIcon(IconManager.getMapImage());
		buttonOpenCloudMap.addActionListener(openCloudMapActionListener);
		panelWeb.add(buttonOpenCloudMap, new CellConstraints(7, 1, CellConstraints.CENTER, CellConstraints.FILL));

		final JPanel panelMapExchange = new JPanel();
		panelMapExchange.setBackground(Color.WHITE);
		panelMapExchange.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("75dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("71dlu"),
				ColumnSpec.decode("2dlu"),
				ColumnSpec.decode("17dlu"),
				ColumnSpec.decode("15dlu"),
				ColumnSpec.decode("57dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu")}));
		getContentPane().add(panelMapExchange, new CellConstraints(2, 4));

		panelMapExchange.add(getComboBoxMappingDataSource(), new CellConstraints(1, 1, 3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
		panelMapExchange.add(getButtonCreateMap(), new CellConstraints(7, 1, CellConstraints.LEFT, CellConstraints.FILL));

		
		ActionListener openDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping) getComboBoxMappingDataSource().getSelectedItem();
				if(dataSource != null){
					OpenFileTask task = new OpenFileTask(MapsFrame.this, MapsFrame.this, dataSource.getFileName());
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
		panelMapExchange.add(buttonOpenDataSource, new CellConstraints(5, 1, CellConstraints.FILL, CellConstraints.FILL));

		final JPanel panelStatus = new JPanel();
		panelStatus.setBackground(Color.WHITE);
		panelStatus.setBorder(new EmptyBorder(0, 0, 0, 0));
		panelStatus.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("211dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("17dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu")}));
		getContentPane().add(panelStatus, new CellConstraints(2, 6, 2, 1, CellConstraints.FILL, CellConstraints.FILL));

		textAreaStatus = new JTextArea();
		textAreaStatus.setFont(new Font("Calibri", Font.BOLD, 12));
		textAreaStatus.setLineWrap(true);
		textAreaStatus.setWrapStyleWord(true);
		textAreaStatus.setOpaque(true);
		textAreaStatus.setEditable(false);
		textAreaStatus.setText(MeshCompactUITranslator.getMapsWindowMessageWelcome(this.propertiesProvider.getLoggedUserName()));

		panelStatus.add(textAreaStatus, new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.FILL));
		panelStatus.add(getImageStatus(), new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));
	}
	
	protected JButton getButtonCreateMap() {
		if (buttonCreateMap == null) {
			buttonCreateMap = new JButton();
			buttonCreateMap.setBorder(new EmptyBorder(0, 0, 0, 0));
			buttonCreateMap.setBorderPainted(false);
			buttonCreateMap.setContentAreaFilled(false);
			buttonCreateMap.setFont(new Font("Arial", Font.PLAIN, 16));
			buttonCreateMap.setText(MeshCompactUITranslator.getMapsWindowLabelCreateMap());
			buttonCreateMap.setIcon(IconManager.getMapImage());
			buttonCreateMap.setToolTipText(MeshCompactUITranslator.getMapsWindowToolTipCreateMap());
			
			ActionListener createMapActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					CreateMapTask task = new CreateMapTask();
					task.execute();
				}
			};	
			buttonCreateMap.addActionListener(createMapActionListener);
		}
		return buttonCreateMap;
	}
	
	public JComboBox getComboBoxMappingDataSource() {
		if (comboBoxMappingDataSource == null) {
			comboBoxMappingDataSource = new JComboBox();
			comboBoxMappingDataSource.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxMappingDataSource.setBackground(Color.WHITE);
			comboBoxMappingDataSource.setToolTipText(MeshCompactUITranslator.getMapsWindowToolTipDataSourcesToCreateMap());
			
			comboBoxMappingDataSource.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					setURLFromSelectedDataSource();
				}
			});
		}
		return comboBoxMappingDataSource;
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
	
	private class OpenCloudMapTask extends SwingWorker<Void, Void> {
		 
		public OpenCloudMapTask(){
			super();
		}
		
		@Override
	    public Void doInBackground() {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			setInProcess(MeshCompactUITranslator.getMapsWindowMessageNetworkMapCreationStart());
			
			String url = textFieldURL.getText();
			if(!HttpSyncAdapterFactory.isValidURL(url)){
    			setError(MeshCompactUITranslator.getErrorInvalidURL());
				return null;
			}
			
			MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping) getComboBoxMappingDataSource().getSelectedItem();
			if(dataSource == null || !MsAccessSyncAdapterFactory.isValidAccessTable(dataSource.getFileName(), dataSource.getTableName())){
				setError(MeshCompactUITranslator.getErrorInvalidMSAccessTable());
				return null;
			}

			String urlWebKml = url + "?format=kml";
			String fileName = getBaseDirectory() + "/"+ dataSource.getAlias() + "_web.kml";
			File file = new File(fileName);
			
 			try{
    			if(!file.exists()){
    				SyncEngineUtil.makeKMLWithNetworkLink(getKmlTemplateNetworkLinkFileName(), fileName, dataSource.getAlias(), urlWebKml);
    			}
    			
    			setOk(MeshCompactUITranslator.getMapsWindowMessageNetworkMapCreationEnd());
    					
    		}catch(Throwable e){
    			LogFrame.Logger.error(e.getMessage(), e);
    			setError(MeshCompactUITranslator.getMapsWindowMessageNetworkMapCreationFailed());
    			return null;
    		}

    		try{
    			OpenFileTask.openFile(MapsFrame.this, file);
    		}catch(Throwable e){
    			LogFrame.Logger.error(e.getMessage(), e);
    			setError(MeshCompactUITranslator.getMapsWindowMessageNetworkMapOpenFailed());
    			return null;
    		}
    		
			return null;
	    }

		@Override
	    public void done() {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}
	
	private class CreateMapTask extends SwingWorker<Void, Void> {
		 
		public CreateMapTask(){
			super();
		}
		
		@Override
	    public Void doInBackground() {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			setInProcess(MeshCompactUITranslator.getMapsWindowMessageMapCreationStart());
			
			MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping) getComboBoxMappingDataSource().getSelectedItem();
			if(dataSource == null || !MsAccessSyncAdapterFactory.isValidAccessTable(dataSource.getFileName(), dataSource.getTableName())){
				setError(MeshCompactUITranslator.getErrorInvalidMSAccessTable());
				return null;
			}
			
			File file = null;
			try{
				file = SyncEngineUtil.generateKML(
						getGeoCoderKey(), 
						getKmlTemplateFileName(), 
						dataSource.getFileName(), 
						dataSource.getAlias(), 
						getBaseDirectory(), 
						getSourceIdMapper(), 
						getIdentityProvider());

    			setOk(MeshCompactUITranslator.getMapsWindowMessageMapCreationEnd());
				
				
    		} catch(Throwable e){
    			LogFrame.Logger.error(e.getMessage(), e);
    			setError(MeshCompactUITranslator.getMapsWindowMessageMapCreationFailed());
    			return null;
    		}

    		try{
    			OpenFileTask.openFile(MapsFrame.this, file);
    		}catch(Throwable e){
    			LogFrame.Logger.error(e.getMessage(), e);
    			setError(MeshCompactUITranslator.getMapsWindowMessageMapOpenFailed());
    			return null;
    		}
			return null;
	    }

		@Override
	    public void done() {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}

	public SourceIdMapper getSourceIdMapper() {
		return this.sourceIdMapper;
	}

	public IIdentityProvider getIdentityProvider() {
		return this.propertiesProvider.getIdentityProvider();
	}

	public String getBaseDirectory() {
		return this.propertiesProvider.getBaseDirectory();
	}
	
	public String getGeoCoderKey(){
		return this.propertiesProvider.getGeoCoderKey();		
	}
	
	public String getKmlTemplateFileName(){
		return this.propertiesProvider.getDefaultKMLTemplateFileName();
	}
	
	public String getKmlTemplateNetworkLinkFileName(){
		return this.propertiesProvider.getDefaultKMLTemplateNetworkLinkFileName();
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
		this.buttonCreateMap.setEnabled(false);
		this.buttonOpenDataSource.setEnabled(false);
	}
	
	public void notifyOwnerNotWorking(){
		this.buttonCreateMap.setEnabled(true);
		this.buttonOpenDataSource.setEnabled(true);
	}

	@Override
	public void notifyError(String error) {
		setError(error);
	}
	
}