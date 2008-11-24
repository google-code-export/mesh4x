package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.healthmarketscience.jackcess.Database;

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
	
}
