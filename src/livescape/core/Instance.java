package livescape.core;

public class Instance
{
	private static final System instance = new System();
	
	public static final System get() 
	{
		return instance;
	}
}
