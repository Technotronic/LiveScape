package livescape.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import livescape.audio.instance.AudioListener;
import livescape.audio.instance.AudioSource;
import livescape.core.util.StringUtil;

public class AudioContext
{
	private long device;
	private long context;
	
	private ALCCapabilities capabilities;
	private AudioListener listener;
	private HashMap<String, AudioSource> sources;
	
		
	public AudioContext()
	{
		// Start by acquiring the default sound device and create a handle for device capabilities
        this.device = ALC10.alcOpenDevice((ByteBuffer)null);
        this.capabilities = ALC.createCapabilities(device);
        
        // Create a fresh and empty context
        IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

        // Note the manner in which parameters are provided to OpenAL
        contextAttribList.put(ALC10.ALC_REFRESH);
        contextAttribList.put(60);

        contextAttribList.put(ALC10.ALC_SYNC);
        contextAttribList.put(ALC10.ALC_FALSE);

        contextAttribList.put(0);
        contextAttribList.flip();

        // Create context with the provided attributes
        this.context = ALC10.alcCreateContext(device, contextAttribList);
        
        // Create empty HashMap for AudioSources
        this.sources = new HashMap<String, AudioSource>();
        
        // Try so set the created context to current and instantiate in OpenAL
        try {
			if(!ALC10.alcMakeContextCurrent(context)) {
	            throw new Exception("Failed to make context current");
	        }
	    	
			// Instantiate in OpenAL library
	    	AL.createCapabilities(capabilities);
		}
		catch(Exception e) { e.printStackTrace(); }
        
        // Set default context distance model and create a listener
        this.setDistanceModel("LINEAR");
	}
	
	// Listener is a single object per context, by default on (0,0,0) when none is created
	public void createListener(Vector3f position)
	{
		// Listener object is only used to neatly store variables
		this.listener = new AudioListener(position);
	}
	public void createListener() { this.createListener(new Vector3f(0f, 0f, 3f)); }
	
	// Create new buffer for audio file
	public String createSource(String name, String filePath)
	{
		// Instantiate new AudioSource object with path to audio file
		AudioSource source = new AudioSource(name, filePath);
		this.sources.put(name, source);
		
		return name;
	}
	public String createSource(String filePath) { return this.createSource("UNK", filePath); }
	
	public void destroy()
	{
		// All done. Delete resources, and close OpenAL
        ALC10.alcSuspendContext(context);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
        
        // Clear local variables containing listeners and sources
        this.listener = null;
        this.sources.clear();
	}
	
	// Function that sets the object audibility model of the context
	public void setDistanceModel(String type)
	{
		switch(type)
		{
			default:
			case "LINEAR":
				AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE);
				break;
				
			case "LINEAR_CLAMPED":
				AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
				break;
			
			case "EXPONENT":
				AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
				break;
				
			case "EXPONENT_CLAMPED":
				AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
				break;
		}
	}
	
	public AudioListener getListener()
	{
		return this.listener;
	}
	
	public HashMap<String, AudioSource> getSources()
	{
		return this.sources;
	}
	
	public AudioSource getSource(String key)
	{
		return this.sources.get(key);
	}
}
