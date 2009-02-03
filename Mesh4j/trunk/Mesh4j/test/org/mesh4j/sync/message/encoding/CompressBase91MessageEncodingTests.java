package org.mesh4j.sync.message.encoding;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.Base91Helper;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.MeshException;


public class CompressBase91MessageEncodingTests {

	@Test(expected = IllegalArgumentException.class)
	public void shouldEncodeThrowsIllegalArgExpWhenMessageIsNull() {
		CompressBase91MessageEncoding.INSTANCE.encode(null);
	}

	@Test
	public void shouldEncodeReturnsEmptyStringWhenMessageEmpty() {
		Assert.assertEquals("", CompressBase91MessageEncoding.INSTANCE
				.encode(""));
	}

	@Test
	public void shouldEncodeReturnsNormalMessageWhenCompressMessageIsGreaterThanMessage() {
		String encodedMessage = CompressBase91MessageEncoding.INSTANCE
				.encode("123");
		Assert.assertNotNull(encodedMessage);
		Assert.assertEquals("n123", encodedMessage);
	}

	@Test
	public void shouldEncodeReturnsCompressMessageWhenCompressMessageIsLessThanMessage()
			throws IOException {
		String msg = TestHelper.newText(5000);
		String result = Base91Helper.encode((ZipUtils.compress(msg
				.getBytes("UTF-8"))));
		String encodedMessage = CompressBase91MessageEncoding.INSTANCE
				.encode(msg);
		Assert.assertNotNull(encodedMessage);
		Assert.assertEquals("c" + result, encodedMessage);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDecodeThrowsIllegalArgExpWhenMessageIsNull() {
		CompressBase91MessageEncoding.INSTANCE.decode(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDecodeThrowsIllegalArgExpWhenMessageIsNotNormalOrCompressed() {
		CompressBase91MessageEncoding.INSTANCE.decode("abc");
	}

	@Test
	public void shouldDecodeReturnsEmptyStringWhenMessageEmpty() {
		Assert.assertEquals("", CompressBase91MessageEncoding.INSTANCE
				.decode(""));
	}

	@Test
	public void shouldDecodeNormalMessage() {
		String decodedMessage = CompressBase91MessageEncoding.INSTANCE
				.decode("n123");
		Assert.assertNotNull(decodedMessage);
		Assert.assertEquals("123", decodedMessage);
	}

	@Test
	public void shouldDecodeCompressedMessage() throws IOException {
		String msg = TestHelper.newText(5000);
		String encodedMessage = "c"
				+ Base91Helper
						.encode((ZipUtils.compress(msg.getBytes("UTF-8"))));
		String decodedMessage = CompressBase91MessageEncoding.INSTANCE
				.decode(encodedMessage);
		Assert.assertNotNull(decodedMessage);
		Assert.assertEquals(msg, decodedMessage);
	}

	@Test(expected = MeshException.class)
	public void shouldDecodeFailsWhenCompressedMessageIsInvalid()
			throws IOException {
		CompressBase91MessageEncoding.INSTANCE.decode("cab");
	}
}
