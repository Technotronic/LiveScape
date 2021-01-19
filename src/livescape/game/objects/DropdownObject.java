package livescape.game.objects;

import java.util.Map;

import org.joml.Vector2i;
import livescape.game.instances.RenderObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIImages;
import livescape.graphics.palettes.UIPalette;
import livescape.graphics.util.Color;

public class DropdownObject extends RenderObject
{
	// Constant variables (size, etc.) for textbox types
	public final static Vector2i SIZE_DEFAULT = new Vector2i(110, 15);
	public final static Vector2i TEXT_OFFSET_DEFAULT = new Vector2i(5, 4);
	public final static Vector2i TEXT_OFFSET_COLOR = new Vector2i(18, 4);
	
	public final static int DRP_TYPE_STRING = 0;
	public final static int DRP_TYPE_COLOR = 10;
	
	// Variables for holding dropdown data references
	private int type;
	private Color colorRef;
	private Map<Integer, Color> colorMap;
	
	// Local draw pointer and entry counter
	private Vector2i p;
	private int counter;
	private int hoverNum;
	
	
	// Constructor when changing Color reference
	public DropdownObject(String key, Vector2i position, Color colorRef, Map<Integer, Color> colorMap)
	{
		this.type = DRP_TYPE_COLOR;
		this.colorRef = colorRef;
		this.colorMap = colorMap;
		this.construct(key, position);
	}
	
	// Handles general construction functions for all dropdown types
	private void construct(String key, Vector2i position)
	{
		this.key = key;
		this.name = "Textbox Object";
		this.position = new Vector2i(position);
		this.size = new Vector2i(SIZE_DEFAULT.x, SIZE_DEFAULT.y);
		this.color = UIPalette.box;
	}
	
	public void onHover(Vector2i position)
	{
		// Calculate relative position of cursor in DropdownObject
		Vector2i relative = new Vector2i(position.x - this.position.x, position.y - this.position.y);
		
		// Calculate which entry is being hovered
		this.hoverNum = (relative.y / SIZE_DEFAULT.y);
	}
	
	public void onClick(Vector2i position)
	{	
		// Switch behaviour, depending on type
		switch(this.type)
		{
			case DRP_TYPE_COLOR:
				
				// Get clicked item and replace referenced color
				Color select = this.colorMap.get(this.hoverNum - 1);
				if(select != null)
				{
					this.colorRef.replace(select);
					this.setFocus(false);
				}
				break;
		}
	}
	
	// Render button object with provided render engine
	public void render(Renderer r)
	{	
		// Reset draw pointer, counter (include 'closed' state as 1) and size
		this.p = new Vector2i(this.position.x, this.position.y);
		this.counter = 0;
		this.size.set(SIZE_DEFAULT.x, SIZE_DEFAULT.y);
		
		// Render textbox background
		r.setColor(UIPalette.box);
		r.drawRectangle(position.x, position.y, SIZE_DEFAULT.x, SIZE_DEFAULT.y);
		
		// Draw arrow texture
		r.drawTexture(UIImages.dropdown, this.position.x + (SIZE_DEFAULT.x - 14), this.position.y + 3, 18, 18);
		
		// Depending on type, draw current dropdown state
		switch(this.type)
		{
			case DRP_TYPE_COLOR:
				r.drawTexture(UIImages.objS, this.colorRef, this.position.x + 5, this.position.y + 3, 18, 18);
				r.drawText("goodbye", this.colorRef.getName(), this.position.x + TEXT_OFFSET_COLOR.x, this.position.y + TEXT_OFFSET_COLOR.y, UIPalette.black);
				
				// When in focus, also draw all entries
				if(this.isFocus())
				{
					for(Map.Entry<Integer, Color> entry : this.colorMap.entrySet())
					{
						// Shift pointer position, draw entry and add to entry counter
						this.shift();
						this.drawColor(r, entry);
					}
					
					// Recalculate size of dropdown
					this.size.set(SIZE_DEFAULT.x, SIZE_DEFAULT.y * (counter + 1));
				}
				break;
		}
	}
	
	// Functions for drawing row, depending on entry type
	private void drawColor(Renderer r, Map.Entry<Integer, Color> entry)
	{
		// Get Color from entry
		Color c = entry.getValue();
		
		// Draw option background
		r.setColor((this.counter == this.hoverNum) ? UIPalette.hover : UIPalette.box);
		r.drawRectangle(this.p.x, this.p.y, SIZE_DEFAULT.x, SIZE_DEFAULT.y);
		
		// Draw colored texture and Color label
		r.drawTexture(UIImages.objS, c, this.p.x + 5, this.p.y + 3, 18, 18);
		r.drawText("goodbye", c.getName(), this.p.x + TEXT_OFFSET_COLOR.x, this.p.y + TEXT_OFFSET_COLOR.y, UIPalette.black);
	}
	
	// Helper function for shifting position of pointer
	private void shift()
	{
		this.p.add(0, SIZE_DEFAULT.y);
		this.counter++;
	}
}
