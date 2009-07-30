package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
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
	private JTextArea textAreaTitle;
	private JTextArea textAreaDescription;	
	
	private String aliasName;
	private Set<String> fieldValues;
	private Mapping mappingResolver;
	private CloudUIController controller;
	private EktooFrame ownerUI;
	
	// BUSINESS METHODS

	public MapConfigurationUI(EktooFrame ui, CloudUIController uicontroller, String aliasNameParam, Set<String> fieldValues, Mapping mappingResolver) {
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
		setBounds(100, 100, 630, 257);
		setLayout(new FormLayout(
			"325dlu",
			"118dlu, 23dlu"));

		final JPanel panelEditMappings = new JPanel();
		panelEditMappings.setBackground(Color.WHITE);
		panelEditMappings.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("49dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("149dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("81dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("19dlu")},
			new RowSpec[] {
				RowSpec.decode("11dlu"),
				RowSpec.decode("40dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("40dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(panelEditMappings, new CellConstraints());

		final JLabel labelTitle = new JLabel();
		labelTitle.setText(EktooUITranslator.getMapConfigurationWindowLabelTitle());
		labelTitle.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelTitle, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.TOP));

		final JLabel labelDescription = new JLabel();
		labelDescription.setText(EktooUITranslator.getMapConfigurationWindowLabelDescription());
		labelDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelDescription, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.TOP));

		final JLabel labelGeoAddress = new JLabel();
		labelGeoAddress.setText(EktooUITranslator.getMapConfigurationWindowLabelAddress());
		labelGeoAddress.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelEditMappings.add(labelGeoAddress, new CellConstraints(2, 6, CellConstraints.FILL, CellConstraints.TOP));

		textAreaTitle = new JTextArea();
		textAreaTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		textAreaTitle.setText("");
		panelEditMappings.add(textAreaTitle, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.FILL));

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
		panelEditMappings.add(buttonAddAttributeToTitle, new CellConstraints(8, 2));

		comboBoxAttributeToDescription = new JComboBox();
		comboBoxAttributeToDescription.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBoxAttributeToDescription.setBackground(Color.WHITE);
		panelEditMappings.add(comboBoxAttributeToDescription, new CellConstraints(6, 4));

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
		panelEditMappings.add(buttonAddAttributeToDescription, new CellConstraints(8, 4));

		final JPanel panelButtons = new JPanel();
		panelButtons.setFont(new Font("", Font.PLAIN, 16));
		panelButtons.setBackground(Color.WHITE);
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("17dlu")},
			new RowSpec[] {
				RowSpec.decode("13dlu")}));
		add(panelButtons, new CellConstraints(1, 2));

		final JButton buttonSave = new JButton();
		buttonSave.setText(EktooUITranslator.getMapConfigurationWindowLabelSave());
		buttonSave.setContentAreaFilled(false);
		buttonSave.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSave.setBorderPainted(false);
		buttonSave.setOpaque(false);
		buttonSave.setFont(new Font("Calibri", Font.BOLD, 16));
		buttonSave.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				controller.setMappings(
					aliasName, 
					textAreaTitle.getText(),
					textAreaDescription.getText(),
					(String)comboBoxGeoAddress.getSelectedItem());
				ownerUI.closePopupViewWindow();				
			}			
		});
		panelButtons.add(buttonSave, new CellConstraints(2, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

		this.initializeFromValues();
	}

	public void initializeFromValues(){
		
		comboBoxAttributeToTitle.setModel(new DefaultComboBoxModel(this.fieldValues.toArray()));
		comboBoxAttributeToDescription.setModel(new DefaultComboBoxModel(this.fieldValues.toArray()));
		comboBoxGeoAddress.setModel(new DefaultComboBoxModel(this.fieldValues.toArray()));
		
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
			String address =  GeoCoderLocationPropertyResolver.getMapping(geoLocMapping);

			if(address != null && address.startsWith(this.aliasName)){
				address = address.substring(this.aliasName.length() + 1, address.length());
				comboBoxGeoAddress.setSelectedItem(address);
			}
			
		} 
	}
}
