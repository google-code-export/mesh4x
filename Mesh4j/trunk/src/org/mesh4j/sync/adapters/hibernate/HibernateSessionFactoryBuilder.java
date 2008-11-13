package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.mesh4j.sync.validations.MeshException;

public class HibernateSessionFactoryBuilder implements IHibernateSessionFactoryBuilder {

	// MODEL VARIABLES
	private Set<File> mappings = new TreeSet<File>();
	private Properties properties = new Properties();
	private File propertiesFile;
	
	// BUSINESS METHODS
	
	public void addSyncInfoMapping() {
		File syncMapping = new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile());
		this.addMapping(syncMapping);
	}

	public void addMapping(File mapping){
		this.mappings.add(mapping);	
	}
	
	public SessionFactory buildSessionFactory() {
		Configuration hibernateConfiguration = new Configuration();
		
		if(propertiesFile != null){
			FileInputStream is = null;
			try{
				Properties prop = new Properties();
				is = new FileInputStream(this.propertiesFile);
				prop.load(new FileInputStream(this.propertiesFile));
				hibernateConfiguration.setProperties(prop);
			} catch (Exception e) {
				throw new MeshException(e);
			} finally{
				if(is != null){
					try{
						is.close();
					} catch (Exception e) {
						throw new MeshException(e);
					}
				}
			}			
		}
		
		if(!properties.isEmpty()){
			hibernateConfiguration.setProperties(properties);
		}
		
		for (File mapping : this.mappings) {
			hibernateConfiguration.addFile(mapping);	
		}
	
		return hibernateConfiguration.buildSessionFactory();
	}

	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);		
	}

	public void setPropertiesFile(File file) {
		this.propertiesFile = file;
	}

}
