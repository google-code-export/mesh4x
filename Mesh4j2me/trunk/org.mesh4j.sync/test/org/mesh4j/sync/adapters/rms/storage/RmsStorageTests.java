package org.mesh4j.sync.adapters.rms.storage;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.samples.PayloadObjectParser;

public class RmsStorageTests extends TestCase{

	private final static String XML = "<HMISFormA xmlns=\"http://gatherdata.org/en_UG/hmis033a\"></HMISFormA>";
	
	public RmsStorageTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public RmsStorageTests(String name) {
		super(name);
	}
	
	public RmsStorageTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new RmsStorageTests("shouldAddRecord", new TestMethod(){public void run(TestCase tc){((RmsStorageTests)tc).shouldAddRecord();}})));
		suite.addTest((new RmsStorageTests("shouldGetRecord", new TestMethod(){public void run(TestCase tc){((RmsStorageTests)tc).shouldGetRecord();}})));
		suite.addTest((new RmsStorageTests("shouldDeleteRecord", new TestMethod(){public void run(TestCase tc){((RmsStorageTests)tc).shouldDeleteRecord();}})));
		suite.addTest((new RmsStorageTests("shouldUpdateRecord", new TestMethod(){public void run(TestCase tc){((RmsStorageTests)tc).shouldUpdateRecord();}})));
		suite.addTest((new RmsStorageTests("shouldGelAll", new TestMethod(){public void run(TestCase tc){((RmsStorageTests)tc).shouldGetAll();}})));
		return suite;
	}

	protected void shouldAddRecord() {
		
		String xml = XML;
		String syncId = IdGenerator.INSTANCE.newID();
		IContent content = new RmsRecordContent(xml, syncId, -1, "MOCK1_TEST");
		
		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "MOCK1_TEST");
		RmsStorageContentWithSyncAdapter adapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		adapter.save(content);
	}
	
	protected void shouldGetRecord() {
		
		String xml = XML;
		String syncId = IdGenerator.INSTANCE.newID();
		IContent content = new RmsRecordContent(xml, syncId, -1, "MOCK1_TEST");
		
		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "MOCK1_TEST");
		RmsStorageContentWithSyncAdapter adapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		adapter.save(content);

		IContent content1 = adapter.get(syncId);
		this.assertEquals(content, content1);

	}
	
	protected void shouldDeleteRecord() {
		
		String xml = XML;
		String syncId = IdGenerator.INSTANCE.newID();
		IContent content = new RmsRecordContent(xml, syncId, -1, "MOCK1_TEST");
		
		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "MOCK1_TEST");
		RmsStorageContentWithSyncAdapter adapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		adapter.save(content);
		
		IContent content1 = adapter.get(syncId);
		adapter.delete(content1);

	}
	
	protected void shouldUpdateRecord() {
		
		String xml = XML;
		String syncId = IdGenerator.INSTANCE.newID();
		IContent content = new RmsRecordContent(xml, syncId, -1, "MOCK1_TEST");
		
		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "MOCK1_TEST");
		RmsStorageContentWithSyncAdapter adapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		adapter.save(content);
		
		RmsRecordContent content1 = (RmsRecordContent)adapter.get(syncId);
		content1.setPayload("<foo>bar</foo>");
		adapter.save(content1);

		IContent content2 = adapter.get(syncId);
		this.assertEquals(content1, content2);
	}
	
	protected void shouldGetAll() {
		
		String xml = XML;
		String syncId = IdGenerator.INSTANCE.newID();
		IContent content = new RmsRecordContent(xml, syncId, -1, "MOCK1_TEST");
		
		RmsStorage contentStorage = new RmsStorage(new PayloadObjectParser(), "MOCK1_TEST");
		RmsStorageContentWithSyncAdapter adapter = new RmsStorageContentWithSyncAdapter(contentStorage);
		adapter.save(content);

		this.assertTrue(adapter.getAll(null).size() >= 1);

	}
}