package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static final String DATE_FORMAT = "M/d/yy";

	public static Date parseDate(String dateString) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.parse(dateString);
	}

	public static String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(date);
	}

	public static int compareDates(Date date1, Date date2) {
		return date1.compareTo(date2);
	}

	public static boolean isValidDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static boolean isWithinRange(Date target, Date start, Date end) {
		return !target.before(start) && !target.after(end);
	}

	public static Date systemDate(){
		return new Date();
	}

	public static boolean isAfterEnd(Date currentEndDate, Date start){
		return !currentEndDate.before(start);
	}
}