package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.io.File;

import org.mesh4j.sync.validations.Guard;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MsAccess implements IMsAccess{

	// MODEL VARIABLES
	private String fileName;
	private Database database;
	
	// BUSINESS METHODS
	public MsAccess(String fileName){
		super();
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		
		if(fileName.trim().toLowerCase().endsWith(".mdb")){
			Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		}
		
		File file = new File(fileName);
		if(!file.exists()){
			Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		}
				
		this.fileName = fileName;
	}

	@Override
	public void open()throws Exception {
		if(this.database == null){
			this.database = Database.open(new File(fileName));
		}
	}
	
	@Override
	public void close() throws Exception {
		if(this.database != null){
			this.database.close();
			this.database = null;
		}		
	}

	@Override
	public String getFileName(){
		return this.fileName;
	}

	@Override
	public Table getTable(String tableName) {
		try{
			boolean isOpen = (this.database != null);
			if(!isOpen){
				this.open();
			}
			
			Table table = this.database.getTable(tableName);
			
			if(!isOpen){
				this.close();
			}
			
			return table;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean fileExists() {
		File file = new File(fileName);
		return file.exists();
	}

}
