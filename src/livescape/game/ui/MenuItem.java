package livescape.game.ui;

import java.util.HashMap;

public class MenuItem
{
	// Different render states for MenuItem
	public final static int ITEM_TYPE_BUTTON = 0;
	public final static int ITEM_TYPE_TITLE = 1;
	public final static int ITEM_TYPE_SPACER = 2;
	
	public int id;
	public String title;
	public int type;
	
	private boolean expanded;
	
	// MenuItems that are accessible from current item i.e. submenu for header menu
	private HashMap<Integer, MenuItem> submenu;
	
	
	public MenuItem(int id, String title, int type)
	{
		this.id = id;
		this.title = title;
		
		this.type = type;
		this.expanded = false;
		
		this.submenu = new HashMap<Integer, MenuItem>();
	}
	public MenuItem(int id, String title) { this(id, title, ITEM_TYPE_BUTTON); }
	
	public void put(MenuItem item)
	{
		this.submenu.put(item.id, item);
	}
	
	public HashMap<Integer, MenuItem> getSubItems()
	{
		return this.submenu;
	}
	
	// Menu item state getters and setters
	public void setType(int type)
	{
		this.type = type;
	}
	
	public boolean isExpanded()
	{
		return this.expanded;
	}
	
	public void setExpanded(boolean state)
	{
		this.expanded = state;
	}
}
