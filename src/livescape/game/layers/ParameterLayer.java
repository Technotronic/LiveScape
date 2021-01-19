package livescape.game.layers;

import org.joml.Vector2i;

import livescape.audio.AudioContext;
import livescape.audio.instance.AudioSource;
import livescape.game.LayerManager;
import livescape.game.ObjectManager;
import livescape.game.instances.Layer;
import livescape.game.instances.Timeline;
import livescape.game.objects.CheckboxObject;
import livescape.game.objects.DropdownObject;
import livescape.game.objects.EntryObject;
import livescape.game.objects.TextboxObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.AOPalette;
import livescape.graphics.palettes.UIPalette;

public class ParameterLayer extends Layer
{
	// Layer (object) constants
	public final static int FRAME_WIDTH = 250;
	public final static int FRAME_HEIGHT = 589;
	
	// Local reference of render engine
	private Renderer r;
	private ObjectManager o;
	
	// Layer position and size
	private Vector2i position;
	private Vector2i size;
	
	// Temporary placeholder for AudioSource
	private AudioContext context;
	private AudioSource source;
	
	public ParameterLayer(LayerManager sceneManager)
	{
		super(sceneManager);
		
		// Create local references to render engine and object manager
		this.r = this.getRenderer();
		this.o = this.getObjectManager();
		this.init();
	}
	
	@Override
	public void init()
	{	
		// Calculate position and size into a vector
		this.position = new Vector2i(this.getSystemWindow().getWidth() - FRAME_WIDTH, InterfaceLayer.FRAME_HEIGHT);
		this.size = new Vector2i(FRAME_WIDTH, FRAME_HEIGHT);
		
		// Get 'basics' Timeline and fetch AudioSource from instance
		Timeline timeline = this.getTimelineManager().get("basics");
		
		// Load source into layer parameters
		this.source = timeline.getSource("bg_arcade_pinball");		
		this.loadAudioSource(source);
		
		// Activate current scene in application cycle
		this.activate();
	}

	@Override
	public void update()
	{
		
	}

	@Override
	public void render()
	{	
		// Draw parameter layer frame
		r.setColor(UIPalette.primary);
		r.drawRectangle(this.position.x, this.position.y, this.size.x, this.size.y);
	}
	
	// Load AudioSource and generate EntryObjects
	public void loadAudioSource(AudioSource source)
	{	
		// Reset position pointer
		Vector2i p = new Vector2i(this.position);
		
		// Custom header entry with data
		o.create(new EntryObject("PAR_ETR_HEADER_SOURCE", "", p, EntryObject.E_TYPE_HEADER_SOURCE, source)); this.shift(p);
		o.create(new EntryObject("PAR_ETR_PATH", "Path", p, EntryObject.E_TYPE_STRING, source.getFilename())); this.shift(p);
		
		// AudioSource Color selector
		o.create(new EntryObject("PAR_ETR_COLOR", "Color", p, EntryObject.E_TYPE_DEFAULT));
		o.create(new DropdownObject("PAR_DRP_COLOR", this.offset(p), source.color, AOPalette.getColors()), ObjectManager.O_TYPE_IMPORTANT);
		this.shift(p);
		
		// Enabled state of AudioSource
		o.create(new EntryObject("PAR_ETR_ENABLED", "Enabled", p, EntryObject.E_TYPE_DEFAULT));
		o.create(new CheckboxObject("PAR_CHB_ENABLED", new Vector2i(p).add(EntryObject.E_OFFSET_CHECKBOX), source));
		this.shift(p);
		
		// Group of time and position
		o.create(new EntryObject("PAR_ETR_GR_TIME", "Time & Position", p, EntryObject.E_TYPE_GROUP)); this.shift(p);
		
		// Entry for object timestamp
		o.create(new EntryObject("PAR_ETR_START_END", "Start/end", p, EntryObject.E_TYPE_STRING));
		o.create(new TextboxObject("PAR_TXT_START", this.offset(p), TextboxObject.T_TYPE_TIMESTAMP, source));
		o.create(new TextboxObject("PAR_TXT_END", this.offsetSecondary(p), TextboxObject.T_TYPE_TIMESTAMP_LOCKED, source));
		this.shift(p);
		
		// Entry for AudioSource position
		o.create(new EntryObject("PAR_ETR_POSITION", "Position", p, EntryObject.E_TYPE_STRING));
		o.create(new TextboxObject("PAR_TXT_X", this.offset(p), TextboxObject.T_TYPE_X, source));
		o.create(new TextboxObject("PAR_TXT_Y", this.offsetSecondary(p), TextboxObject.T_TYPE_Y, source));
		this.shift(p);
	}
	
	// Helper functions for repeated actions
	private void shift(Vector2i position)
	{
		// Shift position of draw pointer and add to count
		position.add(0, EntryObject.E_HEIGHT);
	}
	private Vector2i offset(Vector2i position)
	{
		return new Vector2i(position).add(EntryObject.E_OFFSET_OBJECTS);
	}
	private Vector2i offsetSecondary(Vector2i position)
	{
		return new Vector2i(position).add(EntryObject.E_OFFSET_OBJECTS).add(EntryObject.E_OFFSET_SECONDARY);
	}
}
