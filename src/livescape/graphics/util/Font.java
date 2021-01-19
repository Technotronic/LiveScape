package livescape.graphics.util;

import static java.awt.Font.*;

import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

// Modified class from SilverTiger on GitHub (https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Fonts)
public class Font
{	
	private final Map<Character, Glyph> glyphs;
    private final Texture texture;
	
    // Font atlas properties
    private int atlasWidth;
	private int atlasHeight;
	
	
    public Font(InputStream file, int size, Color color, boolean antiAlias) throws FontFormatException, IOException
    {
    	// Instantiate glyphs and create font object from TTFont file
    	glyphs = new HashMap<>();
    	java.awt.Font font = java.awt.Font.createFont(TRUETYPE_FONT, file).deriveFont(PLAIN, size);
    	
    	this.texture = this.create(font, color, antiAlias);
    }
    
    // 
    public Font(InputStream file, int size) throws FontFormatException, IOException
    {
    	// Same function as with color and anti-alias but then in default states
    	glyphs = new HashMap<>();
    	java.awt.Font font = java.awt.Font.createFont(TRUETYPE_FONT, file).deriveFont(PLAIN, size);
    	
    	// Create texture with white font and black background (to blend)
    	this.texture = this.create(font, new Color(0, 0, 0, 1f), false);
    }
    
    private Texture create(java.awt.Font font, Color color, boolean antiAlias)
    {
        int imageWidth = 0;
        int imageHeight = 0;

        // Iterate through all usable characters to get all sizes
        for(int i = 32; i < 256; i++)
        {
            // Character 127 is the DEL control char, skip this one
        	if (i == 127) {
                continue;
            }
        	
        	// Create bitmap from character
            char c = (char) i;
            BufferedImage ch = createCharImage(font, c, color, antiAlias);
            
            // If this is null then the font file does not contain this char
            if (ch == null)
            {
                continue;
            }

            imageWidth += ch.getWidth();
            imageHeight = Math.max(imageHeight, ch.getHeight());
        }

        this.atlasWidth = imageWidth;
        this.atlasHeight = imageHeight;

        // Create image for use as a OpenGL texture
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Iterate through characters again and create a glyph for each one
        int x = 0;
        for(int i = 32; i < 256; i++)
        {
            if (i == 127)
            {
                continue;
            }
            
            char c = (char) i;
            BufferedImage charImage = createCharImage(font, c, color, antiAlias);
            
            if(charImage == null)
            {
                continue;
            }

            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            // Create a glyph object for character and draw onto an image 
            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += ch.width;
            
            glyphs.put(c, ch);
        }
        
        // Get charWidth and charHeight of complete image of chars
        int width = image.getWidth();
        int height = image.getHeight();

        // Get pixel data of image
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        
        // Put data of the pixels into a ByteBuffer
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                // Process pixel data in RGBA-format
                int pixel = pixels[i * width + j];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        
        // Flip buffer, mandatory reverse reading format for OpenGL
        buffer.flip();
        
        // Finally actually create the texture
        Texture texture = new Texture(width, height, buffer);
        MemoryUtil.memFree(buffer);
                
        return texture;
    }
    
    private BufferedImage createCharImage(java.awt.Font font, char character, Color color, boolean antiAlias)
    {
        // Creating temporary image to extract character size
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Apply anti-alias parameter
        if(antiAlias)
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();

        // Get character charWidth and charHeight
        int charWidth = metrics.charWidth(character);
        int charHeight = metrics.getHeight();
        
        // Skip character if it does not exist in font
        if(charWidth == 0) 
	        return null;

        // Create a buffer with ARGB-format, now rendering it completely to an image
        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        
        if(antiAlias)
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        // Set font and color to start drawing character
        g.setFont(font);
        java.awt.Color awtColor = color.getAWTColor();
        g.setPaint(awtColor);
        
        // Draw character to graphics object
        g.drawString(String.valueOf(character), 0, metrics.getAscent());
        g.dispose();
        
        return image;
    }
    
    // Functions for calculating width and height of text
    public int getWidth(CharSequence text, int letterSpacing)
    {
        int width = 0;
        int lineWidth = 0;
        
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if(c == '\n')
            {
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }
            
            if(c == '\r')
            {
                continue;
            }
            
            Glyph g = glyphs.get(c);
            lineWidth += g.width + letterSpacing;
        }
        width = Math.max(width, lineWidth);
        
        return width;
    }
    public int getWidth(CharSequence text) { return this.getWidth(text, 0); }

    public int getHeight(CharSequence text)
    {
        int height = 0;
        int lineHeight = 0;
        
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if(c == '\n')
            {
                height += lineHeight;
                lineHeight = 0;
                continue;
            }
            
            if(c == '\r')
            {
                continue;
            }
            
            Glyph g = glyphs.get(c);
            lineHeight = Math.max(lineHeight, g.height);
        }
        height += lineHeight;
        
        return height;
    }
    
    // Font properties and sub-objects getters
    public Map<Character, Glyph> getGlyphs()
    {
    	return this.glyphs;
    }
    
    public Texture getTexture()
    {
    	return this.texture;
    }
    
    public int getAtlasWidth()
    {
    	return this.atlasWidth;
    }
    
    public int getAtlasHeight()
    {
    	return this.atlasHeight;
    }
}