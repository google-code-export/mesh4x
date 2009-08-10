package org.mesh4j.sync.adapters.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.payload.schema.xform.SchemaToXFormTranslator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.IdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;


public class HttpSyncAdapterTests {

	// BUSINESS METHODS

	@Test
	public void shouldUploadMesh(){
		String path = "http://localhost:8080/mesh4x/feeds";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, "myMesh", RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
		Element schemaElement = XMLHelper.parseElement("<mySchema><id>33</id></mySchema>");
		ISchema schema = new Schema(schemaElement);
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		HttpSyncAdapter.uploadMeshDefinition(path, "myMesh/myFeed", RssSyndicationFormat.NAME, "my description", schema, mapping, "jmt");
		
		// update feed
		Element schemaElement1 = XMLHelper.parseElement("<mySchema><id>CODE</id><name>NAME</name></mySchema>");
		ISchema schema1 = new Schema(schemaElement1);
		
		Element elementMappings1 = XMLHelper.parseElement("<mappings><title>title</title><desc>desc</desc></mappings>");
		IMapping mapping1 = new Mapping(elementMappings1);

		HttpSyncAdapter.uploadMeshDefinition(path, "myMesh/myFeed", RssSyndicationFormat.NAME, "my description223", schema1, mapping1, "jmt");
	}
	
