package livescape.game.objects;

import org.joml.Vector2i;

import livescape.game.instances.RenderObject;
import livescape.graphics.Renderer;
import livescape.graphics.util.Color;

public class ButtonObject extends RenderObject
{
	// Constant variables (size, etc.) for button types
	public final static int TYPE_DEFAULT = 0;
	public final static Vector2i SIZE_DEFAULT = new Vector2i(35, 17);
	public final static Vector2i TEXTURE_OFFSET_DEFAULT = new Vector2i(13, 4);
	public final static Vector2i TEXTURE_SIZE_DEFAULT = new Vector2i(18, 18);
	
	private String texturePath;
	private String text;
	
	public ButtonObject(String key, Vector2i position, Color color, String texturePath)
	{
		this.construct(key, position, color, texturePath);
	}
	
	private void construct(String key, Vector2i position, Color color, String texturePath)
	{
		this.key = key;
		this.name = "Button Object";
		
		this.position = new Vector2i(position);
		this.size = SIZE_DEFAULT;
		
		this.color = color;
		this.texturePath = texturePath;
		this.text = null;
	}
	
	// Render button object with provided render engine
	public void render(Renderer render)
	{		
		// Draw rectangle with button color and apply texture
		render.setColor(this.getColor());
		render.drawRectangle(position.x, position.y, size.x, size.y);
		render.drawTexture(this.getTexture(), this.getRegion().x + TEXTURE_OFFSET_DEFAULT.x, this.getRegion().y + TEXTURE_OFFSET_DEFAULT.y, TEXTURE_SIZE_DEFAULT.x, TEXTURE_SIZE_DEFAULT.y);
	}
	
	// Getters and setters for object specific properties
	public void setTexture(String texturePath)
	{
		this.texturePath = texturePath;
	}
	public String getTexture() { return this.texturePath; }
	
	public void setText(String text)
	{
		this.text = text;
	}
	public String getText() { return this.text; }
}
