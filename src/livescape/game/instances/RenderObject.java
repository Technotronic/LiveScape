package livescape.game.instances;

import org.joml.Vector2i;
import org.joml.Vector4i;

import livescape.game.objects.TooltipObject;
import livescape.graphics.Renderer;
import livescape.graphics.util.Color;

public class RenderObject
{
	protected String key;
	protected String name;
	
	protected Vector2i position = new Vector2i(0, 0);
	protected Vector2i size = new Vector2i(0, 0);
	
	protected Color color;
	
	// Interaction type variables
	protected boolean isFocus = false;
	protected boolean isClick = false;
	
	// Object child objects
	protected TooltipObject tooltip;
	
	
	// Object interaction functions (cursor hover, click and keyboard input)
	public void onHover(Vector2i position)
	{
		
	}
	
	public void onClick(Vector2i position)
	{
		
	}
	
	public void onDrag(Vector2i position)
	{
		
	}
	
	public void onInput(int characterInt)
	{
		
	}
	
	// System loop functions (tick and render triggers)
	public void update()
	{
		
	}
	
	public void render(Renderer render)
	{
		
	}
	
	
	// Getters and setters for basic object properties
	public String getKey() { return this.key; }
	
	public String getName() { return this.name; }
	
	public void setPosition(int x, int y)
	{
		this.position = new Vector2i(x, y);
	}
	public Vector2i getPosition() { return this.position; }
	
	// Functions that return button size and object region (position and size in a vector)
	public Vector2i getSize() { return this.size; }
	
	public Vector4i getRegion()
	{
		return new Vector4i(position.x, position.y, position.x + size.x, position.y + size.y);
	}
	
	// Getters and setters for render properties
	public void setColor(Color color)
	{
		this.color = color;
	}
	public Color getColor() { return this.color; }
	
	// Getters and setters for interactive properties
	public void setFocus(boolean state)
	{
		this.isFocus = state;
	}
	public boolean isFocus() { return this.isFocus; }
	
	public void setClick(boolean state)
	{
		this.isClick = state;
	}
	public boolean isClick() { return this.isClick; }
	
	// Functions for handling child objects (like tooltips, etc.)
	public void setTooltip(TooltipObject tooltip)
	{
		this.tooltip = tooltip;
	}
	public TooltipObject getTooltip() { return this.tooltip; }
	public boolean hasTooltip() { return (this.tooltip == null) ? false : true; }
}
