/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
*/
package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.*;
/*
 * This class inits a bluetooth connection, waits for the data
 * and then allows access to the data after closing the BT channel.
 * 
 * It should be used by calling the constructor which will automatically wait for
 * data without any further user command
 * 
 * Then, once completed, it will allow access to an instance of the Transmission
 * class which has access to all of the data needed
 */
public class BluetoothConnection {
	private Transmission trans;
	
	public BluetoothConnection() {
		LCD.clear();
		LCD.drawString("Starting BT connection", 0, 0);
		
		NXTConnection conn = Bluetooth.waitForConnection();
		DataInputStream dis = conn.openDataInputStream();
		LCD.drawString("Opened DIS", 0, 1);
		this.trans = ParseTransmission.parse(dis);
		LCD.drawString("Finished Parsing", 0, 2);
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.close();
	}
	
	public Transmission getTransmission() {
		return this.trans;
	}
	
	public void printTransmission() {
		try {
			LCD.clear();
			LCD.drawString(("Transmitted Values"), 0, 0);
			LCD.drawString("Start: " + trans.startingCorner.toString(), 0, 1);
			LCD.drawString("Role: " + trans.role.getId(), 0, 2);
			LCD.drawString("W1: " + trans.w1, 0, 3);
			LCD.drawString("W2: " + trans.w2, 0, 4);
			LCD.drawString("Bx: " + trans.bx, 0, 5);
			LCD.drawString("By: " + trans.by, 0, 6);
			LCD.drawString("Bsigma: " + trans.bsigma, 0, 7);
		} catch (NullPointerException e) {
			LCD.drawString("Bad Trans", 0, 7);
		}
	}
	
}
