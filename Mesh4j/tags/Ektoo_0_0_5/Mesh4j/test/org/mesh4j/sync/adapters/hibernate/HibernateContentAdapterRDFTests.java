package org.mesh4j.sync.adapters.hibernate;

import java.io.File;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

public class HibernateContentAdapterRDFTests extends HibernateContentAdapterTests{

	private RDFSchema schema;
	
	protected IHibernateSessionFactoryBuilder getBuilder() {
		schema = new RDFSchema("user", "http://mesh4x/user#", "user");
		schema.addStringProperty("id", "id", "en");
		schema.addStringProperty("pass", "password", "en");
		schema.addStringProperty("name", "name", "en");
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(HibernateAdapterTests.class.getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(this.getClass().getResource("xx_hibernate.properties").getFile()));
		builder.addRDFSchema(schema);
		return builder;
	}
	
	protected IContent makeContent(String id, String name, String pass) throws DocumentException {
		RDFInstance rdfInstance = this.schema.createNewInstance("uri:urn:"+id);
		rdfInstance.setProperty("name", name);
		rdfInstance.setProperty("pass", pass);
		rdfInstance.setProperty("id", id);
		
		String rdfXml = rdfInstance.asXML();
		Element payload = XMLHelper.parseElement(rdfXml);
		IContent user = new EntityContent(payload, "user", id);
		return user;
	}
	

}
