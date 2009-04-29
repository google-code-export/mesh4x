package org.mesh4j.sync.adapters.hibernate.schema;

import java.io.File;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.junit.Test;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;

public class HibernateMappingExporterTests {

	@Test
	public void shouldExportHbmXmlFromDBTable(){
		HibernateSessionFactoryBuilder builder = this.createHibernateSessionBuilder();
		
		JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		builder.initializeConfiguration(cfg);		
		
		cfg.readFromJDBC();
		cfg.buildMappings();
		
		DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
		configurableNamingStrategy.setSettings(new ReverseEngineeringSettings(configurableNamingStrategy).setDefaultPackageName("org.reveng").setCreateCollectionForForeignKey(false));
		cfg.setReverseEngineeringStrategy(configurableNamingStrategy);
		
		HibernateMappingExporter exporter = new HibernateMappingExporter(cfg, new File("E:\\mesh4x\\tests\\"));
		exporter.start();
	}
	
	public HibernateSessionFactoryBuilder createHibernateSessionBuilder() {
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		
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
