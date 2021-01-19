package livescape.game.instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import livescape.audio.AudioContext;
import livescape.audio.instance.AudioListener;
import livescape.audio.instance.AudioSource;
import livescape.audio.util.TimestampUtil;
import livescape.core.util.JSONUtil;
import livescape.game.TimelineManager;
import livescape.graphics.palettes.AOPalette;

public class Timeline extends TimelineManager.Component
{
	private String key;
	private String directory;
	private JSONObject json;
	
	// Storage of loaded Timeline properties
	private String id;
	private long length;
	
	// Active Timeline properties
	private AudioContext context;
	private long time;
	
	
	public Timeline(TimelineManager timelineManager)
	{
		super(timelineManager);
	}
	
	public void tick()
	{
		// Loop for checking handling timeline progression
		TimelineManager t = this.getTimelineManager();
		if(t.isPlaying())
		{
			// Calculate time since start playing
			this.time = TimestampUtil.now() - t.getStarted();
			
			// Loop over each AudioSource and check if playing needs to be started
			for(Entry<String, AudioSource> entry : this.context.getSources().entrySet())
			{
				AudioSource source = entry.getValue();
				if(this.time >= source.getStart() && !source.isPlaying() && source.enabled)
				{
					source.play();
				}
				
				// Also check if playback has ended
				if(this.time >= source.getEnd() && source.isPlaying())
				{
					source.stop();
				}
			}
						
			// On/over length (end of timeline) stop all sounds and reset
			if(this.time >= this.length)
			{
				this.time = 0;
				this.getTimelineManager().reset();
			}
		}
		
		// If not playing and time has a value, pause Timeline
		else
		{
			// TODO: Make this into a 'pause' state of Timeline
			if(this.time != 0)
			{
				this.time = 0;
			}
		}
	}
	
	public void loadFile(String key, String directory)
	{
		try {
			
			// Open file from resources
			FileReader reader = new FileReader(directory + "timeline.json");
			
			// Instantiate new parser and parse file into JSONObject
		    JSONParser jsonParser = new JSONParser();
		    JSONObject json = (JSONObject) jsonParser.parse(reader);
		    
		    // Set basic instance parameters
		    this.key = key;
	 		this.directory = directory;
	 		this.json = json;
	 		
	 		// Load basic parameters from file
	 		this.id = json.get("id").toString();
	 		
	 		// Get length of Timeline as a float value
	 		this.length = Long.parseLong(json.get("length").toString());
	 		
	 		// Create new AudioContext to store sources and listeners
	 		this.context = new AudioContext();
	 		
	 		// Set timestamp of playback time to 0
	 		this.time = 0;
	 		
		 	// Iterate over listener entries
			JSONArray listeners = (JSONArray) json.get("listeners");
			Iterator<?> i = listeners.iterator();
			
			while(i.hasNext())
			{
				// Get next listener and parse position JSON array to Vector3f
				JSONObject listener = (JSONObject) i.next();
				Vector3f pListener = JSONUtil.toVector3f(listener.get("position"));
				
				// Add listener to AudioContext
				this.context.createListener(pListener);
			}
			
			// Also iterate over source entries
			JSONArray sources = (JSONArray) json.get("sources");
			Iterator<?> j = sources.iterator();
			
			while(j.hasNext())
			{
				// Get next source and parse position JSON array to Vector3f
				JSONObject source = (JSONObject) j.next();
				String sKey = source.get("name").toString();
				
				// Create absolute path to file
				String path = this.directory + "audio/" + source.get("file").toString();
				
				// Add source to AudioContext
				this.context.createSource(sKey, path);
				
				// Load settings into AudioSource
				AudioSource s = this.context.getSource(sKey);
				
				// Load basic parameters into AudioSource
				s.enabled = JSONUtil.toBool(source.get("enabled"));
				s.setGain(JSONUtil.toFloat(source.get("gain")));
				
				// Load color from AOPalette
				int colorId = JSONUtil.toInt(source.get("color"));
				s.getColor().replace(AOPalette.getColor(colorId));
				
				// Load Timeline related settings
				JSONObject sTimeline = (JSONObject) source.get("timeline");
				s.setTrack(JSONUtil.toInt(sTimeline.get("track")));
				s.setStart(JSONUtil.toLong(sTimeline.get("start")));
				
				// Load AudioContext related
				JSONObject sContext = (JSONObject) source.get("context");
				s.setPosition(JSONUtil.toVector3f(sContext.get("position")));
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	public void loadFileByName(String fileName) { this.loadFile(fileName, "./resources/timelines/" + fileName + ".json"); }
	
	// Get audio objects from linked AudioContext object
	public AudioContext getContext()
	{
		return this.context;
	}
	public AudioSource getSource(String name)
	{
		return this.context.getSource(name);
	}
	public HashMap<String, AudioSource> getSources()
	{
		return this.context.getSources();
	}
	public AudioListener getListener()
	{
		return this.context.getListener();
	}
	
	// Getters and setters of active parameters
	public long getTime()
	{
		return this.time;
	}
	public void setTime(long time)
	{
		this.time = time;
	}
	
	// Getters and setters for basic Timeline properties
	public String getKey()
	{
		return this.key;
	}
	public String getDirectory()
	{
		return this.directory;
	}
	public JSONObject getContents()
	{
		return this.json;
	}
	
	// Getters and setters of loaded parameters
	public String getId()
	{
		return this.id;
	}
	public long getLength()
	{
		return this.length;
	}
}
