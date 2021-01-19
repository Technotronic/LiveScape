package livescape.core.util;

import java.util.Date;
import java.util.Iterator;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

public class StringUtil
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyy");
	private static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	// Secure random string generation
	private static final String RANDOM_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static SecureRandom random = new SecureRandom();
	
	// Wildcard matcher
	private static final char WILDCARD = '*';
	private static final String WILDCARD_REGEX = "\\" + WILDCARD;
	
	public static String formatDate(long millis)
	{
		return formatDate(new Date(millis));
	}

	public static String formatDate(Date date)
	{
		return DATE_FORMAT.format(date);
	}

	public static String getCurrentDate()
	{
		return formatDate(new Date());
	}

	public static String formatDateTime(long millis)
	{
		return formatDateTime(new Date(millis));
	}

	public static String getCurrentDateTime()
	{
		return formatDateTime(new Date());
	}

	public static String formatDateTime(Date date)
	{
		return DATETIME_FORMATTER.format(date);
	}

	public static int parseInt(Object obj)
	{
		return parseInt(obj.toString());
	}

	public static int parseInt(String s)
	{
		if(s != null && !s.isEmpty())
		{
			try
			{
				return Integer.parseInt(s);
			}
			catch(Exception ex)
			{

			}
		}
		return 0;
	}

	public static float parseFloat(String s)
	{
		if(s != null && !s.isEmpty())
		{
			try
			{
				return Float.parseFloat(s);
			}
			catch(Exception ex)
			{

			}
		}
		return 0.0f;
	}
	
	public static boolean parseBool(String s)
	{
		if(s != null && !s.isEmpty())
		{
			try
			{
				return Boolean.parseBoolean(s);
			}
			catch(Exception ex)
			{

			}
		}
		return false;
	}

	public static long parseDateTime(String s)
	{
		try
		{
			return DATETIME_FORMATTER.parse(s).getTime();
		}
		catch(Exception ex)
		{
			return 0;
		}
	}
	
	public static String randomString(int l)
	{
		StringBuilder sb = new StringBuilder(l);
		for(int i = 0; i < l; i++) 
			sb.append(RANDOM_CHARS.charAt(random.nextInt(RANDOM_CHARS.length())));
		
		return sb.toString();
	}

	public static String join(Iterable<String> items, String delimiter)
	{
		return null;
	}

	public static String join(String delim, Iterable<String> items)
	{
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = items.iterator();
		while(it.hasNext())
		{
			sb.append(it.next());
			if(it.hasNext())
			{
				sb.append(delim);
			}
		}
		return sb.toString();
	}
	
    
    public static String[] getWildCards(String pattern)
    {
    	// This array can be reused!
    	return pattern.split(WILDCARD_REGEX);
    }
    
    public static boolean matchesWildCardPattern(String text, String pattern)
    {
    	// Pattern has no wildcards? Use simple String comparison!
    	if(pattern.indexOf(WILDCARD) == -1)
    	{
    		return text.equals(pattern);
    	}
    	else
    	{
    		return matchesWildCards(text, getWildCards(pattern));
    	}
    }
    
    public static boolean matchesWildCards(String text, String[] cards)
    {
    	// Single card? Exact match
    	if(cards.length == 1)
    	{
    		return text.equals(cards[0]);
    	}
    	
        // Go through all the cards
    	int offset = 0;
        for (String card : cards)
        {
            // Card not detected in the text?
            int index = text.indexOf(card, offset);
            if(index == -1)
            {
                return false;
            }
            else
            {
            	 // Move ahead, towards the right of the text
            	offset += card.length();
            }
        }
        
        // Matches!
        return true;
    }
}
