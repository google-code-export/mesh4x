package com.mesh4j.sync.adapters.msaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Test;

import sun.jdbc.odbc.JdbcOdbcDriver;

import com.mesh4j.sync.adapters.hibernate.EntityContent;
import com.mesh4j.sync.adapters.hibernate.EntityDAO;
import com.mesh4j.sync.adapters.hibernate.ISessionProvider;

public class MsAccessSpike {
	
	@Test
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
	public void shouldConnectToAccessWithHibernate() throws Exception{
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.addFile(this.getClass().getResource("User.hbm.xml").getFile());	
		SessionFactory sessionFactory = hibernateConfiguration.buildSessionFactory();
		EntityDAO dao = new EntityDAO("user", "id", new MockSessionProvider(sessionFactory.openSession()));
		
		List<EntityContent> items = dao.getAll();
		for (EntityContent entityContent : items) {
			Assert.assertNotNull(entityContent);
			Assert.assertNotNull(entityContent.getPayload());
		}
	}
	
	private class MockSessionProvider implements ISessionProvider{
		
		Session session;
		
		public MockSessionProvider(Session session){
			this.session = session;
		}
		
		@Override
		public Session getCurrentSession() {
			return session;
		}
		
	}
}
