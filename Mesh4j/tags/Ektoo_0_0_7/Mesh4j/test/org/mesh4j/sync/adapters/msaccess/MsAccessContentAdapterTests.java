package org.mesh4j.sync.adapters.msaccess;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.jackcess.msaccess.IMsAccessToXMLMapping;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccess;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessContentAdapter;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessToRDFMapping;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Table;


public class MsAccessContentAdapterTests {
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenMsAccessIsNull(){
		MsAccess msaccess = makeEmptyMDBFile("myTable");
		new MsAccessContentAdapter(null, makeMapping(msaccess, "myTable"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenFileNameIsNull(){
		MsAccess msaccess = makeEmptyMDBFile("myTable");
		new MsAccessContentAdapter(new MsAccess(null), makeMapping(msaccess, "myTable"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenFileNameIsEmpty(){
		MsAccess msaccess = makeEmptyMDBFile("myTable");
		new MsAccessContentAdapter(new MsAccess(""), makeMapping(msaccess, "myTable"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenMappingIsNull(){
		new MsAccessContentAdapter(new MsAccess("myfile.mdb"), null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenTableNameIsNull(){
		MsAccess msaccess = makeEmptyMDBFile("myTable");
		new MsAccessContentAdapter(msaccess, makeMapping(msaccess, null));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsWhenTableNameIsEmpty(){
		MsAccess msaccess = makeEmptyMDBFile("myTable");
		new MsAccessContentAdapter(msaccess, makeMapping(msaccess, ""));
	}
	
	@Test
	public void shouldCreateAdapterFailsWhenTableDoesNotExist(){
		
		IMsAccessToXMLMapping mapping = new IMsAccessToXMLMapping(){
			@Override public Date getLastUpdate(Map<String, Object> row) {return null;}
			@Override public ISchema getSchema() {return null;}
			@Override public Element translateAsElement(Map<String, Object> row) {return null;}
			@Override public Map<String, Object> translateAsRow(Element payload) {return null;}
			@Override public String getId(Map<String, Object> row) {return null;}
			@Override public String getId(Element payload) {return null;}
			@Override public String getType() {return "xxx";}
			@Override public Element getTypeElement(Element payload) {return null;}
			@Override public boolean findRow(Cursor cursor, String meshid) {return false;}
		};
		
		MsAccess msaccess = makeEmptyMDBFile("myTable");
		boolean isOk = false;
		try{			
			new MsAccessContentAdapter(msaccess, mapping);			
		}catch(IllegalArgumentException e){
			isOk = true;
		}
		FileUtils.delete(new File(msaccess.getFileName()));
		Assert.assertTrue(isOk);
	}
	
	@Test
	public void shouldGetTypeReturnsTableName(){
		MsAccess msaccess = makeEmptyMDBFile("mytable");
		MsAccessToRDFMapping mapper = makeMapping(msaccess, "mytable");
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, mapper);
		Assert.assertEquals("mytable", adapter.getType());
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}
	
	@Test
	public void shouldGetAllReturnsEmpty(){
		MsAccess msaccess = makeEmptyMDBFile("mytable");
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, makeMapping(msaccess, "mytable"));
		Assert.assertEquals(0, adapter.getAll(new Date()).size());

		FileUtils.delete(new File(msaccess.getFileName()));
	}

	@Test
	public void shouldGetReturnsNullBecauseItemDoesNotExistsOnTable(){
		MsAccess msaccess = makeEmptyMDBFile("mytable");
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, makeMapping(msaccess, "mytable"));
		Assert.assertNull(adapter.get("1"));
		FileUtils.delete(new File(msaccess.getFileName()));
	}	
	
	@Test
	public void shouldGetReturnsItem(){
		
		Date date = new Date();
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, makeMapping(msaccess, tableName));
		adapter.beginSync();
		
		List<IContent> items = adapter.getAll(null);
		Assert.assertEquals(3, items.size());

		
		assertContent(id1, "jmt1", 1, date, adapter);		
		assertContent(id2, "jmt2", 2, date, adapter);		
		assertContent(id3, "jmt3", 3, date, adapter);		
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}

	@Test
	public void shouldGetAllReturnsItems(){
		Date date = new Date();
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, makeMapping(msaccess, tableName));
		adapter.beginSync();
		
		List<IContent> items = adapter.getAll(null);
		Assert.assertEquals(3, items.size());

		
		assertContent(id1, "jmt1", 1, date, adapter, items.get(0));		
		assertContent(id2, "jmt2", 2, date, adapter, items.get(1));		
		assertContent(id3, "jmt3", 3, date, adapter, items.get(2));		
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}

	@Test
	public void shouldGetAllReturnsItemsFilteringBySinceDate(){
		Date since = TestHelper.makeDate(2008, 11, 10, 1, 1, 1, 0);
		Date date = TestHelper.makeDate(2008, 10, 10, 1, 1, 1, 0);
		Date lastUpdate1 = TestHelper.makeDate(2008, 11, 11, 1, 1, 1, 0);
		Date lastUpdate2 = TestHelper.makeDate(2008, 11, 15, 1, 1, 1, 0);
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		String id4 = IdGenerator.INSTANCE.newID();
		String id5 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		addRow(msaccess, tableName, id4, "jmt4", 4, lastUpdate1);
		addRow(msaccess, tableName, id5, "jmt5", 5, lastUpdate2);
		
		MsAccessToRDFMapping mapping = makeMapping(msaccess, tableName);
		((RDFSchema)mapping.getSchema()).setVersionPropertyName("birthDate");
		
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, mapping);
		adapter.beginSync();
		
		List<IContent> items = adapter.getAll(since);
		Assert.assertEquals(2, items.size());

		
		assertContent(id4, "jmt4", 4, lastUpdate1, adapter, items.get(0));		
		assertContent(id5, "jmt5", 5, lastUpdate2, adapter, items.get(1));		
	
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}
	
	@Test 
	public void shouldDeleteNoProduceChangesWhenItemDoesNotExist(){

		Date date = new Date();
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		
		MsAccessToRDFMapping mapping = makeMapping(msaccess, tableName);
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, mapping);
		adapter.beginSync();
		
		String idToDelete = IdGenerator.INSTANCE.newID(); 
		Map<String, Object> rowMap = new HashMap<String, Object>();
		rowMap.put("id", idToDelete);
		rowMap.put("name", "jmtDeleted");
		rowMap.put("age", 20);
		rowMap.put("birthDate", date);
		
		Element payload = mapping.translateAsElement(rowMap);
		IdentifiableContent content = new IdentifiableContent(payload, mapping, idToDelete);
		adapter.delete(content);

		List<IContent> items = adapter.getAll(null);
		Assert.assertEquals(3, items.size());

		
		assertContent(id1, "jmt1", 1, date, adapter, items.get(0));		
		assertContent(id2, "jmt2", 2, date, adapter, items.get(1));
		assertContent(id3, "jmt3", 3, date, adapter, items.get(2));
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
		
	}
	
	@Test 
	public void shouldDelete(){
		Date date = new Date();
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		
		MsAccessToRDFMapping mapping = makeMapping(msaccess, tableName);
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, mapping);
		adapter.beginSync();
		
		Map<String, Object> rowMap = new HashMap<String, Object>();
		rowMap.put("id", id1);
		rowMap.put("name", "jmt1");
		rowMap.put("age", 1);
		rowMap.put("birthDate", date);
		
		Element payload = mapping.translateAsElement(rowMap);
		IdentifiableContent content = new IdentifiableContent(payload, mapping, id1);
		adapter.delete(content);

		List<IContent> items = adapter.getAll(null);
		Assert.assertEquals(2, items.size());
	
		assertContent(id2, "jmt2", 2, date, adapter, items.get(0));
		assertContent(id3, "jmt3", 3, date, adapter, items.get(1));
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}

	@Test
	public void shouldSave(){
		Date date = new Date();
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		
		MsAccessToRDFMapping mapping = makeMapping(msaccess, tableName);
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, mapping);
		adapter.beginSync();
		
		String id4 = IdGenerator.INSTANCE.newID();
		Map<String, Object> rowMap = new HashMap<String, Object>();
		rowMap.put("id", id4);
		rowMap.put("name", "jmt4");
		rowMap.put("age", 4);
		rowMap.put("birthDate", date);
		
		Element payload = mapping.translateAsElement(rowMap);
		IdentifiableContent content = new IdentifiableContent(payload, mapping, id4);
		adapter.save(content);

		List<IContent> items = adapter.getAll(null);
		Assert.assertEquals(4, items.size());
	
		assertContent(id1, "jmt1", 1, date, adapter, items.get(0));
		assertContent(id2, "jmt2", 2, date, adapter, items.get(1));
		assertContent(id3, "jmt3", 3, date, adapter, items.get(2));
		assertContent(id4, "jmt4", 4, date, adapter, items.get(3));
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}

	@Test
	public void shouldUpdate(){
		Date date = new Date();
		
		String tableName = "mytable";
		MsAccess msaccess = makeEmptyMDBFile(tableName);
		String id1 = IdGenerator.INSTANCE.newID();
		String id2 = IdGenerator.INSTANCE.newID();
		String id3 = IdGenerator.INSTANCE.newID();
		addRow(msaccess, tableName, id1, "jmt1", 1, date);
		addRow(msaccess, tableName, id2, "jmt2", 2, date);
		addRow(msaccess, tableName, id3, "jmt3", 3, date);
		
		MsAccessToRDFMapping mapping = makeMapping(msaccess, tableName);
		MsAccessContentAdapter adapter = new MsAccessContentAdapter(msaccess, mapping);
		adapter.beginSync();
		
		String id4 = IdGenerator.INSTANCE.newID();
		Map<String, Object> rowMap = new HashMap<String, Object>();
		rowMap.put("id", id4);
		rowMap.put("name", "jmt4");
		rowMap.put("age", 4);
		rowMap.put("birthDate", date);
		
		Element payload = mapping.translateAsElement(rowMap);
		IdentifiableContent content = new IdentifiableContent(payload, mapping, id4);
		adapter.save(content);

		List<IContent> items = adapter.getAll(null);
		Assert.assertEquals(4, items.size());
	
		assertContent(id1, "jmt1", 1, date, adapter, items.get(0));
		assertContent(id2, "jmt2", 2, date, adapter, items.get(1));
		assertContent(id3, "jmt3", 3, date, adapter, items.get(2));
		assertContent(id4, "jmt4", 4, date, adapter, items.get(3));
		
		Date newdate = new Date();
		Map<String, Object> rowMapToUpdate = new HashMap<String, Object>();
		rowMapToUpdate.put("id", id4);
		rowMapToUpdate.put("name", "bianca");
		rowMapToUpdate.put("age", 5);
		rowMapToUpdate.put("birthDate", newdate);
		
		Element payloadToUpdate = mapping.translateAsElement(rowMapToUpdate);
		IdentifiableContent contentToUpdate = new IdentifiableContent(payloadToUpdate, mapping, id4);
		adapter.save(contentToUpdate);

		items = adapter.getAll(null);
		Assert.assertEquals(4, items.size());
	
		assertContent(id1, "jmt1", 1, date, adapter, items.get(0));
		assertContent(id2, "jmt2", 2, date, adapter, items.get(1));
		assertContent(id3, "jmt3", 3, date, adapter, items.get(2));
		assertContent(id4, "bianca", 5, newdate, adapter, items.get(3));
		
		adapter.endSync();
		
		FileUtils.delete(new File(msaccess.getFileName()));
	}

	// PRIVATE METHODS

	private MsAccessToRDFMapping makeMapping(MsAccess msaccess, String tableName) {
		return MsAccessRDFSchemaGenerator.extractRDFSchemaAndMappings(msaccess.getFileName(), tableName, "http://localhost:8080/mesh4x/feeds");
	}
		
	private MsAccess makeEmptyMDBFile(String tableName) {
		try{
			String localFileName = this.getClass().getResource("content.mdb").getFile();
			String fileName = TestHelper.fileName("content_"+IdGenerator.INSTANCE.newID()+".mdb");
			byte[] bytes = FileUtils.read(localFileName);
			FileUtils.write(fileName, bytes);

			return new MsAccess(fileName);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	
	private void addRow(MsAccess msaccess, String tableName, String id, String name, int age, Date birthDate) {
		try{
			msaccess.open();
			Table table = msaccess.getTable(tableName);
			
			Map<String, Object> rowMap = new HashMap<String, Object>();
			rowMap.put("id", UUID.fromString(id));
			rowMap.put("name", name);
			rowMap.put("age", age);
			rowMap.put("birthDate", birthDate);
			
			table.addRow(table.asRow(rowMap));
			msaccess.close();
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	private void assertContent(String id, String name, int age, Date birthDate, MsAccessContentAdapter adapter) {
		IContent content = adapter.get(id);
		assertContent(id, name, age, birthDate, adapter, content);
	}
	
	private void assertContent(String id, String name, int age, Date birthDate, MsAccessContentAdapter adapter, IContent content) {

		Assert.assertNotNull(content);

		RDFInstance instance = ((RDFSchema)adapter.getSchema()).createNewInstanceFromRDFXML(content.getPayload().asXML());
		String idRow = (String)instance.getPropertyValue("id");
		String nameRow = (String)instance.getPropertyValue("name");
		int ageRow = (Integer)instance.getPropertyValue("age");
		Date birthDateRow = (Date)instance.getPropertyValue("birthDate");
		
		Assert.assertEquals(id, idRow);
		Assert.assertEquals(name, nameRow);
		Assert.assertEquals(age, ageRow);
		Assert.assertEquals(DateHelper.formatW3CDateTime(birthDate), DateHelper.formatW3CDateTime(birthDateRow));
	}
	
}
