package org.mesh4j.sync.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;

public class IdGeneratorTests {

	@Test
	public void shouldGenereteNotNullID(){
		Assert.assertNotNull(IdGenerator.INSTANCE.newID());
	}
	
	@Test
	public void shouldGenereteNotEmptyID(){
		Assert.assertTrue(IdGenerator.INSTANCE.newID().trim().length() > 0);
	}
	
	@Test
	public void shouldGenerete2DiferentsID(){
		Assert.assertFalse(IdGenerator.INSTANCE.newID().equals(IdGenerator.INSTANCE.newID()));
	}
}
