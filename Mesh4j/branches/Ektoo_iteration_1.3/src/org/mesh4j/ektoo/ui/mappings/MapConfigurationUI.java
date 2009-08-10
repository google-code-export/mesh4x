package org.mesh4j.ektoo.ui.mappings;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MapConfigurationUI extends JPanel implements IMapTaskListener{

	private static final long serialVersionUID = 2231285677459604543L;
	public final static Log Logger = LogFactory.getLog(MapConfigurationUI.class);

	// MODEL VARIABLES
	private JComboBox comboBoxAttributeToTitle;
	private JComboBox comboBoxAttributeToDescription;
	private JComboBox comboBoxGeoAddress;
	private JComboBox comboBoxGeoAddressLon;
	private JComboBox comboBoxGeoAddressLat;
	private JTextArea textAreaTitle;
	private JTextArea textAreaDescription;	
	private JRadioButton noAddressRadioButton; 
	private JRadioButton addressRadioButton; 
	private JRadioButton latLonRadioButton; 
	private JButton buttonView;
	private JTextArea labelStatus;
	
	private EktooFrame ownerUI;
	private AbstractUIController controller;
	private ISyncAdapter adapter;
	private IRDFSchema rdfSchema;
	
	// BUSINESS METHODS

	public MapConfigurationUI(EktooFrame ui, AbstractUIController uicontroller, IRDFSchema schema, Mapping mapping, ISyncAdapter syncAdapter, String comments) {
		super();
		this.adapter = syncAdapter;
		this.ownerUI = ui;
		this.controller = uicontroller;
		this.rdfSchema = schema;
		
		setBackground(Color.WHITE);
		setBounds(100, 100, 519, 434);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("256dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("211dlu"),
				RowSpec.decode("48dlu")}));

		final JPanel panelEditMappings = new JPanel();
		panelEditMappings.setBackground(Color.WHITE);
		panelEditMappings.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("7dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("49dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("81dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("84dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("19dlu")},
			new RowSpec[] {
				RowSpec.decode("14dlu"),
				RowSpec.decode("40dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("40dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC}));
		add(panelEditMappings, new CellConstraints(1, 2, CellConstraints.LEFT, CellConstraints.TOP));

		final JLabel labelTitle = new JLabel();
		labelTitle.setText(EktooUITranslator.getMapConfigurationWindowLabelTitle());
		labelTitle.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelTitle, new CellConstraints(3, 2, CellConstraints.LEFT, CellConstraints.TOP));

		final JLabel labelDescription = new JLabel();
		labelDescription.setText(EktooUITranslator.getMapConfigurationWindowLabelDescription());
		labelDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelDescription, new CellConstraints(3, 4, CellConstraints.LEFT, CellConstraints.TOP));

		textAreaTitle = new JTextArea();
		textAreaTitle.setWrapStyleWord(true);
		textAreaTitle.setLineWrap(true);
		textAreaTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		textAreaTitle.setText("");
		panelEditMappings.add(textAreaTitle, new CellConstraints(5, 2, CellConstraints.FILL, CellConstraints.FILL));

		comboBoxGeoAddress = new JComboBox();
		comboBoxGeoAddress.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxGeoAddress.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxGeoAddress, new CellConstraints(5, 11));

		textAreaDescription = new JTextArea();
		textAreaDescription.setLineWrap(true);
		textAreaDescription.setWrapStyleWord(true);
		textAreaDescription.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panelEditMappings.add(textAreaDescription, new CellConstraints(5, 4, CellConstraints.FILL, CellConstraints.FILL));

		comboBoxAttributeToTitle = new JComboBox();
		comboBoxAttributeToTitle.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxAttributeToTitle.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxAttributeToTitle, new CellConstraints(7, 2));

		final JButton buttonAddAttributeToTitle = new JButton();
		buttonAddAttributeToTitle.setText(EktooUITranslator.getMapConfigurationWindowLabelAdd());
		buttonAddAttributeToTitle.setContentAreaFilled(false);
		buttonAddAttributeToTitle.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonAddAttributeToTitle.setBorderPainted(false);
		buttonAddAttributeToTitle.setOpaque(false);
		buttonAddAttributeToTitle.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonAddAttributeToTitle.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				String attributeName = (String)comboBoxAttributeToTitle.getSelectedItem();
				if(attributeName != null){
					textAreaTitle.setText(textAreaTitle.getText() + 
						Mapping.makeMapping(
							Mapping.makeAttribute(
								rdfSchema.getOntologyClassName(), 
								attributeName)));
				}
			}
		});
		panelEditMappings.add(buttonAddAttributeToTitle, new CellConstraints(9, 2));

		comboBoxAttributeToDescription = new JComboBox();
		comboBoxAttributeToDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxAttributeToDescription.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxAttributeToDescription, new CellConstraints(7, 4));

		final JButton buttonAddAttributeToDescription = new JButton();
		buttonAddAttributeToDescription.setText(EktooUITranslator.getMapConfigurationWindowLabelAdd());
		buttonAddAttributeToDescription.setContentAreaFilled(false);
		buttonAddAttributeToDescription.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonAddAttributeToDescription.setBorderPainted(false);
		buttonAddAttributeToDescription.setOpaque(false);
		buttonAddAttributeToDescription.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonAddAttributeToDescription.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				String attributeName = (String)comboBoxAttributeToDescription.getSelectedItem();
				if(attributeName != null){
					textAreaDescription.setText(textAreaDescription.getText() + 
						Mapping.makeMapping(
							Mapping.makeAttribute(
								rdfSchema.getOntologyClassName(),
								attributeName)));
				}
			}
		});
		panelEditMappings.add(buttonAddAttributeToDescription, new CellConstraints(9, 4));

		noAddressRadioButton = new JRadioButton();
		noAddressRadioButton.setFont(new Font("Calibri", Font.PLAIN, 12));
		noAddressRadioButton.setBackground(Color.WHITE);
		noAddressRadioButton.setText("Do not map the data");
		panelEditMappings.add(noAddressRadioButton, new CellConstraints(3, 8, 5, 1));

		addressRadioButton = new JRadioButton();
		addressRadioButton.setFont(new Font("Calibri", Font.PLAIN, 12));
		addressRadioButton.setBackground(Color.WHITE);
		addressRadioButton.setText("The data has an address field ");
		panelEditMappings.add(addressRadioButton, new CellConstraints(3, 9, 5, 1));

		latLonRadioButton = new JRadioButton();
		latLonRadioButton.setFont(new Font("Calibri", Font.PLAIN, 12));
		latLonRadioButton.setBackground(Color.WHITE);
		latLonRadioButton.setText("The data has latitude and longitude fields");
		panelEditMappings.add(latLonRadioButton, new CellConstraints(3, 13, 5, 1));

		
		ButtonGroup group = new ButtonGroup();
	    group.add(noAddressRadioButton);
	    group.add(addressRadioButton);
	    group.add(latLonRadioButton);

		comboBoxGeoAddressLat = new JComboBox();
		comboBoxGeoAddressLat.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxGeoAddressLat.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxGeoAddressLat, new CellConstraints(7, 15));

		final JLabel mapYourDataLabel = new JLabel();
		mapYourDataLabel.setFont(new Font("Calibri", Font.BOLD, 12));
		mapYourDataLabel.setText("Map your Data");
		panelEditMappings.add(mapYourDataLabel, new CellConstraints(2, 6, 4, 1, CellConstraints.LEFT, CellConstraints.TOP));

		comboBoxGeoAddressLon = new JComboBox();
		comboBoxGeoAddressLon.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxGeoAddressLon.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxGeoAddressLon, new CellConstraints(5, 15));

		final JLabel designMapPushpinsLabel = new JLabel();
		designMapPushpinsLabel.setFont(new Font("Calibri", Font.BOLD, 12));
		designMapPushpinsLabel.setText("Design map pushpins");
		panelEditMappings.add(designMapPushpinsLabel, new CellConstraints(2, 1, 4, 1));

		final JPanel panelButtons = new JPanel();
		panelButtons.setFont(new Font("", Font.PLAIN, 16));
		panelButtons.setBackground(Color.WHITE);
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("188dlu"),
				ColumnSpec.decode("38dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("17dlu")},
			new RowSpec[] {
				RowSpec.decode("18dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("18dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(panelButtons, new CellConstraints(1, 3));

		final JButton buttonSave = new JButton();
		buttonSave.setText(EktooUITranslator.getMapConfigurationWindowLabelSave());
		buttonSave.setContentAreaFilled(false);
		buttonSave.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSave.setBorderPainted(false);
		buttonSave.setOpaque(false);
		buttonSave.setFont(new Font("Calibri", Font.BOLD, 16));
		buttonSave.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				Mapping mapping = makeMapping();
				controller.setMapping(mapping);								
				ownerUI.closePopupViewWindow();				
			}			
		});
		panelButtons.add(buttonSave, new CellConstraints(5, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener viewKmlActionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				OpenMapTask task = new OpenMapTask(ownerUI.getPopupViewWindow(), MapConfigurationUI.this, controller, adapter, rdfSchema, makeMapping());	
				task.execute();
			}	
		};
		
		buttonView = new JButton();
		buttonView.setText(EktooUITranslator.getMapConfigurationWindowLabelView());
		buttonView.setContentAreaFilled(false);
		buttonView.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonView.setBorderPainted(false);
		buttonView.setOpaque(false);
		buttonView.setFont(new Font("Calibri", Font.BOLD, 16));
		buttonView.addActionListener(viewKmlActionListener);
		panelButtons.add(buttonView, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));

		final JTextArea labelComments = new JTextArea();
		labelComments.setLineWrap(true);
		labelComments.setWrapStyleWord(true);
		labelComments.setFont(new Font("Calibri", Font.PLAIN, 10));
		labelComments.setText(comments);	
		panelButtons.add(labelComments, new CellConstraints(2, 3, CellConstraints.FILL, CellConstraints.BOTTOM));

		labelStatus = new JTextArea();
		labelStatus.setForeground(Color.GREEN);
		labelStatus.setLineWrap(true);
		labelStatus.setText("");
		labelStatus.setWrapStyleWord(true);
		labelStatus.setFont(new Font("Calibri", Font.PLAIN, 10));
		panelButtons.add(labelStatus, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.BOTTOM));

	    ActionListener checkListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if(noAddressRadioButton.isSelected()){
					comboBoxGeoAddress.setSelectedItem("");
					comboBoxGeoAddress.setEnabled(false);
					comboBoxGeoAddressLat.setSelectedItem("");
					comboBoxGeoAddressLat.setEnabled(false);
					comboBoxGeoAddressLon.setSelectedItem("");
					comboBoxGeoAddressLon.setEnabled(false);
					buttonView.setEnabled(false);
				} else if(addressRadioButton.isSelected()){
					comboBoxGeoAddressLat.setSelectedItem("");
					comboBoxGeoAddressLat.setEnabled(false);
					comboBoxGeoAddressLon.setSelectedItem("");
					comboBoxGeoAddressLon.setEnabled(false);
					
					buttonView.setEnabled(adapter != null);
					comboBoxGeoAddress.setEnabled(true);
				}else if(latLonRadioButton.isSelected()){
					comboBoxGeoAddress.setSelectedItem("");
					comboBoxGeoAddress.setEnabled(false);

					buttonView.setEnabled(adapter != null);
					comboBoxGeoAddressLat.setEnabled(true);
					comboBoxGeoAddressLon.setEnabled(true);
				}
			}
	    };
	    
	    noAddressRadioButton.addActionListener(checkListener);
	    addressRadioButton.addActionListener(checkListener);
	    latLonRadioButton.addActionListener(checkListener);
		
		this.initializeFromValues(mapping);
	}

	public void initializeFromValues(Mapping mapping){
		
		TreeSet<String> propertyNames = new TreeSet<String>();		
		int size = rdfSchema.getPropertyCount();
		for (int i = 0; i < size; i++) {
			propertyNames.add(rdfSchema.getPropertyName(i));
		}
		
		comboBoxAttributeToTitle.setModel(new DefaultComboBoxModel(propertyNames.toArray()));
		comboBoxAttributeToDescription.setModel(new DefaultComboBoxModel(propertyNames.toArray()));
		
		ArrayList<String> values = new ArrayList<String>();
		values.add("");
		values.addAll(propertyNames);
		
		comboBoxGeoAddress.setModel(new DefaultComboBoxModel(values.toArray()));
		comboBoxGeoAddressLat.setModel(new DefaultComboBoxModel(values.toArray()));
		comboBoxGeoAddressLon.setModel(new DefaultComboBoxModel(values.toArray()));
		
		if(mapping == null){
			textAreaTitle.setText("");
			textAreaDescription.setText("");
		
			noAddressRadioButton.setSelected(true);
			comboBoxGeoAddressLat.setEnabled(false);
			comboBoxGeoAddressLon.setEnabled(false);
			comboBoxGeoAddress.setEnabled(false);
			buttonView.setEnabled(false);
		
		} else {
			String title = mapping.getMapping(ISyndicationFormat.MAPPING_NAME_ITEM_TITLE);
			String description = mapping.getMapping(ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION);
			
			if(title != null){
				textAreaTitle.setText(title);
			}
			
			if(description != null){
				textAreaDescription.setText(description);
			}
			
			String geoLocMapping = mapping.getAttribute(GeoCoderLocationPropertyResolver.MAPPING_NAME);
			if(geoLocMapping != null){
				addressRadioButton.setSelected(true);
				
				String address =  GeoCoderLocationPropertyResolver.getPropertyName(geoLocMapping);
	
				if(address != null && address.startsWith(rdfSchema.getOntologyClassName())){
					address = address.substring(rdfSchema.getOntologyClassName().length() + 1, address.length());
					comboBoxGeoAddress.setSelectedItem(address);
				}
				
				comboBoxGeoAddressLat.setEnabled(false);
				comboBoxGeoAddressLon.setEnabled(false);
				buttonView.setEnabled(adapter != null);
								
			} else {
				String geoLatMapping = mapping.getAttribute(GeoCoderLatitudePropertyResolver.MAPPING_NAME);
				String geoLonMapping = mapping.getAttribute(GeoCoderLongitudePropertyResolver.MAPPING_NAME);
				if(geoLatMapping != null || geoLonMapping != null){
					latLonRadioButton.setSelected(true);
					
					String latAttribute =  GeoCoderLatitudePropertyResolver.getPropertyName(geoLatMapping);		
					if(latAttribute != null && latAttribute.startsWith(rdfSchema.getOntologyClassName())){
						latAttribute = latAttribute.substring(rdfSchema.getOntologyClassName().length() + 1, latAttribute.length());
						comboBoxGeoAddressLat.setSelectedItem(latAttribute);
					}
					
					String lonAttribute =  GeoCoderLongitudePropertyResolver.getPropertyName(geoLonMapping);		
					if(lonAttribute != null && lonAttribute.startsWith(rdfSchema.getOntologyClassName())){
						lonAttribute = lonAttribute.substring(rdfSchema.getOntologyClassName().length() + 1, lonAttribute.length());
						comboBoxGeoAddressLon.setSelectedItem(lonAttribute);
					}
							
					comboBoxGeoAddress.setEnabled(false);
					buttonView.setEnabled(adapter != null);
				} else {
					noAddressRadioButton.setSelected(true);
					comboBoxGeoAddressLat.setEnabled(false);
					comboBoxGeoAddressLon.setEnabled(false);
					comboBoxGeoAddress.setEnabled(false);
					buttonView.setEnabled(false);
				}
			}
			 
		} 
	}
	
	private Mapping makeMapping() {
		String title = textAreaTitle.getText();
		String desc = textAreaDescription.getText();
		if(title == null || title.isEmpty() || desc == null || desc.isEmpty()){
			return null;
		}
		
		if(noAddressRadioButton.isSelected()){
			return controller.makeMapping(
					rdfSchema.getOntologyClassName(), 
					title,
					desc);
		} else if(addressRadioButton.isSelected()){
			String address = (String)comboBoxGeoAddress.getSelectedItem();
			if(address == null || address.isEmpty()){
				return null;
			}
			return controller.makeMapping(
					rdfSchema.getOntologyClassName(), 
					title,
					desc,
					address);
		} else if(latLonRadioButton.isSelected()){
			String lat = (String)comboBoxGeoAddressLat.getSelectedItem();
			String lon = (String)comboBoxGeoAddressLon.getSelectedItem();
			if(lat == null || lat.isEmpty() || lon == null || lon.isEmpty()){
				return null;
			}
			return controller.makeMapping(
					rdfSchema.getOntologyClassName(), 
					title,
					desc,
					lat,
					lon);
		} else {
			return null;
		}
	}
	
	// IMapTaskListener
	
	@Override
	public void notifyProgress(String msg){
		labelStatus.setForeground(Color.black);
		labelStatus.setText(msg);
	}

	@Override
	public void notifyError(String error) {
		labelStatus.setForeground(Color.red);
		labelStatus.setText(error);
	}	
}
