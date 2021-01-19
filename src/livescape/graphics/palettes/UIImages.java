package livescape.graphics.palettes;

import livescape.graphics.Renderer;

public class UIImages
{
	// Local reference to render engine
	private Renderer render;
	
	// Create references to usable files
	public static String cbF = "icons/checkbox-false.png";
	public static String cbT = "icons/checkbox-true.png";
	public static String down = "icons/down.png";
	public static String dropdown = "icons/dropdown.png";
	public static String file = "icons/file.png";
	public static String handle = "icons/handle.png";
	public static String key = "icons/keyframe.png";
	public static String left = "icons/left.png";
	public static String load = "icons/load.png";
	public static String loop = "icons/loop.png";
	public static String minus = "icons/minus.png";
	public static String object = "icons/object.png";
	public static String objM = "icons/object-medium.png";
	public static String objS = "icons/object-small.png";
	public static String play = "icons/play.png";
	public static String plus = "icons/plus.png";
	public static String rewind = "icons/rewind.png";
	public static String right = "icons/right.png";
	public static String save = "icons/save.png";
	public static String sound = "icons/sound.png";
	public static String up = "icons/up.png";
	
	public UIImages(Renderer render)
	{
		this.render = render;
		this.preload();
	}
	
	// Load all given image files into texture buffer
	public void preload()
	{
		// TODO: Probably is better way than this, rewrite later
		this.c(cbF);
		this.c(cbT);
		this.c(down);
		this.c(dropdown);
		this.c(file);
		this.c(handle);
		this.c(key);
		this.c(left);
		this.c(load);
		this.c(loop);
		this.c(minus);
		this.c(object);
		this.c(objM);
		this.c(objS);
		this.c(play);
		this.c(plus);
		this.c(rewind);
		this.c(right);
		this.c(save);
		this.c(sound);
		this.c(up);
	}
	
	// Proxy function for better readability of creating texture
	private void c(String path)
	{
		render.createTexture(path);
	}
}
