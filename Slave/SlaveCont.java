package Slave;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * This is the slave's main class.
 *
 * @author Anthony 
 */
public class SlaveCont {

	public static void main(String[] args){
		
		NXTConnection connection = Bluetooth.waitForConnection();
		DataInputStream dis = connection.openDataInputStream();
		DataOutputStream dos = connection.openDataOutputStream();
	}
}
