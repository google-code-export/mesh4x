package org.mesh4j.sync.adapters.msexcel;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelRDFSyncAdapterFactoryTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfRDFUrlIsNull(){
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), "sheetName", new String[0], null, NullIdentityProvider.INSTANCE, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfRDFUrlIsEmpty(){
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), "sheetName", new String[0], null, NullIdentityProvider.INSTANCE, "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFileNameIsNull(){
		String fileName = null;
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), "sheetName", new String[0], null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfMsExcelIsNull(){
		IMsExcel excel = null;
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(excel, "sheetName", new String[0], null,  NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFileNameIsEmpty(){

		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(""), "sheetName", new String[0], null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfSheetNameIsNull(){

		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), null, new String[]{"idColumnName"}, null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfSheetNameIsEmpty(){

		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), "", new String[]{"idColumnName"}, null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdColumnIsNull(){

		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), "sheetName", null, null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdColumnIsEmpty(){

		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), "sheetName", new String[0], null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdentityProviderIsNull(){
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), "sheetName", new String[0], null, null, "http://localhost:8080/mesh4x/myExample");
	}
	
	@Test
	public void shouldCreateAdapter(){
		String fileName = getFileNameToTest();
		
		ISyncAdapter adapter = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), "Oswego", new String[]{"Code"}, null, NullIdentityProvider.INSTANCE, "http://localhost:8080/mesh4x/myExample");
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);
		
	}

	// FROM RDF
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfFileNameIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		String fileName = null;
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfFileNameIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(""), NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdColumnIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdColumnIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		rdfSchema.setIdentifiablePropertyName("");
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdentityProviderIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), null, rdfSchema);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfRDFSchemaIsNull(){
		MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel("excelFileName.xls"), NullIdentityProvider.INSTANCE, null);
	}
	
	@Test
	public void shouldCreateAdapterFromRDFFileExistsEqualRDFSchema(){
		String fileName = getFileNameToTest();
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(new MsExcel(fileName), "Oswego", new String[]{"Code"}, null, "http://localhost:8080/mesh4x/myExample");

		ISyncAdapter adapter = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);		
	}
	
	@Test(expected=MeshException.class)
	public void shouldCreateAdapterFromRDFFileFailsWhenFileExistsNotEqualRDFSchema(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		rdfSchema.addStringProperty("Code", "code", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.setIdentifiablePropertyName("Code");
		
		String fileName = getFileNameToTest();
		ISyncAdapter adapter = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);		
	}

	@Test
	public void shouldCreateAdapterFromRDFCreateFile(){
		String ontologyBaseUri = "http://localhost:8080/mesh4x/myExample";
		
		String fileName = getFileNameToTest();
		SplitAdapter adapterSource = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(fileName), "Oswego", new String[]{"Code"}, null, NullIdentityProvider.INSTANCE, ontologyBaseUri);
		IRDFSchema rdfSchema = (IRDFSchema)((MsExcelContentAdapter)adapterSource.getContentAdapter()).getSchema();
		
		String newFileName = TestHelper.fileName("MsExcel_RDF_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(newFileName), NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapterTarget);
		Assert.assertEquals(0, adapterTarget.getAll().size());
		Assert.assertTrue(adapterSource.getAll().size() > 0);
		
		SyncEngine syncEngine = new SyncEngine(adapterTarget, adapterSource);
		List<Item> conflicts = syncEngine.synchronize();
	
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		Assert.assertEquals(adapterSource.getAll().size(), adapterTarget.getAll().size());
	}
	
	private String getFileNameToTest() {
		try{
			String originalFileName = this.getClass().getResource("epiinfo.xls").getFile();
			String fileName = TestHelper.fileName("MsExcel_"+IdGenerator.INSTANCE.newID()+".xls");
			FileUtils.copyFile(originalFileName, fileName);
			return fileName;
		}catch(Exception e){
			throw new MeshException(e);
		}
	}
}
