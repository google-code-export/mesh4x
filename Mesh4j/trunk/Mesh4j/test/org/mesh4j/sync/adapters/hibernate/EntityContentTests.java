package org.mesh4j.sync.adapters.hibernate;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToPlainXMLMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateToPlainXMLMappingTests;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.test.utils.TestHelper;


public class EntityContentTests {

	@Test
	public void shouldReturnsEqualsTrueWithClones(){
		Element payload = TestHelper.makeElement("<foo>bar</foo>");
		EntityContent c = new EntityContent(payload, "foo", "bar");
		Assert.assertEquals(c, c.clone());
	}
	
	@Test
	public void shouldBeClonesHasSameHasCodes(){
		Element payload = TestHelper.makeElement("<foo>bar</foo>");
		EntityContent c = new EntityContent(payload, "foo", "bar");
		Assert.assertTrue(c.hashCode() == c.clone().hashCode());
	}
	
	@Test
	public void shouldReturnsFalse(){
		Element payload = TestHelper.makeElement("<foo>bar</foo>");
		EntityContent c = new EntityContent(payload, "foo", "bar");
		Assert.assertFalse(c.equals("qq"));
	}
	
	@Test
	public void shouldReturnsSameFalseWithClones(){
		Element payload = TestHelper.makeElement("<foo>bar</foo>");
		EntityContent c = new EntityContent(payload, "foo", "bar");
		Assert.assertFalse(c == c.clone());
	}
	
	@Test
	public void shouldNormalizeFromHibernateContent(){
		Element e = TestHelper.makeElement("<foo><id>1</id></foo>");
		EntityContent c = new EntityContent(e, "foo", "id");
		EntityDAO dao = new EntityDAO(null, new HibernateToPlainXMLMapping("foo", "id"));
		
		Assert.assertSame(c, dao.normalizeContent(c));
	}
	
	@Test
	public void shouldNormalizeFromContentWithPayloadNameEqualsEntityName(){
		Element e = TestHelper.makeElement("<foo><id>1</id></foo>");
		MyContent c = new MyContent(e);
		EntityDAO dao = new EntityDAO(null, new HibernateToPlainXMLMapping("foo", "id"));
		
		Assert.assertSame(e, dao.normalizeContent(c).getPayload());		
	}

	@Test
	public void shouldNormalizeFromContentIfEntityNameIntoPayload(){
		Element e = TestHelper.makeElement("<bar><foo><id>1</id></foo></bar>");
		MyContent c = new MyContent(e);
		EntityDAO dao = new EntityDAO(null, new HibernateToPlainXMLMapping("foo", "id"));
		
		IContent cn = dao.normalizeContent(c);
		Assert.assertNotSame(e, cn.getPayload());
		Assert.assertNotNull(cn.getPayload());
		Assert.assertEquals("<foo><id>1</id></foo>", cn.getPayload().asXML());
	}	
	
	@Test
	public void shouldNormalizeReturnsNullIfEntityNameIsNotInPayload(){
		Element e = TestHelper.makeElement("<bar><foo>1</foo></bar>");
		MyContent c = new MyContent(e);
		EntityDAO dao = new EntityDAO(null, new HibernateToPlainXMLMapping("foo", "id"));
		
		Assert.assertNull(dao.normalizeContent(c));
	}	

	
	private class MyContent implements IContent{
		
		private Element e;
		
		public MyContent(Element e){
			this.e = e;
		}
		public Element getPayload(){
			return e;
		}
		
		public IContent clone(){
			return new MyContent(e);
		}
		
		public String getId(){
			return String.valueOf(e.asXML().hashCode());
		}
		@Override
		public int getVersion() {
			return e.asXML().hashCode();
		}
	}
	
}
