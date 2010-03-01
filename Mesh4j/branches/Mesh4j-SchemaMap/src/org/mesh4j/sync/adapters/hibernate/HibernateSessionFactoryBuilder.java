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
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateMsAccessToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToPlainXMLMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessDialect;
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
			if(this.useSunDriverForMsAccess()){
				this.setProperty("hibernate.connection.provider_class", "org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessDriverManagerConnectionProvider");
			}
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

	public String getIdentifierPropertyName(SessionFactory sessionFactory, String entityName) {
		ClassMetadata classMetadata = this.getClassMetadata(sessionFactory, entityName);
		return classMetadata.getIdentifierPropertyName();	
	}

	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(SessionFactory sessionFactory, String entityName){
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
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName, String idNode) {
		IRDFSchema rdfSchema = this.rdfSchemas.get(entityName);
		if(rdfSchema != null){
			if(this.isMsAccess()){
				return new HibernateMsAccessToRDFMapping(rdfSchema);
			} else {
				return new HibernateToRDFMapping(rdfSchema);
			}
		} else {
			return new HibernateToPlainXMLMapping(entityName, idNode);
		}
	}

/*	@Override
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName, String idNode, 
			Map<String, Resource> syncSchema, Map<String, String> schemaConversionMap) {
		IRDFSchema rdfSchema = this.rdfSchemas.get(entityName);
		if(rdfSchema != null){
			if(this.isMsAccess()){
				return new HibernateMsAccessToRDFMapping(rdfSchema);
			} else {
				return new HibernateToRDFExtendedMapping(rdfSchema, syncSchema, schemaConversionMap);
			}
		} else {
			return new HibernateToPlainXMLMapping(entityName, idNode);
		}
	}*/
	
	@Override
	public boolean isMsAccess() {
		return useSunDriverForMsAccess(); 
// TODO(JMT) XHTT driver, add this line  ==> || HxttAccessDialect.class.getName().equals(dialect);
	}
	
	private boolean useSunDriverForMsAccess() {
		String dialect = (String)this.properties.get("hibernate.dialect");
		return  dialect != null && MsAccessDialect.class.getName().equals(dialect); 
	}

	@Override
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName) {
		return buildMeshMapping(sessionFactory, entityName, this.getIdentifierPropertyName(sessionFactory, entityName));
	}
	
/*	@Override
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName,
			Map<String, Resource> syncSchema, Map<String, String> schemaConversionMap){
		return buildMeshMapping(sessionFactory, entityName, this.getIdentifierPropertyName(sessionFactory, entityName), syncSchema,	schemaConversionMap);
	}*/
	
	public void addRDFSchema(String tableName, IRDFSchema rdfSchema){
		this.rdfSchemas.put(tableName, rdfSchema);
	}

}
