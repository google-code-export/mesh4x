package org.mesh4j.geo.coder;

public class NullGeoCoder implements IGeoCoder {

	public static final IGeoCoder INSTANCE = new NullGeoCoder();

	@Override
	public GeoLocation getLocation(String address) {
		return null;
	}

}
