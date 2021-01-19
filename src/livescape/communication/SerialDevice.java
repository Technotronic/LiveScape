package livescape.communication;

import com.fazecast.jSerialComm.*;

public class SerialDevice
{
	public SerialDevice()
	{
		
	}
	
	// Function that returns array of all available serial devices
	public static void listDevices()
	{
		for(SerialPort port : SerialPort.getCommPorts())
		{
	        String portName = port.getSystemPortName();
	        System.out.println(portName);
		}
	}
}
