package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Hibernate;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbmlint.detector.TableSelectorStrategy;
import org.hibernate.type.Type;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.SqlDBUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.mysql.jdbc.Driver;

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
	public static <T extends java.sql.Driver, F extends Dialect> SplitAdapter createHibernateAdapter(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, String tableName, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider) {
		TreeSet<String> tables = new TreeSet<String>();
		tables.add(tableName);
		
		SplitAdapter[] adapters = createHibernateAdapters(connectionURL, user, password, driverClass, dialectClass, tables, rdfBaseURL, baseDirectory, identityProvider);
		return adapters[0];
	}
	
	public static <T extends java.sql.Driver, F extends Dialect> CompositeSyncAdapter createSyncAdapterForMultiTables(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, Set<String> tables, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider, ISyncAdapter opaqueAdapter) {
		SplitAdapter[] splitAdapters = createHibernateAdapters(connectionURL, user, password, driverClass, dialectClass, tables, rdfBaseURL, baseDirectory, identityProvider);
		
		IIdentifiableSyncAdapter[] adapters =  new IIdentifiableSyncAdapter[splitAdapters.length];
		int i = 0;
		for (SplitAdapter splitAdapter : splitAdapters) {
			HibernateContentAdapter contentAdapter = (HibernateContentAdapter)splitAdapter.getContentAdapter();
			String type = contentAdapter.getType();
			adapters[i] = new IdentifiableSyncAdapter(type, splitAdapter);
			i = i +1;
		}
		
		return new CompositeSyncAdapter("Hibernate composite", opaqueAdapter, identityProvider, adapters);
	}
	
	private static <T extends java.sql.Driver, F extends Dialect> SplitAdapter[] createHibernateAdapters(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, Set<String> tables, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider) {
	
		HibernateSessionFactoryBuilder builder = createHibernateFactoryBuilder(connectionURL, user, password, driverClass, dialectClass, null);
		
		HashMap<String, PersistentClass> contentMappings = createMappings(builder, baseDirectory, tables);

		SplitAdapter[] splitAdapters = new SplitAdapter[tables.size()];
		int i = 0;
		for (String tableName : contentMappings.keySet()) {
			String syncTableName = getSyncTableName(tableName);
			PersistentClass contentMapping = contentMappings.get(tableName);
			IRDFSchema rdfSchema = createRDFSchema(tableName, rdfBaseURL, contentMapping);
			if(rdfSchema != null){
				builder.addRDFSchema(tableName, rdfSchema);
			}
			
			SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, syncTableName);
		
			HibernateSyncRepository syncRepository = new HibernateSyncRepository(builder, syncInfoParser);
			HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builder, tableName);
			SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, identityProvider);
			splitAdapters[i] = splitAdapter;
			i = i +1;
		}
		return splitAdapters;
	}

	@SuppressWarnings("unchecked")
	private static RDFSchema createRDFSchema(String tableName, String rdfBaseURL, PersistentClass mapping) {
		if(rdfBaseURL == null){
			return null;
		}
		
		RDFSchema rdfSchema = new RDFSchema(tableName, rdfBaseURL+ "/" + tableName + "#", tableName);
		
		Property property = mapping.getIdentifierProperty();
		addRDFProperty(rdfSchema, property);
		
		Iterator<Property> it = mapping.getPropertyIterator();
		while(it.hasNext()){
			property = it.next();
			addRDFProperty(rdfSchema, property);
		}
		return rdfSchema;
	}
	
	// TODO (JMT) RDF: improve Hibernate type to RDF type mappings
	private static void addRDFProperty(RDFSchema rdfSchema, Property property) {
		String propertyName = null;
		if (property.getValue().getColumnIterator().hasNext()){
			propertyName = ((Column) property.getValue()
					.getColumnIterator().next()).getName();
		}else{
			property.getName();
		}
		
		/*code changed by Sharif: May 05, 2009
		 
		Reason: we need to use the column name (if available) rather than the property name itself 
		because they might be different in case (see example below), in which case data (from database) of
		corresponding column will not be synced with same column of other repository if the 
		other repository is created automatically using the schema from the hibernate repository
		
		for example:
		<property name="pass" type="string" node="PASS">
            <column name="PASS" length="50" />
        </property>
        
        */
		
		Type type = property.getType();
		
		if(Hibernate.STRING.equals(type)){
			rdfSchema.addStringProperty(propertyName, propertyName, "en");
		}
		
		if(Hibernate.BOOLEAN.equals(type)){
			rdfSchema.addBooleanProperty(propertyName, propertyName, "en");
		}
		
		if(Hibernate.DATE.equals(type) || Hibernate.TIMESTAMP.equals(type)){
			rdfSchema.addDateTimeProperty(propertyName, propertyName, "en");
		}

		if(Hibernate.LONG.equals(type)){
			rdfSchema.addLongProperty(propertyName, propertyName, "en");
		}
		
		if(Hibernate.INTEGER.equals(type)){
			rdfSchema.addIntegerProperty(propertyName, propertyName, "en");
		}

		if(Hibernate.DOUBLE.equals(type)){
			rdfSchema.addDoubleProperty(propertyName, propertyName, "en");
		}

		if(Hibernate.BIG_DECIMAL.equals(type)){
			rdfSchema.addDecimalProperty(propertyName, propertyName, "en");
		}
		
		if(Hibernate.FLOAT.equals(type)){
			rdfSchema.addFloatProperty(propertyName, propertyName, "en");
		}
	}
	
	public static HashMap<String, PersistentClass> createMappings(HibernateSessionFactoryBuilder builder, String baseDirectory, Set<String> tables) {
		
		autodiscoveryMappings(builder, baseDirectory, tables);

		boolean mustCreateTables = false;
		String syncTableName;
		for (String tableName : tables) {
			syncTableName = getSyncTableName(tableName);
			
			File contentMapping = FileUtils.getFile(baseDirectory, tableName+".hbm.xml");
			if(!contentMapping.exists()){
				Guard.throwsException("INVALID_TABLE_NAME");
			}			
			
			File syncFileMapping = FileUtils.getFile(baseDirectory, syncTableName+".hbm.xml");
			if(!syncFileMapping.exists()){
				try{
					String template = "<?xml version=\"1.0\"?><!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">"+
					"<hibernate-mapping>"+
					"	<class entity-name=\"{0}\" node=\"{0}\" table=\"{0}\">"+
					"		<id name=\"sync_id\" type=\"string\" column=\"sync_id\">"+
					"			<generator class=\"assigned\"/>"+
					"		</id>"+
					"		<property name=\"entity_name\" column=\"entity_name\" node=\"entity_name\" type=\"string\"/>"+
					"		<property name=\"entity_id\" column=\"entity_id\" node=\"entity_id\" type=\"string\"/>"+
					"		<property name=\"entity_version\" column=\"entity_version\" node=\"entity_version\" type=\"string\"/>"+
					"		<property name=\"sync_data\" column=\"sync_data\" node=\"sync_data\" type=\"string\" length=\"65535\"/>"+
					"	</class>"+
					"</hibernate-mapping>";
					
					String xml = MessageFormat.format(template, syncTableName);
					FileUtils.write(syncFileMapping.getCanonicalPath(), xml.getBytes());
					mustCreateTables = true;
				} catch (Exception e) {
					throw new MeshException(e);
				}
			}
			
			builder.addMapping(syncFileMapping);
			builder.addMapping(contentMapping);
		}
		
		Configuration cfg = builder.buildConfiguration();
		if(mustCreateTables){			
			SchemaUpdate schemaExport = new SchemaUpdate(cfg);
			schemaExport.execute(true, true);
		}
		
		HashMap<String, PersistentClass> mappings = new HashMap<String, PersistentClass>();
		
		
		for (String tableName : tables) {
			syncTableName = getSyncTableName(tableName);

			PersistentClass syncMapping = cfg.getClassMapping(syncTableName);
			if(syncMapping == null){
				Guard.throwsException("INVALID_TABLE_NAME");
			}
			
			PersistentClass contentMapping = cfg.getClassMapping(tableName);
			if(contentMapping == null){  // TODO (JMT) create tables automatically if absent from RDF schema
				Guard.throwsException("INVALID_TABLE_NAME");
			}
			
			mappings.put(tableName, contentMapping);
		}
		
		return mappings;
		
	}

	private static void autodiscoveryMappings(HibernateSessionFactoryBuilder builder, String baseDirectory, Set<String> tables) {
		JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		builder.initializeConfiguration(cfg);		
		
		TableSelectorStrategy reverseEngineeringStrategy = new TableSelectorStrategy(new DefaultReverseEngineeringStrategy());
		
		for (String tableName : tables) {
			String syncTableName = getSyncTableName(tableName);
			
			reverseEngineeringStrategy.addSchemaSelection(new SchemaSelection(null, null, tableName));
			reverseEngineeringStrategy.addSchemaSelection(new SchemaSelection(null, null, syncTableName));
		}
		
		cfg.setReverseEngineeringStrategy(reverseEngineeringStrategy);
		cfg.readFromJDBC();		
		cfg.buildMappings();
		
		HibernateMappingExporter exporter = new HibernateDOMMappingExporter(cfg, new File(baseDirectory));
		exporter.start();
	}

	public static String getSyncTableName(String tableName) {
		return tableName + "_sync";
	}

	public static <T extends java.sql.Driver, F extends Dialect> HibernateSessionFactoryBuilder createHibernateFactoryBuilder(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, File propertyFile) {
		
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

	public static Set<String> getMySqlTableNames(String host, int port, String schema, String user, String password) {
		String url = SqlDBUtils.getMySqlUrlConnection(host, port, schema);
		Set<String> tableNames = SqlDBUtils.getTableNames(Driver.class, url, user, password);
		TreeSet<String> result = new TreeSet<String>();
		
		for (String tableName : tableNames) {
			if(!tableName.toLowerCase().endsWith("_sync")){
				result.add(tableName);
			}
		}
		return result;
	}

}
