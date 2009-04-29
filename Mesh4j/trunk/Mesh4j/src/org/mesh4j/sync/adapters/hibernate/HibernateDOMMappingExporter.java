package org.mesh4j.sync.adapters.hibernate;

import java.io.File;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.PersistentClass;
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
}
