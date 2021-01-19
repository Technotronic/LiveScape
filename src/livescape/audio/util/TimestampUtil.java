package livescape.audio.util;

import java.text.DecimalFormat;
import java.util.Calendar;

public class TimestampUtil
{
	// Function for converting Long (from source length) to String timestamp
	public static String toTimestamp(Long length)
	{
		// Create Calendar object for milliseconds conversion
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(length);
		
		// Create DecimalFormat instance with a length of 2 digits
		DecimalFormat df = new DecimalFormat("00");
		df.setMaximumIntegerDigits(2);
		
		// Format all components of the timestamp
		String minutes = df.format(cal.get(Calendar.MINUTE));
		String seconds = df.format(cal.get(Calendar.SECOND));
		String millis = df.format(cal.get(Calendar.MILLISECOND));
		
		// Assemble timestamp String
		String timestamp = minutes + ":" + seconds + "." + millis;
		
		return timestamp;
	}
	
	// Function to convert String back to Long (for TextboxObject reasons)
	public static Long toLong(String timestamp)
	{
		// Break string down in individual integers
		int minutes = Integer.valueOf(timestamp.substring(0, 2));
		int seconds = Integer.valueOf(timestamp.substring(3, 5));
		int millis = Integer.valueOf(timestamp.substring(6, 8));
		
		// Multiply all values to their weight
		int length = (minutes * 60000) + (seconds * 1000) + (millis * 10);
		
		// Make it loooong now (and return)
		return Long.valueOf(length);
	}
	
	// Function calculates difference to 'now' in milliseconds and outputs timestamp
	public static String fromMillis(Long millis)
	{
		Long d = TimestampUtil.now() - millis;
		return TimestampUtil.toTimestamp(d);
	}
	
	// Function also exists in SystemUtil, repeat for completeness
	public static Long now()
	{
		return java.lang.System.currentTimeMillis();
	}
}
