package livescape.game.objects;

import org.joml.Vector2i;

import livescape.audio.instance.AudioSource;
import livescape.game.LayerManager;
import livescape.game.instances.RenderObject;
import livescape.game.layers.ParameterLayer;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIImages;
import livescape.graphics.palettes.UIPalette;

public class TrackObject extends RenderObject
{
	public final static double TRACK_WIDTH_SCALE = 0.025;
	public final static int TRACK_HEIGHT = 12;
	public final static Vector2i OFFSET_BOX = new Vector2i(1, 1);
	public final static Vector2i OFFSET_BOX_TXT = new Vector2i(11, 1);
	
	// Origin position of timeline background
	private Vector2i origin;
	
	// Linked AudioSource object
	private Renderer r;
	private AudioSource s;
	
	public TrackObject(String key, Vector2i origin, AudioSource source)
	{
		this.origin = origin;
		this.s = source;
		this.init(key);
	}
	
	// On initialization, calculate initial position from AudioSource data
	public void init(String key)
	{
		this.key = key;
		this.updatePosition();
	}
	
	public void onClick(Vector2i position)
	{
		// Oof, very hacky way to get to the ParameterLayer (TODO: DEF-O REWRITE)
		if(this.r != null)
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
		// Create local reference to Renderer
		this.r = render;
		
		// If active, draw white border
		if(this.isFocus())
		{
			render.setColor(UIPalette.white);
			render.drawRectangle(this.position.x, this.position.y, this.size.x + (OFFSET_BOX.y * 2), this.size.y);
		}
		
		// Create render pointer
		Vector2i p = new Vector2i(this.position);
		p.add(OFFSET_BOX);
		
		// Draw background of box in AudioSource color
		render.setColor(this.s.getColor());
		render.drawRectangle(p.x, p.y, this.size.x, this.size.y - (OFFSET_BOX.y * 2));
		
		// Draw texture of box handle
		render.drawTexture(UIImages.handle, this.s.getColor().getSecondary(), p.x, p.y, 18, 18);
		
		render.drawText("goodbye", this.s.getName(), p.x + OFFSET_BOX_TXT.x, p.y + OFFSET_BOX_TXT.y);
	}
	
	
	// Helper function for calculating position of TrackObject
	private void updatePosition()
	{
		// Calculate position depending on track number and start (scale is 25px per 1000ms)
		int x = (int) (this.s.getStart() * TRACK_WIDTH_SCALE) + 1;
		int y = (this.s.getTrack() * TRACK_HEIGHT) + TRACK_HEIGHT;
		this.position = new Vector2i(x, y);
		
		// Add origin of timeline background and its margin
		this.position.add(this.origin);
		
		// Also calculate size with same scale
		// TODO: Create minimum size, no matter how long sample is?
		int width = (int) (this.s.getLength() * TRACK_WIDTH_SCALE);
		this.size = new Vector2i(width, TRACK_HEIGHT);
	}
}
