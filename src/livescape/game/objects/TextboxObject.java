package livescape.game.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;

import livescape.audio.instance.AudioSource;
import livescape.audio.util.TimestampUtil;
import livescape.game.instances.RenderObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIPalette;
import livescape.graphics.util.Color;

public class TextboxObject extends RenderObject
{
	// Constant variables (size, etc.) for textbox types
	public final static int CHAR_APPEND_INTERVAL = 10;
	public final static int CARET_INDEX_INTERVAL = 15;
	public final static Vector2i SIZE_DEFAULT = new Vector2i(110, 15);
	public final static Vector2i TEXT_OFFSET_DEFAULT = new Vector2i(5, 4);
	public final static Vector2i SIZE_SMALL = new Vector2i(55, 15);
	
	// Constants for TextboxObject types
	public final static int T_TYPE_DEFAULT = 0;
	public final static int T_TYPE_TIMESTAMP = 1;
	public final static int T_TYPE_TIMESTAMP_LOCKED = 2;
	public final static int T_TYPE_X = 3;
	public final static int T_TYPE_Y = 4;
	
	// Caret index related variables (position, is visible, etc.)
	private int CICount = 0;
	private boolean CIShow = false;
	
	// Local variables for TextboxObject
	private int type;
	private String value;
	private String placeholder;
	
	private boolean isLocked = false;
	
	// Linked objects for manipulating values
	private AudioSource tSource;
	
	
	// Constructors for specific linked data objects
	public TextboxObject(String key, Vector2i position, int type, AudioSource source)
	{
		// Set referenced value and workable value
		this.tSource = source;
		
		// Depending on type, set current value
		switch(type)
		{
			case T_TYPE_TIMESTAMP:
				this.value = TimestampUtil.toTimestamp(source.getStart());
				this.placeholder = "00:00.00";
				break;
				
			case T_TYPE_TIMESTAMP_LOCKED:
				this.value = TimestampUtil.toTimestamp(source.getStart() + source.getLength());
				this.placeholder = "00:00.00";
				this.isLocked = true;
				break;
				
			case T_TYPE_X:
				this.value = String.valueOf(source.getPosition().x);
				this.placeholder = "0.0";
				break;
				
			case T_TYPE_Y:
				this.value = String.valueOf(source.getPosition().y);
				this.placeholder = "0.0";
				break;
		}
		
		// Set textbox size and proceed to general inits
		this.size = new Vector2i(SIZE_SMALL);
		this.construct(key, position, type);
	}
	
	private void construct(String key, Vector2i position, int type)
	{
		this.key = key;
		this.name = "Textbox Object";
		this.type = type;
		this.position = new Vector2i(position);
	}
	
	// Callback from active system window keyboard input
	public void onInput(int characterInt)
	{
		// Only handle input if window is active
		if(this.isFocus() && !this.isLocked())
		{
			// Check if character is alphanumeric, if so add
			if(characterInt >= 32 && characterInt <= 93)
			{
				this.value += Character.toString ((char) characterInt);
			}
			
			// Backspaces are for removing last characters
			if(characterInt == 259)
			{
				if(this.value != null && this.value.length() > 0)
				{
					this.value = this.value.substring(0, this.value.length() - 1);
					
					// If no length found, reset value to empty string
					if(this.value.toCharArray().length == 0)
					{
						this.value = "";
					}
			    }
			}
			
			// Pressing enter removes object focus
			if(characterInt == 257)
			{
				// Remove focus state from textbox
				this.setFocus(false);
				
				// Depending on type, push data to right reference
				switch(type)
				{
					case T_TYPE_TIMESTAMP:
						this.tSource.setStart(TimestampUtil.toLong(this.value));
						break;
						
					case T_TYPE_X:
						Vector3f pX = this.tSource.getPosition();
						Vector3f nPX = new Vector3f(Float.parseFloat(this.value), pX.y, pX.z);
						this.tSource.setPosition(nPX);
						break;
					
					// TODO: Not very D.R.Y.
					case T_TYPE_Y:
						Vector3f pY = this.tSource.getPosition();
						Vector3f nPY = new Vector3f(pY.x, Float.parseFloat(this.value), pY.z);
						this.tSource.setPosition(nPY);
						break;
						
				}
			}
			
			// TODO: Move caret index on arrow keys
			// TODO: When enabling caret arrow keys move, also detect caret position on click
		}
	}
	
	public void update()
	{
		// Only keep track of caret index count when active
		if(this.isFocus() && !this.isLocked())
		{
			// Inverse CIShow state on interval number
			if(CICount >= CARET_INDEX_INTERVAL)
			{
				CIShow = (CIShow) ? false : true;
				CICount = 0;
			}
			CICount++;
		}
		
		// Update 'end' time each cycle
		switch(type)
		{
			case T_TYPE_TIMESTAMP_LOCKED:
				this.value = TimestampUtil.toTimestamp(this.tSource.getStart() + this.tSource.getLength());
				break;
		}
	}
	
	// Render button object with provided render engine
	public void render(Renderer render)
	{	
		// If textbox is active, draw dark border
		if(this.isFocus() && !this.isLocked())
		{
			render.setColor(UIPalette.focus);
			render.drawRectangle(position.x - 1, position.y - 1, size.x + 2, size.y + 2);
		}
	
		// Render textbox background
		render.setColor((this.isLocked()) ? UIPalette.locked : UIPalette.box);
		render.drawRectangle(position.x, position.y, size.x, size.y);
		
		// Calculate text offset
		Vector2i offset = new Vector2i(this.position).add(TEXT_OFFSET_DEFAULT);
		
		// Check if placeholder or text is displayed
		String renderText = (this.getValue() == null) ? this.getPlaceholder() : this.getValue();
		Color textColor = (this.getValue() == null) ? UIPalette.placeholder : UIPalette.text;
		textColor = (this.isLocked()) ? UIPalette.white : textColor;
		
		// Draw text in box
		render.drawText("goodbye", renderText, offset.x, offset.y, textColor);
		
		// Display caret index (blinker) when textbox is active
		if(isFocus && CIShow)
		{
			// Calculate offset from index
			int indexPosition = render.getFont("goodbye").getWidth(this.value);
			
			render.setColor(UIPalette.text);
			render.drawRectangle(offset.x + indexPosition, offset.y, 1, 7);
		}
	}
	
	// Getters and setters for color, texture and text	
	public void setValue(String text)
	{
		this.value = text;
	}
	public String getValue() { return (this.value == null || this.value == "") ? null : this.value; }
	
	public String getPlaceholder() { return (this.placeholder == null || this.placeholder == "") ? "" : this.placeholder; }
	
	// Getters and setters for interactive type variables
	public boolean isLocked() { return this.isLocked; }
}
