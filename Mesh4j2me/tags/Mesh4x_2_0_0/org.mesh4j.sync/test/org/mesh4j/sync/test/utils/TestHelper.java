package org.mesh4j.sync.test.utils;

import java.util.Calendar;
import java.util.Date;

import org.mesh4j.sync.id.generator.IdGenerator;

public class TestHelper {

	public static final String DEFAUT_PLACEMARK = "<Placemark xmlns=\"http://earth.google.com/kml/2.2\"><name>my favorite placemark</name><visibility>0</visibility><LookAt><longitude>-95.26548319399998</longitude><latitude>38.95938957099998</latitude><altitude>0</altitude><range>6000264.254089176</range><tilt>0</tilt><heading>-9.382636310317375e-014</heading></LookAt><styleUrl>#msn_ylw-pushpin</styleUrl><Point><coordinates>-95.265483194,38.95938957099998,0</coordinates></Point></Placemark>";

	public synchronized static String newID() {
		return IdGenerator.INSTANCE.newID();
	}

	public static Date now() {
		return new Date();
	}

	public static Date nowAddSeconds(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, i + cal.get(Calendar.SECOND));
		return cal.getTime();
	}
	
	public static Date makeDate(int year, int month, int day, int hour,
			int minute, int second, int millisecond) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, millisecond);
		cal.set(Calendar.AM_PM, hour <= 12 ? Calendar.AM : Calendar.PM);
		return cal.getTime();
	}
	
	public static Date nowAddDays(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, i + cal.get(Calendar.DATE));
		return cal.getTime();
	}
	
	public static Date nowAddMinutes(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, i + cal.get(Calendar.MINUTE));
		return cal.getTime();
	}
	
	public static Date nowSubtractMinutes(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, (-1 * i) + cal.get(Calendar.MINUTE));
		return cal.getTime();
	}

	public static Date nowSubtractHours(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, (-1 * i) + cal.get(Calendar.HOUR));
		return cal.getTime();
	}
	
	public static Date nowSubtractSeconds(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, (-1 * i) + cal.get(Calendar.SECOND));
		return cal.getTime();
	}
	
}
