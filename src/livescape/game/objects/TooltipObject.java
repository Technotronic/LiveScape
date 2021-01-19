package livescape.game.objects;

import org.joml.Vector2i;

import livescape.game.instances.RenderObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIPalette;

public class TooltipObject extends RenderObject
{
	// Constant variables (size, etc.) for button types
	public final static int HEIGHT = 13;
	public final static Vector2i CURSOR_OFFSET = new Vector2i(8, 18);
	public final static Vector2i TEXT_OFFSET = new Vector2i(3, 3);
	
	private String parent;
	private String text;
	
	public TooltipObject(String key, String parent, String text)
	{
		this.key = key;
		this.parent = parent;
		this.color = UIPalette.textDark;
		this.text = text;
	}
	
	// Render button object with provided render engine
	public void render(Renderer render, Vector2i position)
	{		
		// Calculate text width for background box
		int width = render.getFont("goodbye").getWidth(text);
		
		// Create origin to start draw from
		Vector2i pointer = new Vector2i(position.x + CURSOR_OFFSET.x, position.y + CURSOR_OFFSET.y);
		
		// Draw background and text
		render.setColor(this.getColor());
		render.drawRectangle(pointer.x, pointer.y, width + 5, HEIGHT);
		render.drawText("goodbye", text, pointer.x + TEXT_OFFSET.x, pointer.y + TEXT_OFFSET.y);
	}
	
	
	// Getters and setters for object properties
	public String getParent() { return this.parent; }
	
	public void setText(String text)
	{
		this.text = text;
	}
	public String getText() { return this.text; }
}
