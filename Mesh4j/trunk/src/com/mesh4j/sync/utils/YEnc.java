package com.mesh4j.sync.utils;

import java.io.ByteArrayOutputStream;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

public class YEnc implements BinaryDecoder, BinaryEncoder {

	/**
	 * Decodes a byte[] containing containing YEnc data
	 * 
	 * @param bytes
	 *            A byte array containing YEnc data
	 * @return a byte array containing binary data
	 */
	public byte[] decode(byte[] bytes) throws DecoderException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		for (int pos = 0; pos < bytes.length; pos++) {
			c = bytes[pos];
			if ('\r' == c || '\n' == c)
				continue;
			if ('=' == c) {
				pos++;
				c = (bytes[pos] - 64) % 255;
			}
			baos.write((c - 42) % 255);
		}
		return baos.toByteArray();
	}

	// Implementation of the Encoder Interface
	/**
	 * Encodes a byte[] containing containing binary data
	 * 
	 * @param bytes
	 *            A byte array containing binary data
	 * @return a byte array containing YEnc data
	 */
	public byte[] encode(byte[] bytes) throws EncoderException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		for (int pos = 0; pos < bytes.length; pos++) {
			c = (bytes[pos] + 42) % 256;
			if (c == 0x00 || c == 0x0A || c == 0x0D || c == 0x3D) {
				baos.write((int) '=');
				baos.write((c + 64) % 256);
			} else {
				baos.write(c);
			}
		}
		return baos.toByteArray();
	}

	/**
	 * Enccodes an Object using the yenc algorithm. This method is provided in
	 * order to satisfy the requirements of the Enccoder interface, and will
	 * throw an EncoderException if the supplied object is not of type byte[].
	 * 
	 * @param Object
	 *            Object to encode
	 * @return An object (of type byte[]) containing the yenc encoded data which
	 *         corresponds to the byte[] supplied.
	 * @throws EncoderException
	 *             if the parameter supplied is not of type byte[]
	 */
	public Object encode(Object obj) throws EncoderException {
		if (!(obj instanceof byte[]))
			throw new EncoderException(
					"Parameter supplied to YEnc encode is not a byte[]");

		return encode((byte[]) obj);
	}

	/**
	 * Decodes an Object using the yenc algorithm. This method is provided in
	 * order to satisfy the requirements of the Decoder interface, and will
	 * throw an DecoderException if the supplied object is not of type byte[].
	 * 
	 * @param Object
	 *            Object to decode
	 * @return An object (of type byte[]) containing the yenc decoded data which
	 *         corresponds to the byte[] supplied.
	 * @throws DecoderException
	 *             if the parameter supplied is not of type byte[]
	 */
	public Object decode(Object obj) throws DecoderException {
		if (!(obj instanceof byte[]))
			throw new DecoderException(
					"Parameter supplied to YEnc decode is not a byte[]");

		return decode((byte[]) obj);
	}
	
}
