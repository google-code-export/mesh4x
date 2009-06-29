package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
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
		
	//TODO (raju & sharif this manipulation has some bug.it creates two content instead of one)
	@Test
	public void ShouldSyncGoogleSpreadSheetToExcelByRDFAndMustCreateTargetSchema() throws Exception{
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"testspreadsheet",
				"gspreadsheet.test@gmail.com",
				"java123456",
				"id",
				"user_source",
				"user"
				);
		
//		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String rdfUrl = new PropertiesProvider().getMeshSyncServerURL();
		IGoogleSpreadSheet gss = new GoogleSpreadsheet(spreadSheetInfo.getGoogleSpreadSheetName(), spreadSheetInfo.getUserName(), spreadSheetInfo.getPassWord());
		
		ArrayList<String> pks = new ArrayList<String>();
		pks.add(spreadSheetInfo.getIdColumnName());
		IRDFSchema rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(
			gss, 
			spreadSheetInfo.getSheetName(), 
			pks,
			null,
			rdfUrl);
		
		File contentFile = new File(TestHelper.baseDirectoryForTest() + "contentFile.xls");
		ISyncAdapter sourceAsGoogleSpreadSheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(contentFile.getAbsolutePath(), rdfSchema);
		
		SyncEngine syncEngine = new SyncEngine(sourceAsGoogleSpreadSheet, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);
	}
	
	@Test
	public void ShouldSyncMsExcelToMsExcelByRDFAndMustCreateTargetSchema(){

//		PropertiesProvider propertiesProvider = new PropertiesProvider();
//		String rdfUrl = propertiesProvider.getMeshSyncServerURL();
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		String contentFilePathAsString = createMsExcelFileForTest("source.xls");
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(contentFilePathAsString, "user", new String[]{"id"}, true);

//		MsExcel msExcel = new MsExcel(contentFilePathAsString);
//		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(msExcel, "user", rdfUrl);
		
		SplitAdapter splitAdapterSource = ((SplitAdapter)sourceAsExcel);
		ISchema sourceSchema = ((MsExcelContentAdapter)splitAdapterSource.getContentAdapter()).getSchema();
		IRDFSchema rdfSchema = (IRDFSchema)sourceSchema;
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "target.xls", rdfSchema);
		
		SyncEngine syncEngine = new SyncEngine(sourceAsExcel, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);

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
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "s.xls", (IRDFSchema)sourceSchema);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);

	}
	
	@Test
	public void ShouldSyncMySQLToExistingGoogleSpreadsheetByRDF(){
		String user = "root";
		String password = "test1234";
		String tableName = "user";
		String dbname = "mesh4xdb";

		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		SplitAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password, "localhost" ,3306, dbname,tableName);
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)sourceAsMySql.getContentAdapter()).getMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"spreadsheettest2",
				"gspreadsheet.test@gmail.com",
				"java123456",
				mapping.getSchema().getIdentifiablePropertyNames().get(0),
				mapping.getType(),
				mapping.getType()
				);
		
		SplitAdapter targetAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) mapping.getSchema());
			
		targetAsGoogleSpreadsheet.add(makeNewItem(((GoogleSpreadSheetContentAdapter)targetAsGoogleSpreadsheet.getContentAdapter()).getMapper()));
		
		SyncEngine syncEngine = new SyncEngine(sourceAsMySql, targetAsGoogleSpreadsheet);
		TestHelper.syncAndAssert(syncEngine);
	}

	@Test
	public void ShouldSyncMySQLToNonExistingGoogleSpreadsheetByRDF(){
		String user = "root";
		String password = "admin";
		String tableName = "user";
		String dbname = "mesh4xdb";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		SplitAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password, "localhost" ,3306, dbname,tableName);
		
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)sourceAsMySql.getContentAdapter()).getMapping();
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				"new spreadsheet",
				"gspreadsheet.test@gmail.com",
				"java123456",
				mapping.getSchema().getIdentifiablePropertyNames().get(0),
				mapping.getType(),
				mapping.getType()
				);
		
		SplitAdapter targetAsGoogleSpreadsheet = builder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, (IRDFSchema) mapping.getSchema());
		
		targetAsGoogleSpreadsheet.add(makeNewItem(((GoogleSpreadSheetContentAdapter)targetAsGoogleSpreadsheet.getContentAdapter()).getMapper()));
		
		SyncEngine syncEngine = new SyncEngine(sourceAsMySql, targetAsGoogleSpreadsheet);
		TestHelper.syncAndAssert(syncEngine);
	}

	
	@Test
	public void ShouldSyncMsAccessToExcelByRDF() throws IOException{
		
		String rdfBaseURl = "http://localhost:8080/mesh4x/feeds" +"/ektoo"+"#";
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		ISyncAdapter sourceAsAccess = builder.createMsAccessAdapter(TestHelper.baseDirectoryForTest() +"ektoo.mdb" , "ektoo");
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsAccess;
		IRDFSchema sourceSchema = MsAccessRDFSchemaGenerator.extractRDFSchema(TestHelper.baseDirectoryForTest() +"ektoo.mdb", "ektoo", "ektoo", rdfBaseURl);
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", sourceSchema);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterSource, targetAsExcel);
		TestHelper.syncAndAssert(syncEngine);

		
	}
	
	private Item makeNewItem(IIdentifiableMapping mapping){
		try{
			String id = IdGenerator.INSTANCE.newID();
			
			String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:user=\"http://localhost:8080/mesh4x/feeds/user#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"> " +
			  "<user:user rdf:about=\"uri:urn:r213\">"+
			    "<user:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">r213</user:id>"+
			    "<user:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">iklas</user:name>"+
			    "<user:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">hummm</user:pass>"+
			  "</user:user>"+
			"</rdf:RDF>";
			
			Element payload = DocumentHelper.parseText(xml).getRootElement();
			
			IContent content = new IdentifiableContent(payload, mapping, id);
			Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "sharif", new Date(), false);
			return new Item(content, sync);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	
	private void createMysqlTableForTest(String dbname, String username, String password, String dataTablename, boolean addSampleData){
		//String dataTablename="user"; boolean addSampleData=true;
		//String username ="root";
		//String password ="admin";
		
		//String dbname="mesh4xdb";
		String url = "jdbc:mysql://localhost:3306/mysql";
		String drivername = "com.mysql.jdbc.Driver";
		
		String syncTableName = dataTablename+"_sync"; 
		
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
