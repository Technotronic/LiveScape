package livescape.game.layers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.joml.Vector2i;

import livescape.audio.instance.AudioSource;
import livescape.audio.util.TimestampUtil;
import livescape.game.LayerManager;
import livescape.game.ObjectManager;
import livescape.game.instances.Layer;
import livescape.game.instances.Timeline;
import livescape.game.objects.ButtonObject;
import livescape.game.objects.TrackObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIImages;
import livescape.graphics.palettes.UIPalette;

public class TimelineLayer extends Layer
{
	public final static Vector2i FRAME_PADDING = new Vector2i(8, 8);
	
	
	// Linked Objects to TimelineLayer
	private Renderer r;
	private Timeline timeline;
	private String tKey;
	
	// Layer related properties
	private Vector2i position;
	private Vector2i size;
	
	public TimelineLayer(LayerManager sceneManager) {
		super(sceneManager);
		
		// Instantiate empty object and local variable of renderer
		this.r = this.getRenderer();		
		this.position = new Vector2i();
		this.size = new Vector2i();
		this.init();
	}
	
	@Override
	public void init()
	{	
		// Calculate frame origin position and put button elements in render list
		this.position = new Vector2i(0, InterfaceLayer.FRAME_HEIGHT + ParameterLayer.FRAME_HEIGHT);
		this.createButtons();
		
		// Activate current scene in application cycle
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
			p.add(FRAME_PADDING.x, (FRAME_PADDING.y * 2) + ButtonObject.SIZE_DEFAULT.y);
			
			// Iterate over HashMap
			for(Entry<String, AudioSource> entry : sources.entrySet())
			{
				// Create new TrackObject for each loaded source
				AudioSource s = entry.getValue();
				o.create(new TrackObject("TRK_" + s.getName(), p, s));				
			}
		}
		
