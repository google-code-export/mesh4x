package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTable;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableCellRenderer;
import org.mesh4j.ektoo.ui.component.treetable.xmltreetable.JXmlTreeTableModel;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.xml.sax.InputSource;

public class SchemaViewUI extends AbstractUI{

	private static final long serialVersionUID = 9007453368340141274L;
	private static final Log LOGGER = LogFactory.getLog(SchemaViewUI.class);
	private IRDFSchema schema = null;
	
	public SchemaViewUI(IRDFSchema schema){
		this.schema = schema;
		this.setLayout(new BorderLayout(0,0));
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
    	this.add(xmlTable,BorderLayout.CENTER);
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
