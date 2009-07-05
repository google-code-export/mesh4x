package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.StringReader;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import org.mesh4j.ektoo.ui.component.RoundBorder;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlComparerTreeTableCellRenderer;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTable;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableCellRenderer;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableModel;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.xml.sax.InputSource;

public class SchemaComparisonViewUI extends JPanel{

	
	private IRDFSchema source ;
	private IRDFSchema target;
	
	
	public SchemaComparisonViewUI(IRDFSchema source ,IRDFSchema target){
		this.source = source;
		this.target = target;
		this.setLayout(new BorderLayout());
		initialize();
	}
	
	private void initialize(){
		JXmlTreeTable xmlTable = null;
		InputSource is = null;
		JXmlTreeTableModel xmlTreeTableModelSource = null;
		JXmlTreeTableModel xmlTreeTableModelTarget = null;
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
		
		is = new InputSource(new StringReader(source.asXML()));
		try {
			xmlTreeTableModelSource = new JXmlTreeTableModel(is);
		} catch (Exception e) {
		}
		xmlTable = new JXmlTreeTable(xmlTreeTableModelSource);
    	xmlTable.getTree().setCellRenderer(new JXmlTreeTableCellRenderer());
    	splitPane.setLeftComponent(xmlTable);
    	
    	is = new InputSource(new StringReader(target.asXML()));
		try {
			xmlTreeTableModelTarget = new JXmlTreeTableModel(is);
		} catch (Exception e) {
		}
		xmlTable = new JXmlTreeTable(xmlTreeTableModelTarget);
    	xmlTable.getTree().setCellRenderer(new JXmlComparerTreeTableCellRenderer(xmlTreeTableModelSource));
    	splitPane.setRightComponent(xmlTable);
    	
    	JScrollPane scrollPane = new JScrollPane();
    	scrollPane.setViewportView(splitPane);
        
    	this.add(scrollPane,BorderLayout.CENTER);
    	this.add(getSchemaComparisonInfoPanel(),BorderLayout.NORTH);
	}
	
	private JPanel getSchemaComparisonInfoPanel(){
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel colorBandPanel = new JPanel();
		colorBandPanel.setLayout(gridBagLayout);
		
		JPanel containerPanel = new JPanel(new FlowLayout());
	
		JLabel conflictItemLabelText = new JLabel(EktooUITranslator.getConflictsTextforSchema());
		JPanel conflictItemColor = new JPanel();
		conflictItemColor.setBackground(Color.RED);
		//conflictItemColor.setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY)));
		
		JLabel newItemLabelText = new JLabel(EktooUITranslator.getNewItemTextforSchema());
		JPanel newItemColor = new JPanel();
		newItemColor.setBackground(Color.GREEN);
		//newItemColor.setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY)));
		
		containerPanel.add(conflictItemLabelText);
		containerPanel.add(conflictItemColor);
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(5,10));
		containerPanel.add(separator);
		containerPanel.add(newItemLabelText);
		containerPanel.add(newItemColor);
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 0;
		colorBandPanel.add(containerPanel, c);
		
		return colorBandPanel;
	}
	
}
