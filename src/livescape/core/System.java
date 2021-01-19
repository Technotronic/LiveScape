package livescape.core;

import static org.lwjgl.glfw.GLFW.*;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import livescape.core.util.Logger;
import livescape.core.util.StringUtil;
import livescape.core.util.Configuration;
import livescape.core.util.SystemUtil;
import livescape.game.InteractionManager;
import livescape.game.LayerManager;
import livescape.game.ObjectManager;
import livescape.game.TimelineManager;
import livescape.graphics.Renderer;
import livescape.graphics.Window;

public class System
{	
	private static final String propertiesFile = "livescape.properties";
	private static final String versionString = "v0.2.3-ALPHA 'GRAD-DEMO'";
	
	private Logger logger;
	private Properties props;
	private Configuration config;
	private Window window;
	private Renderer render;
	
	private TimelineManager timelines;
	private InteractionManager interaction;
	private ObjectManager objects;
	private LayerManager layers;
	
	final double FPS_LIMIT = 30;
	final double MILLISECONDS_PER_UPDATE = (1 / FPS_LIMIT) * 1000;
	
	private double previous = SystemUtil.now();
	private double steps = 0;
	
	
	public System()
	{
		this.logger = new Logger();
		this.props 	= new Properties();
		this.config = new Configuration(this);
		this.render = new Renderer(this);
		
		this.timelines = new TimelineManager(this);
		this.interaction = new InteractionManager(this);
		this.objects = new ObjectManager(this);
		this.layers = new LayerManager(this);
	}
	
	public boolean start(Window window)
	{	
		// Output all data in US locale format
		Locale.setDefault(Locale.US);
		
		// Print version
		this.logger.write(String.format("Booting %s...", this.getVersion()));
		
		// Load .properties file into memory
		try
		{
			this.props.load(new FileInputStream(System.propertiesFile));
		}
		catch(Exception ex)
		{
			this.logger.write(String.format("Could not load '%s'! Please validate the LiveScape installation.", System.propertiesFile));
			return false;
		}
		
		// Assign window to system instance
		if(window == null)
		{
			this.logger.write("Unable to instantiate system object, no window assigned.");
			java.lang.System.exit(1);
		}
		
		// Fetch properties for main window
		boolean vsyncEnabled = StringUtil.parseBool(this.props.getProperty("win.vsync.enabled"));
		window.setVsync(vsyncEnabled);
		
		// Start assigning and running instanced subsystems
		this.window = window;
		this.render.init();
		this.timelines.init();
		this.interaction.init();
		this.layers.init();
		
		this.logger.write("Subsystems loaded and prepared! Starting application cycle...");
		
		// Run functions at set FPS in game loop
		while(!this.window.shouldClose())
        {
			// Keep track of how long each cycle takes
			double start = SystemUtil.now();
			double elapsed = start - this.previous;
			
			this.previous = start;
			this.steps += elapsed;
			
			// Process queued GLFW input states
            glfwPollEvents();
			
			while(this.steps >= MILLISECONDS_PER_UPDATE)
			{
				// Update game state, calculate logic etc.
				this.tick();
				
				this.steps -= MILLISECONDS_PER_UPDATE;
			}
            
			// Send render request to active scene
			this.layers.render();
			this.objects.render();
			
            // Swap front and back buffers of window
            this.window.swapBuffers();
            
            // Synchronize cycle to set amount of frames per second
            this.sync(start);
        }
		this.destroy();
		
		return true;
	}

	// Function that is called when system process is being stopped
	public void destroy()
	{
		// Stop and clean-up all running threads
		this.interaction.stop();
		this.objects.destroy();
		this.layers.stop();
		this.timelines.destroy();
		
		// At last, destroy the window
		this.window.destroy();
	}

	// Functions that handle game logic and input calls and rendering synchronization
	public void tick()
	{
		// Clear graphic buffer
		this.getSystemWindow().clear();
		
		// Tick renderer and interaction map check
		this.timelines.tick();
		this.layers.tick();
		this.objects.tick();
		this.interaction.tick();
	}
	
	private void sync(double start)
	{
		double end = start + MILLISECONDS_PER_UPDATE;
		while(SystemUtil.now() < end) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {}
		}
	}
	
	public String getVersion()
	{
		return System.versionString;
	}
	
	public Logger getLogger()
	{
		return this.logger;
	}
	
	public Properties getProperties()
	{
		return this.props;
	}
	
	public Configuration getConfig()
	{
		return this.config;
	}
	
	public Window getSystemWindow()
	{
		return this.window;
	}
	
	public Renderer getRenderer()
	{
		return this.render;
	}
	
	public TimelineManager getTimelineManager()
	{
		return this.timelines;
	}
	
	public InteractionManager getInteractionManager()
	{
		return this.interaction;
	}
	
	public ObjectManager getObjectManager()
	{
		return this.objects;
	}
	
	public LayerManager getLayerManager()
	{
		return this.layers;
	}
	
	public static abstract class Component
	{
		protected final System system;
		
		public Component(System system)
		{
			this.system = system;
		}
		
		public System getSystem()
		{
			return this.system;
		}
		
		public Logger getLogger()
		{
			return this.system.getLogger();
		}
		
		public Properties getProperties()
		{
			return this.system.getProperties();
		}
		
		public Configuration getConfig()
		{
			return this.system.getConfig();
		}
		
		public Window getSystemWindow()
		{
			return this.system.getSystemWindow();
		}
		
		public Renderer getRenderer()
		{
			return this.system.getRenderer();
		}
		
		public TimelineManager getTimelineManager()
		{
			return this.system.getTimelineManager();
		}
		
		public InteractionManager getInteractionManager()
		{
			return this.system.getInteractionManager();
		}
		
		public ObjectManager getObjectManager()
		{
			return this.system.objects;
		}
		
		public LayerManager getLayerManager()
		{
			return this.system.getLayerManager();
		}
	}
}
