package com.technical.commonutil;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final String RESPONSE_TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	public static Date addMinutes(Integer minutes) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, minutes);
		return now.getTime();
	}
}
