package livescape.game;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2i;
import org.joml.Vector4i;

import livescape.core.System;
import livescape.game.instances.InteractionMap;
import livescape.game.instances.InteractionObject;
import livescape.graphics.Window;

public class InteractionManager extends System.Component
{
	private HashMap<Long, InteractionMap> readMap;
	private HashMap<Long, InteractionMap> writeMap;
	
	private Vector2i cacheCursor;
	private boolean cacheCursorLeft;
	
	// System has two maps, one that is waiting to be written and pushed forward. Another that is being read.
	// Respectively; writeMap and readMap. At the end of each cycle write is pushed to read and a new map is made for the writeMap.
	public InteractionManager(System system)
	{
		super(system);
		this.writeMap = new HashMap<Long, InteractionMap>();
		this.cacheCursor = new Vector2i();
	}
	
	public void init()
	{
		// Create new interaction map, attached to window
		this.createMap(this.getSystemWindow());
	}
	
	// Only check regions when cursor position has changed
	public void tick()
	{
		// TODO: Upgrade checking if movement has been inside current active window (within [0,0] and [MAX_W,MAX_H])
		if(this.getSystemWindow().getCursorPosition() != this.cacheCursor || this.getSystemWindow().getButtonLeft() != this.cacheCursorLeft)
		{
			// Update cursor position
			this.cacheCursor = this.getSystemWindow().getCursorPosition();
			this.cacheCursorLeft = this.getSystemWindow().getButtonLeft();
			
			// On every tick, check if cursor is in any region of an active map
			for(Map.Entry<String, InteractionObject> entry : this.getWriteMap().getObjects().entrySet())
			{
				Vector2i cursor = this.getSystemWindow().getCursorPosition();
				Vector4i region = entry.getValue().getRegion();
				
				// Check if cursor position is inside region of object
				if(cursor.x >= region.x && cursor.y >= region.y && cursor.x <= region.z && cursor.y <= region.w)
				{
					entry.getValue().setHover();
					
					// Detect if cursor button left/right is also pressed
					if(this.getSystemWindow().getButtonLeft())
						entry.getValue().setClickLeft();
				}
			}
			
			// Move constructed map to cache and prepare new map for next cycle
			this.readMap = this.writeMap;
			this.writeMap = new HashMap<Long, InteractionMap>();
			this.createMap(this.getSystemWindow());
		}
	}
	
	public void stop()
	{
		this.readMap.clear();
		this.writeMap.clear();
	}
	
	// Create a new interaction map for a specific window
	public void createMap(Window window)
	{
		InteractionMap map = new InteractionMap();
		this.writeMap.put(window.getId(), map);
	}
	
	// Functions that look for the map that is currently active
	public InteractionMap getReadMap()
	{
		// System window is now a fixed instance, can be changed into active window in future update
		Long windowId = this.getSystemWindow().getId();
		
		// Botched way of preventing from doing things too early in the loop
		return (this.readMap == null) ? null : this.readMap.get(windowId);
	}
	
	public InteractionMap getWriteMap()
	{
		// System window is now a fixed instance, can be changed into active window in future update
		Long windowId = this.getSystemWindow().getId();
		return this.writeMap.get(windowId);
	}	
	
	// General functions to check interactables
	public boolean isHover(String key)
	{
		// Get current active read map and test hover
		InteractionMap map = this.getReadMap();
		return map.isObjectHovered(key);
	}
	
	public boolean isLeftClick(String key)
	{
		// Get current active read map and test left mouse click
		InteractionMap map = this.getReadMap();
		return map.isObjectClickedLeft(key);
	}
	
	// Functions to add regions for interface objects
	public Vector4i addRegion(String key, Vector4i region)
	{
		// Get current active write map and add button region
		InteractionMap map = this.getWriteMap();
		map.add(key, region);
		
		// Return region to caller
		return region;
	}
}
