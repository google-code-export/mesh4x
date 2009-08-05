package org.mesh4j.ektoo.ui.conflicts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ItemEditorUI extends JPanel implements IWinnerUI{

	private static final long serialVersionUID = 5465988903813201850L;
	private final static Log LOGGER = LogFactory.getLog(ItemEditorUI.class);
	
	// MODEL VARIABLES
	private IRDFSchema rdfSchema;
	private RDFInstance rdfInstance;
	private HashMap<String, JTextField> properties = new HashMap<String, JTextField>();
	private Item sourceItem;
		
	private ConflictsUI owner;
	private JButton buttonWinner;
	private JButton buttonXML;
	private boolean viewAsXML;
	private JScrollPane scrollPanePropertiesTable;
	private JScrollPane scrollPaneXML;
	private JTextArea textAreaXML;
	
	// BUSINESS METHODS
		
	public ItemEditorUI(ConflictsUI ui, IRDFSchema rdfSchema, Item item){
		
		this.owner = ui;
		this.sourceItem = item;
		this.rdfSchema = rdfSchema;
		
		setPreferredSize(new Dimension(90, 106));

		setBounds(100, 100, 199, 265);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("76dlu"),
				ColumnSpec.decode("14dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("3dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("106dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		setBackground(Color.WHITE);
		
		final JLabel labelBy = new JLabel();
		labelBy.setText(getByMessage());
		add(labelBy, new CellConstraints(2, 2, 2, 1));

		final JLabel labelVersion = new JLabel();
		labelVersion.setText(getLastVersionMessage());
		add(labelVersion, new CellConstraints(2, 4));

		buttonWinner = new JButton();
		buttonWinner.setContentAreaFilled(true);
		buttonWinner.setBorderPainted(true);
		buttonWinner.setBackground(Color.WHITE);
		buttonWinner.setText(EktooUITranslator.getConflictItemLabelChooseWinner());
		buttonWinner.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) { setAsWinner(); }});
		add(buttonWinner, new CellConstraints(2, 8, 2, 1, CellConstraints.FILL, CellConstraints.FILL));
		
		textAreaXML = new JTextArea();
		textAreaXML.setWrapStyleWord(true);
		textAreaXML.setLineWrap(true);
		textAreaXML.setEditable(rdfSchema == null && !this.sourceItem.isDeleted());
		textAreaXML.setText("");
		
		scrollPaneXML = new JScrollPane(textAreaXML);
		add(scrollPaneXML, new CellConstraints(2, 6, 2, 1, CellConstraints.FILL, CellConstraints.FILL));

		buttonXML = new JButton();
		buttonXML.setMargin(new Insets(0, 0, 0, 0));
		buttonXML.setIconTextGap(4);
		buttonXML.setContentAreaFilled(true);
		buttonXML.setBorderPainted(false);
		buttonXML.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent arg) { setViewPanel(); }});
		buttonXML.setBackground(Color.WHITE);
		buttonXML.setText(EktooUITranslator.getConflictItemLabelXML());
		add(buttonXML, new CellConstraints(3, 4, CellConstraints.CENTER, CellConstraints.FILL));
		
		if(this.sourceItem.isDeleted()){
			scrollPaneXML.setVisible(true);
			buttonXML.setVisible(false);
			buttonWinner.setEnabled(false);
		} else {
			buttonWinner.setEnabled(true);
			
			if(rdfSchema == null){
				scrollPaneXML.setVisible(true);
				buttonXML.setVisible(false);
				
				try{
					textAreaXML.setText(XMLHelper.formatXML(item.getContent().getPayload(), OutputFormat.createPrettyPrint()));
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			} else {
				scrollPaneXML.setVisible(false);
				buttonXML.setVisible(true);
				
				this.rdfInstance = rdfSchema.createNewInstanceFromRDFXML(item.getContent().getPayload());
			
				int rowCount = rdfSchema.getPropertyCount();
				RowSpec[] rowSpec = new RowSpec[rowCount *2];
				for (int i = 0; i < rowSpec.length; i++) {
					if(i%2!=0){
						rowSpec[i]=RowSpec.decode("15dlu");
					} else {
						if(i==0){
							rowSpec[i]=RowSpec.decode("5dlu");
						} else {
							rowSpec[i]=RowSpec.decode("2dlu");
						}
					}
				}
				
				JPanel panelEditProperties = new JPanel();
				panelEditProperties.setBackground(Color.WHITE);
				panelEditProperties.setLayout(new FormLayout(
					new ColumnSpec[] {
						ColumnSpec.decode("3dlu"),
						ColumnSpec.decode("30dlu"),
						ColumnSpec.decode("3dlu"),
						ColumnSpec.decode("45dlu"),
						ColumnSpec.decode("3dlu")},
					rowSpec));
		
				for (int i = 0; i < rowCount; i++) {
					String propertyName = rdfSchema.getPropertyName(i);
					String propertyLabel = rdfSchema.getPropertyLabel(propertyName);
					String propertyType = rdfSchema.getPropertyType(propertyName);
					
					JLabel label = new JLabel(propertyLabel);
					panelEditProperties.add(label, new CellConstraints(2, (i*2) + 2, CellConstraints.FILL, CellConstraints.FILL));
					
					String value = this.rdfInstance.getPropertyValueAsLexicalForm(propertyName);
					
					JTextField component = makeComponent(value, propertyType);
					
					if(rdfSchema.isIdentifiablePropertyName(propertyName)){
						component.setEnabled(false);
					}
					
					this.properties.put(propertyName, component);
					panelEditProperties.add(component, new CellConstraints(4, (i*2) + 2, CellConstraints.FILL, CellConstraints.FILL));
				}
		
				scrollPanePropertiesTable = new JScrollPane(panelEditProperties);
				add(scrollPanePropertiesTable, new CellConstraints(2, 6, 2, 1, CellConstraints.FILL, CellConstraints.FILL));
			}
		}
	}

	private String getLastVersionMessage(){
		return EktooUITranslator.getIemEditorLastVersionMessage(this.sourceItem.getLastUpdate().getSequence() + 1);
	}
	
	private String getByMessage() {
		return EktooUITranslator.getIemEditorByMessage();
	}

	private JTextField makeComponent(String value, String propertyType) {
		JTextField textField = new JTextField();
		textField.setText(value);
		return textField;
	}
	
	public void setViewPanel(){
		if(!viewAsXML){
			try{
				setValues();
				textAreaXML.setText(XMLHelper.formatXML(rdfInstance.asElementPlainXml(ISchema.EMPTY_FORMATS, null), OutputFormat.createPrettyPrint()));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				textAreaXML.setText("");
			}
			
			scrollPanePropertiesTable.setVisible(false);
			scrollPaneXML.setVisible(true);
			buttonXML.setText(EktooUITranslator.getConflictItemLabelVAL());
			viewAsXML = true;
		} else {
			scrollPaneXML.setVisible(false);
			scrollPanePropertiesTable.setVisible(true);
			buttonXML.setText(EktooUITranslator.getConflictItemLabelXML());
			viewAsXML = false;
		}
	}
	
	private void setValues() {
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		
		for (String propertyName : this.properties.keySet()) {
			JTextField field = this.properties.get(propertyName);
			
			String value = field.getText();
			propertyValues.put(propertyName, value);
		}
		
		this.rdfInstance = this.rdfSchema.createNewInstanceFromProperties(this.rdfInstance.getId(), propertyValues);		
	}

	public void setAsWinner() {
		this.buttonWinner.setVisible(false);
		this.setBackground(Color.GREEN);		
		this.buttonXML.setBackground(Color.GREEN);
		
		this.owner.setWinner(this);
	}
	
	public void setAsConflict() {
		this.buttonWinner.setVisible(true);
		this.buttonWinner.setEnabled(true);
		this.setBackground(Color.WHITE);
		this.buttonXML.setBackground(Color.WHITE);		
	}

	@Override
	public String getSyncId() {
		return this.sourceItem.getSyncId();
	}

	@Override
	public Item getWinner() {
		if(rdfSchema == null){
			Element payload = XMLHelper.parseElement(textAreaXML.getText());
			IContent content = new XMLContent(this.sourceItem.getSyncId(), "", "", payload);
			Sync sync = this.sourceItem.getSync().clone().update(LoggedInIdentityProvider.getUserName(), new Date(), false);
			Item item = new Item(content, sync);
			return item;
		} else {
			IContent content = new XMLContent(this.rdfInstance.getId(), "", "", this.rdfInstance.asElementRDFXML());
			Sync sync = this.sourceItem.getSync().clone().update(LoggedInIdentityProvider.getUserName(), new Date(), false);
			Item item = new Item(content, sync);
			return item;
		}
	}
	
}
