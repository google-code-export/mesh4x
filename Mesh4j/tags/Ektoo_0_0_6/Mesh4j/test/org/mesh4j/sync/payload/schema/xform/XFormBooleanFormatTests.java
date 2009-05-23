package org.mesh4j.sync.payload.schema.xform;

import junit.framework.Assert;

import org.junit.Test;

public class XFormBooleanFormatTests {

	@Test
	public void shouldFormatTrue(){	
		Assert.assertEquals(XFormBooleanFormat.OPTION_YES, XFormBooleanFormat.INSTANCE.format(true));
	}
	
	@Test
	public void shouldFormatFalse(){
		Assert.assertEquals(XFormBooleanFormat.OPTION_NO, XFormBooleanFormat.INSTANCE.format(false));
	}
	
	@Test
	public void shouldFormatFailsIfValueIsNotBoolean(){
		Assert.assertNull(XFormBooleanFormat.INSTANCE.format(""));
	}

	@Test
	public void shouldParseTrue(){	
		Assert.assertTrue((Boolean)XFormBooleanFormat.INSTANCE.parseObject(XFormBooleanFormat.OPTION_YES));
	}
	
	@Test
	public void shouldParseFalse(){	
		Assert.assertFalse((Boolean)XFormBooleanFormat.INSTANCE.parseObject(XFormBooleanFormat.OPTION_NO));
	}
	
	@Test
	public void shouldParseOtherwise(){	
		Assert.assertFalse((Boolean)XFormBooleanFormat.INSTANCE.parseObject("sdda"));
	}

}
