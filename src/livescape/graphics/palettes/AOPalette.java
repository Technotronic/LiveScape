package livescape.graphics.palettes;

import java.util.HashMap;
import java.util.Map;

import livescape.graphics.util.Color;

public class AOPalette
{
	// Static color objects for AudioSource and AudioListener
	public static Color red = new Color("Red", "#EC3C59", "#FE8E95");
	public static Color orange = new Color("Orange", "#FA8132", "#FEC597");
	public static Color yellow = new Color("Yellow", "#F7B730", "#FDF5E3");
	public static Color green = new Color("Green", "#21BE6B", "#7BED9F");
	public static Color turquoise = new Color("Turquoise", "#3DC1D3", "#93E9E0");
	public static Color blue = new Color("Blue", "#2D99D9", "#92CEF7");
	public static Color darkblue = new Color("Dark Blue", "#3867D6", "#97B3F3");
	public static Color purple = new Color("Purple", "#8954D0", "#CFA8F3");
	public static Color lightgrey = new Color("Light Grey", "#A4B0BE", "#747D8C");
	public static Color darkgrey = new Color("Dark Grey", "#4B6583", "#95A6B8");
	
	// Return all available colors in current palette
	public static Map<Integer, Color> getColors()
	{
		// Create new map and list all hardcoded colors
		Map<Integer, Color> c = new HashMap<Integer, Color>();
		c.put(0, AOPalette.red);
		c.put(1, AOPalette.orange);
		c.put(2, AOPalette.yellow);
		c.put(3, AOPalette.green);
		c.put(4, AOPalette.turquoise);
		c.put(5, AOPalette.blue);
		c.put(6, AOPalette.darkblue);
		c.put(7, AOPalette.purple);
		c.put(8, AOPalette.lightgrey);
		c.put(9, AOPalette.darkgrey);
		
		return c;
	}
	
	// Find Color object by color id
	public static Color getColor(int id)
	{
		// Create map of all colors and select by given key
		Map<Integer, Color> map = AOPalette.getColors();
		return map.get(id);
	}
}
