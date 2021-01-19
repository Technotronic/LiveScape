package livescape.game.objects;

import org.joml.Vector2i;

import livescape.audio.instance.AudioSource;
import livescape.game.instances.RenderObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIImages;

public class CheckboxObject extends RenderObject
{
	// Constants for checkbox types
	public final static Vector2i SIZE_DEFAULT = new Vector2i(18, 18);
	
	public final static int C_TYPE_DEFAULT = 0;
	public final static int C_TYPE_SOURCE_ENABLED = 1;
	
	// Reference variables for checkbox state and type
	private int type;
	private AudioSource sourceRef;
	
	
	// Constuctor function for linking with AudioSource object
	public CheckboxObject(String key, Vector2i position, AudioSource sourceRef)
	{
		// Set references to passed AudioSource object
		this.type = C_TYPE_SOURCE_ENABLED;
		this.sourceRef = sourceRef;
		this.construct(key, position);
	}
	
	private void construct(String key, Vector2i position)
	{
		this.key = key;
		this.name = "Checkbox Object";
		this.position = new Vector2i(position);
		this.size = new Vector2i(SIZE_DEFAULT);
	}
	
	public void onClick(Vector2i position)
	{
		// Toggle state of boolean on click
		switch(type)
		{
			case C_TYPE_SOURCE_ENABLED:
				this.sourceRef.enabled = (this.sourceRef.enabled == true) ? false : true;
				break;
		}
	}
	
	public void update()
	{
		
	}
	
	public void render(Renderer r)
	{
		// Render texture, depending on state of boolean
		String texture = (this.getLinkedState() == true) ? UIImages.cbT : UIImages.cbF;
		r.drawTexture(texture, this.position.x, this.position.y, this.size.x, this.size.y);
	}
	
	// Get state of linked boolean
	private boolean getLinkedState()
	{
		switch(type)
		{
			case C_TYPE_SOURCE_ENABLED:
				return this.sourceRef.enabled;
		}
		return false;
	}
}