	@Test
	public void shouldUploadMeshAndGetXFormWithRDFSchemaAndXForm() throws IOException{
		String path = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataset = "test";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Oswego");
		rdfSchema.addBooleanProperty("ill", "ill", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("dateOnset", "dateOnSet", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("AgeDouble", "ageDouble", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("AgeInt", "ageInt", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("AgeLong", "ageLong", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		String xformXml = new String(FileUtils.read(new File(getClass().getResource("xformcustomized.txt").getFile())));

		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup+"/"+dataset, RssSyndicationFormat.NAME, "my description", rdfSchema, mapping, "jmt", xformXml);
		
		String xform = HttpSyncAdapter.getXForm(path,meshGroup,dataset);
		
		Element element = XMLHelper.parseElement(xformXml);
		Element elementLoaded = XMLHelper.parseElement(xform);
		Assert.assertEquals(XMLHelper.canonicalizeXML(element), XMLHelper.canonicalizeXML(elementLoaded));
	}
	
	@Test
	public void shouldUploadMeshAndGetXFormWithRDFSchemaAndWithoutXForm() throws IOException{
		String path = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataset = "test";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Oswego");
		rdfSchema.addBooleanProperty("ill", "ill", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("dateOnset", "dateOnSet", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("AgeDouble", "ageDouble", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("AgeInt", "ageInt", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("AgeLong", "ageLong", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup+"/"+dataset, RssSyndicationFormat.NAME, "my description", rdfSchema, mapping, "jmt", null);
		
		String xform = HttpSyncAdapter.getXForm(path,meshGroup,dataset);
		
		String xformXml = SchemaToXFormTranslator.translate(rdfSchema);
		Element element = XMLHelper.parseElement(xformXml);
		Element elementLoaded = XMLHelper.parseElement(xform);
		Assert.assertEquals(XMLHelper.canonicalizeXML(element), XMLHelper.canonicalizeXML(elementLoaded));
	}
	
	@Test
	public void shouldUploadMeshAndGetXFormWithoutRDFSchemaAndWithoutXForm() throws IOException{
		String path = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataset = "test";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
				
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup+"/"+dataset, RssSyndicationFormat.NAME, "my description", null, mapping, "jmt", null);
		
		String xform = HttpSyncAdapter.getXForm(path,meshGroup,dataset);
		
		Assert.assertEquals("", xform);
	}
	
	@Test
	public void shouldUploadMeshAndGetXFormWithoutRDFSchemaAndXForm() throws IOException{
		String path = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataset = "test";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		String xformXml = new String(FileUtils.read(new File(getClass().getResource("xformcustomized.txt").getFile())));

		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup+"/"+dataset, RssSyndicationFormat.NAME, "my description", null, mapping, "jmt", xformXml);
		
		String xform = HttpSyncAdapter.getXForm(path,meshGroup,dataset);
		
		Element element = XMLHelper.parseElement(xformXml);
		Element elementLoaded = XMLHelper.parseElement(xform);
		Assert.assertEquals(XMLHelper.canonicalizeXML(element), XMLHelper.canonicalizeXML(elementLoaded));
	}
	
	@Test
	public void shouldUploadMeshAndGetXFormWithSchemaAndXForm() throws IOException{
		String path = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataset = "test";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
		Schema schema = new Schema(XMLHelper.parseElement("<foo>bar</foo>"));
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		String xformXml = new String(FileUtils.read(new File(getClass().getResource("xformcustomized.txt").getFile())));

		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup+"/"+dataset, RssSyndicationFormat.NAME, "my description", schema, mapping, "jmt", xformXml);
		
		String xform = HttpSyncAdapter.getXForm(path,meshGroup,dataset);
		
		Element element = XMLHelper.parseElement(xformXml);
		Element elementLoaded = XMLHelper.parseElement(xform);
		Assert.assertEquals(XMLHelper.canonicalizeXML(element), XMLHelper.canonicalizeXML(elementLoaded));
	}
	
	@Test
	public void shouldUploadMeshAndGetXFormWithSchemaAndWithoutXForm() throws IOException{
		String path = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataset = "test";
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// add feed
		Schema schema = new Schema(XMLHelper.parseElement("<foo>bar</foo>"));
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		
		HttpSyncAdapter.uploadMeshDefinition(path, meshGroup+"/"+dataset, RssSyndicationFormat.NAME, "my description", schema, mapping, "jmt", null);
		
		String xform = HttpSyncAdapter.getXForm(path,meshGroup,dataset);
		
		Assert.assertEquals("", xform);
	}
	
	@Test
	public void shouldExecuteGetAllDataSetAsXFormContent() throws IOException{
		String serverURL = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = IdGenerator.INSTANCE.newID().substring(0, 5);
		
		// add mesh
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// dataset schema/mappings/xform definition
		Schema schema = new Schema(XMLHelper.parseElement("<foo>bar</foo>"));
		
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Oswego");
		rdfSchema.addBooleanProperty("ill", "ill", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("dateOnset", "dateOnSet", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("AgeDouble", "ageDouble", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("AgeInt", "ageInt", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("AgeLong", "ageLong", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		
		Element elementMappings = XMLHelper.parseElement("<mappings><title>title</title></mappings>");
		IMapping mapping = new Mapping(elementMappings);
		
		String xformXml = new String(FileUtils.read(new File(getClass().getResource("xformcustomized.txt").getFile())));
		
		// add dataSets
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup+"/rdfxform", RssSyndicationFormat.NAME, "my description", rdfSchema, mapping, "jmt", xformXml);
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup+"/rdf", RssSyndicationFormat.NAME, "my description", rdfSchema, mapping, "jmt", null);
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup+"/nothing", RssSyndicationFormat.NAME, "my description", null, mapping, "jmt", null);
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup+"/xform", RssSyndicationFormat.NAME, "my description", null, mapping, "jmt", xformXml);
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup+"/schemaxform", RssSyndicationFormat.NAME, "my description", schema, mapping, "jmt", xformXml);
		HttpSyncAdapter.uploadMeshDefinition(serverURL, meshGroup+"/schema", RssSyndicationFormat.NAME, "my description", schema, mapping, "jmt", null);
		
		// get all
		String url = HttpSyncAdapter.makeSchemaAsXFormURL(serverURL, meshGroup);
		
		HttpSyncAdapter httpAdapter = makeAdapter(url);
		
		List<Item> items = httpAdapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(4, items.size());
		
		for (Item item : items) {
			Assert.assertTrue(item.getContent().getPayload().asXML().startsWith("<schema><h:html"));
		}
	}
	
	@Test
	public void shouldExecuteGetAll(){
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		List<Item> items = httpAdapter.getAll();
		Assert.assertNotNull(items);
	}

	private HttpSyncAdapter makeAdapter(String path) {
		return new HttpSyncAdapter(path, RssSyndicationFormat.INSTANCE, new IdentityProvider("jmt"), IdGenerator.INSTANCE, ContentWriter.INSTANCE, ContentReader.INSTANCE);
	}
	
	@Test
	public void shouldExecuteGetAllSince(){
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		List<Item> items = httpAdapter.getAllSince(TestHelper.now());
		Assert.assertNotNull(items);
	}

	
	@Test
	public void shouldExecuteMerge() throws Exception{
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		
		Date now = TestHelper.now();
		Thread.sleep(500);
		int size = httpAdapter.getAll().size();
		int size1 = httpAdapter.getAllSince(now).size();
		
		String syncId = IdGenerator.INSTANCE.newID();		

		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar").setText("fooBar");
		
		XMLContent content = new XMLContent(syncId, "myTitle", "myDesc", payload);
		Sync sync = new Sync(syncId, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		
		Feed feed = new Feed(item);
				
		List<Item> result = httpAdapter.merge(feed.getItems());
		Assert.assertEquals(0, result.size());
		
		List<Item> items = httpAdapter.getAll();
		Assert.assertEquals(size + 1, items.size());
		
		items = httpAdapter.getAllSince(now);
		Assert.assertEquals(size1 + 1, items.size());
		
		// update item
		sync.update("jmt2", new Date());
		content.getPayload().addElement("barfoo");
		content.refreshVersion();
		
		result = httpAdapter.merge(feed.getItems());
		Assert.assertEquals(0, result.size());
		
		items = httpAdapter.getAll();
		Assert.assertEquals(size + 1, items.size());
		
		items = httpAdapter.getAllSince(now);
		Assert.assertEquals(size1 + 1, items.size());
	}

	@Test
	public void shouldMakeXFormURL() {
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/schema?content=xform", HttpSyncAdapter.makeSchemaAsXFormURL("http://localhost:8080/mesh4x/feeds","myMesh","myFeed"));
	}
	
	@Test
	public void shouldMakeMeshGroupURLToSync() {
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?viewALLGroupMeshItems", HttpSyncAdapter.makeMeshGroupURLToSync("http://localhost:8080/mesh4x/feeds/myMesh/myFeed"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/?viewALLGroupMeshItems", HttpSyncAdapter.makeMeshGroupURLToSync("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf&viewALLGroupMeshItems", HttpSyncAdapter.makeMeshGroupURLToSync("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf"));
	}
	
	@Test
	public void shouldMakeMappingsURL() {
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/mappings", HttpSyncAdapter.makeMappingsURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/mappings", HttpSyncAdapter.makeMappingsURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/mappings?format=rdf", HttpSyncAdapter.makeMappingsURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf"));
	}
	
	@Test
	public void shouldMakeSchemaURL() {
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/schema", HttpSyncAdapter.makeSchemaURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/schema", HttpSyncAdapter.makeSchemaURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/schema?format=rdf", HttpSyncAdapter.makeSchemaURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf"));
	}
	
	@Test
	public void shouldMakeAddItemFromRawDataURL() {
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/add", HttpSyncAdapter.makeAddItemFromRawDataURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/add", HttpSyncAdapter.makeAddItemFromRawDataURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/add?format=rdf", HttpSyncAdapter.makeAddItemFromRawDataURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf"));
	}

