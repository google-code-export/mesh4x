package com.mesh4j.sync.hibernate;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.test.utils.TestHelper;

public class ItemHibernateContentTests {

	@Test
	public void shouldReturnsEqualsTrueWithClones(){
		ItemHibernateContent c = new ItemHibernateContent(TestHelper.makeElement("<foo>bar</foo>"));
		Assert.assertEquals(c, c.clone());
	}
	
	@Test
	public void shouldBeClonesHasSameHasCodes(){
		ItemHibernateContent c = new ItemHibernateContent(TestHelper.makeElement("<foo>bar</foo>"));
		Assert.assertTrue(c.hashCode() == c.clone().hashCode());
	}
	
	@Test
	public void shouldReturnsFalse(){
		ItemHibernateContent c = new ItemHibernateContent(TestHelper.makeElement("<foo>bar</foo>"));
		Assert.assertFalse(c.equals("qq"));
	}
	
	@Test
	public void shouldReturnsSameFalseWithClones(){
		ItemHibernateContent c = new ItemHibernateContent(TestHelper.makeElement("<foo>bar</foo>"));
		Assert.assertFalse(c == c.clone());
	}
	
	@Test
	public void shouldNormalizeFromHibernateContent(){
		Element e = TestHelper.makeElement("<foo><bar>1</bar></foo>");
		ItemHibernateContent c = new ItemHibernateContent(e);
		
		Assert.assertSame(e, ItemHibernateContent.normalizeContent("foo", c));
	}
	
	@Test
	public void shouldNormalizeFromContentWithPayloadNameEqualsEntityName(){
		Element e = TestHelper.makeElement("<foo><bar>1</bar></foo>");
		MyContent c = new MyContent(e);
		
		Assert.assertSame(e, ItemHibernateContent.normalizeContent("foo", c));		
	}

	@Test
	public void shouldNormalizeFromContentIfEntityNameIntoPayload(){
		Element e = TestHelper.makeElement("<bar><foo>1</foo></bar>");
		MyContent c = new MyContent(e);
		
		Element n = ItemHibernateContent.normalizeContent("foo", c);
		Assert.assertNotSame(e, n);
		Assert.assertNotNull(n);
		Assert.assertEquals("<foo>1</foo>", n.asXML());
	}	
	
	@Test
	public void shouldNormalizeReturnsNullIfEntityNameIsNotInPayload(){
		Element e = TestHelper.makeElement("<bar><foo>1</foo></bar>");
		MyContent c = (MyContent) new MyContent(e).clone();
		
		Assert.assertNull(ItemHibernateContent.normalizeContent("myFoo", c));
	}	

	
	@SuppressWarnings("unused")
	private class MyContent implements Content{
		
		private Element e;
		
		public MyContent(Element e){
			this.e = e;
		}
		public Element getPayload(){
			return e;
		}
		
		public Content clone(){
			return new MyContent(e);
		}
	}
	
}
