package org.mesh4j.sync.utils;

import org.junit.Assert;
import org.junit.Test;

public class IdGeneratorTests {

	@Test
	public void shouldGenereteNotNullID(){
		Assert.assertNotNull(IdGenerator.newID());
	}
	
	@Test
	public void shouldGenereteNotEmptyID(){
		Assert.assertTrue(IdGenerator.newID().trim().length() > 0);
	}
	
	@Test
	public void shouldGenerete2DiferentsID(){
		Assert.assertFalse(IdGenerator.newID().equals(IdGenerator.newID()));
	}
}
