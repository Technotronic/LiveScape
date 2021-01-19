package livescape.game.instances;

import org.joml.Vector4i;

// Class containing all base functionality for interactive objects
public class InteractionObject
{
	private String id;
	private Vector4i region;
	
	private boolean cursorHover;
	private boolean cursorClickLeft;
	private boolean cursorClickRight;
	
	public InteractionObject(String id, Vector4i region)
	{
		this.id = id;
		this.region = region;
		
		// Set default boolean states on construct
		this.cursorHover = false;
		this.cursorClickLeft = false;
		this.cursorClickRight = false;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public Vector4i getRegion()
	{
		return this.region;
	}
	
	// Set object state when interacted with cursor
	public boolean isHover() { return this.cursorHover; }
	public void setHover() { this.cursorHover = true; }
	public boolean isClickLeft() { return this.cursorClickLeft; }
	public void setClickLeft() { this.cursorClickLeft = true; }
	public boolean isClickRight() { return this.cursorClickRight; }
	public void setClickRight() { this.cursorClickRight = true; }
}
