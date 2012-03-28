package Slave;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * This is the slave's main class.
 * 
 * @author Anthony
 */
public class SlaveCont {

	public static void main(String[] args) {

		Button.waitForPress();
		
		int command = 0;
		NXTRegulatedMotor loadMotor = Motor.A;
		NXTRegulatedMotor holdMotor = Motor.B;
		
		Offense off = new Offense(loadMotor, holdMotor);
		Defense def = new Defense();

		NXTConnection connection = Bluetooth.waitForConnection();
		DataInputStream dis = connection.openDataInputStream();

		// Read command from data input stream (an integer)
		LCD.clear();
		LCD.drawString("Reading:", 0, 0);
		
		try {
			command = dis.readInt();
			LCD.clear();
			LCD.drawString("Command read.", 0, 0);
		} catch (IOException e) {
		}

		LCD.drawString("Command: " + command, 0, 4);
		
		// Take command and use it to determine desired action (1 = offense, 2 =
		// defense)
		if (command == 1) {
			off.run();
			LCD.clear();
			LCD.drawString("Shot", 0, 0);
		} else if (command == 2) {
			def.run();
		}
		
		LCD.drawString("Finished.", 0, 2);
		
		Button.waitForPress();

	}
}
