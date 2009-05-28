package org.mesh4j.sync.adapters.msexcel;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

public class MsExcelRDFSyncAdapterFactoryXLSXTests {

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
		factory.createSyncAdapter("excelFileName.xlsx", null, "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfSheetNameIsEmpty(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "", "idColumnName", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdColumnIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", null, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdColumnIsEmpty(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", "", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfIdentityProviderIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", "idColumnName", null);
	}
	
	@Test
	public void shouldCreateAdapter(){
		String fileName = this.getClass().getResource("epiinfo.xlsx").getFile();
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
		factory.createSyncAdapter("excelFileName.xlsx", null, "idColumnName", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfSheetNameIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "", "idColumnName", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdColumnIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", null, NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdColumnIsEmpty(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", "", NullIdentityProvider.INSTANCE, rdfSchema);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfIdentityProviderIsNull(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", "idColumnName", null, rdfSchema);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFromRDFFailsIfRDFSchemaIsNull(){
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		factory.createSyncAdapter("excelFileName.xlsx", "sheetName", "idColumnName", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test
	public void shouldCreateAdapterFromRDFFileExistsEqualRDFSchema(){
		String fileName = this.getClass().getResource("epiinfo.xlsx").getFile();
		RDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(new MsExcel(fileName), "Oswego", "http://localhost:8080/mesh4x/myExample");

		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		ISyncAdapter adapter = factory.createSyncAdapter(fileName, "Oswego", "Code", NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);		
	}
	
	@Test(expected=MeshException.class)
	public void shouldCreateAdapterFromRDFFileFailsWhenFileExistsNotEqualRDFSchema(){
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/myExample/Oswego#", "Oswego");
		String fileName = this.getClass().getResource("epiinfo.xlsx").getFile();
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/myExample");
		ISyncAdapter adapter = factory.createSyncAdapter(fileName, "Oswego", "Code", NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapter);
		Assert.assertTrue(adapter.getAll().size() > 0);		
	}

	
	@Test
	public void shouldCreateAdapterFromRDFCreateFile(){
		String ontologyBaseUri = "http://localhost:8080/mesh4x/myExample";
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(ontologyBaseUri);
		
		RDFSchema rdfSchema = new RDFSchema("example", ontologyBaseUri+"#", "example");
		rdfSchema.addStringProperty("code", "code", "en");
		rdfSchema.addStringProperty("string", "string", "en");
		rdfSchema.addIntegerProperty("integer", "int", "en");
		rdfSchema.addBooleanProperty("boolean", "boolean", "en");
		rdfSchema.addDateTimeProperty("datetime", "datetime", "en");
		rdfSchema.addDoubleProperty("double", "double", "en");
		rdfSchema.addLongProperty("long", "long", "en");
		rdfSchema.addDecimalProperty("decimal", "decimal", "en");        
    	
        RDFInstance rdfInstance = rdfSchema.createNewInstance("uri:urn:1");
        rdfInstance.setProperty("code", "1");
        rdfInstance.setProperty("string", "abc");
        rdfInstance.setProperty("integer", Integer.MAX_VALUE);
        rdfInstance.setProperty("boolean", true);
        rdfInstance.setProperty("datetime", new Date());
        rdfInstance.setProperty("double", Double.MAX_VALUE);
        rdfInstance.setProperty("long", Long.MAX_VALUE);
        rdfInstance.setProperty("decimal", BigDecimal.TEN);
        
        String xml = rdfInstance.asXML();
        Element payload = XMLHelper.parseElement(xml);
        Item item = new Item(new EntityContent(payload, "example", "code", "1"), new Sync(IdGenerator.INSTANCE.newID(), LoggedInIdentityProvider.getUserName(), new Date(), false));
        
		String newFileNameSource = TestHelper.fileName("MsExcel_RDF_SOURCE_"+IdGenerator.INSTANCE.newID()+".xlsx");
		SplitAdapter adapterSource = factory.createSyncAdapter(newFileNameSource, "example", "code", NullIdentityProvider.INSTANCE, rdfSchema);
		adapterSource.beginSync();
		adapterSource.add(item);
		adapterSource.endSync();
		
		String newFileNameTarget = TestHelper.fileName("MsExcel_RDF_TARGET_"+IdGenerator.INSTANCE.newID()+".xlsx");
		ISyncAdapter adapterTarget = factory.createSyncAdapter(newFileNameTarget, "example", "code", NullIdentityProvider.INSTANCE, rdfSchema);
		
		Assert.assertNotNull(adapterSource);
		Assert.assertEquals(1, adapterSource.getAll().size());

		Assert.assertNotNull(adapterTarget);
		Assert.assertEquals(0, adapterTarget.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(adapterTarget, adapterSource);
		TestHelper.assertSync(syncEngine);
	}
}
