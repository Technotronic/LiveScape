package livescape.graphics;

import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import livescape.game.instances.RenderObject;
import livescape.graphics.util.Color;

import java.util.*;

import static livescape.core.util.GLFWUtil.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
	final int WINDOW_MIN_WIDTH = 400;
	final int WINDOW_MIN_HEIGHT = 400;
	final boolean USE_LEGACY_OPENGL = true;

	// Window object properties
	private long id;

	private int width;
	private int height;

	private boolean vsync = false;

	// Window input position and button variables
	private Vector2i cursorPosition;
	private boolean cursorLeft;
	private boolean cursorRight;

	// Window keyboard callback register
	private RenderObject activeObject;

    private Callback debugProc;


    // Instantiate new window with properties when constructing class
    public Window(String name, int width, int height)
    {
    	this.create(name, width, height);
    }

    public Window(String name)
    {
    	this.create(name, WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT);
    }


    private void create(String name, int width, int height)
    {
    	GLFWErrorCallback.createPrint().set();
        if(!glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Prepare GLFW window properties
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Set some additional properties for use in MacOS
        if(Platform.get() == Platform.MACOSX)
        {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);

            // Block that enables a version between 3.2 and 4.1, if not implemented a legacy version of OpenGL is used
            if(!USE_LEGACY_OPENGL)
            {
            	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
	            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
            }
            else
            {
            	// Notify user we're using a legacy version of OpenGL
            	System.err.println("Reverting back to a legacy version of OpenGL.");
            }
        }

        // Instantiate a new GLFW window
        long window = glfwCreateWindow(width, height, name, NULL, NULL);
        if(window == NULL)
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set window size and aspect properties
        glfwSetWindowSizeLimits(window, width, height, GLFW_DONT_CARE, GLFW_DONT_CARE);
        glfwSetWindowAspectRatio(window, 1, 1);

        // Get primary system monitor
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(monitor));

        // Center window on selected monitor
        glfwSetWindowPos(
            window,
            (vidmode.width() - width) / 2,
            (vidmode.height() - height) / 2
        );

        // Set callback for specific key presses in window
        glfwSetKeyCallback(window, (windowHnd, key, scancode, action, mods) ->
        {
            if(action == GLFW_RELEASE)
            {
                return;
            }

            // Send key to active callback object for handling input
        	if(this.activeObject != null)
        	{
        		this.activeObject.onInput(key);
        	}

            // Put pressed key into class variable
            switch(key)
            {
            	// Close window on ESC key
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(windowHnd, true);
                	break;
            }
        });

        // Set callback to update cursor related input
        this.cursorPosition = new Vector2i(0, 0);
        glfwSetCursorPosCallback(window, (windowHnd, x, y) ->
        {
        	this.cursorPosition = new Vector2i((int) x, (int) y);
        });

        glfwSetMouseButtonCallback(window, (windowHnd, button, action, mods) ->
        {
        	if(button == GLFW_MOUSE_BUTTON_LEFT)
        	{
        		this.cursorLeft = (action == GLFW_PRESS ? true : false);
        	}

        	if(button == GLFW_MOUSE_BUTTON_RIGHT)
        	{
        		this.cursorRight = (action == GLFW_PRESS ? true : false);
        	}
        });

        // Set callback pointer to function that handles size changes
        glfwSetFramebufferSizeCallback(window, this::framebufferSizeChanged);

        // Handle window refresh callback (after resize, etc.)
        glfwSetWindowRefreshCallback(window, windowHnd ->
        {
            glfwSwapBuffers(windowHnd);
        });

        // Create OpenGL context and set pointer to current instance
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Set pointer to function that handles debug messages
        debugProc = GLUtil.setupDebugMessageCallback();

        // Enable specific GL functionality
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        this.setDefaultBlend();

        // Refresh buffer at GPU rate (if enabled) and show window
        if(this.getVsync())
        {
        	glfwSwapInterval(1);
        }
        glfwInvoke(window, null, this::framebufferSizeChanged);
        glfwShowWindow(window);

        // Set class parameters
        this.id = window;
        this.width = width;
        this.height = height;
    }

    public void clear()
    {
    	// Clear all OpenGL buffers, binds and pointers
    	glClear(GL_COLOR_BUFFER_BIT);
    	glBindTexture(GL_TEXTURE_2D, 0);
    }

    // Function that is called when current window instance is being destroyed
	public void destroy()
	{
		// Free up allocated memory of window and its context
	    GL.setCapabilities(null);

	    if(debugProc != null)
	    {
	        debugProc.free();
	    }

	    if(this.getId() != NULL)
	    {
	        glfwFreeCallbacks(this.getId());
	        glfwDestroyWindow(this.getId());
	    }

	    // Terminate GLFW window handler of this context
	    glfwTerminate();
	    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

	// Function that is called when window size is changed
    private void framebufferSizeChanged(long window, int width, int height)
    {
        if(width == 0 || height == 0)
        {
            return;
        }

        // TODO: Make updating GL viewport work properly
        //GL11.glViewport(0, 0, 400, 400);

        // Update local variable without sending update to GLFW
        this.setSize(width, height);
    }

    // Functions that handles window specific GLFW operations
    public boolean shouldClose()
    {
    	return glfwWindowShouldClose(this.getId());
    }

    public void swapBuffers()
    {
    	glfwSwapBuffers(this.getId());
    }

    // Functions that handle window key inputs
    public boolean isKeyPressed(int keyCode)
    {
        return glfwGetKey(this.getId(), keyCode) == GLFW_PRESS;
    }

    public boolean isKeyReleased(int keyCode)
    {
        return glfwGetKey(this.getId(), keyCode) == GLFW_RELEASE;
    }

    // Window properties getters and setters
    public void setSize(int width, int height, boolean sync)
    {
    	if(width < WINDOW_MIN_WIDTH || height < WINDOW_MIN_HEIGHT) { return; }

    	// Set variables to class
    	this.width = width;
    	this.height = height;

    	// Write properties to the window object
    	if(sync)
    	{
    		glfwSetWindowSize(this.getId(), width, height);
    	}
    }
    public void setSize(int width, int height) { this.setSize(width, height, false); }

    public void setVsync(boolean state)
    {
    	this.vsync = state;
    }

    public void setClearColor(Color c, boolean doClear)
    {
    	// Set clearing color to specific HEX color and clear immediately
		glClearColor(c.getR(), c.getG(), c.getB(), 1);
		if(doClear)
		{
			this.clear();
		}
    }
    public void setClearColor(Color c) { this.setClearColor(c, false); }

    // OpenGL blend functionality from Anders Riggelsen (https://www.andersriggelsen.dk/glblendfunc.php)
    public void setDefaultBlend()
    {
    	// Default blending function, still a bit buggy sometime though
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    // Object in window that has input focus
    public void setActiveObject(RenderObject object)
    {
    	this.activeObject = object;
    }
    public RenderObject getActiveObject() { return (this.activeObject != null) ? this.activeObject : null; }

    public void enableBlend() { GL11.glEnable(GL11.GL_BLEND); }
    public void disableBlend() { GL11.glDisable(GL11.GL_BLEND); }

    public long getId() { return this.id; }
    public Vector2i getDimensions() { return new Vector2i(this.width, this.height); }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public boolean getVsync() { return this.vsync; }

    public Vector2i getCursorPosition() { return this.cursorPosition; }
    public boolean getButtonLeft() { return this.cursorLeft; }
    public boolean getButtonRight() { return this.cursorRight; }

}
