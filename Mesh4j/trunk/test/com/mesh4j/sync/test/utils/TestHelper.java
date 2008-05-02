package com.mesh4j.sync.test.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TestHelper {
	
	private static long LAST_ID = 0;
	private static Random ID_GENERATOR = new Random();
		
	public synchronized static String newID() {
		int currnetID = random();
		while (LAST_ID == currnetID){
			currnetID = random();
		}
		LAST_ID = currnetID;
		return String.valueOf(LAST_ID);
	}
	
	private static int random(){
		int i = ID_GENERATOR.nextInt();
		if(i < 0){
			i = i * -1;
		}
		return i;
	}

	public static Element makeElement(String xmlAsString) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlAsString);
		} catch (DocumentException e) {
			throw new IllegalArgumentException(e);
		}
		return doc.getRootElement();
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
