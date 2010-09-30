package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbmlint.detector.TableSelectorStrategy;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.mapping.MappingGenerator;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
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
	public static <T extends java.sql.Driver, F extends Dialect> SplitAdapter createHibernateAdapter(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, String tableName, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider, File propertiesFile) {
		return createHibernateAdapter(connectionURL, user, password, driverClass, dialectClass, tableName, null, rdfBaseURL, baseDirectory, identityProvider, propertiesFile);
	}
	
	public static <T extends java.sql.Driver, F extends Dialect> SplitAdapter createHibernateAdapter(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, String tableName, IRDFSchema schema, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider, File propertiesFile) {
		TreeSet<String> tables = new TreeSet<String>();
		tables.add(tableName);
		
		HashMap<String, IRDFSchema> schemas = new HashMap<String, IRDFSchema>();
		if (schema != null)
			schemas.put(tableName, schema);
		
		SplitAdapter[] adapters = createHibernateAdapters(connectionURL, user, password, driverClass, dialectClass, tables, schemas, rdfBaseURL, baseDirectory, identityProvider, propertiesFile);
		return adapters[0];
	}
	
	public static <T extends java.sql.Driver, F extends Dialect> CompositeSyncAdapter createSyncAdapterForMultiTables(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, Set<String> tables, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider, ISyncAdapter opaqueAdapter, File propertiesFile) {
		SplitAdapter[] splitAdapters = createHibernateAdapters(connectionURL, user, password, driverClass, dialectClass, tables, new HashMap<String, IRDFSchema>(), rdfBaseURL, baseDirectory, identityProvider, propertiesFile);
		
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
	
	private static <T extends java.sql.Driver, F extends Dialect> SplitAdapter[] createHibernateAdapters(String connectionURL, String user, String password, Class<T> driverClass, Class<F> dialectClass, Set<String> tables, Map<String, IRDFSchema> schemas, String rdfBaseURL, String baseDirectory, IIdentityProvider identityProvider, File propertiesFile) {
	
		HibernateSessionFactoryBuilder builder = createHibernateFactoryBuilder(connectionURL, user, password, driverClass, dialectClass, propertiesFile);
		
		HashMap<String, PersistentClass> contentMappings = createMappings(builder, baseDirectory, tables, schemas);

		SplitAdapter[] splitAdapters = new SplitAdapter[tables.size()];
		int i = 0;
		for (String tableName : contentMappings.keySet()) {
			String syncTableName = getSyncTableName(tableName);
			PersistentClass contentMapping = contentMappings.get(tableName);
			IRDFSchema rdfSchema = createRDFSchema(builder, tableName, rdfBaseURL, contentMapping);
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
	private static RDFSchema createRDFSchema(IHibernateSessionFactoryBuilder builder, String tableName, String rdfBaseURL, PersistentClass mapping) {
		if(rdfBaseURL == null){
			return null;
		}
		
		RDFSchema rdfSchema = new RDFSchema(tableName, rdfBaseURL+ "/" + tableName + "#", tableName);
		
		Property property = mapping.getIdentifierProperty();
		if(property.isComposite()){
			ComponentType componentType = (ComponentType)property.getType();
			String[] propertyNames = componentType.getPropertyNames();
			Type[] propertyTypes = componentType.getSubtypes();
			
			ArrayList<String> ids = new ArrayList<String>();
			ArrayList<String> guids = new ArrayList<String>();

			for (int i = 0; i < propertyTypes.length; i++) {
				String propName = propertyNames[i];
				String idName = RDFSchema.normalizePropertyName(propName);
				ids.add(idName);
				
				Type propertyType = propertyTypes[i];
				if(Hibernate.BINARY.equals(propertyType) && builder.isMsAccess()){
					rdfSchema.setGUIDPropertyName(idName);
				}
				addRDFProperty(rdfSchema, idName, propName, propertyType);
			}

			rdfSchema.setIdentifiablePropertyNames(ids);
			rdfSchema.setGUIDPropertyNames(guids);
		} else {
			String hibernatePropertyName = getHibernatePropertyName(property);
			String propertyName = RDFSchema.normalizePropertyName(hibernatePropertyName);
			Type propertyType = property.getType();
			
			addRDFProperty(rdfSchema, propertyName, hibernatePropertyName, propertyType);
			rdfSchema.setIdentifiablePropertyName(propertyName);
			if(Hibernate.BINARY.equals(propertyType) && builder.isMsAccess()){
				rdfSchema.setGUIDPropertyName(propertyName);
			}
		}
		
		
		Property version = mapping.getVersion();
		if(version != null){
			String hibernatePropertyName = getHibernatePropertyName(version);
			String propertyName = RDFSchema.normalizePropertyName(hibernatePropertyName);
			rdfSchema.setVersionPropertyName(propertyName);
		}
		
		Iterator<Property> it = mapping.getPropertyIterator();
		while(it.hasNext()){
			property = it.next();
			
			String hibernatePropertyName = getHibernatePropertyName(property);
			String propertyName = RDFSchema.normalizePropertyName(hibernatePropertyName);
			addRDFProperty(rdfSchema, propertyName, hibernatePropertyName, property.getType());
		}
		return rdfSchema;
	}
	
	// TODO (JMT) RDF: improve Hibernate type to RDF type mappings
	private static void addRDFProperty(RDFSchema rdfSchema, String propertyName, String label, Type type) {
		
		if(Hibernate.STRING.equals(type) || Hibernate.BINARY.equals(type)){
			rdfSchema.addStringProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(Hibernate.BOOLEAN.equals(type)){
			rdfSchema.addBooleanProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(Hibernate.DATE.equals(type) || Hibernate.TIMESTAMP.equals(type)){
			rdfSchema.addDateTimeProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}

		if(Hibernate.LONG.equals(type)){
			rdfSchema.addLongProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(Hibernate.INTEGER.equals(type)){
			rdfSchema.addIntegerProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}

		if(Hibernate.DOUBLE.equals(type)){
			rdfSchema.addDoubleProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}

		if(Hibernate.BIG_DECIMAL.equals(type)){
			rdfSchema.addDecimalProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}
		
		if(Hibernate.FLOAT.equals(type)){
			rdfSchema.addFloatProperty(propertyName, label, IRDFSchema.DEFAULT_LANGUAGE);
		}
	}

	private static String getHibernatePropertyName(Property property) {
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
		return propertyName;
	}
	
	public static HashMap<String, PersistentClass> createMappings(HibernateSessionFactoryBuilder builder, String baseDirectory, Set<String> tables, Map<String, IRDFSchema> schemas) {
		
		for (String tableName : tables) {
			String syncTableName = getSyncTableName(tableName);
			File contentMapping = FileUtils.getFile(baseDirectory, tableName+".hbm.xml");
			FileUtils.delete(contentMapping);
			File syncFileMapping = FileUtils.getFile(baseDirectory, syncTableName+".hbm.xml");
			FileUtils.delete(syncFileMapping);
		}
		
		boolean mustCreateOrUpdateTables = 
			autodiscoveryMappings(builder, baseDirectory, tables, schemas);
		
		String syncTableName;
		for (String tableName : tables) {
			File contentMapping = FileUtils.getFile(baseDirectory, tableName+".hbm.xml");
			if(!contentMapping.exists()){
				if (!schemas.containsKey(tableName)) {
					Guard.throwsException("SCHEMA_MISSING");
				}
				IRDFSchema schema = schemas.get(tableName);
				createMappingFromSchema(contentMapping, schema);
				mustCreateOrUpdateTables = true;
			}
			
			syncTableName = getSyncTableName(tableName);
			
			File syncFileMapping = FileUtils.getFile(baseDirectory, syncTableName+".hbm.xml");
			if(!syncFileMapping.exists()){
				mustCreateOrUpdateTables = true;
				createSyncMapping(syncTableName, syncFileMapping);
			}
			
			builder.addMapping(syncFileMapping);
			builder.addMapping(contentMapping);
		}
		
		Configuration cfg = builder.buildConfiguration();
		if(mustCreateOrUpdateTables){
			SchemaUpdate schemaExport = new SchemaUpdate(cfg);
			schemaExport.execute(true, true);
			if (schemaExport.getExceptions().size() > 0) {
				throw new MeshException(MessageTranslator.translate("COULD_NOT_CREATE_TABLES"), (Throwable) schemaExport.getExceptions().get(0));
			}
		}
		
		HashMap<String, PersistentClass> mappings = new HashMap<String, PersistentClass>();
		
		
		for (String tableName : tables) {
			syncTableName = getSyncTableName(tableName);

			PersistentClass syncMapping = cfg.getClassMapping(syncTableName);
			if(syncMapping == null){
				Guard.throwsException("INVALID_TABLE_NAME");
			}
			
			PersistentClass contentMapping = cfg.getClassMapping(tableName);
			if(contentMapping == null){
				Guard.throwsException("INVALID_TABLE_NAME");
			}
			
			mappings.put(tableName, contentMapping);
		}
		
		return mappings;
		
	}

	private static void createMappingFromSchema(File contentMapping,
			IRDFSchema schema) {
		try {
			MappingGenerator.createMapping(schema, contentMapping.getAbsolutePath());
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private static void createSyncMapping(String syncTableName, File syncFileMapping) {
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
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean autodiscoveryMappings(HibernateSessionFactoryBuilder builder, String baseDirectory, Set<String> tables, Map<String, IRDFSchema> schemas) {
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
		
		// Add columns that are specified in the schema (if any)
		// but are not in the database table
		for (Iterator<PersistentClass> i = cfg.getClassMappings(); i.hasNext();) {
			PersistentClass persistent = i.next();
			Table table = persistent.getTable();
			
			IRDFSchema schema = schemas.get(table.getName());
			if (schema == null) continue;
			
			for (int j = 0; j < schema.getPropertyCount(); j++) {
				String propertyName = schema.getPropertyName(j);
				try {
					persistent.getProperty(propertyName);
				} catch (MappingException e) {
					Property prop = new Property();
					prop.setName(propertyName);
					prop.setNodeName(propertyName);
					
					SimpleValue value = new SimpleValue(table);
					value.setTypeName(MappingGenerator.getHibernateTypeFromXSD(schema.getPropertyType(propertyName)));
					value.addColumn(new Column(propertyName));
					
					prop.setValue(value);
					
					persistent.addProperty(prop);
				}
			}
		}
	
		HibernateMappingExporter exporter = new HibernateDOMMappingExporter(cfg, new File(baseDirectory));
		exporter.start();
		
		return true;
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
		builder.setProperty("hibernate.connection.password", (password == null ? "" : password));
		builder.setProperty("hibernate.show_sql", "true");
		builder.setProperty("hibernate.format_sql", "true");

		if(propertyFile != null){
			builder.setPropertiesFile(propertyFile);
		}
		return builder;
	}

	public static Set<String> getMySqlTableNames(String host, int port, String schema, String user, String password) {
		String url = SqlDBUtils.getMySqlConnectionUrl(host, port, schema);
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
