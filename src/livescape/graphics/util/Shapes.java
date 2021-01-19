package livescape.graphics.util;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Shapes
{
	// Constant variables for draw types
//	final static public int DRAW_POLYGON = 0;
	
//	GL_POINTS         = 0x0,
//  GL_LINES          = 0x1,
//  GL_LINE_LOOP      = 0x2,
//  GL_LINE_STRIP     = 0x3,
//  GL_TRIANGLES      = 0x4,
//  GL_TRIANGLE_STRIP = 0x5,
//  GL_TRIANGLE_FAN   = 0x6,
//  GL_QUADS          = 0x7,
//  GL_QUAD_STRIP     = 0x8,
//  GL_POLYGON        = 0x9;
	
	
	// RGB float values (1.0f = 255)
	private float r = 0;
	private float g = 0;
	private float b = 0;
	
	// Alpha color (transparency)
	private float a = 1;
	
	
	// Draw different shapes using the set parameters of the class
	public void line(Vector2f start, Vector2f end)
	{
		// Line is drawn between two Vector2f points
		glBegin(GL_LINE_LOOP);
		glVertex2f(start.x, start.y);
		glVertex2f(end.x, end.y);
		
		glEnd();
	}
	
	public void rectangle(Vector4f coords, boolean enableFill, boolean enableBlend)
	{	
		if(!enableBlend)
			glDisable(GL_BLEND);
		
		// Set drawing method to drawing a quadrangle
		int type = (enableFill) ? GL_QUADS : GL_LINE_LOOP;
		glBegin(type);
		
		// Draw square in the center of the coordinates
        glVertex2f(coords.x, coords.y);
        glVertex2f(coords.z, coords.y);
        glVertex2f(coords.z, coords.w);
        glVertex2f(coords.x, coords.w);
        
        glEnd();
        
        if(!enableBlend)
			glEnable(GL_BLEND);
	}
	public void rectangle(Vector4f coords, boolean enableBlend) { this.rectangle(coords, true, enableBlend); }
	
	// Square function is a constrained aspect ratio version of rectangle
	public void square(Vector2f position, float size, boolean enableBlend)
	{	
		Vector4f coords = new Vector4f(position.x, position.y, position.x + size, position.y - size);
		this.rectangle(coords, enableBlend);
	}
	
	public void circle(Vector2f position, float radius, int steps, boolean fill)
	{
		// Draw the circle using multiple points
		if(fill)
		{
			glBegin(GL_POLYGON);
		}
		else
		{
			glBegin(GL_LINE_LOOP);
		}
		
		// For each degree, draw a point and connect them in a polygon
		double k;
	    for(k = 0; k <= 360; k += (360 / steps))
	    {
	    	glVertex2f((float)(position.x + radius * Math.cos(Math.toRadians(k))), (float)(position.y - radius * Math.sin(Math.toRadians(k))));
	    }
	    glEnd();
	}
	public void circle(Vector2f position, float radius, int steps) { this.circle(position, radius, steps, true); }
	
	// Diamond drawing shape reuses the circle function
	public void diamond(Vector2f position, float size, boolean fill)
	{
		this.circle(position, (size / 2), 4, fill);
	}
	public void diamond(Vector2f position, float size) { this.circle(position, (size / 2), 4, true); }
	
	// Function for setting OpenGL draw color
	public void setColor(Color color)
	{
		this.r = color.getR();
		this.g = color.getG();
		this.b = color.getB();
		this.a = color.getA();
		
		// Set drawing color
		glColor4f(this.r, this.g, this.b, this.a);
	}
}
