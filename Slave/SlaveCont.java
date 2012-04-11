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

		LCD.drawString("Press to start.", 0, 0);
		
		Button.waitForPress();

		int command = 0;
		NXTRegulatedMotor loadMotor = Motor.B;
		NXTRegulatedMotor holdMotor = Motor.A;
		NXTRegulatedMotor defMotor = Motor.C;

		Offense off = new Offense(loadMotor, holdMotor);
		Defense def = new Defense(defMotor);

		NXTConnection connection = Bluetooth.waitForConnection();
		DataInputStream dis = connection.openDataInputStream();

		try {
			command = dis.readInt();
			LCD.clear();
			LCD.drawString("Command received.", 0, 0);
		} catch (IOException e) {
		}

		// Take command and use it to determine desired action (1 = offense, 2 =
		// defense)
		if (command == 1) {
			off.run();
			LCD.clear();
			LCD.drawString("Running OFFENSE.", 0, 0);
			command = 0;
		} else if (command == 2) {
			def.run();
			LCD.clear();
			LCD.drawString("Running DEFENSE.", 0, 0);
		}
		
		// Try to receive another command if trying to shoot again
		try {
			command = dis.readInt();
			LCD.clear();
			LCD.drawString("Command received.", 0, 0);
		} catch (IOException e) {
		}
		if (command == 1) {
			off.run();
			LCD.drawString("Running OFFENSE.", 0, 0);
			LCD.clear();
		}

		Button.waitForPress();

	}
}
