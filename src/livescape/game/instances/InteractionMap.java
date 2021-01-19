package livescape.game.instances;

import java.util.HashMap;

import org.joml.Vector4i;

import livescape.core.util.StringUtil;

public class InteractionMap
{
	private String id;
	private boolean active;
	
	private HashMap<String, InteractionObject> objects;
	
	public InteractionMap()
	{
		this.id = StringUtil.randomString(6);
		this.active = false;
		this.objects = new HashMap<String, InteractionObject>();
	}
	
	public void add(String key, Vector4i region)
	{
		// Create new InteractionObject and put into cache
		InteractionObject object = new InteractionObject(key, region);
		this.objects.put(key, object);
	}
	
	public void tick()
	{
		
	}
	
	public void clear()
	{
		this.objects.clear();
	}
	
	// Functions for getting specific or all objects
	public HashMap<String, InteractionObject> getObjects()
	{
		return this.objects;
	}
	
	public InteractionObject getObject(String key)
	{
		return this.objects.get(key);
	}
	
	public boolean isObjectHovered(String key)
	{
		if(this.objects.get(key) != null)
		{
			return this.objects.get(key).isHover();
		}
		return false;
	}
	
	public boolean isObjectClickedLeft(String key)
	{
		if(this.objects.get(key) != null)
		{
			return this.objects.get(key).isClickLeft();
		}
		return false;
	}
}
