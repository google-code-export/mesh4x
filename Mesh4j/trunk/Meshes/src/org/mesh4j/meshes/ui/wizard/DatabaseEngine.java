package org.mesh4j.meshes.ui.wizard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum DatabaseEngine {
	
	mysql("mysql", "org.hibernate.dialect.MySQLDialect", "com.mysql.jdbc.Driver");
	
	private String schema;
	private String dialectClass;
	private String driverClass;
	
	private DatabaseEngine(String schema, String dialectClass, String driverClass) {
		this.schema = schema;
		this.dialectClass = dialectClass;
		this.driverClass = driverClass;
	}
	
	public String getConnectionUrl(String host, String port) {
		return getConnectionUrl(host, port, "");
	}
	
	public String getConnectionUrl(String host, String port, String database) {
		return "jdbc:" + schema + "://" + host + ":" + port + "/" + database;
	}
	
	public String getDialectClass() {
		return dialectClass;
	}
	
	public String getDriverClass() {
		return driverClass;
	}
	
	public String getShowDatabasesQuery() {
		return "show databases";
	}
	
	public String getShowTablesQuery() {
		return "show tables";
	}
	
	public List<String> getDatabaseNames(String url, String user, String password) throws SQLException, ClassNotFoundException {
		List<String> databaseNames = new ArrayList<String>();
		addDatabaseNames(url, user, password, databaseNames);
		return databaseNames;
	}
	
	public void addDatabaseNames(String url, String user, String password, List<String> databaseNames) throws SQLException, ClassNotFoundException {
		add(url, user, password, getShowDatabasesQuery(), databaseNames);
	}

	public List<String> getTableNames(String url, String user, String password) throws SQLException, ClassNotFoundException {
		List<String> tableNames = new ArrayList<String>();
		addTableNames(url, user, password, tableNames);
		return tableNames;
	}
	
	public void addTableNames(String url, String user, String password, Collection<String> tableNames) throws SQLException, ClassNotFoundException {
		add(url, user, password, getShowTablesQuery(), tableNames);
	}
	
	private void add(String url, String user, String password, String query, Collection<String> tableNames) throws SQLException, ClassNotFoundException {
		Class.forName(getDriverClass());
		Connection conn = DriverManager.getConnection(url, user, password);
		PreparedStatement ps = conn.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			tableNames.add(rs.getString(1));
		}
		rs.close();
		ps.close();
		conn.close();
	}

}
