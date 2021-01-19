package livescape.graphics.palettes;

import livescape.graphics.util.Color;

public class UIPalette
{
	// Main UI palette colors (for UI frames, for instance)
	public static Color head = new Color("#5A6275");
	public static Color primary = new Color("#21252E");
	public static Color secondary = new Color("#13151A");
	public static Color background = new Color("#2F3542");
	
	// Colors for input objects (for TextboxObject, DropdownObject, etc.)
	public static Color box = new Color("#FFFFFF");
	public static Color locked = UIPalette.head;
	public static Color text = new Color("#000000");
	public static Color placeholder = new Color("#848FA8");
	public static Color focus = UIPalette.placeholder;
	public static Color hover = new Color("#DDE1E7");
	
	// Colors for MatrixLayer objects (grid, labels, etc.)
	public static Color matrix = UIPalette.primary;
	
	// Colors for TimelineLayer objects (lines, timestamps, etc.)
	public static Color lines = UIPalette.background;
	
	// Colors for ParameterLayer objects (entries, text, etc.)
	public static Color entry = UIPalette.secondary;
	public static Color subentry = new Color("#1A1D24");
	
	// Sub-types of text colors (for UI frames mostly)
	public static Color textDark = UIPalette.secondary;
	public static Color textDarkish = UIPalette.head;
	public static Color textMidtone = UIPalette.background;
	public static Color textLightish = new Color("#525D74");
	public static Color textLight = UIPalette.placeholder;
	
	// Vibrant color selection (for UI elements like buttons)
	public static Color red = new Color("#AB0900");
	public static Color orange = new Color("#DE9200");
	public static Color yellow = new Color("#FFD144");
	public static Color green = new Color("#04A359");
	public static Color cyan = new Color("#00B6B4");
	public static Color blue = new Color("#3C40C6");
	public static Color grey = new Color("#848FA8");
	public static Color black = new Color("#000000");
	public static Color white = new Color("#FFFFFF");
}
