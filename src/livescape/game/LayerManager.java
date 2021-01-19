package livescape.game;

import java.util.HashMap;
import java.util.Map;

import livescape.core.System;
import livescape.core.util.StringUtil;
import livescape.game.instances.Layer;
import livescape.game.layers.InterfaceLayer;
import livescape.game.layers.MatrixLayer;
import livescape.game.layers.ParameterLayer;
import livescape.game.layers.TimelineLayer;

public class LayerManager extends System.Component
{
	public final static String KEY_PARAMETER_LAYER = "LAYER_PARAMS";
	
	private HashMap<String, Layer> layers;
	
	
	public LayerManager(System system)
	{
		super(system);
	}

	// Initialize first layers (i.e. menu or pre-loading state)
	public void init()
	{
		// Start LayerManager and add default layers to buffer
		this.layers = new HashMap<String, Layer>();
		this.add(new InterfaceLayer(this));
		
		// ParameterLayer has functions that can be called, assign key
		this.add(KEY_PARAMETER_LAYER, new ParameterLayer(this));
		
		this.add(new TimelineLayer(this));
		this.add(new MatrixLayer(this));
	}
	
	// Adding and removal of layers in manager
	// TODO: Create draworder?
	public void add(Layer layer)
	{
		this.layers.put(StringUtil.randomString(6), layer);
	}
	public void add(String key, Layer layer)
	{
		this.layers.put(key, layer);
	}
	
	public void remove(String key)
	{
		if(this.getLayer(key).isActive())
			this.getLayer(key).stop();
		
		this.layers.remove(key);
	}
	
	// Logic update tick when requested by application cycle
	public void tick()
	{
		for(Map.Entry<String, Layer> entry : this.layers.entrySet())
		{
		    if(entry.getValue() != null && entry.getValue().isActive())
			{
				entry.getValue().update();
			}
		}
	}
	
	// Render information from scene when requested by application cycle
	public void render()
	{
		for(Map.Entry<String, Layer> entry : this.layers.entrySet())
		{
		    if(entry.getValue() != null && entry.getValue().isActive())
			{
				entry.getValue().render();
			}
		}
	}
	
	// Clear scene when application is shutting down
	public void stop()
	{
		for(Map.Entry<String, Layer> entry : this.layers.entrySet())
		{
		    if(entry.getValue() != null && entry.getValue().isActive())
			{
				entry.getValue().stop();
			}
		}
	}
	
	// Getters and property setters for layers
	public Layer getLayer(String key)
	{
		return this.layers.get(key);
	}
	
	public static abstract class Component extends System.Component
	{
		private LayerManager sceneManager;
		
		public Component(LayerManager sceneManager)
		{
			super(sceneManager.getSystem());
			this.sceneManager = sceneManager;
		}
		
		public System getSystem()
		{
			return this.sceneManager.getSystem();
		}
		
		public LayerManager getSceneManager()
		{
			return this.sceneManager;
		}
	}
}
