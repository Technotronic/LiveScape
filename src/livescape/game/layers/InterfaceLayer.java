package livescape.game.layers;

import org.joml.Vector2i;

import livescape.game.LayerManager;
import livescape.game.ObjectManager;
import livescape.game.instances.Layer;
import livescape.game.objects.ButtonObject;
import livescape.graphics.Renderer;
import livescape.graphics.palettes.UIPalette;
import livescape.graphics.palettes.UIImages;

public class InterfaceLayer extends Layer
{	
	public final static int FRAME_HEIGHT = 27;
	public final static Vector2i FRAME_PADDING = new Vector2i(8, 5);
	
	public InterfaceLayer(LayerManager sceneManager)
	{
		super(sceneManager);
		this.init();
	}

	@Override
	public void init()
	{
		// Add buttons to handle file functionality
		this.createButtons();
		this.activate();
	}

	@Override
	public void update()
	{
		
	}

	@Override
	public void render()
	{
		// Draw header and application version String
		Renderer render = this.getRenderer();
		render.setColor(UIPalette.head);
		render.drawRectangle(0, 0, this.getSystemWindow().getWidth(), FRAME_HEIGHT);
		
		String text = this.getSystem().getVersion();
		int pos = this.getSystemWindow().getWidth() - render.getFont("goodbye").getWidth(text) - 10;
		render.drawText("goodbye", text, pos, 10, UIPalette.textDark);
	}
	
	// Function to assign buttons to header on init
	private void createButtons()
	{
		// Create local reference of objects and define pointer
		ObjectManager objects = this.getObjectManager();
		Vector2i p = new Vector2i(0 + FRAME_PADDING.x, 0 + FRAME_PADDING.y);
		
		// Add button for creating new files
		objects.create(new ButtonObject("BTN_NEW_FILE",
			p, UIPalette.grey, UIImages.file
		));
		objects.addTooltip("BTN_NEW_FILE", "TT_NEW_FILE", "Create new project");
		p.add(ButtonObject.SIZE_DEFAULT.x + 1, 0);
		
		// Add button for loading files
		objects.create(new ButtonObject("BTN_LOAD_FILE",
			p, UIPalette.white, UIImages.load
		));
		objects.addTooltip("BTN_LOAD_FILE", "TT_LOAD_FILE", "Load project");
		p.add(ButtonObject.SIZE_DEFAULT.x + 1, 0);
		
		// Add button for saving current file
		objects.create(new ButtonObject("BTN_SAVE_FILE",
			p, UIPalette.white, UIImages.save
		));
		objects.addTooltip("BTN_SAVE_FILE", "TT_SAVE_FILE", "Save project");
	}
}
