package org.mesh4j.geo.coder;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.mesh4j.sync.validations.Guard;

public class GeoLocation {
	
	// MODEL VARIABLES
	private double latitude;
	private double longitude;
	private String address;

	// BUSINESS METHODS
	public GeoLocation(String address, double latitude, double longitude) {
		checkLatLongIsInRange(latitude, longitude);

		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}

	public static GeoLocation parse(String address, String latitude, String longitude) {
		GeoLocation latLong = new GeoLocation(
			address,
			parseLatLongComponent(latitude),
			parseLatLongComponent(longitude));
		return latLong;
	}

	// / <summary>
	// / Parses a latitude or longitude value.
	// / The string value could be in both DMS and Decimal formats.
	// / </summary>
	// / <param name="component"></param>
	// / <returns></returns>
	private static double parseLatLongComponent(String component) {
		
		// Switch , to . in case the user is in another locale
		component = component.replace(',', '.');
		String[] componentParts = component.split(".");

		if (componentParts.length > 2) {
			// DMS component value
			// TODO: Check how the sign will be included in the component string

			double sign = parseLatLongComponent(componentParts[0]) >= 0 ? 1
					: -1;
			double degrees = Math
					.abs(Math
							.round(parseLatLongComponent(componentParts[0]) * 1000000d));
			double minutes = Math
					.abs(Math
							.round(parseLatLongComponent(componentParts[1]) * 1000000d));
			double seconds = Double.MIN_VALUE;
			if (componentParts.length == 3) {
				seconds = Math
						.abs(Math
								.round(parseLatLongComponent(componentParts[2]) * 1000000d));
			} else {
				seconds = Math.abs(Math.round(parseLatLongComponent(StringUtils.join(new String[] { componentParts[2], componentParts[3] }, '.')) * 1000000d));
			}

			return Math.round(degrees + (minutes / 60d) + (seconds / 3600d))
					* sign / 1000000d;
		} else {
			// Decimal component value
			return Double.parseDouble(component);
		}
	}

	void checkLatLongIsInRange(double latitude, double longitude) {
		if (latitude < -90 || latitude > 90) {
			Guard.throwsArgumentException("latitude");
		}

		if (longitude < -180 || latitude > 180) {
			Guard.throwsArgumentException("longitude");

		}
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getAddress() {
		return address;
	}
	
	@Override
	public String toString(){
		return MessageFormat.format("Latitude: {0}, Longitude: {1}, Address: {2}", this.latitude, this.longitude, this.address);
	}
}
