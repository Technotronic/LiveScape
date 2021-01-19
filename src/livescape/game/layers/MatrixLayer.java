package livescape.game.layers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.joml.Vector2i;

import livescape.audio.instance.AudioListener;
import livescape.audio.instance.AudioSource;
import livescape.game.LayerManager;
import livescape.game.ObjectManager;
import livescape.game.instances.Layer;
import livescape.game.instances.Timeline;
import livescape.game.objects.MatrixObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIPalette;

public class MatrixLayer extends Layer
{
	// Constant values for layer objects
	public final static int MATRIX_LABEL_WIDTH = 16 + 1;
	
	// Linked Timeline objects
	private Timeline timeline;
	private String tKey;
	
	// Variables for layer position and size
	private Vector2i position;
	private Vector2i size;
	
	public MatrixLayer(LayerManager sceneManager)
	{
		super(sceneManager);
		this.init();
	}
	
	@Override
	public void init()
	{	
		// Calculate position and size of layer
		this.timeline = null;
		this.position = new Vector2i(0, InterfaceLayer.FRAME_HEIGHT);
		this.size = new Vector2i(this.getSystemWindow().getWidth() - ParameterLayer.FRAME_WIDTH, ParameterLayer.FRAME_HEIGHT);
		
		// Allow frame to start rendering
		this.activate();
	}
	
	@Override
	public void update()
	{
		// New active Timeline found, load object
		Timeline timeline = this.getTimelineManager().getActive();
		if(this.tKey == null && timeline != null)
		{
			this.timeline = timeline;
			this.tKey = timeline.getKey();
			
			// Get all loaded AudioSource and AudioListener
			ObjectManager o = this.getObjectManager();
			HashMap<String, AudioSource> sources = timeline.getSources();
			
			// Calculate timeline background origin
			Vector2i p = new Vector2i(this.position);
			
			// Iterate over HashMap
			for(Entry<String, AudioSource> entry : sources.entrySet())
			{
				// Create new TrackObject for each loaded source
				AudioSource s = entry.getValue();
				o.create(new MatrixObject("MAT_" + s.getName(), p, s));				
			}
			
			// Also create listener
			AudioListener l = timeline.getListener();
			o.create(new MatrixObject("MAT_LISTENER_" + l.id, p, l));
		}
	}
	
	@Override
	public void render()
	{
		// Draw background of matrix
		this.renderBackground();
		
	}
	
	// Proxy function to render matrix background on position
	private void renderBackground()
	{
		// Create local reference to render engine
		Renderer r = this.getRenderer();
		
		// Draw background octagon shape of virtual room
		r.setColor(UIPalette.matrix);
		r.drawCircle(275, 315, 500, 24, false);
		r.drawCircle(275, 315, 502, 24, false);
		r.drawCircle(275, 315, 375, 24, false);
		r.drawCircle(275, 315, 377, 24, false);
		r.drawCircle(275, 315, 250, 24, false);
		r.drawCircle(275, 315, 252, 24, false);
		
		// Draw x- and y-axis
		r.drawRectangle(24, 314, 500, 2);
		r.drawRectangle(274, 66, 2, 500);
		
		// Draw diagonal lines
		r.drawLine(99, 139, 451, 491);
		r.drawLine(99, 491, 451, 139);
		
		// Draw matrix label backgrounds (x-axis)
		r.setColor(UIPalette.background);
		r.drawRectangle(29, 310, MATRIX_LABEL_WIDTH, 7);
		r.drawRectangle(154, 310, MATRIX_LABEL_WIDTH, 7);
		r.drawRectangle(396 - MATRIX_LABEL_WIDTH, 310, MATRIX_LABEL_WIDTH, 7);
		r.drawRectangle(521 - MATRIX_LABEL_WIDTH, 310, MATRIX_LABEL_WIDTH, 7);
		
		// Draw label texts (x-axis)
		r.drawText("goodbye", "-2.0", 30, 310, UIPalette.textLightish);
		r.drawText("goodbye", "-1.0", 155, 310, UIPalette.textLightish);
		r.drawText("goodbye", "+1.0", 397 - MATRIX_LABEL_WIDTH, 310, UIPalette.textLightish);
		r.drawText("goodbye", "+2.0", 522 - MATRIX_LABEL_WIDTH, 310, UIPalette.textLightish);
		
		// Repeat drawing for y-axis
		r.setColor(UIPalette.background);
		r.drawRectangle(266, 69, MATRIX_LABEL_WIDTH, 7);
		r.drawRectangle(266, 194, MATRIX_LABEL_WIDTH, 7);
		r.drawRectangle(266, 429, MATRIX_LABEL_WIDTH, 7);
		r.drawRectangle(266, 554, MATRIX_LABEL_WIDTH, 7);
		
		// And draw label texts (y-axis)
		r.drawText("goodbye", "+2.0", 266, 69, UIPalette.textLightish);
		r.drawText("goodbye", "+1.0", 266, 194, UIPalette.textLightish);
		r.drawText("goodbye", "-1.0", 266, 429, UIPalette.textLightish);
		r.drawText("goodbye", "-2.0", 266, 554, UIPalette.textLightish);
	}
}
