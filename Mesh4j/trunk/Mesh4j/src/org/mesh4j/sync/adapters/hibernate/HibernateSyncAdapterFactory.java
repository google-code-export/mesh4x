package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbmlint.detector.TableSelectorStrategy;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.schema.HibernateDOMMappingExporter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;

public class HibernateSyncAdapterFactory implements ISyncAdapterFactory{

	// BUSINESS METHODS
	
	// TODO (JMT) ISyncAdapterFactory methods read JDBC url connections
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return false;
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		return null;
	}

	@Override
	public String getSourceType() {
		return null;
	}
	
	// ADAPTER CREATION
	@SuppressWarnings("unchecked")
	public static ISyncAdapter createHibernateAdapter(String connectionURL, String user, String password, Class driverClass, Class dialectClass, String tableName, String syncTableName, String rdfURL, String baseDirectory) {
	
		HibernateSessionFactoryBuilder builder = createHibernateFactoryBuilder(connectionURL, user, password, driverClass, dialectClass, null);
		
		createMappings(builder, tableName, syncTableName, baseDirectory);
		
		Configuration cfg = builder.buildConfiguration();
		PersistentClass mapping = cfg.getClassMapping(tableName);
		
//		if(mapping == null){  // TODO (JMT) create tables automatically if absent from RDF schema
//			SchemaExport schemaExport = new SchemaExport(cfg);
//			
//			boolean mustShowScript = true;
//			boolean mustCreateTables = false;
//			schemaExport.create(mustShowScript, mustCreateTables);
//			
//			mapping = cfg.getClassMapping(tableName);
//		}
		
		RDFSchema schema = createRDFSchema(tableName, rdfURL, mapping);
		builder.addRDFSchema(schema);
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, syncTableName);
		
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builder, syncInfoParser);
		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builder, tableName);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
		return splitAdapter;
	}

	@SuppressWarnings("unchecked")
	private static RDFSchema createRDFSchema(String tableName, String rdfURL, PersistentClass mapping) {
		RDFSchema schema = new RDFSchema(tableName, rdfURL, tableName);
		Property p = mapping.getIdentifierProperty();
		schema.addStringProperty(p.getName(), p.getName(), "en");
		
		Iterator<Property> it = mapping.getPropertyIterator();
		while(it.hasNext()){
			p = it.next();
			
			String name = p.getType().getName();  // TODO (JMT) add sql to rdf attribute translation
			if(name.equals("string")){
				schema.addStringProperty(p.getName(), p.getName(), "en");
			}
		}
		return schema;
	}
	
	public static void createMappings(HibernateSessionFactoryBuilder builder, String tableName, String syncTableName, String baseDirectory){
		JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		builder.initializeConfiguration(cfg);		
		
		TableSelectorStrategy reverseEngineeringStrategy = new TableSelectorStrategy(new DefaultReverseEngineeringStrategy());
		reverseEngineeringStrategy.addSchemaSelection(new SchemaSelection(null, null, tableName));
		reverseEngineeringStrategy.addSchemaSelection(new SchemaSelection(null, null, syncTableName));
		
		cfg.setReverseEngineeringStrategy(reverseEngineeringStrategy);
		cfg.readFromJDBC();		
		cfg.buildMappings();
		
		HibernateMappingExporter exporter = new HibernateDOMMappingExporter(cfg, new File(baseDirectory));
		exporter.start();
		
		builder.addMapping(new File(baseDirectory + tableName+".hbm.xml"));
		builder.addMapping(new File(baseDirectory + syncTableName+".hbm.xml"));
	}

	@SuppressWarnings("unchecked")
	public static HibernateSessionFactoryBuilder createHibernateFactoryBuilder(String connectionURL, String user, String password, Class driverClass, Class dialectClass, File propertyFile) {
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.setProperty("hibernate.dialect", dialectClass.getName());
		builder.setProperty("hibernate.connection.driver_class", driverClass.getName());
		builder.setProperty("hibernate.connection.url", connectionURL);
		builder.setProperty("hibernate.connection.username", user);
		builder.setProperty("hibernate.connection.password", password);
		builder.setProperty("hibernate.show_sql", "true");
		builder.setProperty("hibernate.format_sql", "true");

		if(propertyFile != null){
			builder.setPropertiesFile(propertyFile);
		}
		return builder;
	}

}
