package livescape.game;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joml.Vector2i;

import livescape.core.System;
import livescape.game.instances.RenderObject;
import livescape.game.objects.TooltipObject;
import livescape.graphics.Renderer;

public class ObjectManager extends System.Component
{	
	// Constant for types of objects
	public final static int O_TYPE_DEFAULT = 0;
	public final static int O_TYPE_IMPORTANT = 1;
	
	// HashMaps to keep track of objects
	private HashMap<String, RenderObject> objects;
	private HashMap<String, RenderObject> importantObjects;
	
	// Local reference to renderer class
	private Renderer render;
	
	// 
	
	
	public ObjectManager(System system)
	{
		super(system);
		this.objects = new LinkedHashMap<String, RenderObject>();
		this.importantObjects = new LinkedHashMap<String, RenderObject>();
		this.render = this.getRenderer();
	}
	
	// Function to send update calls to all objects
	public void tick()
	{
		// Tick order, important object first then regular types
		this.tickRelay(this.importantObjects.entrySet());
		this.tickRelay(this.objects.entrySet());
	}
	private void tickRelay(Set<Entry<String, RenderObject>> set)
	{
		for(Map.Entry<String, RenderObject> entry : set)
		{
			// Get instance of render object and send update (calculate logic, etc.)
			RenderObject object = entry.getValue();
			object.update();
		}
	}
	
	// Function to check if added elements should be displayed
	public void render()
	{
		// Render important object on top (mainly object that potentially overlap, like DropdownObject, etc.)
		this.renderRelay(this.objects.entrySet());
		this.renderRelay(this.importantObjects.entrySet());
	}
	private void renderRelay(Set<Entry<String, RenderObject>> set)
	{
		// Go through mapped object, check for interactive properties and render
		for(Map.Entry<String, RenderObject> entry : set)
		{
			// Get instance of render object
			RenderObject object = entry.getValue();
			
			// Trigger object interaction functions when found
			boolean isHover = this.getInteractionManager().isHover(object.getKey());
			boolean isLeftClick = this.getInteractionManager().isLeftClick(object.getKey());
			
			// Get cursor position for potential interaction functions
			Vector2i cursor = this.getSystemWindow().getCursorPosition();
			
			// Trigger object hover function
			if(isHover) {
				object.onHover(cursor);
			}
			
			// Trigger object left-click function and append focus
			if(isLeftClick) {
								
				// Trigger onClick function once
				if(!object.isClick())
				{
					// Get current active object (if any) and remove active state
					RenderObject activeObject = this.getSystemWindow().getActiveObject();
					if(activeObject != null)
					{
						activeObject.setFocus(false);
					}
					
					// Set focus to new object and append window active object reference
					object.setFocus(true);
					this.getSystemWindow().setActiveObject(object);
					
					// Set click state to 'true' to prevent multiple function calls
					object.setClick(true);
					object.onClick(cursor);
				}
				
				// Continuously call onDrag function for repeated action on mouse down
				object.onDrag(cursor);
			}
			
			// Reset click state when no left mouse button is down
			else {
				object.setClick(false);
			}
			
			// Render tooltip on cursor position, if object has one
			if(isHover && object.hasTooltip()) {
				object.getTooltip().render(render, cursor);
			}
			
			// Render object with provided render engine
			object.render(render);
		
			// Add button region to interaction- and local region map
			this.getInteractionManager().addRegion(object.getKey(), object.getRegion());
		}
	}
	
	// Destroy objects hashmap en remove all references
	public void destroy()
	{
		this.clear();
		this.render = null;
	}
	
	// Functions for adding and removing general object types (buttons, textboxes, etc.)
	public void create(RenderObject object, int type)
	{
		// Put object in different render order, depending on type
		switch(type)
		{
			default:
			case O_TYPE_DEFAULT:
				this.objects.put(object.getKey(), object);
				break;
				
			case O_TYPE_IMPORTANT:
				this.importantObjects.put(object.getKey(), object);
				break;
		}
	}
	public void create(RenderObject object) { this.create(object, O_TYPE_DEFAULT); }
	
	public void remove(String key, int type)
	{
		switch(type)
		{
			default:
			case O_TYPE_DEFAULT:
				this.objects.remove(key);
				break;
				
			case O_TYPE_IMPORTANT:
				this.importantObjects.remove(key);
				break;
		}
	}
	public void remove(String key) { this.remove(key, O_TYPE_DEFAULT); }
	
	public void clear()
	{
		this.objects.clear();
		this.importantObjects.clear();
	}
	
	// Functions to add deviating render object types
	public void addTooltip(String parent, String key, String text)
	{
		// Get parent object and append tooltip
		RenderObject parentObject = this.objects.get(parent);
		
		// If none found, also check important objects
		if(parentObject == null)
		{
			parentObject = this.importantObjects.get(parent);
		}
		
		TooltipObject tooltip = new TooltipObject(key, parent, text);
		parentObject.setTooltip(tooltip);
	}
}
