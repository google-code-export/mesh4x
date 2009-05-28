package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.msaccess.MsAccessDialect;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.test.utils.TestHelper;

import sun.jdbc.odbc.JdbcOdbcDriver;


public class HibernateContentadapterWithMsAccessTest {

	//@Test
	public void shouldConnectToExcel() throws Exception{
		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
		Assert.assertNotNull(driver);
		
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access/DevDB.mdb";
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}";
		Connection conn = DriverManager.getConnection(dbURL,"","");
		
		Statement command = conn.createStatement();
		ResultSet rs = command.executeQuery("select * from [User$]");
		while (rs.next())
		{
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
		}

		command.close();
		conn.close();
		System.out.println("Connected To Excel");

	}
	
	@Test
	public void shouldConnectToAccess() throws Exception{
		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
		Assert.assertNotNull(driver);
		
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access/DevDB.mdb";
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}";
		Connection conn = DriverManager.getConnection(dbURL,"","");
		Statement command = conn.createStatement();
		ResultSet rs = command.executeQuery("select user0_.id as uid0_, user0_.name as name0_, user0_.pass as pass0_ from User user0_");
		while (rs.next())
		{
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
		}

		System.out.println("Connected To Access");
		command.close();
		conn.close();
	}
	
	@Test
	public void shouldHibernateRead(){
		HibernateContentAdapter adapter = createAdapter();
	
		List<IContent> items = adapter.getAll();
		for (IContent entityContent : items) {
			Assert.assertNotNull(entityContent);
			Assert.assertNotNull(entityContent.getPayload());
		}
	}

	@Test
	public void shouldHibernateGet(){
		HibernateContentAdapter adapter = createAdapter();
		EntityContent entity = adapter.get("1");
		Assert.assertNotNull(entity);
		Assert.assertEquals("jmt", entity.getPayload().element("name").getText());
		Assert.assertEquals("123", entity.getPayload().element("pass").getText());
	}
	
	@Test
	public void shouldHibernateAdd(){
		HibernateContentAdapter adapter = createAdapter();		
		String id = IdGenerator.INSTANCE.newID();
		Element payload = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>"+id+"</pass></user>");
		EntityContent entity = new EntityContent(payload, "user", "id", id);
		
		adapter.save(entity);
		EntityContent entityAdded = adapter.get(id);

		Assert.assertNotNull(entityAdded);
		Assert.assertEquals(id, entityAdded.getPayload().element("id").getText());
		Assert.assertEquals(id, entityAdded.getPayload().element("name").getText());
		Assert.assertEquals(id, entityAdded.getPayload().element("pass").getText());
	}
	
	@Test
	public void shouldHibernateUpdate() throws Exception{
		HibernateContentAdapter adapter = createAdapter();		
		String id = IdGenerator.INSTANCE.newID();
		Element payload = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>"+id+"</pass></user>");
		EntityContent entity = new EntityContent(payload, "user", "id", id);
		
		adapter.save(entity);
		EntityContent entityAdded = adapter.get(id);

		Assert.assertNotNull(entityAdded);
		
		payload = TestHelper.makeElement("<user><id>"+id+"</id><name>5555</name><pass>5555</pass></user>");
		entity = new EntityContent(payload, "user", "id", id);
		adapter.save(entity);

		EntityContent entityUpdated = adapter.get(id);
		Assert.assertNotNull(entityUpdated);
		Assert.assertEquals("5555", entityUpdated.getPayload().element("name").getText());
		Assert.assertEquals("5555", entityUpdated.getPayload().element("pass").getText());
	}
	
	@Test
	public void shouldHibernateDelete() throws Exception{

		HibernateContentAdapter adapter = createAdapter();		
		String id = IdGenerator.INSTANCE.newID();
		Element payload = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user>");
		EntityContent entity = new EntityContent(payload, "user", "id", id);
		
		adapter.save(entity);
		EntityContent entityAdded = adapter.get(id);
		Assert.assertNotNull(entityAdded);
		
		adapter.delete(entityAdded);
		EntityContent entityDeleted = adapter.get(id);
		Assert.assertNull(entityDeleted);

	}
	
	private HibernateContentAdapter createAdapter() {
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access/DevDB.mdb";
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}";
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builder.setProperty("hibernate.connection.driver_class", JdbcOdbcDriver.class.getName());
		builder.setProperty("hibernate.connection.url", dbURL);
		builder.setProperty("hibernate.connection.username","");
		builder.setProperty("hibernate.connection.password","");
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));

		HibernateContentAdapter adapter = new HibernateContentAdapter(builder, "user");
		return adapter;
	}
}
