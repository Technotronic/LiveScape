package livescape.audio.instance;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class AudioListener
{
	public int id;
	
	// Position, orientation and velocity variables in 3D JOML vectors
	private Vector3f position;
	private Vector3f orientation;
	private Vector3f velocity;
	
	// TODO: Generate proper position manipulating functions
	
	public AudioListener(Vector3f position)
	{	
		this.create(position);
	}
	
	private void create(Vector3f p)
	{
		// Initialize listener instance to position
		AL10.alListener3f(AL10.AL_POSITION, p.x, p.y, p.z);
		AL10.alListener3f(AL10.AL_ORIENTATION, 0, 0, 0);
	    AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	    
	    // Assign position values to vectors
	    this.position = p;
	    this.orientation = new Vector3f(0, 0, 0);
	    this.velocity = new Vector3f(0, 0, 0);
	}
	
	public Vector3f getPosition()
	{
		return this.position;
	}
	
	public Vector3f getOrientation()
	{
		return this.orientation;
	}
	
	public Vector3f getVelocity()
	{
		return this.velocity;
	}
}
