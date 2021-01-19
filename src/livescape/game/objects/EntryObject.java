package livescape.game.objects;

import org.joml.Vector2i;

import livescape.audio.instance.AudioSource;
import livescape.game.instances.RenderObject;
import livescape.game.layers.ParameterLayer;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIImages;
import livescape.graphics.palettes.UIPalette;

public class EntryObject extends RenderObject
{
	// Static constants for entry settings
	public final static int E_HEIGHT = 26;
	public final static Vector2i E_TEXT_MARGIN = new Vector2i(10, 9);
	public final static Vector2i E_OFFSET_OBJECTS = new Vector2i(128, 5);
	public final static Vector2i E_OFFSET_CHECKBOX = new Vector2i(230, 8);
	public final static Vector2i E_OFFSET_SECONDARY = new Vector2i(56, 0);
	
	// Static constants of entry types
	public final static int E_TYPE_DEFAULT = 0;
	public final static int E_TYPE_STRING = 1;
	public final static int E_TYPE_INT = 2;
	public final static int E_TYPE_FLOAT = 3;
	public final static int E_TYPE_BOOLEAN = 4;
	
	public final static int E_TYPE_HEADER = 10;
	public final static int E_TYPE_HEADER_SOURCE = 11;
	public final static int E_TYPE_GROUP = 12;
	
	// Local reference to render engine
	private Renderer r;
	
	// Linked references to generic types of entry
	private int type;
	private String title;
	private String text;
	
	// Linked complex objects references
	private AudioSource eSource;
	
	
	// Constructors for specific entry types
	public EntryObject(String key, String title, Vector2i position, int type)
	{
		this.text = "";
		this.construct(key, title, position, type);
	}
	public EntryObject(String key, String title, Vector2i position, int type, String text)
	{
		this.text = text;
		this.construct(key, title, position, type);
	}
	public EntryObject(String key, String title, Vector2i position, int type, AudioSource eSource)
	{
		this.eSource = eSource;
		this.construct(key, title, position, type);
	}
	
	// General function for all types of EntryObject constructor
	private void construct(String key, String title, Vector2i position, int type)
	{
		// Construct generic object variables
		this.key = key;
		this.title = title;
		this.position = new Vector2i(position);
		this.size = new Vector2i(ParameterLayer.FRAME_WIDTH, E_HEIGHT);
		this.type = type;		
	}
	
	// Functions for system loop ticks or render triggers
	@Override
	public void update()
	{
		
	}
	
	@Override
	public void render(Renderer r)
	{
		// Set reference to render engine
		this.r = r;
		
		// Draw entry background first
		r.setColor(UIPalette.entry);
		r.drawRectangle(this.position.x, this.position.y, this.size.x, this.size.y);
		
		// Switch execution, depending on set entry type
		switch(this.type)
		{	
			case E_TYPE_DEFAULT:
			case E_TYPE_STRING:
				this.renderLabel(this.title);
				this.renderText();
				break;
				
			case E_TYPE_GROUP:
				this.renderGroupTitle();
				break;
		
			case E_TYPE_HEADER_SOURCE:
				this.renderSourceHeader();
				break;
		}
	}
	
	
	// Render different types of input
	private void renderText()
	{
		// Draw text on the right side of entry box
		int width = r.getFont("goodbye").getWidth(this.text);
		r.drawText("goodbye", this.text, 
			this.position.x + size.x - width - E_TEXT_MARGIN.x,
			this.position.y + E_TEXT_MARGIN.y
		);
	}
	
	// Render different types of headers and/or titles
	private void renderGroupTitle()
	{
		// Re-draw background
		r.setColor(UIPalette.primary);
		r.drawRectangle(this.position.x, this.position.y, this.size.x, this.size.y);
		
		// Append text with margins
		r.drawText("goodbye", this.title, this.position.x + E_TEXT_MARGIN.x, this.position.y + E_TEXT_MARGIN.y);
	}
	private void renderSourceHeader()
	{
		// Draw source color icon and name
		r.drawTexture(UIImages.object, this.eSource.color, this.position.x + 10, this.position.y + 8, 18, 18);
		r.drawText("goodbye", this.eSource.getName(), this.position.x + 25, this.position.y + E_TEXT_MARGIN.y);
		
		// Calculate position of duration label
		int width = r.getFont("goodbye").getWidth("00:00.00");
		Vector2i pointer = new Vector2i(this.position.x + (this.size.x - width) - E_TEXT_MARGIN.x, this.position.y + E_TEXT_MARGIN.y);
		
		// Convert source length to timestamp
		String timestamp = this.eSource.getLengthTimestamp();
		
		// Draw timestamp accordingly
		r.drawText("goodbye", timestamp.substring(0, 3), pointer.x, pointer.y, UIPalette.textDarkish);
		r.drawText("goodbye", timestamp.substring(3, 8), pointer.x + 12, pointer.y, UIPalette.white);
	}
	
	// Helper functions for repeated elements (label, offset, etc.)
	private void renderLabel(String key)
	{
		r.drawText("goodbye", key + ":", this.position.x + E_TEXT_MARGIN.x, this.position.y + E_TEXT_MARGIN.y, UIPalette.textDarkish);
	}
}
