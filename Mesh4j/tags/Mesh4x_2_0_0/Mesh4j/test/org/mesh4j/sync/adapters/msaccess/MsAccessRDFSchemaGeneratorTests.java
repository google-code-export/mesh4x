package org.mesh4j.sync.adapters.msaccess;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsAccessRDFSchemaGeneratorTests {


	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfFileIsNull(){
		String fileName = null;
		String tableName = "Oswego";

		String ontologyURI = "http://mesh4x";
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfFileIsEmpty(){
		String fileName = "";
		String tableName = "Oswego";

		String ontologyURI = "http://mesh4x";
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfFileDoesNotExist() throws IOException{
		String fileName = "epiinfo.mdb";
		String tableName = "Oswego";

		String ontologyURI = "http://mesh4x";
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfTableNameIsNull(){
		String fileName = getMsAccessFileNameToTest();
		String tableName = null;

		String ontologyURI = "http://mesh4x";
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfTableNameIsEmpty(){
		String fileName = getMsAccessFileNameToTest();
		String tableName = "";

		String ontologyURI = "http://mesh4x";
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfTableDoesNotExist(){
		String fileName = getMsAccessFileNameToTest();
		String tableName = "OswegoXXXX";

		String ontologyURI = "http://mesh4x";
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfOntologyURIIsNull(){
		String fileName = getMsAccessFileNameToTest();
		String tableName = "Oswego";

		String ontologyURI = null;
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldExtractRDFSchemaFailsIfOntologyURIIsEmpty(){
		String fileName = getMsAccessFileNameToTest();
		String tableName = "Oswego";

		String ontologyURI = "";
		
		MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName,  ontologyURI);
	}
	
	@Test
	public void shouldExtractRDFSchema() throws Exception{
		String fileName = getMsAccessFileNameToTest();
		String tableName = "Oswego";
		String ontologyURI = "http://mesh4x";
		
		IRDFSchema rdfSchema = MsAccessRDFSchemaGenerator.extractRDFSchema(fileName, tableName, ontologyURI);
		Assert.assertNotNull(rdfSchema);
		System.out.println(rdfSchema.asXML());

		Assert.assertEquals(24, rdfSchema.getPropertyCount());
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType("Name"));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType("Code"));
		Assert.assertEquals(IRDFSchema.XLS_DOUBLE, rdfSchema.getPropertyType("AGE"));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType("SEX"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("ILL"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("BAKEDHAM"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("SPINACH"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("MASHEDPOTA"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("CABBAGESAL"));		
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("JELLO"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("ROLLS"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("BROWNBREAD"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("MILK"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("COFFEE"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("WATER"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("CAKES"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("VANILLA"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("CHOCOLATE"));
		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, rdfSchema.getPropertyType("FRUITSALAD"));
		Assert.assertEquals(IRDFSchema.XLS_DATETIME, rdfSchema.getPropertyType("TimeSupper"));
		Assert.assertEquals(IRDFSchema.XLS_DATETIME, rdfSchema.getPropertyType("DateOnset"));
		Assert.assertEquals(IRDFSchema.XLS_INTEGER, rdfSchema.getPropertyType("RecStatus"));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType("Address"));
		Assert.assertEquals(IRDFSchema.XLS_STRING, rdfSchema.getPropertyType("County"));
	}
	
	private String getMsAccessFileNameToTest() {
		try{
			String localFileName = this.getClass().getResource("epiinfo.mdb").getFile();
			String fileName = TestHelper.fileName("msAccess"+IdGenerator.INSTANCE.newID()+".mdb");
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
