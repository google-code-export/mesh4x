package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.IdGenerator;

import sun.jdbc.odbc.JdbcOdbcDriver;


public class HibernateContentadapterWithMsAccessTest {
	
	//@Test
	public void shouldConnectToAccess() throws Exception{
		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
		Assert.assertNotNull(driver);
		
		Connection conn = DriverManager.getConnection("jdbc:odbc:DevDB","mesh4j","mesh4j");
		Statement command = conn.createStatement();
		ResultSet rs = command.executeQuery("select user0_.id as uid0_, user0_.name as name0_, user0_.pass as pass0_ from User user0_");
		while (rs.next())
		{
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
		}

		System.out.println("Connected To Access");

	}
	
	//@Test
	public void shouldHibernateRead(){
		try{
			HibernateContentAdapter adapter = createAdapter();
		
			List<IContent> items = adapter.getAll();
			for (IContent entityContent : items) {
				Assert.assertNotNull(entityContent);
				Assert.assertNotNull(entityContent.getPayload());
			}
		}finally{
			this.removeHibernateProperties();
		}
	}

	//@Test
	public void shouldHibernateGet(){
		try{
			HibernateContentAdapter adapter = createAdapter();
			EntityContent entity = adapter.get("1");
			Assert.assertNotNull(entity);
			Assert.assertEquals("jmt", entity.getPayload().element("name").getText());
			Assert.assertEquals("jmt", entity.getPayload().element("pass").getText());
		}finally{
			this.removeHibernateProperties();
		}		
	}
	
	//@Test
	public void shouldHibernateAdd(){
		try{
			HibernateContentAdapter adapter = createAdapter();		
			String id = IdGenerator.newID();
			Element payload = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>"+id+"</pass></user>");
			EntityContent entity = new EntityContent(payload, "user", id);
			
			adapter.save(entity);
			EntityContent entityAdded = adapter.get(id);
	
			Assert.assertNotNull(entityAdded);
			Assert.assertEquals(id, entityAdded.getPayload().element("id").getText());
			Assert.assertEquals(id, entityAdded.getPayload().element("name").getText());
			Assert.assertEquals(id, entityAdded.getPayload().element("pass").getText());
		}finally{
			this.removeHibernateProperties();
		}
	}
	
	//@Test
	public void shouldHibernateUpdate() throws Exception{
		try{
			HibernateContentAdapter adapter = createAdapter();		
			String id = IdGenerator.newID();
			Element payload = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>"+id+"</pass></user>");
			EntityContent entity = new EntityContent(payload, "user", id);
			
			adapter.save(entity);
			EntityContent entityAdded = adapter.get(id);
	
			Assert.assertNotNull(entityAdded);
			
			payload = TestHelper.makeElement("<user><id>"+id+"</id><name>5555</name><pass>5555</pass></user>");
			entity = new EntityContent(payload, "user", id);
			adapter.save(entity);
	
			EntityContent entityUpdated = adapter.get(id);
			Assert.assertNotNull(entityUpdated);
			Assert.assertEquals("5555", entityUpdated.getPayload().element("name").getText());
			Assert.assertEquals("5555", entityUpdated.getPayload().element("pass").getText());
		}finally{
			this.removeHibernateProperties();
		}
	}
	
	//@Test
	public void shouldHibernateDelete() throws Exception{
		try{
			HibernateContentAdapter adapter = createAdapter();		
			String id = IdGenerator.newID();
			Element payload = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user>");
			EntityContent entity = new EntityContent(payload, "user", id);
			
			adapter.save(entity);
			EntityContent entityAdded = adapter.get(id);
			Assert.assertNotNull(entityAdded);
			
			adapter.delete(entityAdded);
			EntityContent entityDeleted = adapter.get(id);
			Assert.assertNull(entityDeleted);
		}finally{
			this.removeHibernateProperties();
		}
	}
	
	private HibernateContentAdapter createAdapter() {
		this.addHibernateProperties();
		HibernateContentAdapter adapter = new HibernateContentAdapter(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		return adapter;
	}

	private void addHibernateProperties() {
		System.setProperty("hibernate.dialect","org.mesh4j.sync.adapters.hibernate.MSAccessDialect");
		System.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		System.setProperty("hibernate.connection.url","jdbc:odbc:DevDB");
		System.setProperty("hibernate.connection.username","mesh4j");
		System.setProperty("hibernate.connection.password","mesh4j");
	}

	private void removeHibernateProperties() {
		System.clearProperty("hibernate.dialect");
		System.clearProperty("hibernate.connection.driver_class");
		System.clearProperty("hibernate.connection.url");
		System.clearProperty("hibernate.connection.username");
		System.clearProperty("hibernate.connection.password");
	}
}
