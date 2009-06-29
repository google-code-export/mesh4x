package org.mesh4j.sync.adapters.multikey;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class HibernateMultiKeyTests {

	@Test
	public void shouldTest() throws Exception{
		String connectionUri = "jdbc:mysql://localhost:3306/mesh4xdb";

		SplitAdapter adapter = HibernateSyncAdapterFactory.createHibernateAdapter(
				connectionUri,
				"root",
				"",
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class, 
				"mesh_multi_key", 
				"http://localhost:8080/mesh4x/feeds",
				TestHelper.baseDirectoryForTest(),
				NullIdentityProvider.INSTANCE);
		
		HibernateToRDFMapping mapping = (HibernateToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
		
		List<Item> items = adapter.getAll();
		
		for (Item item : items) {
			System.out.println(item.getContent().getPayload().asXML());
			System.out.println(mapping.convertXMLToRow(item.getContent().getPayload()).asXML());
		}
		
		String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:mesh_multi_key=\"http://localhost:8080/mesh4x/feeds/mesh_multi_key#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><mesh_multi_key:mesh_multi_key rdf:about=\"uri:urn:1,3\"><mesh_multi_key:id1 rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</mesh_multi_key:id1><mesh_multi_key:id2 rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">3</mesh_multi_key:id2><mesh_multi_key:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">sol</mesh_multi_key:name></mesh_multi_key:mesh_multi_key></rdf:RDF>";
		Item item = new Item(new IdentifiableContent(XMLHelper.parseElement(xml), mapping, "1,3"), new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);
		
		Assert.fail("pending task");
	}
}
