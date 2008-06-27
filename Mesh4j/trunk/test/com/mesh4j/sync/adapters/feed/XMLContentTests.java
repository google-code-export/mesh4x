package com.mesh4j.sync.adapters.feed;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

public class XMLContentTests {

	@Test
	public void shouldEqualsDoesNotFailsWhenATitleIsNull(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", null, "b", element);
		XMLContent contentB = new XMLContent("1", "a", "b", element);
		
		Assert.assertFalse(contentA.equals(contentB));
	}
	
	@Test
	public void shouldEqualsReturnsFalseWhenTitleIsDiff(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", "a", "b", element);
		XMLContent contentB = new XMLContent("1", "b", "b", element);
		
		Assert.assertFalse(contentA.equals(contentB));
	}
	
	@Test
	public void shouldEqualsReturnsFalseWhenDescriptionIsDiff(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", "a", "b", element);
		XMLContent contentB = new XMLContent("1", "a", "v", element);
		
		Assert.assertFalse(contentA.equals(contentB));
	}
	
	@Test
	public void shouldEqualsReturnsTrue(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", "a", "b", element);
		XMLContent contentB = new XMLContent("1", "a", "b", element);
		
		Assert.assertTrue(contentA.equals(contentB));
	}
	
	@Test
	public void shouldEqualsDoesNotFailsWhenBTitleIsNull(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", "a", "b", element);
		XMLContent contentB = new XMLContent("1", null, "b", element);
		
		Assert.assertFalse(contentA.equals(contentB));
	}

	@Test
	public void shouldEqualsDoesNotFailsWhenBDescriptionIsNull(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", "a", "b", element);
		XMLContent contentB = new XMLContent("1", "a", null, element);
		
		Assert.assertFalse(contentA.equals(contentB));
	}
	
	@Test
	public void shouldEqualsDoesNotFailsWhenADescriptionIsNull(){		
		Element element = DocumentHelper.createElement("example");
		XMLContent contentA = new XMLContent("1", "a", null, element);
		XMLContent contentB = new XMLContent("1", "a", "b", element);
		
		Assert.assertFalse(contentA.equals(contentB));
	}
}
