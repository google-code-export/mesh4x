package org.mesh4j.sync.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.validations.Guard;

public class SqlDBUtils {

	private final static Log LOGGER = LogFactory.getLog(SqlDBUtils.class);

	public static <T extends java.sql.Driver> Set<String> getTableNames(Class<T> driverClass, String urlConnection, String user, String password) {
		
		Guard.argumentNotNull(driverClass, "driverClass");
		Guard.argumentNotNullOrEmptyString(urlConnection, "urlConnection");
		Guard.argumentNotNullOrEmptyString(user, "user");
		Guard.argumentNotNull(password, "password");
		
		TreeSet<String> tables = new TreeSet<String>();

		LOGGER.info("Listing all table name in Database!");
		
		try{
			Class.forName(driverClass.getName());
			Connection con = DriverManager.getConnection(urlConnection, user, password);
			try {
				DatabaseMetaData dbm = con.getMetaData();
				String[] types = { "TABLE" };
				ResultSet rs = dbm.getTables(null, null, "%", types);
				while (rs.next()) {
					String table = rs.getString("TABLE_NAME");
					if (table != null && table.trim().length() > 0){
						tables.add(table);
					}
					LOGGER.info("Table: " + table);
				}
			}catch(SQLException e) {
				LOGGER.error(e.getMessage(), e);
				LOGGER.info("No any table in the database");
			}finally{
				con.close();
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return tables;
	}

	public static String getMySqlUrlConnection(String host, int port, String schema) {
		Guard.argumentNotNullOrEmptyString(host, "host");
		Guard.argumentNotNullOrEmptyString(schema, "schema");		
		return "jdbc:mysql://" + host + ":" + port + "/"+schema;
	}
	
	public static String getMySqlUrlConnection(String schema) {
		Guard.argumentNotNullOrEmptyString(schema, "schema");		
		return "jdbc:mysql:///"+schema;
	}

	public static List<String> getDBNames(Class<Driver> driverClass, String urlConnection, String user, String password) {
		
		Guard.argumentNotNull(driverClass, "driverClass");
		Guard.argumentNotNullOrEmptyString(urlConnection, "urlConnection");
		Guard.argumentNotNullOrEmptyString(user, "user");
		Guard.argumentNotNull(password, "password");
		
		ArrayList<String> dbNames = new ArrayList<String>();

		LOGGER.info("Listing all db name in Database!");
		
		try{
			Class.forName(driverClass.getName());
			Connection con = DriverManager.getConnection(urlConnection, user, password);
			try {
				DatabaseMetaData dbm = con.getMetaData();
				ResultSet rs = dbm.getSchemas();
				while (rs.next()) {
					String dbSchemaName = rs.getString("TABLE_SCHEM");
					//String dbCatalogName = rs.getString("TABLE_CATALOG");
					if (dbSchemaName != null && dbSchemaName.trim().length() > 0){
						dbNames.add(dbSchemaName);
					}
					LOGGER.info("Schema: " + dbSchemaName);
				}
			}catch(SQLException e) {
				LOGGER.error(e.getMessage(), e);
				LOGGER.info("No any table in the database");
			}finally{
				con.close();
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return dbNames;
	}
}
