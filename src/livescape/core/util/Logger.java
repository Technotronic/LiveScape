package livescape.core.util;

import java.sql.Timestamp;

public class Logger
{
	
	public void write(String s)
	{
		System.out.println(this.t() + " - " + s);
	}

	public void write(int i)
	{
		System.out.println(i);
	}
	
	public void write(float f)
	{
		System.out.println(f);
	}

	public void write(double d)
	{
		System.out.println(d);
	}
	
	// Write without new line
	public void print(String s)
	{
		System.out.print(s);
	}
	
	// Helper function for generating timestamp
	private String t()
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.toString();
	}
}
