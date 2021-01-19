package livescape.game.instances;

import livescape.game.LayerManager;

public class Layer extends LayerManager.Component
{
	private int id;
	private String name;
	
	private boolean active;
	
	public Layer(LayerManager sceneManager)
	{
		super(sceneManager);
		this.active = false;
	}
	
	// Block of code that is called when scene is instantiated
	public void init()
	{
		
	}
	
	// Function that calculates specific logic for current scene
	public void update()
	{
		
	}
	
	// Game loop calls this function when asking for when to render
	public void render()
	{
		
	}
	
	// Clear memory and references in other classes
	public void stop()
	{
		this.disable();
	}
	
	// Handle get and set of class property
	public int getId() { return this.id; }
	public String getName() { return this.name; }
	
	public void disable() { this.active = false; }
	public void activate() { this.active = true; }
	
	public boolean isActive()
	{
		return this.active;
	}
}
