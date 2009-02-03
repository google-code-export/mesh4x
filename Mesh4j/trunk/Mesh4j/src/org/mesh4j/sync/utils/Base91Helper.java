package org.mesh4j.sync.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.mesh4j.sync.validations.MeshException;


public class Base91Helper
{
	public static byte[] decode(String data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		decode(is, os);
		return os.toByteArray();
	}

	
	public static String encode(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		encode(is, os);
		return new String(os.toByteArray());
	}
	
	private static void decode(InputStream is, OutputStream os)
	{
		int s;
		byte[] ibuf = new byte[65536];
		byte[] obuf = new byte[57344];
		Base91 b91 = new Base91();

		try {
			while ((s = is.read(ibuf)) > 0) {
				s = b91.decode(ibuf, s, obuf);
				os.write(obuf, 0, s);
			}
			s = b91.decEnd(obuf);
			os.write(obuf, 0, s);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private static void encode(InputStream is, OutputStream os)
	{
		int s;
		byte[] ibuf = new byte[53248];
		byte[] obuf = new byte[65536];
		Base91 b91 = new Base91();

		try {
			while ((s = is.read(ibuf)) > 0) {
				s = b91.encode(ibuf, s, obuf);
				os.write(obuf, 0, s);
			}
			s = b91.encEnd(obuf);
			os.write(obuf, 0, s);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
