package com.feed.sync.utils;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public static Date normalize(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

}
