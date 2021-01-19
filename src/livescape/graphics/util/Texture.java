package livescape.graphics.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture
{
	private int id;
	
	private int width;
	private int height;
	
	// Static final states for drawing parameters
	final static public int ORIGIN_LEFT_TOP = 0;
	final static public int ORIGIN_CENTER = 1;
	
	// Constructing functions are for filling buffers with pixel data (format of getting buffer differs)
	public Texture(String filePath) throws Exception
	{
		// Prepare buffers
		IntBuffer bufferWidth = BufferUtils.createIntBuffer(1);
		IntBuffer bufferHeight = BufferUtils.createIntBuffer(1);
		IntBuffer bufferComp = BufferUtils.createIntBuffer(1);
		
		// Create byte buffer with pixel data with STB
		ByteBuffer pixels = stbi_load(filePath, bufferWidth, bufferHeight, bufferComp, 4);
		
		if(pixels == null)
		{
			throw new Exception(stbi_failure_reason());
		}
		
		// Set width and height of texture we're generating
		this.width = bufferWidth.get();
		this.height = bufferHeight.get();
		
		// Process buffer
		this.create(pixels);
	}
	
	public Texture(int width, int height, ByteBuffer buffer)
	{
		// Set width and height and process buffer through creation process
		this.width = width;
		this.height = height;
		this.create(buffer);
	}
	
	private void create(ByteBuffer buffer)
	{
		// Generate ID for the texture
		this.id = glGenTextures();
		
		// Bind 2D texture property to object
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		// And put texture into OpenGL storage
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, this.id);
	}
	
	public void unbind()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void draw(Vector2f position, int origin, Color color, boolean enableBlend, float width, float height)
	{
		// Bind texture to quad we're drawing now
		this.bind();
		
		// Set empty background and full texture alpha
		if(!enableBlend)
			glDisable(GL_BLEND);
		
		// Start drawing a square with given coordinates and stretch texture in square
		glBegin(GL_QUADS);
		
		glColor4f(color.getR(), color.getG(), color.getB(), color.getA());
		
		Vector4f offset = new Vector4f();
		switch(origin)
		{
			// Draw with a regular bitmap origin, left top is 0
			case ORIGIN_LEFT_TOP:
				glTexCoord2f(0, 0);
				glVertex2f(position.x, position.y);
				glTexCoord2f(1, 0);
				glVertex2f(position.x + width, position.y);
				glTexCoord2f(1, 1);
				glVertex2f(position.x + width, position.y - height);
				glTexCoord2f(0, 1);
				glVertex2f(position.x, position.y - height);
				break;
		
			// Draw with an offset of half the size of the draw rectangle
			case ORIGIN_CENTER:
				offset.x = width / 2;
				offset.y = height / 2;
				offset.z = offset.x;
				offset.w = offset.y;
				
				// Draw order, left top, right top, right bottom and left bottom
				glTexCoord2f(0, 0);
				glVertex2f(position.x - offset.x, position.y + offset.y);
				glTexCoord2f(1, 0);
				glVertex2f(position.x + offset.x, position.y + offset.w);
				glTexCoord2f(1, 1);
				glVertex2f(position.x + offset.z, position.y - offset.w);
				glTexCoord2f(0, 1);
				glVertex2f(position.x - offset.z, position.y - offset.y);
				break;
		}
			
		glEnd();
		
		if(!enableBlend)
			glEnable(GL_BLEND);
		
		this.unbind();
	}
	public void draw(Vector2f position, float width, float height) { this.draw(position, ORIGIN_LEFT_TOP, new Color(255, 255, 255, 1f), true, width, height); }
	public void draw(Vector2f position, float width, float height, Color color) { this.draw(position, ORIGIN_LEFT_TOP, color, true, width, height); }
	
	// Draw a region of a texture by selecting a square that needs to be drawn
	// TODO: Make drawRegion origin parameter a separate function that is currently within draw
	private void drawRegion(float x, float y, float x1, float y1, float x2, float y2, Color c, boolean blend, float width, float height)
	{
		// Bind texture to quad we're drawing now
		this.bind();
		
		// Assign blending and set color to drawing region
		if(!blend)
			glDisable(GL_BLEND);
		
		glColor4f(c.getR(), c.getG(), c.getB(), c.getA());
		
		glBegin(GL_QUADS);
			glTexCoord2f(x1, y1);
			glVertex2f(x, y);
			glTexCoord2f(x2, y1);
			glVertex2f(x + width * 2, y);
			glTexCoord2f(x2, y2);
			glVertex2f(x + width * 2, y - height * 2);
			glTexCoord2f(x1, y2);
			glVertex2f(x, y - height * 2);
		glEnd();
		
		if(!blend)
			glEnable(GL_BLEND);
		
		this.unbind();
	}
	
	// Version of function that works with drawing a region with pixel driven values
	public void drawRegion(Vector2f position, Vector4f region, Color color, boolean enableBlend, float width, float height)
	{
		// Convert vector points in pixel values to mapped float value
		float x1 = region.x / (float) this.width;
		float y1 = region.y / (float) this.height;
		float x2 = region.z / (float) this.width;
		float y2 = region.w / (float) this.height;
		
		// Send converted values to drawing function
		this.drawRegion(position.x, position.y, x1, y1, x2, y2, color, enableBlend, width, height);
	}
	public void drawRegion(Vector2f position, Vector4f region, float width, float height) { this.drawRegion(position, region, new Color(255, 255, 255, 1f), true, width, height); }
}
