package org.mesh4j.sync.adapters.hibernate.schema;

import java.io.File;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;

public class HibernateSchemaExporterTest {

	@Test
	public void shouldCreateTableIfAbsentFromHibernateMapping(){
		HibernateSessionFactoryBuilder builder = this.createHibernateSessionBuilder();

		SchemaExport schemaExport = new SchemaExport(builder.buildConfiguration());
		
		boolean mustShowScript = true;
		boolean mustCreateTables = false;
		schemaExport.create(mustShowScript, mustCreateTables);
	}

	public HibernateSessionFactoryBuilder createHibernateSessionBuilder() {
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile()));
	
		builder.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		builder.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		builder.setProperty("hibernate.connection.url", "jdbc:mysql:///mesh4xdb");
		builder.setProperty("hibernate.connection.username", "root");
		builder.setProperty("hibernate.connection.password", "");
		builder.setProperty("hibernate.show_sql", "true");
		builder.setProperty("hibernate.format_sql", "true");

		//builder.setPropertiesFile(new File(this.getClass().getResource("xx_hibernate.properties").getFile()));
		builder.setProperty("hibernate.query.substitutions", "yes 'Y', no 'N'");
		builder.setProperty("hibernate.max_fetch_depth", "1");
		builder.setProperty("hibernate.jdbc.batch_versioned_data", "true");
		builder.setProperty("hibernate.jdbc.use_streams_for_binary", "true");
		builder.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
		return builder;
	}

}
