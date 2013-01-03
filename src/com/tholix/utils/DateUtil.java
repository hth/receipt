/**
 * 
 */
package com.tholix.utils;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author hitender 
 * @when Dec 27, 2012 2:53:16 PM
 *
 */
public class DateUtil {
	private static final Log log = LogFactory.getLog(DateUtil.class);
	
	private enum DateType {
		FRM_1("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}PM", 			"12/15/2012 02:13PM", 		"MM/dd/yyyy hh:mma"),
		FRM_2("\\d{2}/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}", 				"12/24/12 19:03", 			"MM/dd/yy kk:mm"),
		FRM_3("\\d{2}/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}:\\d{2}",		"12/25/12 16:54:57", 		"MM/dd/yy kk:mm:ss"),
		FRM_4("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}",		"12/15/2012 16:46:53", 		"MM/dd/yyyy kk:mm:ss"),
		FRM_5("\\d{2}/\\d{2}/\\d{4}\\sPM\\s\\d{2}:\\d{2}:\\d{2}", 	"12/15/2012 PM 04:49:45", 	"MM/dd/yyyy a hh:mm:ss"),
		FRM_6("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\sPM", 			"08/29/2012 03:07 PM", 		"MM/dd/yyyy hh:mm a"),
		;
		
		private final String regex;
		private final String example;
		private final DateTimeFormatter formatter;
		private DateType(String regex, String example, String formatter) {
			this.regex = regex;
			this.example = example;
			this.formatter = DateTimeFormat.forPattern(formatter);
		}
		
		public String getRegex() {
			return regex;
		}
		
		@SuppressWarnings("unused")
		public String getExample() {
			return example;
		}

		public DateTimeFormatter getFormatter() {
			return formatter;
		}		
	}
	
	/**
	 * Support various kinds of date format
	 * 	12/15/2012 02:13PM			
	 * 	12/24/12 19:03
	 * 	12/25/12 16:54:57
	 * 	12-15-2012 16:46:53
	 * 	12/15/2012 PM 04:49:45
	 * 	08/29/2012 03:07 PM
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDateFromString(String dateString) {
		dateString = dateString.replaceAll("-", "/");
		for(DateType dateType :DateType.values()) {
			if(dateString.matches(dateType.getRegex())) {
				return DateTime.parse(dateString, dateType.getFormatter()).toDate();
			}
		}
		
		log.error("Unsupported date condition reached: Not supported date string : " + dateString);
		return new Date();
	}

}
