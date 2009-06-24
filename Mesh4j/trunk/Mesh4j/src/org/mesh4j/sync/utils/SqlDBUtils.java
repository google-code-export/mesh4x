package org.mesh4j.sync.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	public static void executeSqlScript(Class<?> driverClass,
			String urlConnection, String user, String password,	String scriptFileName) {
		Guard.argumentNotNull(driverClass, "driverClass");
		Guard.argumentNotNullOrEmptyString(urlConnection, "urlConnection");
		Guard.argumentNotNullOrEmptyString(user, "user");
		Guard.argumentNotNull(password, "password");
		Guard.argumentNotNull(scriptFileName, "scriptFileName");

		File scriptFile = new File(scriptFileName);
		try {
			Class.forName(driverClass.getName());
			Connection con = DriverManager.getConnection(urlConnection, user, password);
			try {
				Statement stmt = con.createStatement();
				
				//read the file and identify all the individual query
				BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
				String line;
				StringBuffer query = new StringBuffer();
				boolean queryEnds = false;

				LOGGER.info("Listing individualt query...");
				while ((line = reader.readLine()) != null) {
					if (isComment(line))
						continue;
					queryEnds = line.indexOf(';') != -1;
					query.append(line);
					if (queryEnds) {
						LOGGER.info("# " + query);
						stmt.addBatch(query.toString());
						query.setLength(0);
					}
				}

				stmt.executeBatch();

			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				con.close();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private static boolean isComment(String line) {
		if ((line != null) && (line.length() > 0))
			return (line.charAt(0) == '#');
		return false;
	}
}
