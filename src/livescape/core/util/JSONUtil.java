package livescape.core.util;

import org.joml.Vector3f;
import org.json.simple.JSONArray;

public class JSONUtil
{
	// Helper functions for parsing JSON values to Java Objects
	public static Vector3f toVector3f(Object object)
	{
		// Cast object to JSONArray
		JSONArray array = (JSONArray) object;
		
		// Create new vector
		Vector3f vector = new Vector3f();
		
		// Parse all values to a float value
		vector.x = Float.parseFloat(array.get(0).toString());
		vector.y = Float.parseFloat(array.get(1).toString());
		vector.z = Float.parseFloat(array.get(2).toString());
		
		return vector;
	}
	
	// Parse JSONObject entry to a variety of data types
	public static int toInt(Object object)
	{
		return Integer.parseInt(object.toString());
	}
	public static float toFloat(Object object)
	{
		return Float.valueOf(object.toString());
	}
	public static long toLong(Object object)
	{
		return Long.parseLong(object.toString());
	}
	public static boolean toBool(Object object)
	{
		return Boolean.valueOf(object.toString());
	}
}
