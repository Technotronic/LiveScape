package livescape.core;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;

import livescape.graphics.Window;

public class Bootstrapper
{
	public static void main(String[] args) 
	{	
		// ATW and GLFW conflict on MacOS solved by running Java AWT headless
		if(Platform.get() == Platform.MACOSX)
			java.lang.System.setProperty("java.awt.headless", "true");
		
		// Enable LWJGL debug level messages
		Configuration.DEBUG.set(false);
		
		// Initialize new GLFW window loop
		Window window = new Window("LiveScape Audio Tool", 800, 800);
		
		// Create new system and assign main window to system object
		System system = Instance.get();
		if(!system.start(window))
		{
			system.getLogger().write("Booting LiveScape has failed.");
			java.lang.System.exit(1);
		}
	}
}