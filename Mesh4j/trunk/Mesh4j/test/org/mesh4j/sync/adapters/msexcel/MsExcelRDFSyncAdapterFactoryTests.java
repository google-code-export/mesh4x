package org.mesh4j.sync.adapters.msexcel;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISupportReadSchema;
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
		new MsExcelRDFSyncAdapterFactory(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfRDFUrlIsEmpty(){
		new MsExcelRDFSyncAdapterFactory("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFileNameIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		String fileName = null;
		factory.createSyncAdapter(fileName, "sheetName", "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfMsExcelIsNull(){
		IMsExcel excel = null;
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter(excel, "sheetName", "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfFileNameIsEmpty(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("", "sheetName", "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfSheetNameIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", null, "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfSheetNameIsEmpty(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "", "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdColumnIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", null, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdColumnIsEmpty(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", "", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdentityProviderIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", "idColumnName", null);
	}
	
	@Test
	public void shouldCreateAdapter(){
		String fileName = getFileNameToTest();
		
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		ISyncAdapter adapter = factory.createSyncAdapter(fileName, "Oswego", "Code", NullIdentityProvider.INSTANCE);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);
		
	}

	// FROM RDF
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfFileNameIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		String fileName = null;
		factory.createSyncAdapter(fileName, "sheetName", "idColumnName", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfFileNameIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("", "sheetName", "idColumnName", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfSheetNameIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", null, "idColumnName", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfSheetNameIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "", "idColumnName", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdColumnIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", null, NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdColumnIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", "", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdentityProviderIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", "idColumnName", null, rdfSchema);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfRDFSchemaIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xls", "sheetName", "idColumnName", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test
	public void shouldCreateAdapterFromRDFFileExistsEqualRDFSchema(){
		String fileName = getFileNameToTest();
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(new MsExcel(fileName), "Oswego", "http://localhost:8080/mesh4x/myExample");

		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		ISyncAdapter adapter = factory.createSyncAdapter(fileName, "Oswego", "Code", NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);		
	}
	
	@Test(expected=MeshException.class)
	public void shouldCreateAdapterFromRDFFileFailsWhenFileExistsNotEqualRDFSchema(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		String fileName = getFileNameToTest();
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		ISyncAdapter adapter = factory.createSyncAdapter(fileName, "Oswego", "Code", NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);		
	}

	
	@Test
	public void shouldCreateAdapterFromRDFCreateFile(){
		String ontologyBaseUri = "http://localhost:8080/mesh4x/myExample";
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(ontologyBaseUri);
		
		String fileName = getFileNameToTest();
		SplitAdapter adapterSource = factory.createSyncAdapter(fileName, "Oswego", "Code", NullIdentityProvider.INSTANCE);
		IRDFSchema rdfSchema = (IRDFSchema)((ISupportReadSchema)adapterSource.getContentAdapter()).getSchema();
		
		String newFileName = TestHelper.fileName("MsExcel_RDF_"+IdGenerator.INSTANCE.newID()+".xls");
		ISyncAdapter adapterTarget = factory.createSyncAdapter(newFileName, "Oswego", "Code", NullIdentityProvider.INSTANCE, rdfSchema);
		
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
