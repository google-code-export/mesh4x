package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.payload.mappings.MappingResolver;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.tasks.IErrorListener;
import org.mesh4j.sync.ui.tasks.OpenURLTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.utils.IconManager;
import org.mesh4j.sync.utils.KmlGenerator;
import org.mesh4j.sync.utils.KmlGeneratorFactory;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MeshAdminFrame extends JFrame implements IErrorListener {
	
	private static final long serialVersionUID = 6389763865972504440L;
	public final static Log Logger = LogFactory.getLog(MeshAdminFrame.class);

	// MODEL VARIABLES
	private JComboBox comboBoxAttributeToTitle;
	private JComboBox comboBoxAttributeToDescription;
	private JComboBox comboBoxGeoAddress;
	private JComboBox comboBoxIll;
	private JComboBox comboBoxUpdateTimestamp;
	private JTextField textFieldURL;
	private JTextArea textAreaTitle;
	private JTextArea textAreaDescription;
	private JLabel labelMappingsStatus;
	private JLabel labelCloudStatus;
	private JTextField textFieldMeshDescription;
	
	private MSAccessDataSourceMapping dataSource;
	private PropertiesProvider propertiesProvider;
	
	// BUSINESS METHODS

	public MeshAdminFrame(PropertiesProvider prop) {
		super();
		this.propertiesProvider = prop;
		
		setIconImage(IconManager.getCDCImage());
		getContentPane().setBackground(Color.WHITE);
		setTitle(MeshCompactUITranslator.getMeshAdminWindowTitle());
		setResizable(false);
		setBounds(100, 100, 712, 575);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("343dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("200dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("115dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JPanel panelEditMappings = new JPanel();
		panelEditMappings.setBorder(new TitledBorder(null, MeshCompactUITranslator.getMeshAdminWindowLabelMappings(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Calibri", Font.BOLD, 14), null));
		panelEditMappings.setBackground(Color.WHITE);
		panelEditMappings.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("55dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("149dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("81dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("14dlu")},
			new RowSpec[] {
				RowSpec.decode("11dlu"),
				RowSpec.decode("40dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("40dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("11dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		getContentPane().add(panelEditMappings, new CellConstraints(2, 2));

		final JLabel labelTitle = new JLabel();
		labelTitle.setText(MeshCompactUITranslator.getMeshAdminWindowLabelTitle());
		labelTitle.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelTitle, new CellConstraints(2, 2, CellConstraints.DEFAULT, CellConstraints.TOP));

		final JLabel labelDescription = new JLabel();
		labelDescription.setText(MeshCompactUITranslator.getMeshAdminWindowLabelDescription());
		labelDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelDescription, new CellConstraints(2, 4, CellConstraints.DEFAULT, CellConstraints.TOP));

		final JLabel labelGeoAddress = new JLabel();
		labelGeoAddress.setText(MeshCompactUITranslator.getMeshAdminWindowLabelAddress());
		labelGeoAddress.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelGeoAddress, new CellConstraints(2, 6));

		final JLabel labelIll = new JLabel();
		labelIll.setText(MeshCompactUITranslator.getMeshAdminWindowLabelIll());
		labelIll.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelIll, new CellConstraints(2, 8));

		final JLabel labelUpdateTimestamp = new JLabel();
		labelUpdateTimestamp.setText(MeshCompactUITranslator.getMeshAdminWindowLabelUpdateTimestamp());
		labelUpdateTimestamp.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelUpdateTimestamp, new CellConstraints(2, 10));

		textAreaTitle = new JTextArea();
		textAreaTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		textAreaTitle.setText("");
		panelEditMappings.add(textAreaTitle, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.FILL));

		comboBoxUpdateTimestamp = new JComboBox();
		comboBoxUpdateTimestamp.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxUpdateTimestamp.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxUpdateTimestamp, new CellConstraints(4, 10));

		comboBoxIll = new JComboBox();
		comboBoxIll.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxIll.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxIll, new CellConstraints(4, 8));

		comboBoxGeoAddress = new JComboBox();
		comboBoxGeoAddress.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxGeoAddress.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxGeoAddress, new CellConstraints(4, 6));

		textAreaDescription = new JTextArea();
		textAreaDescription.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panelEditMappings.add(textAreaDescription, new CellConstraints(4, 4, CellConstraints.FILL, CellConstraints.FILL));

		comboBoxAttributeToTitle = new JComboBox();
		comboBoxAttributeToTitle.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxAttributeToTitle.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxAttributeToTitle, new CellConstraints(6, 2));

		final JButton buttonAddAttributeToTitle = new JButton();
		buttonAddAttributeToTitle.setText(MeshCompactUITranslator.getMeshAdminWindowLabelAdd());
		buttonAddAttributeToTitle.setContentAreaFilled(false);
		buttonAddAttributeToTitle.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonAddAttributeToTitle.setBorderPainted(false);
		buttonAddAttributeToTitle.setOpaque(false);
		buttonAddAttributeToTitle.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonAddAttributeToTitle.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				String attributeName = (String)comboBoxAttributeToTitle.getSelectedItem();
				if(attributeName != null){
					textAreaTitle.setText(textAreaTitle.getText() + makeMapping(makeAttribute(attributeName)));
				}
			}
		});
		panelEditMappings.add(buttonAddAttributeToTitle, new CellConstraints(8, 2));

		comboBoxAttributeToDescription = new JComboBox();
		comboBoxAttributeToDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxAttributeToDescription.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxAttributeToDescription, new CellConstraints(6, 4));

		final JButton buttonAddAttributeToDescription = new JButton();
		buttonAddAttributeToDescription.setText(MeshCompactUITranslator.getMeshAdminWindowLabelAdd());
		buttonAddAttributeToDescription.setContentAreaFilled(false);
		buttonAddAttributeToDescription.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonAddAttributeToDescription.setBorderPainted(false);
		buttonAddAttributeToDescription.setOpaque(false);
		buttonAddAttributeToDescription.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonAddAttributeToDescription.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				String attributeName = (String)comboBoxAttributeToDescription.getSelectedItem();
				if(attributeName != null){
					textAreaDescription.setText(textAreaDescription.getText() + makeMapping(makeAttribute(attributeName)));
				}
			}
		});
		panelEditMappings.add(buttonAddAttributeToDescription, new CellConstraints(8, 4));

		final JButton buttonSave = new JButton();
		panelEditMappings.add(buttonSave, new CellConstraints(2, 12, CellConstraints.LEFT, CellConstraints.DEFAULT));
		buttonSave.setText(MeshCompactUITranslator.getMeshAdminWindowLabelSave());
		buttonSave.setContentAreaFilled(false);
		buttonSave.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSave.setBorderPainted(false);
		buttonSave.setOpaque(false);
		buttonSave.setFont(new Font("Calibri", Font.BOLD, 14));
		buttonSave.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				SaveMappingsTask task = new SaveMappingsTask();
				task.execute();
			}
		});

		labelMappingsStatus = new JLabel();
		labelMappingsStatus.setFont(new Font("Calibri", Font.PLAIN, 12));
		labelMappingsStatus.setText("");
		panelEditMappings.add(labelMappingsStatus, new CellConstraints(3, 12, 6, 1));
		
		final JPanel panelCloud = new JPanel();
		panelCloud.setBorder(new TitledBorder(null, MeshCompactUITranslator.getMeshAdminWindowLabelCloud(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Calibri", Font.BOLD, 14), null));
		panelCloud.setBackground(Color.WHITE);
		panelCloud.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("14dlu"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("256dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default"),
				RowSpec.decode("11dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		getContentPane().add(panelCloud, new CellConstraints(2, 4));

		final JButton buttonPublish = new JButton();
		buttonPublish.setText(MeshCompactUITranslator.getMeshAdminWindowLabelPublish());
		buttonPublish.setContentAreaFilled(false);
		buttonPublish.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonPublish.setBorderPainted(false);
		buttonPublish.setOpaque(false);
		buttonPublish.setFont(new Font("Calibri", Font.BOLD, 14));
		buttonPublish.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				CloudCrudTask task = new CloudCrudTask();
				task.execute();
			}
		});
		panelCloud.add(buttonPublish, new CellConstraints(2, 6, CellConstraints.LEFT, CellConstraints.DEFAULT));

		textFieldURL = new JTextField();
		textFieldURL.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelCloud.add(textFieldURL, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.CENTER));

		final JButton buttonViewCloud = new JButton();
		buttonViewCloud.setText("");
		buttonViewCloud.setContentAreaFilled(false);
		buttonViewCloud.setBorderPainted(false);
		buttonViewCloud.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonViewCloud.setBackground(Color.WHITE);
		buttonViewCloud.setText("");
		buttonViewCloud.setToolTipText(MeshCompactUITranslator.getSyncWindowTooltipViewFeed());
		buttonViewCloud.setIcon(IconManager.getViewCloudIcon());
		buttonViewCloud.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				OpenURLTask task = new OpenURLTask(MeshAdminFrame.this, MeshAdminFrame.this, getMappingsURL());
				task.execute();
			}
		});	
		panelCloud.add(buttonViewCloud, new CellConstraints(6, 2));

		final JLabel labelURL = new JLabel();
		labelURL.setText(MeshCompactUITranslator.getMeshAdminWindowLabelURL());
		labelURL.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelCloud.add(labelURL, new CellConstraints(2, 2));

		labelCloudStatus = new JLabel();
		labelCloudStatus.setFont(new Font("Calibri", Font.PLAIN, 12));
		labelCloudStatus.setText("");
		panelCloud.add(labelCloudStatus, new CellConstraints(4, 6, 3, 1));

		final JLabel labelMeshDescription = new JLabel();
		labelMeshDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		labelMeshDescription.setText(MeshCompactUITranslator.getMeshAdminWindowLabelDescription());
		panelCloud.add(labelMeshDescription, new CellConstraints(2, 4));

		textFieldMeshDescription = new JTextField();
		textFieldMeshDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelCloud.add(textFieldMeshDescription, new CellConstraints(4, 4));

	}

	protected String getMappingsURL() {
		return HttpSyncAdapter.makeMappingsURL(textFieldURL.getText());
	}

	protected String makeMapping(String mappingName) {
		return "{" + mappingName + "}";
	}
	
	protected String makeAttribute(String attributeName) {
		return this.dataSource.getAlias() + "/" + attributeName;
	}

	public void setDataSource(MSAccessDataSourceMapping dataSource){
		this.dataSource = dataSource;
		
		Set<String> textFields = SourceIdMapper.getTableTextColumns(dataSource.getFileName(), dataSource.getTableName());
		Set<String> booleanFields = SourceIdMapper.getTableBooleanColumns(dataSource.getFileName(), dataSource.getTableName());
		Set<String> timestampFields = SourceIdMapper.getTableTimestampColumns(dataSource.getFileName(), dataSource.getTableName());

		comboBoxAttributeToTitle.setModel(new DefaultComboBoxModel(textFields.toArray()));
		comboBoxAttributeToDescription.setModel(new DefaultComboBoxModel(textFields.toArray()));
		comboBoxGeoAddress.setModel(new DefaultComboBoxModel(textFields.toArray()));
		comboBoxIll.setModel(new DefaultComboBoxModel(booleanFields.toArray()));
		comboBoxUpdateTimestamp.setModel(new DefaultComboBoxModel(timestampFields.toArray()));
		
		textFieldURL.setText(this.propertiesProvider.getMeshURL(dataSource.getAlias()));
		
		MappingResolver mappingResolver = KmlGeneratorFactory.createMappingResolver(
			dataSource.getAlias(), 
			this.propertiesProvider.getBaseDirectory(),  
			this.propertiesProvider.getGeoCoderKey());
		
		if(mappingResolver == null){
			textAreaTitle.setText("");
			textAreaDescription.setText("");
		} else {
			String title = KmlGenerator.getTitleMapping(mappingResolver);
			String description = KmlGenerator.getDescriptionMapping(mappingResolver);
			
			String address = KmlGenerator.getAddressMapping(mappingResolver);
			if(address.startsWith(dataSource.getAlias())){
				address = address.substring(dataSource.getAlias().length() + 1, address.length());
			}
	
			String ill = KmlGenerator.getIllMapping(mappingResolver);
			if(ill.startsWith(dataSource.getAlias())){
				ill = ill.substring(dataSource.getAlias().length() + 1, ill.length());
			}
			
			
			String updateTimestamp = KmlGenerator.getUpdateTimestampMapping(mappingResolver);
			if(updateTimestamp.startsWith(dataSource.getAlias())){
				updateTimestamp = updateTimestamp.substring(dataSource.getAlias().length() + 1, updateTimestamp.length());
			}
			
			textAreaTitle.setText(title);
			textAreaDescription.setText(description);
			comboBoxGeoAddress.setSelectedItem(address);
			comboBoxIll.setSelectedItem(ill);
			comboBoxUpdateTimestamp.setSelectedItem(updateTimestamp);
		} 
	}

	@Override
	public void notifyError(String error) {
		setCloudStatusError(error);	
	}
	
	private void setMappingsStatusError(String error) {
		labelMappingsStatus.setText(error);
		labelMappingsStatus.setForeground(Color.RED);		
	}
	
	private void setMappingsStatusReady() {
		labelMappingsStatus.setText("");
	}
	
	private void setCloudStatusError(String error) {
		labelCloudStatus.setText(error);
		labelCloudStatus.setForeground(Color.RED);		
	}
	
	private void setCloudStatusReady() {
		labelCloudStatus.setText("");
	}
	
	private class SaveMappingsTask extends SwingWorker<Void, Void> {
		 
		public SaveMappingsTask(){
			super();
		}
		
		@Override
	    public Void doInBackground() {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			setMappingsStatusReady();
			String title = textAreaTitle.getText();
			String description = textAreaDescription.getText();
			String address = makeAttribute((String) comboBoxGeoAddress.getSelectedItem());
			String ill = makeMapping(makeAttribute((String) comboBoxIll.getSelectedItem()));
			String updateTimestamp = makeMapping(makeAttribute((String) comboBoxUpdateTimestamp.getSelectedItem()));
			try{
				String location = makeMapping(GeoCoderLocationPropertyResolver.makeMapping(address));
				String latitude = makeMapping(GeoCoderLatitudePropertyResolver.makeMapping(address)); 
				String longitude = makeMapping(GeoCoderLongitudePropertyResolver.makeMapping(address));
				SyncEngineUtil.saveMappings(
					dataSource.getAlias(), 
					propertiesProvider.getDefaultMappingsTemplateFileName(), 
					propertiesProvider.getBaseDirectory(), 
					title, 
					description, 
					location, 
					latitude,
					longitude,
					ill, 
					updateTimestamp);
			} catch (Exception e) {
				LogFrame.Logger.error(e.getMessage(), e);
				setMappingsStatusError(MeshCompactUITranslator.getErrorSaveMappingsFailed());
			}
			return null;
	    }

		@Override
	    public void done() {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}
	
	private class CloudCrudTask extends SwingWorker<Void, Void> {
		 
		public CloudCrudTask(){
			super();
		}
		
		@Override
	    public Void doInBackground() {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			setCloudStatusReady();
			
			try{
				SyncEngineUtil.uploadMeshDefinition(dataSource, textFieldMeshDescription.getText(), propertiesProvider);
				
			}catch (Exception e) {
				LogFrame.Logger.error(e.getMessage(), e);
				setCloudStatusError(MeshCompactUITranslator.getErrorSaveMeshCloudFailed());
			}
			return null;
	    }

		@Override
	    public void done() {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}
}
