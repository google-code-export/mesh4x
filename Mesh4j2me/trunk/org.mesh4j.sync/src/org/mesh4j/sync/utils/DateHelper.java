package org.mesh4j.sync.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

	public static Date normalize(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static String formatW3CDateTime(Date date) {
		// yyyy-MM-dd'T'HH:mm:ss'Z'
		return formatDateYYYYMMDDHHMMSS(date, "-", "T", "Z", TimeZone.getTimeZone("GMT"));
	}

	public static Date parseW3CDateTime(String sDate) {
		return parseDateYYYYMMDDHHMMSS(sDate, TimeZone.getTimeZone("GMT"));
	}


	// RSS date format RFC822
	public static String formatRFC822(Date date) {
		//return date.toString();
		return formatW3CDateTime(date);
	}
	
	public static Date parseRFC822(String sDate) {
//		try{
//			return new Date(DateParser.parse(sDate));  // TODO (JMT) MIDP_2.1
//		} catch(IllegalArgumentException e){
//			return parseW3CDateTime(sDate);
//		}
		return parseW3CDateTime(sDate);
	}

	// yyyy-MM-dd'T'HH:mm:ss'Z'
	public static String formatDateYYYYMMDDHHMMSS(Date date, String dateSeparator, String dateTimeSeparator, String endIndicator, TimeZone timeZone) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(timeZone != null){
			cal.setTimeZone(timeZone);
		}

		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		month = month.length() < 2 ? "0" + month : month;
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		day = day.length() < 2 ? "0" + day : day;
		String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		hour = hour.length() < 2 ? "0" + hour : hour;
		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		minute = minute.length() < 2 ? "0" + minute : minute;
		String second = String.valueOf(cal.get(Calendar.SECOND));
		second = second.length() < 2 ? "0" + second : second;

		StringBuffer sb = new StringBuffer();
		sb.append(cal.get(Calendar.YEAR));
		sb.append(dateSeparator);
		sb.append(month);
		sb.append(dateSeparator);
		sb.append(day);
		sb.append(dateTimeSeparator);
		sb.append(hour);
		sb.append(":");
		sb.append(minute);
		sb.append(":");
		sb.append(second);
		if (endIndicator != null) {
			sb.append(endIndicator);
		}
		return sb.toString();
	}

	// y y y y - M M - d d T  H  H  :  m  m  :  s  s  Z
	// 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19
	private static Date parseDateYYYYMMDDHHMMSS(String sDate, TimeZone timeZone) {
		
		int year = Integer.parseInt(sDate.substring(0, 4));
		int month = Integer.parseInt(sDate.substring(5, 7)) - 1;
		int day = Integer.parseInt(sDate.substring(8, 10));
		int hour = Integer.parseInt(sDate.substring(11, 13));
		int minute = Integer.parseInt(sDate.substring(14, 16));
		int second = Integer.parseInt(sDate.substring(17, 19));
		
		Calendar cal = Calendar.getInstance();
		if(timeZone != null){
			cal.setTimeZone(timeZone);
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}
	
//	public static String formatDateTime(Date date) {
//		if (date == null) {
//			return "";
//		}
//		return String.valueOf(date.getTime());
//	}
//
//	public static Date parseDateTime(String dateTimeAsString) {
//		if (dateTimeAsString == null || dateTimeAsString.length() == 0) {
//			return null;
//		}
//		try {
//			return new Date(Long.parseLong(dateTimeAsString));
//		} catch (NumberFormatException e) {
//			return null;
//		}
//	}
}
