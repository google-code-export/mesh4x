package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.MeshException;

public class InterRepositoryRDFSyncTest {
	
	
	@Test
	public void ShouldSyncMsExcelToMsExcelByRDF(){
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		String contentFilePathAsString = createMsExcelFileForTest("source.xls");
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(contentFilePathAsString, "user", "id",true);
		
		SplitAdapter splitAdapterSource = ((SplitAdapter)sourceAsExcel);
		ISchema sourceSchema = ((MsExcelContentAdapter)splitAdapterSource.getContentAdapter()).getSchema();
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "target.xls", "user", "id",(IRDFSchema)sourceSchema);
		
		SyncEngine engine = new SyncEngine(sourceAsExcel,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());	
	}
	
	@Test
	public void ShouldSyncMySQLToExcelByRDF(){
		String user = "root";
		String password = "test1234";
		String tableName = "user";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
		
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsMySql;
		ISchema sourceSchema = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping().getSchema();
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "s.xls", "user", "id", (IRDFSchema)sourceSchema);
		
		SyncEngine engine = new SyncEngine(splitAdapterSource,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());	
	}
	
	@Test
	public void ShouldSyncMySQLToExistingGoogleSpreadsheetByRDF(){
		String user = "root";
		String password = "admin";
		String tableName = "user";
		String dbname = "mesh4xdb";
		boolean addSampleData = true;
		
		//createMysqlTableForTest(dbname, user, password, tableName, addSampleData);
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password, "localhost" ,3306, dbname,tableName);
		
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsMySql;
		IHibernateToXMLMapping mapping = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"spreadsheettest2",
				"gspreadsheet.test@gmail.com",
				"java123456",
				mapping.getIDNode(),
				mapping.getEntityNode(),
				mapping.getEntityNode()
				);
		ISyncAdapter targetAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) mapping.getSchema());
		
		targetAsGoogleSpreadsheet.add(makeNewItem());
		
		SyncEngine engine = new SyncEngine(splitAdapterSource, targetAsGoogleSpreadsheet);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(splitAdapterSource.getAll().size(), targetAsGoogleSpreadsheet.getAll().size());	
	}

	@Test
	public void ShouldSyncMySQLToNonExistingGoogleSpreadsheetByRDF(){
		String user = "root";
		String password = "admin";
		String tableName = "user";
		String dbname = "mesh4xdb";
		boolean addSampleData = true;
		
		//createMysqlTableForTest(dbname, user, password, tableName, addSampleData);
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password, "localhost" ,3306, dbname,tableName);
		
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsMySql;
		IHibernateToXMLMapping mapping = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"new spreadsheet",
				"gspreadsheet.test@gmail.com",
				"java123456",
				mapping.getIDNode(),
				mapping.getEntityNode(),
				mapping.getEntityNode()
				);
		ISyncAdapter targetAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) mapping.getSchema());
		
		targetAsGoogleSpreadsheet.add(makeNewItem());
		
		SyncEngine engine = new SyncEngine(splitAdapterSource, targetAsGoogleSpreadsheet);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		Assert.assertEquals(splitAdapterSource.getAll().size(), targetAsGoogleSpreadsheet.getAll().size());	
	}

	
	@Test
	public void ShouldSyncMsAccessToExcelByRDF() throws IOException{
		
		String rdfBaseURl = "http://localhost:8080/mesh4x/feeds" +"/ektoo"+"#";
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		ISyncAdapter sourceAsAccess = builder.createMsAccessAdapter(TestHelper.baseDirectoryForTest() +"ektoo.mdb" , "ektoo");
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsAccess;
		IRDFSchema sourceSchema = MsAccessRDFSchemaGenerator.extractRDFSchema(TestHelper.baseDirectoryForTest() +"ektoo.mdb", "ektoo", "ektoo", rdfBaseURl);
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", "ektoo", "id", sourceSchema);
		
		SyncEngine engine = new SyncEngine(splitAdapterSource,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		
	}
	
	private Item makeNewItem(){
		
		String id = IdGenerator.INSTANCE.newID();
		
		String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:user=\"http://localhost:8080/mesh4x/feeds/user#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"> " +
		  "<user:user rdf:about=\"uri:urn:r213\">"+
		    "<user:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">r213</user:id>"+
		    "<user:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">iklas</user:name>"+
		    "<user:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">hummm</user:pass>"+
		  "</user:user>"+
		"</rdf:RDF>";
		
		Element payload = null;
		try {
			payload = DocumentHelper.parseText(xml).getRootElement();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "sharif", new Date(), false);
		return new Item(content, sync);
	}
	
	
	private void createMysqlTableForTest(String dbname, String username, String password, String dataTablename, boolean addSampleData){
		//String dataTablename="user"; boolean addSampleData=true;
		//String username ="root";
		//String password ="admin";
		
		//String dbname="mesh4xdb";
		String url = "jdbc:mysql://localhost:3306/mysql";
		String drivername = "com.mysql.jdbc.Driver";
		
		String syncTableName = dataTablename+"_sync_info"; 
		
		String dropDatabase = "DROP DATABASE IF EXISTS "+ dbname+"; ";
		String createDatabase =	"CREATE DATABASE "+dbname+"; ";
		String allowGrant = "GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP ON "+dbname+".* TO "+username+"@localhost IDENTIFIED BY '"+password+"';";
		
		String dropDataTable = "DROP TABLE IF EXISTS "+dbname+"."+dataTablename+"; ";
		String createDataTable=  "CREATE TABLE  "+dbname+"."+dataTablename+" ( " +
				"id varchar(50) NOT NULL, " +
				"name varchar(50) " + "default NULL," +
				"pass varchar(50) default NULL, PRIMARY KEY  USING BTREE (id) " +
				") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
	 
		String dropSyncTable = "DROP TABLE IF EXISTS "+dbname+"."+syncTableName+"; ";
		
		String createSyncTable = "CREATE TABLE  "+dbname+"."+syncTableName+" ( " +
				"  sync_id varchar(50) NOT NULL, " +
				"  entity_name varchar(50) default NULL, " +
				"  entity_id varchar(255) default NULL, " +
				"  entity_version varchar(50) default NULL, " +
				"  sync_data text, " +
				"  PRIMARY KEY  (sync_id) " +
				") ENGINE=InnoDB DEFAULT CHARSET=latin1;"; 
		
		String sampleData = "INSERT INTO "+dbname+"."+dataTablename+" VALUES ('r352','sharif','mesh4x');";
		
		Connection con;
		Statement stmt;
		
		try {
			Class.forName(drivername);
			con = DriverManager.getConnection(url,username, password);
			stmt = con.createStatement();	 
			
			stmt.executeUpdate(dropDatabase);
			stmt.executeUpdate(createDatabase);
			stmt.executeUpdate(allowGrant);
			
			stmt.executeUpdate(dropDataTable);
			stmt.executeUpdate(createDataTable);
			
			stmt.executeUpdate(dropSyncTable);
			stmt.executeUpdate(createSyncTable);
			
			if(addSampleData)
				stmt.executeUpdate(sampleData);

			stmt.close();
			con.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		 
	}

	private String createMsExcelFileForTest(String filename){
		
		String sheetName = "user";
		String idColumn = "id";

		File file;
		try {
			file = TestHelper.makeFileAndDeleteIfExists(filename);
		} catch (IOException e) {
			throw new MeshException(e);
		}
		
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(sheetName);
		sheet.createRow(0);
		Row row = sheet.getRow(0);
		Cell cell;

		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,idColumn));
		

		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"name"));
		
		

		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"age"));

		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"city"));

		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"country"));

		//add sample data
		row = sheet.createRow(1);	

		cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"1"));

		cell = row.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"raju"));

		cell = row.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"29"));

		cell = row.createCell(3, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"dhaka"));

		cell = row.createCell(4, Cell.CELL_TYPE_STRING);
		cell.setCellValue(getRichTextString(workbook,"bangladesh"));

		MsExcelUtils.flush(workbook, file.getAbsolutePath());

		return file.getAbsolutePath();
	}
	
	
	
	public static RichTextString getRichTextString(Workbook workbook,String value){
		return workbook.getCreationHelper().createRichTextString(value);
	}
}
