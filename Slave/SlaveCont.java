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
		NXTRegulatedMotor loadMotor = Motor.B;
		NXTRegulatedMotor holdMotor = Motor.A;

		Offense off = new Offense(loadMotor, holdMotor);
		Defense def = new Defense();

		NXTConnection connection = Bluetooth.waitForConnection();
		DataInputStream dis = connection.openDataInputStream();
		
		try {
			command = dis.readInt();
			LCD.clear();
			LCD.drawString("COmmand received.", 0, 0);
		} catch (IOException e) {
		}

		// Take command and use it to determine desired action (1 = offense, 2 =
		// defense)
		if (command == 1) {
			off.run();
			LCD.clear();
		} else if (command == 2) {
			def.run();
		}

		Button.waitForPress();

	}
}
