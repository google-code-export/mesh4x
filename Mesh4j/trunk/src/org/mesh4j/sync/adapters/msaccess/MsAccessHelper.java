package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.IOException;
import java.util.Set;

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
	
}