	@Test
	public void shouldMakeGetURL() {
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed", null));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/", null));
		
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed", ""));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/", ""));
		
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?filter=Name=JMT", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed", "Name=JMT"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/?filter=Name=JMT", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/", "Name=JMT"));

		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf&filter=Name=JMT", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed?format=rdf", "Name=JMT"));
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/?format=rdf&filter=Name=JMT", HttpSyncAdapter.makeGetURL("http://localhost:8080/mesh4x/feeds/myMesh/myFeed/?format=rdf", "Name=JMT"));
	}	
	
	@Test
	public void shouldObtainsSchema(){
		ISchema schema = HttpSyncAdapter.getSchema("http://localhost:8080/mesh4x/feeds", "myMesh ", "myFeed");
		Assert.assertNotNull(schema);
	}

	@Test
	public void shouldObtainsMappings(){
		IMapping mappings = HttpSyncAdapter.getMappings("http://localhost:8080/mesh4x/feeds", "myMesh ", "myFeed");
		Assert.assertNotNull(mappings);
	}
	
	@Test
	public void shouldAddNewItemFromRawData() throws InterruptedException{
		String path = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter httpAdapter = makeAdapter(path);
		
		int size = httpAdapter.getAll().size();

		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar").setText(IdGenerator.INSTANCE.newID());
		
		String xml = payload.asXML();
		httpAdapter.addItemFromRowData(xml);
		
		List<Item> items = httpAdapter.getAll();
		Assert.assertEquals(size + 1, items.size());
		
		int matchNumber = 0;
		for (Item item : items) {
			if(xml.equals(item.getContent().getPayload().asXML())){
				matchNumber++;
			}
		}
		Assert.assertEquals(1, matchNumber);
	}
	
	
	@Test
	public void shoulCreateMeshGroupAndDataSetOnCloudIfAbsent(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);  
		
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, rdfSchema, null);
		Assert.assertNotNull(adapter);
		
		ISchema schema = adapter.getSchema();
		Assert.assertNotNull(schema);
		Assert.assertTrue(rdfSchema.isCompatible(schema));
	}
	
	@Test
	public void shoulUpdateMeshGroupAndDataSetOnCloudIfExistsWithNullSchema(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, null, null);
		Assert.assertNotNull(adapter);
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);  
		
		adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, rdfSchema, null);
		Assert.assertNotNull(adapter);
		
		ISchema schema = adapter.getSchema();
		Assert.assertNotNull(schema);
		Assert.assertTrue(rdfSchema.isCompatible(schema));
	}
	
	@Test(expected=MeshException.class)
	public void shoulCreateMeshGroupAndDataSetOnCloudFailsIfSchemaAreNotCompatibles(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);  
		
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, rdfSchema, null);
		Assert.assertNotNull(adapter);
		
		ISchema schema = adapter.getSchema();
		Assert.assertNotNull(schema);
		Assert.assertTrue(rdfSchema.isCompatible(schema));
		
		RDFSchema rdfSchema2 = new RDFSchema(dataSetId, url+"#", dataSetId);
		
		HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, rdfSchema2, null);

	}
	
	@Test
	public void shouldGetSchemaReturnsNullWhenDataSetHasNotSchema(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
				
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, null, null);
		Assert.assertNotNull(adapter);
		
		ISchema schema = adapter.getSchema();
		Assert.assertNull(schema);

	}

	@Test
	public void shouldGetSchemaReturnsSchemaWhenDataSetWasCreatedWithBasicSchema(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		
		Element schemaElement = XMLHelper.parseElement("<mySchema><id>is an string</id></mySchema>");
		Schema basicSchema = new Schema(schemaElement);

		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, basicSchema, null);
		Assert.assertNotNull(adapter);
		
		ISchema schema = adapter.getSchema();
		Assert.assertNotNull(schema);
		Assert.assertEquals(basicSchema.asXML(), schema.asXML());
		Assert.assertTrue(basicSchema.isCompatible(schema));
	}

	@Test
	public void shouldGetSchemaReturnsRDFSchemaWhenDataSetWasCreatedWithRDFSchema(){
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "meshGroup" + IdGenerator.INSTANCE.newID().substring(0, 5);
		String dataSetId = "dataSetId" + IdGenerator.INSTANCE.newID().substring(0, 5);
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		String url = serverUrl+"/"+meshGroup+"/"+dataSetId;
		
		RDFSchema rdfSchema = new RDFSchema(dataSetId, url+"#", dataSetId);
		rdfSchema.addStringProperty("id1", "id1", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("id2", "id2", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);  
		
		ArrayList<String> pks = new ArrayList<String>();
		pks.add("id1");
		pks.add("id2");
		rdfSchema.setIdentifiablePropertyNames(pks);
		rdfSchema.setVersionPropertyName("datetime");
		
		
		HttpSyncAdapter adapter = HttpSyncAdapterFactory.createSyncAdapterAndCreateOrUpdateMeshGroupAndDataSetOnCloudIfAbsent(serverUrl, meshGroup, dataSetId, identityProvider, rdfSchema, null);
		Assert.assertNotNull(adapter);
		
		ISchema schema = adapter.getSchema();
		Assert.assertNotNull(schema);
		Assert.assertEquals(rdfSchema.asXML(), schema.asXML());
		Assert.assertTrue(rdfSchema.isCompatible(schema));
		
		Assert.assertEquals("id1", ((RDFSchema)schema).getIdentifiablePropertyNames().get(0));
		Assert.assertEquals("id2", ((RDFSchema)schema).getIdentifiablePropertyNames().get(1));
	}
	

}
