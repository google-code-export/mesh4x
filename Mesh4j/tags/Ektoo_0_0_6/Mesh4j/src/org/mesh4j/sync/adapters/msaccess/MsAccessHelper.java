package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import org.mesh4j.sync.validations.MeshException;

import sun.jdbc.odbc.JdbcOdbcDriver;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MsAccessHelper {

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
			 try {
			        Class.forName(JdbcOdbcDriver.class.getName());
	
			        String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName + ";DriverID=22;READONLY=false}";
			        Connection con = DriverManager.getConnection(dbURL, "",""); 
			        Statement s = con.createStatement();
			        s.execute("create table " + syncTableName + " ( sync_id text, entity_name text, entity_id text, entity_version text, sync_data memo )"); // create a table
			        s.close(); // close the Statement to let the database know we're done with it
			        con.close(); // close the Connection to let the database know we're done with it
			    } catch (Exception e) {
			        throw new MeshException(e);
			    }
		}
	}
	
}
