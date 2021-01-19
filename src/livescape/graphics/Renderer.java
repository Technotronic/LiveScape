package livescape.graphics;

import java.io.FileInputStream;
import java.util.HashMap;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import livescape.core.System;
import livescape.graphics.palettes.UIImages;
import livescape.graphics.palettes.UIPalette;
import livescape.graphics.util.Color;
import livescape.graphics.util.Font;
import livescape.graphics.util.Glyph;
import livescape.graphics.util.Shapes;
import livescape.graphics.util.Texture;

public class Renderer extends System.Component
{
	// Conversion direction and origin states
	final static public int DIRECTION_HORIZONTAL = 0;
	final static public int DIRECTION_VERTICAL = 1;
	final static public int ORIGIN_NONE = 0;
	final static public int ORIGIN_LEFT_TOP = 1;
	
	final static public int INTERFACE_STYLE_DEFAULT = 0;
	
	public HashMap<String, Texture> textures;
	public HashMap<String, Font> fonts;
	
	public Shapes shapes;
	public UIImages ui;
	
	public Renderer(System system)
	{
		super(system);
	}
	
	public void init()
	{
		// Set render engine specific OpenGL parameters
		this.getSystemWindow().setClearColor(UIPalette.background);
		
		// Create caches for textures and fonts
		this.textures = new HashMap<String, Texture>();
		this.fonts = new HashMap<String, Font>();
		this.createFont("goodbye", "goodbye.ttf", 8);
		this.createFont("goodbye-big", "goodbye.ttf", 16);
		
		// Instantiate new shape drawing utility
		this.shapes = new Shapes();
		
		// Initialize palette's and pre-load textures in there
		this.ui = new UIImages(this);
		
		
		this.getLogger().write("Renderer and extended factories running...");
	}
	
