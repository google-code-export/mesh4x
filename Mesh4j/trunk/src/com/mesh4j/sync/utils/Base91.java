package com.mesh4j.sync.utils;

public class Base91
{
	private int ebq, en, dbq, dn, dv;
	public final byte[] enctab;
	private final byte[] dectab;

	public int encode(byte[] ib, int n, byte[] ob)
	{
		int i, c = 0;

		for (i = 0; i < n; ++i) {
			ebq |= (ib[i] & 255) << en;
			en += 8;
			if (en > 13) {
				int ev = ebq & 8191;

				if (ev > 88) {
					ebq >>= 13;
					en -= 13;
				} else {
					ev = ebq & 16383;
					ebq >>= 14;
					en -= 14;
				}
				ob[c++] = enctab[ev % 91];
				ob[c++] = enctab[ev / 91];
			}
		}
		return c;
	}

	public int encEnd(byte[] ob)
	{
		int c = 0;

		if (en > 0) {
			ob[c++] = enctab[ebq % 91];
			if (en > 7 || ebq > 90)
				ob[c++] = enctab[ebq / 91];
		}
		encReset();
		return c;
	}

	public void encReset()
	{
		ebq = 0;
		en = 0;
	}

	public int decode(byte[] ib, int n, byte[] ob)
	{
		int i, c = 0;

		for (i = 0; i < n; ++i) {
			if (dectab[ib[i]] == -1)
				continue;
			if (dv == -1)
				dv = dectab[ib[i]];
			else {
				dv += dectab[ib[i]] * 91;
				dbq |= dv << dn;
				dn += (dv & 8191) > 88 ? 13 : 14;
				do {
					ob[c++] = (byte) dbq;
					dbq >>= 8;
					dn -= 8;
				} while (dn > 7);
				dv = -1;
			}
		}
		return c;
	}

	public int decEnd(byte[] ob)
	{
		int c = 0;

		if (dv != -1)
			ob[c++] = (byte) (dbq | dv << dn);
		decReset();
		return c;
	}

	public void decReset()
	{
		dbq = 0;
		dn = 0;
		dv = -1;
	}

	public Base91()
	{
		int i;
		String ts = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"";

		enctab = ts.getBytes();
		dectab = new byte[256];
		for (i = 0; i < 256; ++i)
			dectab[i] = -1;
		for (i = 0; i < 91; ++i)
			dectab[enctab[i]] = (byte) i;
		encReset();
		decReset();
	}
}
