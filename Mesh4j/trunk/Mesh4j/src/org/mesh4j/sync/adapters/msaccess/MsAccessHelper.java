package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.validations.MeshException;

import sun.jdbc.odbc.JdbcOdbcDriver;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.Index.ColumnDescriptor;

public class MsAccessHelper {
	
	private final static Log LOGGER = LogFactory.getLog(MsAccessHelper.class);

	public static Set<String> getTableNames(String mdbFileName) throws IOException {
		Set<String> tableNames = null;
		
		File mdbFile = new File(mdbFileName);
		Database db = Database.open(mdbFile);
		try{
			tableNames = db.getTableNames();
		} finally{
			db.close();
		}
		return tableNames;
	}
	
	public static boolean existTable(String mdbFileName, String mdbTableName){
		try{
			File mdbFile = new File(mdbFileName);
			Database db = Database.open(mdbFile);
			try{
				Table table = db.getTable(mdbTableName);
				return table != null;
			} finally{
				db.close();
			}
		}catch (Exception e) {
			return false;
		}
	}

	public static Set<String> getTableColumnNames(String fileName, String tableName) throws IOException {
		
		TreeSet<String> columnNames = new TreeSet<String>();
		
		File mdbFile = new File(fileName);
		Database db = Database.open(mdbFile);
		try{

			Table table = db.getTable(tableName);
			
			for (Column column : table.getColumns()) {
				columnNames.add(column.getName());
			}
		} finally{
			db.close();
		}
		
		return columnNames;
	}

	public static void createSyncTableIfAbsent(String mdbFileName, String syncTableName) {
		if(!existTable(mdbFileName, syncTableName)){
			Connection con = null;
			Statement s = null;
			 try {
			        Class.forName(JdbcOdbcDriver.class.getName());
	
			        String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName + ";DriverID=22;READONLY=false}";
			        
			        con = DriverManager.getConnection(dbURL, "","");      
			        
			        s = con.createStatement();
			        
			        s.execute("create table " + syncTableName + " ( sync_id text, entity_name text, entity_id text, entity_version text, sync_data memo )"); // create a table
			    } catch (Throwable e) {
			    	LOGGER.error(e.getMessage(), e);
			        throw new MeshException(e);
			    }finally{
			    	if(s != null){
			    		try{
			    			s.close(); // close the Statement to let the database know we're done with it
			    		}catch (SQLException sqle) {
			    			LOGGER.error(sqle.getMessage(), sqle);
						}
			    	}
			    	
			    	if(con != null){
			    		try{
				    		con.close(); // close the Connection to let the database know we're done with it
			    		}catch (SQLException sqle) {
			    			LOGGER.error(sqle.getMessage(), sqle);
						}
			    	}
			    }
		}
	}
	
	public static String getEntityName(String tableName) {
		return tableName.trim().replaceAll(" ", "_");
	}
	
	public static List<Column> getPrimaryKeys(Table table) {
		for (Index index : table.getIndexes()) {
			if(index.isPrimaryKey()){				
				List<ColumnDescriptor> descs = index.getColumns();
				List<Column> cols = new ArrayList<Column>(descs.size());
				for(ColumnDescriptor desc : descs) {
					cols.add(desc.getColumn());
				}
				return cols;
			}
		}
		return null;
	}

	public static void addColumn(String mdbFileName, String tableName, String propertyName) {
		Connection conn = null;
		try {
			Class.forName(JdbcOdbcDriver.class.getName());
			String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName + ";DriverID=22;READONLY=false}";
			conn = DriverManager.getConnection(dbURL, "","");
			
			Statement stmt = conn.createStatement();
			stmt.execute(String.format("ALTER TABLE %s ADD COLUMN %s VARCHAR(255)", tableName, propertyName));
		} catch (Exception e) {
			throw new MeshException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
}
