package livescape.game.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;

import livescape.audio.instance.AudioListener;
import livescape.audio.instance.AudioSource;
import livescape.game.LayerManager;
import livescape.game.instances.RenderObject;
import livescape.game.layers.ParameterLayer;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.AOPalette;
import livescape.graphics.palettes.UIImages;
import livescape.graphics.palettes.UIPalette;
import livescape.graphics.util.Color;

public class MatrixObject extends RenderObject
{
	public final static Vector2i SIZE_DEFAULT = new Vector2i(18, 18);
	public final static int ORIGIN_RADIUS = 120;
	public final static Vector2i ORIGIN_POSITION = new Vector2i(271, 284);
	
	public final static int TYPE_SOURCE = 0;
	public final static int TYPE_LISTENER = 1;
	public final static int DRAW_TYPE_CARTESIAN = 0;
	public final static int DRAW_TYPE_RADIAL = 1;
	
	// Origin position of timeline background and drawtype
	private Vector2i origin;
	private int type;
	private int drawType;
		
	// Linked objects
	private Renderer r;
	private AudioSource s;
	private AudioListener l;
	
	
	public MatrixObject(String key, Vector2i origin, AudioSource source)
	{
		this.s = source;
		this.type = TYPE_SOURCE;
		this.init(key, origin);
	}
	public MatrixObject(String key, Vector2i origin, AudioListener listener)
	{
		this.l = listener;
		this.type = TYPE_LISTENER;
		this.init(key, origin);
	}
	
	// On initialization, calculate initial position from AudioSource data
	public void init(String key, Vector2i origin)
	{
		this.key = key;
		this.size = new Vector2i(SIZE_DEFAULT);
		
		// Set origin point of parent MatrixLayer
		this.origin = origin;
		
		// Set drawtype of map and update position
		this.drawType = DRAW_TYPE_RADIAL;
		this.updatePosition();
	}
		
	public void onClick(Vector2i position)
	{
		// Oof, very hacky way to get to the ParameterLayer (TODO: DEF-O REWRITE)
		if(this.type == TYPE_SOURCE && this.r != null)
		{
			ParameterLayer pLayer = (ParameterLayer) r.getLayerManager().getLayer(LayerManager.KEY_PARAMETER_LAYER);
			pLayer.loadAudioSource(this.s);
		}
	}
	
	public void update()
	{
		this.updatePosition();
	}
	
	public void render(Renderer render)
	{
		// Create render pointer
		this.r = render;
		Vector2i p = new Vector2i(this.position);
		
		// Set color based on state of source playback
		switch(this.type)
		{
			// Draw a source object
			case TYPE_SOURCE:
				Color c = (this.s.isPlaying()) ? UIPalette.white : UIPalette.textLight;
				
				// Render square texture and object name in text
				render.drawTexture(UIImages.objM, this.s.getColor(), p.x, p.y, 18, 18);
				render.drawText("goodbye", this.s.getName(), p.x + 10, p.y + 10, c);
				break;
				
			case TYPE_LISTENER:
				render.drawTexture(UIImages.objM, UIPalette.textLightish, p.x, p.y, 18, 18);
				render.drawText("goodbye", "Listener-" + this.l.id, p.x + 10, p.y + 10, UIPalette.textLight);
				break;
		}
	}
	
	
	// Helper function for calculating position of TrackObject
	private void updatePosition()
	{
		// Get position from object, depending on type
		Vector3f pN = (this.type == TYPE_SOURCE) ? this.s.getPosition() : this.l.getPosition();
		
		// Do calculations depending on draw type
		switch(this.drawType)
		{
			case DRAW_TYPE_CARTESIAN:
				int x = (int) (pN.x * ORIGIN_RADIUS);
				int y = (int) (pN.y * ORIGIN_RADIUS);
				this.position = new Vector2i(ORIGIN_POSITION.x + x, ORIGIN_POSITION.y + y);
				this.position.add(this.origin);
				break;
				
			// TODO: Math does not line up yet, coordinates are cartesian but matrix is radial. FIX NEEDED
			case DRAW_TYPE_RADIAL:
				
				// Shorten reference to position
				Vector3f p = pN;
				
				// Do some Pythagoras to get coordinates angle and radius (TODO: And this is wrong, I know)
				double angleRadial = Math.atan2(p.y, p.x);
				double radius = Math.sqrt((Math.pow(p.x * ORIGIN_RADIUS, 2)) + (Math.pow(p.y * ORIGIN_RADIUS, 2)));
				
				// Calculate x- and y-points from angle and radius
				Vector2i pRadial = new Vector2i(
					(int) (radius * Math.cos(-angleRadial)),
					(int) (radius * Math.sin(-angleRadial))
				);
				
				// Add centre of map and offset from layer origin position
				pRadial.add(ORIGIN_POSITION.x, ORIGIN_POSITION.y);
				pRadial.add(this.origin);
				
				this.position = pRadial;
				break;
		}
	}
}
