package Master;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.*;
import bluetooth.*;

/**
 * This is the main class for our demo.
 * 
 * @author Anthony
 */

public class DemoController {

	public static void main(String[] args) {

		Button.waitForPress();

		BluetoothConnection conn = new BluetoothConnection();
		Transmission t = conn.getTransmission();

		int w1 = 0, w2 = 0, bx = 0, by = 0, bsigma = 0;

		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			StartCorner corner = t.startingCorner;
			PlayerRole role = t.role;
			w1 = t.w1;
			w2 = t.w2;
			bx = t.bx;
			by = t.by;
			bsigma = t.bsigma;

			// print out the transmission information
			conn.printTransmission();
		}

		// Code for Bluetooth transmission to slave
		RemoteDevice btrd = Bluetooth.getKnownDevice("T10S");
		if (btrd == null) {
			LCD.clear();
			LCD.drawString("No such device", 0, 0);
			Button.waitForPress();
			System.exit(1);
		}

		NXTConnection connection = Bluetooth.connect(btrd);
		if (connection == null) {
			LCD.clear();
			LCD.drawString("Connect fail", 0, 0);
			Button.waitForPress();
			System.exit(1);
		}

		DataOutputStream dos = connection.openDataOutputStream();

		Robot robot = new Robot(SystemConstants.leftMotor,
				SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);
		ObstacleDetection us = new ObstacleDetection(new USPoller(
				SystemConstants.USL), new USPoller(SystemConstants.USR));
		Navigation nav = new Navigation(odo, robot, us, snapper);
		Printer lcd = new Printer(odo);
		
		for (int i = 0; i < 2; i++) {
			nav.travelTo(bx * SystemConstants.TILE, by * SystemConstants.TILE);
			
//			nav.travelTo(5 * SystemConstants.TILE, 3 * SystemConstants.TILE);
			nav.travelTo(3 * SystemConstants.TILE, 5 * SystemConstants.TILE - 84.0);
			nav.turnTo(90);
			
			// Shoot the ball (writing 1 means shoot)
			try {
				dos.writeInt(1);
				dos.flush();
			} catch (IOException e) {
			}
			
			try {
				Thread.sleep(7500);
			} catch (InterruptedException e) {
			}
			
		}

		try {
			dos.close();
			connection.close();
		} catch (IOException e) {
		}
		
		Button.waitForPress();
	}
}
