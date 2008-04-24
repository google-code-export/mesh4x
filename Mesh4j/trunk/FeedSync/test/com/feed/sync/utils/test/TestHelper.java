package com.feed.sync.utils.test;

import java.util.Calendar;
import java.util.Date;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

public class TestHelper {
	
	private static long LAST_ID = 0L;
		
	public synchronized static String newID() {
		long currnetID = System.currentTimeMillis();
		while (LAST_ID == currnetID){
			currnetID = System.currentTimeMillis();
		}
		LAST_ID = currnetID;
		return String.valueOf(LAST_ID);
	}

	public static Element makeElement(String xmlAsString) {
		return new DOMElement(xmlAsString);
	}

	public static Date now() {
		return new Date();
	}

	public static Date nowSubtractMinutes(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, (-1 * i));
		return cal.getTime();
	}

	public static Date nowSubtractHours(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, (-1 * i));
		return cal.getTime();
	}

	public static Date nowAddMinutes(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, i);
		return cal.getTime();
	}

	public static Date nowAddDays(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}

	public static Date nowAddSeconds(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, i);
		return cal.getTime();
	}

	public static Date nowSubtractSeconds(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, (-1*i));
		return cal.getTime();
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, millisecond);
		return cal.getTime();
	}

	public static Date nowSubtractDays(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, (-1 * i));
		return cal.getTime();
	}
	
	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}			
	}
	
}
