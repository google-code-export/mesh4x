package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.StringReader;

import javax.swing.JScrollPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTable;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableCellRenderer;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableModel;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.xml.sax.InputSource;

public class SchemaViewUI extends AbstractUI{

	private static final Log LOGGER = LogFactory.getLog(SchemaViewUI.class);
	private IRDFSchema schema = null;
	
	public SchemaViewUI(IRDFSchema schema){
		this.schema = schema;
		this.setLayout(new BorderLayout());
		initilize();
	}
	private void initilize(){
		StringReader reader = new StringReader(schema.asXML());
		InputSource is = new InputSource(reader);
    	JXmlTreeTableModel xmlTreeTableMode = null;
		try {
			xmlTreeTableMode = new JXmlTreeTableModel(is);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
    	JXmlTreeTable xmlTable = new JXmlTreeTable(xmlTreeTableMode);
    	xmlTable.getTree().setCellRenderer(new JXmlTreeTableCellRenderer());
    	
    	//empty JScrollPane implementation for removing column header
    	JScrollPane scroll = new JScrollPane() {
    	public void setColumnHeaderView(Component component) {
    	}
    	};
    	scroll.setViewportView(xmlTable);
    	this.add(scroll,BorderLayout.CENTER);
	}
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

}
