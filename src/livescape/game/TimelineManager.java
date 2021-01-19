package livescape.game;

import java.util.HashMap;
import java.util.Map.Entry;

import livescape.audio.instance.AudioSource;
import livescape.audio.util.TimestampUtil;
import livescape.core.System;
import livescape.game.instances.Timeline;

public class TimelineManager extends System.Component
{
	// Variables to store loaded Timeline objects
	private HashMap<String, Timeline> timelines;
	private String activeTimeline;
	
	// Store state of global Timeline trigger (and when started)
	private boolean keyState;
	private boolean isPlaying;
	private long started;

	public TimelineManager(System system)
	{
		super(system);
	}
	
	public void init()
	{
		this.getLogger().write("Initialising TimelineManager...");
		
		// Create empty storage
		this.timelines = new HashMap<String, Timeline>();
		
		// Load project from directory
		this.create("basics", "./projects/examples/basics/");
		
		// Set empty container for time when playing has started
		this.keyState = false;
		this.isPlaying = false;
		this.started = 0;
	}
	
	// Trigger procedural events in created Timeline objects
	public void tick()
	{
		// If spacebar is pressed, set active Timeline object to isPlaying
		if(this.getSystemWindow().isKeyPressed(32) && this.keyState == false)
		{
			// Make flip-flop mechanism for spacebar
			this.keyState = true;
			if(this.started == 0)
			{
				this.isPlaying = true;
				this.started = TimestampUtil.now();
			}
			else
			{
				this.reset();
			}
		}
		if(this.getSystemWindow().isKeyReleased(32) && this.keyState == true)
		{
			this.keyState = false;
		}
		
		// Also tick each Timeline every system tick
		for(Entry<String, Timeline> entry : this.timelines.entrySet())
		{
			entry.getValue().tick();
		}
	}
	
	public void destroy()
	{
		for(Entry<String, Timeline> entry : this.timelines.entrySet())
		{
			entry.getValue().getContext().destroy();
		}
	}
	
	// Load file and parse new Timeline object
	public void create(String key, String directory)
	{
		// TODO: Verify if path exists and if ends with '/'
		Timeline timeline = new Timeline(this);
		timeline.loadFile(key, directory);
		
		// Put newly created timeline in storage
		this.activeTimeline = key;
		this.timelines.put(key, timeline);
	}
	
	public void reset()
	{
		this.isPlaying = false;
		this.started = 0;
		
		// Stop all playback of all attached sources
		for(Entry<String, AudioSource> entry : this.get(this.activeTimeline).getSources().entrySet())
		{
			AudioSource source = entry.getValue();
			source.stop();
		}
	}
	
	// Get specific Timeline objects from manager
	public Timeline get(String key)
	{
		return this.timelines.get(key);
	}
	public Timeline getActive()
	{
		return this.timelines.get(this.activeTimeline);
	}
	public boolean isPlaying()
	{
		return this.isPlaying;
	}
	public long getStarted()
	{
		return this.started;
	}
	

	public static abstract class Component extends System.Component
	{
		private TimelineManager timelineManager;
		
		public Component(TimelineManager timelineManager)
		{
			super(timelineManager.getSystem());
			this.timelineManager = timelineManager;
		}
		
		public System getSystem()
		{
			return this.timelineManager.getSystem();
		}
		
		public TimelineManager getTimelineManager()
		{
			return this.timelineManager;
		}
	}
}
