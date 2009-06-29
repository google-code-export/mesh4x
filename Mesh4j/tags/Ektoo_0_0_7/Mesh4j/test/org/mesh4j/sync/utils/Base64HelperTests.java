package org.mesh4j.sync.utils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class Base64HelperTests {

	@Test
	public void shouldEncodeDecode(){
		String encoded = Base64Helper.encode("jmt");
		Assert.assertNotNull(encoded);
		Assert.assertEquals("am10", encoded);
		
		String original = Base64Helper.decodeAsString(encoded);
		Assert.assertNotNull(original);
		Assert.assertEquals("jmt", original);
	}
	
	@Test
	public void shouldEncodeDecodeFile() throws IOException{
		byte[] originalBytes = FileUtils.read(this.getClass().getResource("kmzExample_star.jpg").getFile());
		String encoded = Base64Helper.encode(originalBytes);
		Assert.assertNotNull(encoded);
		
		byte[] decodedBytes = Base64Helper.decode(encoded);
		Assert.assertNotNull(decodedBytes);
		Assert.assertArrayEquals(originalBytes, decodedBytes);
	}
}
