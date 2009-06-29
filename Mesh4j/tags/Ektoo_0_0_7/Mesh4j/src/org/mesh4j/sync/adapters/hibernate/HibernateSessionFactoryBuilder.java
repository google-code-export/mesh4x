package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToPlainXMLMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.MeshException;

public class HibernateSessionFactoryBuilder implements IHibernateSessionFactoryBuilder {

	// MODEL VARIABLES
	private Set<File> mappings = new TreeSet<File>();
	private Properties properties = new Properties();
	private File propertiesFile;
	private HashMap<String, IRDFSchema> rdfSchemas = new HashMap<String, IRDFSchema>();
	
	// BUSINESS METHODS
	
	public void addMapping(File mapping){
		this.mappings.add(mapping);	
	}
	
	public SessionFactory buildSessionFactory() {
		Configuration hibernateConfiguration = buildConfiguration();	
		return hibernateConfiguration.buildSessionFactory();
	}

	public Configuration buildConfiguration() {
		Configuration hibernateConfiguration = new Configuration();		
		initializeConfiguration(hibernateConfiguration);
		return hibernateConfiguration;
	}

	public void initializeConfiguration(Configuration hibernateConfiguration) {
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
	}

	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);		
	}

	public void setPropertiesFile(File file) {
		this.propertiesFile = file;
	}

	public String getIdentifierPropertyName(String entityName) {
		ClassMetadata classMetadata = this.getClassMetadata(entityName);
		return classMetadata.getIdentifierPropertyName();	
	}

	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(String entityName){
		SessionFactory sessionFactory = buildSessionFactory();
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		for (Iterator<ClassMetadata> iterator = map.values().iterator(); iterator.hasNext();) {
			ClassMetadata classMetadata = iterator.next(); 
			if(classMetadata.getEntityName().equals(entityName)){
				return classMetadata;
			}
		}
		return null;
	}

	@Override
	public IHibernateToXMLMapping buildMeshMapping(String entityName, String idNode) {
		IRDFSchema rdfSchema = this.rdfSchemas.get(entityName);
		if(rdfSchema != null){
			return new HibernateToRDFMapping(rdfSchema);
		} else {
			return new HibernateToPlainXMLMapping(entityName, idNode);
		}
	}

	@Override
	public IHibernateToXMLMapping buildMeshMapping(String entityName) {
		return buildMeshMapping(entityName, this.getIdentifierPropertyName(entityName));
	}
	
	public void addRDFSchema(String tableName, IRDFSchema rdfSchema){
		this.rdfSchemas.put(tableName, rdfSchema);
	}

}