	// Cache functions for fonts and images/textures
	public void createTexture(String resourcePath)
	{
		// Get texture file (.jpg/.png/.gif/.psd) from a sub-path in the ./resources folder
		try {
			Texture texture = new Texture("./resources/" + resourcePath);
			
			if(texture != null)
				this.textures.put(resourcePath, texture);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void createFont(String name, String fileName, int size)
	{
		// Get font file from resources and add to storage
		try {
			FileInputStream file = new FileInputStream("./resources/fonts/" + fileName);
			
			// Parse font into a texture through Font class
			if(file != null)
				this.fonts.put(name, new Font(file, size, new Color("#FFFFFF"), true));
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	// Functions that draw created renderable objects
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		// Convert coordinates to float values and pass to function
		Vector2f start = this.generatePosition(x1, y1);
		Vector2f end = this.generatePosition(x2, y2);
		
		this.shapes.line(start, end);
	}
	public void drawLine(Vector2i start, Vector2i end) { this.drawLine(start.x, start.y, end.x, end.y); }
	
	public void drawRectangle(int x, int y, int width, int height, boolean enableFill)
	{
		// Convert draw positions to float value
        Vector2f origin = this.generatePosition(x, y);
        Vector2f tail = this.generatePosition(x + width, y + height);
        Vector4f coords = new Vector4f(origin.x, origin.y, tail.x, tail.y);
        
		// Pass all converted values to draw function
		this.shapes.rectangle(coords, enableFill, false);
	}
	public void drawRectangle(int x, int y, int width, int height) { this.drawRectangle(x, y, width, height, true); }
	
	// Circle is always drawn from center of coordinates
	public void drawCircle(int x, int y, int radius, int steps, boolean enableFill)
	{
		// Calculate values of position
		Vector2f coords = this.generatePosition(x, y);
		float floatRadius = this.pixelToFloat(radius, DIRECTION_HORIZONTAL, ORIGIN_NONE);
		
		// Pass variables to shapes function
		this.shapes.circle(coords, floatRadius, steps, enableFill);
	}
	public void drawCircle(int x, int y, int radius) { this.drawCircle(x, y, radius, 12, true); }
	
	// TODO: Look if Texture is already stored in cache
	public void drawTexture(Texture texture, Color color, int x, int y, int width, int height)
	{
		// Convert draw positions to float value
        Vector2f position = this.generatePosition(x, y);
        
        // Calculate width and height relative to window size
        float drawWidth = this.pixelToFloat(width, DIRECTION_VERTICAL, ORIGIN_NONE);
        float drawHeight = this.pixelToFloat(height, DIRECTION_HORIZONTAL, ORIGIN_NONE);
        
		// Pass all converted values to draw function
		texture.draw(position, drawWidth, drawHeight, color);
	}
	public void drawTexture(String resourcePath, int x, int y, int width, int height) { this.drawTexture(this.getTexture(resourcePath), UIPalette.white, x, y, width, height); }
	public void drawTexture(String resourcePath, Color color, int x, int y, int width, int height) { this.drawTexture(this.getTexture(resourcePath), color, x, y, width, height); }
	
	public void drawText(Font font, CharSequence text, int x, int y, Color color, int letterSpacing, int lineHeight)
    {
		// Integers are pixel values, OpenGL float conversion is happening later
        int drawX = x;
        int drawY = y;
                
        // For each character in text, get glyph data and start drawing
        for(int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
            
            // Line feed, move to next line after this character
            if(ch == '\n')
            {
                drawY += font.getAtlasHeight() + lineHeight;
                drawX = x;
                continue;
            }
            
            // Carriage return, skip for now
            if(ch == '\r')
            {
                continue;
            }
            Glyph g = font.getGlyphs().get(ch);
            
            // Convert draw positions to float value
            Vector2f position = this.generatePosition(drawX, drawY);
                        
            // Set pixel region of texture that needs to be drawn
            Vector4f region = new Vector4f(g.x, g.y, (g.x + g.width), (g.y + g.height));
            
            // Calculate width and height relative to window size
            float width = this.pixelToFloat(g.width, DIRECTION_VERTICAL, ORIGIN_NONE);
            float height = this.pixelToFloat(g.height, DIRECTION_HORIZONTAL, ORIGIN_NONE);
            
            // Draw specified region and push
            font.getTexture().drawRegion(position, region, color, true, width, height);
            
            // Add character width to pointer and proceed looping
            drawX += g.width + letterSpacing;
        }
    }
	public void drawText(String fontName, CharSequence text, int x, int y) { this.drawText(this.getFont(fontName), text, x, y, new Color("#FFFFFF"), 0, 0); }
	public void drawText(String fontName, CharSequence text, int x, int y, Color color) { this.drawText(this.getFont(fontName), text, x, y, color, 0, 0); }
	
	// Generators for common values
	public Vector2f generatePosition(int x, int y)
	{
		Vector2f position = new Vector2f(
    		this.pixelToFloat(x, DIRECTION_VERTICAL, ORIGIN_LEFT_TOP),
    		this.pixelToFloat(y, DIRECTION_HORIZONTAL, ORIGIN_LEFT_TOP)
        );
		return position;
	}
	
	// Calculate relative float value from horizontal or vertical pixel value
	public float pixelToFloat(int value, int dir, int origin)
	{
		double pixels = value;
		double windowDirSize = (dir == DIRECTION_VERTICAL) ? (double) this.getSystemWindow().getHeight() : (double) this.getSystemWindow().getWidth();
		
		float converted = (float) (pixels / windowDirSize);
		
		// TODO: Rewrite conversion? Pixels will not always be square (when window aspect ratio is not 1:1), window draw range is always -1 to +1
		
		if(origin == ORIGIN_LEFT_TOP)
			converted = (dir == DIRECTION_VERTICAL) ? -1f + (converted * 2) : 1f - (converted * 2);
		
		//this.getLogger().write(converted);
		return converted;
	}
	
	// Get stored drawing attributes from class
	public Font getFont(String fontName)
	{
		return this.fonts.get(fontName);
	}
	
	public Texture getTexture(String resourcePath)
	{
		return this.textures.get(resourcePath);
	}
	
	public Shapes getShapes()
	{
		return (this.shapes == null) ? new Shapes() : this.shapes;
	}
	
	// Color setting functions
	public void setColor(Color color)
	{
		if(this.shapes != null)
			this.shapes.setColor(color);
	}
}
