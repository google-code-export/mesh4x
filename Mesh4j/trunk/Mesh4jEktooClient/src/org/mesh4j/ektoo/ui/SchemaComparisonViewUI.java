package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.io.StringReader;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlComparerTreeTableCellRenderer;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTable;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableCellRenderer;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableModel;
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
	}
	
}
