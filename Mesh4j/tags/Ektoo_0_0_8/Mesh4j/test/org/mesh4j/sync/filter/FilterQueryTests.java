package org.mesh4j.sync.filter;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

public class FilterQueryTests {

	@Test
	public void shouldCreateQueryFilterAcceptsNullSchema(){
		new FilterQuery("true", null);
	}

	@Test
	public void shouldCreateQueryFilterAcceptsEmptyFilterExpression(){
		new FilterQuery("", new Schema(XMLHelper.parseElement("<foo><bar>string</bar></foo>")));
	}

	@Test
	public void shouldCreateQueryFilterAcceptsNullFilterExpression(){
		new FilterQuery(null, new Schema(XMLHelper.parseElement("<foo><bar>string</bar></foo>")));
	}
	
	@Test
	public void shouldAppliesReturnsFalseIfItemIsNull(){
		FilterQuery fq = new FilterQuery("true", new Schema(XMLHelper.parseElement("<foo><bar>string</bar></foo>")));
		Assert.assertFalse(fq.applies(null));
	}
	
	@Test
	public void shouldAppliesReturnsTrueIfFilterExpressionIsNull(){
		String id = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(id, "title", "des", "link", XMLHelper.parseElement("<foo><bar>jmt</bar></foo>"));
		Sync sync = new Sync(id, "jmt", new Date(), false);
		Item item = new Item(content, sync);
		
		FilterQuery fq = new FilterQuery(null, new Schema(XMLHelper.parseElement("<foo><bar>string</bar></foo>")));
		Assert.assertTrue(fq.applies(item));
	}

	@Test
	public void shouldAppliesReturnsTrueIfFilterExpressionIsEmpty(){
		String id = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(id, "title", "des", "link", XMLHelper.parseElement("<foo><bar>jmt</bar></foo>"));
		Sync sync = new Sync(id, "jmt", new Date(), false);
		Item item = new Item(content, sync);
		
		FilterQuery fq = new FilterQuery("", new Schema(XMLHelper.parseElement("<foo><bar>string</bar></foo>")));
		Assert.assertTrue(fq.applies(item));
	}
	
	@Test
	public void shouldAppliesUsePlainXmlTextIfFilterExpressionIsNotNullAndSchemaIsNull(){
		String id = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(id, "title", "des", "link", XMLHelper.parseElement("<foo><bar>jmt</bar></foo>"));
		Sync sync = new Sync(id, "jmt", new Date(), false);
		Item item = new Item(content, sync);
		
		FilterQuery fq = new FilterQuery("bar=jmt", null);
		Assert.assertTrue(fq.applies(item));
	}
	
	@Test
	public void shouldAppliesUsePlainXmlTextIfFilterExpressionIsNotNullAndSchemaIsBasicSchema(){
		String id = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(id, "title", "des", "link", XMLHelper.parseElement("<foo><bar>jmt</bar></foo>"));
		Sync sync = new Sync(id, "jmt", new Date(), false);
		Item item = new Item(content, sync);
		
		FilterQuery fq = new FilterQuery("bar=jmt", new Schema(XMLHelper.parseElement("<foo><bar>string</bar></foo>")));
		Assert.assertTrue(fq.applies(item));
	}
	
	@Test
	public void shouldAppliesUseRDFInstancePropertyValuesAsLexicalFormIfFilterExpressionIsNotNullAndSchemaIsRDFSchema(){
		RDFSchema rdfSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        rdfSchema.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        rdfSchema.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
        rdfSchema.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
        rdfSchema.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        rdfSchema.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
        rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);        
    	
        RDFInstance rdfInstance = rdfSchema.createNewInstance("uri:urn:1");
        rdfInstance.setProperty("string", "abc");
        rdfInstance.setProperty("integer", Integer.MAX_VALUE);
        rdfInstance.setProperty("boolean", true);
        rdfInstance.setProperty("datetime", new Date());
        rdfInstance.setProperty("double", Double.MAX_VALUE);
        rdfInstance.setProperty("long", Long.MAX_VALUE);
        rdfInstance.setProperty("decimal", BigDecimal.TEN);
        
		String id = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(id, "title", "des", "link", XMLHelper.parseElement(rdfInstance.asXML()));
		Sync sync = new Sync(id, "jmt", new Date(), false);
		Item item = new Item(content, sync);
		
		FilterQuery fq = new FilterQuery("string=abc and boolean=true and double<="+Double.MAX_VALUE, rdfSchema);
		Assert.assertTrue(fq.applies(item));
	}
}
