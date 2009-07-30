package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

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
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.payload.mappings.Mapping;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MapConfigurationUI extends JPanel {

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
	
	private String aliasName;
	private Set<String> fieldValues;
	private Mapping mappingResolver;
	private AbstractUIController controller;
	private EktooFrame ownerUI;
	
	// BUSINESS METHODS

	public MapConfigurationUI(EktooFrame ui, AbstractUIController uicontroller, String aliasNameParam, Set<String> fieldValues, Mapping mappingResolver) {
		super();
		this.ownerUI = ui;
		this.controller = uicontroller;
		this.aliasName = aliasNameParam;
		this.fieldValues = fieldValues;
		if(mappingResolver == null){
			this.mappingResolver = new Mapping(null);
		} else {
			this.mappingResolver = mappingResolver;
		}
		
		setBackground(Color.WHITE);
		setBounds(100, 100, 519, 400);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("256dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("211dlu"),
				RowSpec.decode("33dlu")}));

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
				ColumnSpec.decode("81dlu"),
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
		textAreaTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		textAreaTitle.setText("");
		panelEditMappings.add(textAreaTitle, new CellConstraints(5, 2, CellConstraints.FILL, CellConstraints.FILL));

		comboBoxGeoAddress = new JComboBox();
		comboBoxGeoAddress.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxGeoAddress.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxGeoAddress, new CellConstraints(5, 11));

		textAreaDescription = new JTextArea();
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
								aliasName, 
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
								aliasName,
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
			"237dlu, 17dlu",
			"19dlu"));
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
				
				if(noAddressRadioButton.isSelected()){
					controller.setMappings(
							aliasName, 
							textAreaTitle.getText(),
							textAreaDescription.getText());
				} else if(addressRadioButton.isSelected()){
					controller.setMappings(
							aliasName, 
							textAreaTitle.getText(),
							textAreaDescription.getText(),
							(String)comboBoxGeoAddress.getSelectedItem());
				} else if(latLonRadioButton.isSelected()){
					controller.setMappings(
							aliasName, 
							textAreaTitle.getText(),
							textAreaDescription.getText(),
							(String)comboBoxGeoAddressLat.getSelectedItem(),
							(String)comboBoxGeoAddressLon.getSelectedItem());
				}
								
				ownerUI.closePopupViewWindow();				
			}			
		});
		panelButtons.add(buttonSave, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.FILL));

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
				} else if(addressRadioButton.isSelected()){
					comboBoxGeoAddressLat.setSelectedItem("");
					comboBoxGeoAddressLat.setEnabled(false);
					comboBoxGeoAddressLon.setSelectedItem("");
					comboBoxGeoAddressLon.setEnabled(false);
					
					comboBoxGeoAddress.setEnabled(true);
				}else if(latLonRadioButton.isSelected()){
					comboBoxGeoAddress.setSelectedItem("");
					comboBoxGeoAddress.setEnabled(false);

					comboBoxGeoAddressLat.setEnabled(true);
					comboBoxGeoAddressLon.setEnabled(true);
				}
			}
	    };
	    
	    noAddressRadioButton.addActionListener(checkListener);
	    addressRadioButton.addActionListener(checkListener);
	    latLonRadioButton.addActionListener(checkListener);
		
		this.initializeFromValues();
	}

	public void initializeFromValues(){
		
		comboBoxAttributeToTitle.setModel(new DefaultComboBoxModel(this.fieldValues.toArray()));
		comboBoxAttributeToDescription.setModel(new DefaultComboBoxModel(this.fieldValues.toArray()));
		
		ArrayList<String> values = new ArrayList<String>();
		values.add("");
		values.addAll(this.fieldValues);
		
		comboBoxGeoAddress.setModel(new DefaultComboBoxModel(values.toArray()));
		comboBoxGeoAddressLat.setModel(new DefaultComboBoxModel(values.toArray()));
		comboBoxGeoAddressLon.setModel(new DefaultComboBoxModel(values.toArray()));
		
		if(mappingResolver == null){
			textAreaTitle.setText("");
			textAreaDescription.setText("");
		} else {
			String title = mappingResolver.getMapping(ISyndicationFormat.MAPPING_NAME_ITEM_TITLE);
			String description = mappingResolver.getMapping(ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION);
			
			if(title != null){
				textAreaTitle.setText(title);
			}
			
			if(description != null){
				textAreaDescription.setText(description);
			}
			
			String geoLocMapping = mappingResolver.getAttribute(GeoCoderLocationPropertyResolver.MAPPING_NAME);
			if(geoLocMapping != null){
				addressRadioButton.setSelected(true);
				
				String address =  GeoCoderLocationPropertyResolver.getPropertyName(geoLocMapping);
	
				if(address != null && address.startsWith(this.aliasName)){
					address = address.substring(this.aliasName.length() + 1, address.length());
					comboBoxGeoAddress.setSelectedItem(address);
				}
				
				comboBoxGeoAddressLat.setEnabled(false);
				comboBoxGeoAddressLon.setEnabled(false);
								
			} else {
				String geoLatMapping = mappingResolver.getAttribute(GeoCoderLatitudePropertyResolver.MAPPING_NAME);
				String geoLonMapping = mappingResolver.getAttribute(GeoCoderLongitudePropertyResolver.MAPPING_NAME);
				if(geoLatMapping != null || geoLonMapping != null){
					latLonRadioButton.setSelected(true);
					
					String latAttribute =  GeoCoderLatitudePropertyResolver.getPropertyName(geoLatMapping);		
					if(latAttribute != null && latAttribute.startsWith(this.aliasName)){
						latAttribute = latAttribute.substring(this.aliasName.length() + 1, latAttribute.length());
						comboBoxGeoAddressLat.setSelectedItem(latAttribute);
					}
					
					String lonAttribute =  GeoCoderLongitudePropertyResolver.getPropertyName(geoLonMapping);		
					if(lonAttribute != null && lonAttribute.startsWith(this.aliasName)){
						lonAttribute = lonAttribute.substring(this.aliasName.length() + 1, lonAttribute.length());
						comboBoxGeoAddressLon.setSelectedItem(lonAttribute);
					}
							
					comboBoxGeoAddress.setEnabled(false);
				} else {
					noAddressRadioButton.setSelected(true);
					comboBoxGeoAddressLat.setEnabled(false);
					comboBoxGeoAddressLon.setEnabled(false);
					comboBoxGeoAddress.setEnabled(false);
				}
			}
			 
		} 
	}
}
