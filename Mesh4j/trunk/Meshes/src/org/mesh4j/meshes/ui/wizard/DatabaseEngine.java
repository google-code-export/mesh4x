package org.mesh4j.meshes.ui.wizard;

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

}
