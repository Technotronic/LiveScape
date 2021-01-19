package livescape.audio.instance;

import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import livescape.audio.util.AudioBuffer;
import livescape.audio.util.TimestampUtil;
import livescape.graphics.palettes.AOPalette;
import livescape.graphics.util.Color;

public class AudioSource
{
	// Global AudioSource properties
	final static public float REFERENCE_DISTANCE = 1f;
	final static public float ROLLOFF_FACTOR = 1f;
	final static public float MAX_DISTANCE = 2f;
	final static public float VELOCITY_FACTOR = 2500f;
	
	private int id;
	private String name;
	public boolean enabled;
	
	// File related variables
	private String path;
	private IntBuffer buffer;
	private long length;
	private String lengthTimestamp;
	
	// Position, orientation and velocity variables in 3D JOML vectors
	public Vector3f position;
	public Vector3f orientation;
	public Vector3f velocity;
	
	public float pitch;
	public float gain;
	public boolean looping;
	
	// Source properties from Timeline object
	public Color color;
	
	// Timeline related properties
	private int track;
	private long start;
	
	private boolean isPlaying = false;
	
	
	public AudioSource(String name, String filePath)
	{
		this.create(name, filePath);
	}
	
	// Create new buffer for an audio file
	private void create(String name, String filePath)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
        AL10.alGenBuffers(buffer);
        
        long length = 0;
        try {
			length = AudioBuffer.createBufferData(buffer.get(0), filePath);
		} catch (Exception e) { e.printStackTrace(); }
        
        // Set default positioning parameters
        this.position = new Vector3f(0f, 0f, 0f);
        
        // Define source in initialized state
        int source = AL10.alGenSources();
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer.get(0));
        AL10.alSource3f(source, AL10.AL_POSITION, this.position.x, this.position.y, this.position.z);
        AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f);
        
        // Audio source sub-parameters
        AL10.alSourcef(source, AL10.AL_PITCH, 1f);
        AL10.alSourcef(source, AL10.AL_GAIN, 1f);
        AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);
        
        // Position current audio source on an relative distance to listener
        AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_FALSE);
        
        // Set distance parameters (only enabled on relative distance)
        AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, REFERENCE_DISTANCE);
        AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, ROLLOFF_FACTOR);
        AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, MAX_DISTANCE);
        
        // Set basic Timeline properties
        this.color = new Color(AOPalette.yellow);
        
        // Calculate length of source
        this.lengthTimestamp = TimestampUtil.toTimestamp(length);
        
        // Apply properties to instance
        this.id = source;
        this.name = name;
        this.enabled = true;
        this.path = filePath;
        this.buffer = buffer;
        this.length = length;
	}
	
	// Commands for playback related functionality
	public void play(boolean blocking)
	{
		// Set state of playback
		this.isPlaying = true;
		
		AL10.alSourcePlay(this.id);
        if(blocking)
        {
	        try {
	            Thread.sleep(length); // Wait for the sound to finish
	        } catch(InterruptedException ex) {}
	        
	        // Command source to stop playing loaded buffer
	        AL10.alSourceStop(this.id);
        }
	}
	public void play() { this.play(false); }
	
	public void pause()
	{
		this.isPlaying = false;
		AL10.alSourcePause(this.id);
	}
	public void stop()
	{
		this.isPlaying = false;
		AL10.alSourceStop(this.id);
	}
	public void destroy()
	{
		this.isPlaying = false;
		AL10.alDeleteSources(this.id);
		AL10.alDeleteBuffers(this.buffer);
	}
	
	// Getters and setters for instance key properties
	public String getName()
	{
		return this.name;
	}
	public void setName(String string)
	{
		this.name = string;
	}
	
	// Getters and setters for file properties
	public String getPath()
	{
		return this.path;
	}
	public String getFilename()
	{
		String[] bits = this.path.split("/");
		return bits[bits.length - 1];
	}
	public long getLength()
	{
		return this.length;
	}
	public String getLengthTimestamp()
	{
		return this.lengthTimestamp;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	// Getters and setters for Timeline related properties
	public int getTrack()
	{
		return this.track;
	}
	public void setTrack(int track)
	{
		this.track = track;
	}
	public long getStart()
	{
		return this.start;
	}
	public void setStart(long start)
	{
		this.start = start;
	}
	public long getEnd()
	{
		return this.start + this.length;
	}
	
	public boolean isPlaying()
	{
		return this.isPlaying;
	}
	
	// Getters and setters for source related properties
	public void setLooping(boolean state)
	{
		int type = (state) ? AL10.AL_TRUE : AL10.AL_FALSE;
		AL10.alSourcei(this.id, AL10.AL_LOOPING, type);
	}
	public void setGain(float value)
	{
		this.gain = value;
		AL10.alSourcef(this.id, AL10.AL_GAIN, value);
	}
	
	// Functions that get or set the current instance positioning parameters
	public Vector3f getPosition()
	{
		return this.position;
	}
	public void setPosition(Vector3f position, boolean doAddition)
	{
		// If change is an addition, calculate velocity first
		if(doAddition)
		{
			// Multiply change in movement to VELOCITY_FACTOR
			Vector3f movement = new Vector3f();
			position.mul(VELOCITY_FACTOR, movement);
			
			// Apply new velocity to current object
			this.setVelocity(movement);
		}
		else
		{
			// If not an addition (mouse click), reset velocity
			this.setVelocity(new Vector3f(0, 0, 0));
		}
		
		// Now perform position change
		this.position = (doAddition) ? this.position.add(position) : position;
		AL10.alSource3f(this.id, AL10.AL_POSITION, this.position.x, this.position.y, this.position.z);
	}
	public void setPosition(Vector3f position) { this.setPosition(position, false); }
	public void setPositionRelative(boolean isTrue)
	{
		int type = (isTrue) ? AL10.AL_TRUE : AL10.AL_FALSE;
		AL10.alSourcei(this.id, AL10.AL_SOURCE_RELATIVE, type);
	}
	
	// Variation on set position, but adding given position to current value
	public void movePosition(Vector3f position)
	{
		this.setPosition(position, true);
	}
	
	public void setOrientation(Vector3f orientation)
	{
		this.orientation = orientation;
		AL10.alSource3f(this.id, AL10.AL_ORIENTATION, this.orientation.x, this.orientation.y, this.orientation.z);
	}
	
	public void setVelocity(Vector3f velocity)
	{
		this.velocity = velocity;
		AL10.alSource3f(this.id, AL10.AL_VELOCITY, this.velocity.x, this.velocity.y, this.velocity.z);
	}
}
