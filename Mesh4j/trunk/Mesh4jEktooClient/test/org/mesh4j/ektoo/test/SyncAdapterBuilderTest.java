package org.mesh4j.ektoo.test;

import java.util.Date;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class SyncAdapterBuilderTest {

	
	
	@Test
	public void shouldCreateGoogleSpreadSheetAdapter(){
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo();
		spreadSheetInfo.setUserName("mesh4x@gmail.com");
		spreadSheetInfo.setPassWord("g@l@xy24");
		spreadSheetInfo.setGOOGLE_SPREADSHEET_FIELD("pLUqch-enpf1-GcqnD6qjSA");
		
		spreadSheetInfo.setIdColumnName("id");
		spreadSheetInfo.setIdColumnPosition(1);
		spreadSheetInfo.setLastUpdateColumnPosition(6);
		spreadSheetInfo.setSheetName("user_source");
		
		
		spreadSheetInfo.setIdentityProvider(NullIdentityProvider.INSTANCE);
		spreadSheetInfo.setIdGenerator(IdGenerator.INSTANCE);
		
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter syncAdapterA = adapterBuilder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
		
//		spreadSheetInfo.setSheetName("user_target");
//		ISyncAdapter syncAdapterB = adapterBuilder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
//		
//		SyncEngine engine = new SyncEngine(syncAdapterA,syncAdapterB);
//		engine.synchronize();
		
		Assert.assertEquals(0,syncAdapterA.getAll().size());
	}
	
	@Test
	public void shouldCreateMsAccessAdapter() throws Exception{
	    ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
	    ISyncAdapter syncAdapter = adapterBuilder.createMsAccessAdapter(TestHelper.baseDirectoryForTest(), "http://mesh4x/feeds/grammen", "ektoo", "C:\\jtest\\ektoo.mdb", "ektoo");
	    
	   Assert.assertEquals(0, syncAdapter.getAll().size());

	}
	
	@Test
	public void shouldCreateExcelAdapter() throws DocumentException{
	
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = contentFile;
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile, syncFile, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		Assert.assertEquals(0, excelAdapter.getAll().size());
		
		excelAdapter.add(getItem());
		
		
		Assert.assertEquals(1, excelAdapter.getAll().size());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfSheetNameEmptyOrNull(){
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("", "id", contentFile, syncFile, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdEmptyOrNull(){
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "", contentFile, syncFile, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfContenFileIsNull(){
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", "", syncFile, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfSyncFileIsNull(){
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile, "", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdentityIsNull(){
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile, syncFile, null, IdGenerator.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGenarateExceptionIfIdGeneratorIsNull(){
		String contentFile = "C:\\jtest\\contentFile.xls";
		String syncFile = "C:\\jtest\\syncFile.xls";
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter excelAdapter = adapterBuilder.createMsExcelAdapter("user", "id", contentFile, syncFile, NullIdentityProvider.INSTANCE, null);
	}
	
	private Item getItem() throws DocumentException {
		
		String id = IdGenerator.INSTANCE.newID();
		String rawDataAsXML = "<user>" +
								"<id>"+id+"</id>" +
								"<name>Marcelo</name>" +
								"<age>25</age>" +
								"<city>Buens aires</city>" +
								"<country>Argentina</country>" +
								"</user>";
		
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new EntityContent(payload, "user", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	private Item makeRDFItem(IRDFSchema schema){
		
		String id = IdGenerator.INSTANCE.newID();
		
		String rawDataAsXML = "<ektoo>" +
		"<ID>"+id+"</ID>" +
		"<Name>Raju</Name>" +
		"<Age>25</Age>" +
		"</ektoo>";

		Element payload = XMLHelper.parseElement(rawDataAsXML);
		payload = schema.getInstanceFromPlainXML(id, payload, ISchema.EMPTY_FORMATS);
		
		IContent content = new EntityContent(payload, "ektoo", id);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "Raju", new Date(), false);
		return new Item(content, sync);
	}
	
}