		// TODO: Detect if active Timeline is unloaded. If so, remove local references
	}

	@Override
	public void render()
	{		
		// Get window size and calculate timeline frame
		Vector2i windowSize = this.getSystemWindow().getDimensions();
		this.size = new Vector2i(windowSize.x, windowSize.y - (InterfaceLayer.FRAME_HEIGHT + ParameterLayer.FRAME_HEIGHT));
		
		// Timeline frame is in between window bottom and ParameterLayer height
		r.setColor(UIPalette.secondary);
		r.drawRectangle(this.position.x, this.position.y, size.x, size.y);
		
		// Draw timeline background in position (with offset for scrollbar)
		this.drawTimeline(0);
	}
	
	
	// Proxy functions for drawing the timeline object and its components
	private void drawTimeline(int offset)
	{
		// Calculate pointer of timeline object
		Vector2i p = new Vector2i(this.position);
		p.add(FRAME_PADDING.x, (FRAME_PADDING.y * 2) + ButtonObject.SIZE_DEFAULT.y);
		
		// Calculate timeline object size
		Vector2i timelineSize = new Vector2i(
			this.getSystemWindow().getWidth() - (FRAME_PADDING.x * 2),
			size.y - (FRAME_PADDING.y * 3) - ButtonObject.SIZE_DEFAULT.y
		);
		
		// Draw timeline background
		r.setColor(UIPalette.primary);
		r.drawRectangle(p.x, p.y, timelineSize.x, timelineSize.y);
		
		// Draw scrollbar background
		r.setColor(UIPalette.lines);
		r.drawRectangle(p.x + 2, p.y + timelineSize.y - 12, timelineSize.x - 4, 10);
		
		// Only draw contents if a Timeline object is loaded
		if(this.timeline != null)
		{
			// Calculate active timestamp position
			Vector2i ATPosition = new Vector2i(
				(this.position.x + this.size.x) / 2 - (92 / 2),
				this.position.y + 10
			);
			
			// Draw timeline active timestamp
			this.drawActiveTimestamp(ATPosition);
			
			// Draw position timestamps with scroll offset
			this.drawTimestamps(p, timelineSize, offset);
			
			this.drawActiveLine(p);
		}
	}

	// Proxy function for drawing active project timestamp (above timeline object)
	private void drawActiveTimestamp(Vector2i position)
	{
		// Format time in Timeline to String timestamp
		String timestamp = TimestampUtil.toTimestamp(this.timeline.getTime());
		
		// Draw timestamp from Timeline object
		r.drawText("goodbye-big", "00:", position.x, position.y, UIPalette.textMidtone);
		r.drawText("goodbye-big", timestamp, position.x + 24, position.y, UIPalette.white);
	}
	
	// Proxy function for drawing timestamps in timeline object
	private void drawTimestamps(Vector2i position, Vector2i size, int offset)
	{
		// Take frame pointer as origin and add margin
		Vector2i l = new Vector2i(position); l.add(3, 2);
		
		// Define step size of each line in seconds and calculate amount of timestamps
		int timestampCount = 0; int timestampStep = 2;
		int seconds = (int) this.timeline.getLength() / 1000;
		int blocks = (int) Math.ceil(seconds / 2) + 1;
		
		// Draw timestamp lines with scroll offset
		for(int i = 0; i < blocks; i++)
		{
			r.setColor(UIPalette.lines);
			r.drawLine(l.x - offset, l.y, l.x - offset, l.y + size.y - 4);
			
			// Also draw label with formatted timestamp
			String timestamp = String.format("%02d", timestampCount);
			r.drawText("goodbye", "00:" + timestamp, l.x - offset + 2, l.y - 1, UIPalette.textLightish);
			
			// Add to counters (which are different from step counter)
			timestampCount += timestampStep;
			l.add(50, 0);
		}
	}
	private void drawActiveLine(Vector2i position)
	{
		// Calculate what position on x-axis line is
		int x = (int) (TrackObject.TRACK_WIDTH_SCALE * this.getTimelineManager().getActive().getTime());
		position.add(x, 0);
		
		// Draw yellow line on playback position
		r.setColor(UIPalette.yellow);
		r.drawRectangle(position.x + 2, position.y + 2, 1, 129);
	}
	
	// Function to assign buttons to top of timeline frame
	private void createButtons()
	{
		// Create local reference of objects and define pointer
		ObjectManager objects = this.getObjectManager();
		Vector2i p = new Vector2i(this.position.x + FRAME_PADDING.x, this.position.y + FRAME_PADDING.y);
		
		// Create timeline play/pause button
		objects.create(new ButtonObject("BTN_PLAY",
			p, UIPalette.green, UIImages.play
		), ObjectManager.O_TYPE_IMPORTANT);
		objects.addTooltip("BTN_PLAY", "TT_PLAY", "Play/Pause Timeline");
		p.add(ButtonObject.SIZE_DEFAULT.x + 1, 0);
		
		// Create button for rewinding timeline
		objects.create(new ButtonObject("BTN_REW",
			p, UIPalette.cyan, UIImages.rewind
		), ObjectManager.O_TYPE_IMPORTANT);
		objects.addTooltip("BTN_REW", "TT_REW", "Rewind Timeline");
		p.add(ButtonObject.SIZE_DEFAULT.x + 1, 0);
		
		// Create button for enabling looping
		objects.create(new ButtonObject("BTN_LOOP",
			p, UIPalette.blue, UIImages.loop
		), ObjectManager.O_TYPE_IMPORTANT);
		objects.addTooltip("BTN_LOOP", "TT_LOOP", "Toggle Timeline Loop");
		
		// Set pointer to right side of window (minus margin and first button size)
		p.set(this.getSystemWindow().getWidth() - ButtonObject.SIZE_DEFAULT.x - 8, p.y);
		
		// Create button to add audio file
		objects.create(new ButtonObject("BTN_ADD_AUDIO",
			p, UIPalette.orange, UIImages.plus
		));
		p.sub(ButtonObject.SIZE_DEFAULT.x + 1, 0);
		
		// Create button to remove audio file
		objects.create(new ButtonObject("BTN_MIN_AUDIO",
			p, UIPalette.red, UIImages.minus
		));
	}
}
