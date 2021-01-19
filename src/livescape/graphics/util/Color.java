package livescape.graphics.util;

public class Color
{	
	private String name;
	private float r, g, b, a;
	
	// Possibility to append secondary color to primary
	private Color secondary;
	
	
	// Construct normal Color object
	public Color(Color color)
	{
		this.replace(color);
	}
	public Color(String hex)
	{
		this.name = "";
		this.setHexColor(hex);
	}
	public Color(int r, int g, int b, float a)
	{
		this.name = "";
		this.setRGBAColor(r, g, b, a);
	}
	
	// Constructs for name, primary and secondary Color objects
	public Color(String name, String hex)
	{
		this.name = name;
		this.setHexColor(hex);
	}
	public Color(String name, String primary, String secondary)
	{
		this.name = name;
		
		// Initialise primary color and append secondary
		this.setHexColor(primary);
		this.setSecondary(new Color(secondary));
	}
	
	
	// Function to replace this color object with new Color
	public void replace(Color c)
	{
		this.name = c.getName();
		this.r = c.getR();
		this.g = c.getG();
		this.b = c.getB();
		this.a = c.getA();
		
		this.setSecondary(c.getSecondary());
	}
	
	// Color picker functions in different color formats
	private void setRGBAColor(int r, int g, int b, float a)
	{
		// Function auto-checks valid value and sets appropriate float
		this.r  = this.toRGBFloat(r);
		this.g  = this.toRGBFloat(g);
		this.b  = this.toRGBFloat(b);
		this.a 	= a;
	}
	private void setHexColor(String hex)
	{
		// Parse hex value to numerical values
		int rTemp, gTemp, bTemp;
		rTemp = Integer.valueOf(hex.substring(1, 3), 16);
        gTemp = Integer.valueOf(hex.substring(3, 5), 16);
        bTemp = Integer.valueOf(hex.substring(5, 7), 16);
        
        // Push values through existing RGB converter
        this.setRGBAColor(rTemp, gTemp, bTemp, 1f);
	}
	
	// Helper function for deciding whether parameters are in range
	private boolean inHexRange(int value)
	{
		if(value < 0 || value > 255)
			return false;
		
		return true;
	}
	
	// Helper function for converting RGB value to float
	private float toRGBFloat(int value)
	{
		if(!this.inHexRange(value))
			return 0;
		
		// Divide value through RGB max value and return
		float valueTemp = value / 255.0f;
		return valueTemp;
	}
	
	// Helper for getting the Color name
	public String getName()
	{
		return this.name;
	}
	
	// Helper functions for getting and setting secondary color
	private void setSecondary(Color color)
	{
		this.secondary = color;
	}
	public Color getSecondary()
	{
		return this.secondary;
	}
	
	// Get functions for different float variables
	public float getR()
	{
		return this.r;
	}
	public float getG()
	{
		return this.g;
	}
	public float getB()
	{
		return this.b;
	}
	public float getA()
	{
		return this.a;
	}
	
	public java.awt.Color getAWTColor()
	{
		return new java.awt.Color(this.r, this.g, this.b);
	}
}
