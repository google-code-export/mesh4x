package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.Iterator;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

public class HibernateDOMMappingExporter extends HibernateMappingExporter{

	public HibernateDOMMappingExporter(JDBCMetaDataConfiguration cfg, File file) {
		super(cfg, file);
	}

	@Override
	protected void init() {
		setTemplateName("hbm/hibernate-mapping-DOM.hbm.ftl");
    	setFilePattern("{package-name}/{class-name}.hbm.xml");    	
	}
	
	@Override
	protected String getClassNameForFile(POJOClass element) {
		return ((PersistentClass)element.getDecoratedObject()).getTable().getName();
	}
	
	public String getNodeName(Column column){
		return column.isQuoted() ? column.getName().replace(" ", "_") : column.getQuotedName();
	}
	
	@SuppressWarnings("unchecked")
	public String getNodeName(Property property){
		Iterator<Column> itColumn = property.getValue().getColumnIterator();
		Column column = itColumn.next();
		if(itColumn.hasNext()){
			return property.getName();
		} else {
			return getNodeName(column);
		}
	}
}
