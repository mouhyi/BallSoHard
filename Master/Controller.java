package Master;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.*;
import bluetooth.*;

/**
 * This is the main class that starts all the other threads.
 * 
 * @author Mouhyi
 */
public class Controller {

	public static void main(String[] args) {

		// RConsole.openBluetooth((30000));

		Button.waitForPress();

		LCD.drawString("Started", 0, 0);

		// BluetoothConnection conn = new BluetoothConnection();
		// Transmission t = conn.getTransmission();
		//
		// int w1 = 0;
		// int w2 = 0;
		// int bx = 0;
		// int by = 0;
		// int bsigma = 0;
		//
		//
		// if (t == null) {
		// LCD.drawString("Failed to read transmission", 0, 5);
		// } else {
		// StartCorner corner = t.startingCorner;
		// PlayerRole role = t.role;
		// w1 = t.w1;
		// w2 = t.w2;
		// bx = t.bx;
		// by = t.by;
		// bsigma = t.bsigma;
		//
		// // print out the transmission information
		// conn.printTransmission();
		// }

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
		
	    LCD.clear();
	    LCD.drawString("Connected", 0, 0);

		DataOutputStream dos = connection.openDataOutputStream();

		// Robot robot = new Robot(SystemConstants.leftMotor,
		// SystemConstants.rightMotor);
		// Odometer odo = new Odometer(robot);
		// OdoCorrection snapper = new OdoCorrection(odo, robot);
		// snapper.setEnabled(true);
		// LineDetector.init(snapper, SystemConstants.FORWARD_SPEED);
		// ObstacleDetection us = new ObstacleDetection(new USPoller(
		// SystemConstants.USL), new USPoller(SystemConstants.USR));
		// Navigation nav = new Navigation(odo, robot, us, snapper);
		// Printer lcd = new Printer(odo);

		// Drive Square
		/*
		 * nav.travelTo(SystemConstants.TILE, 0);
		 * nav.travelTo(2*SystemConstants.TILE, 0);
		 * nav.travelTo(2*SystemConstants.TILE, SystemConstants.TILE);
		 * nav.travelTo(2*SystemConstants.TILE, 2*SystemConstants.TILE);
		 * nav.travelTo(SystemConstants.TILE, 2*SystemConstants.TILE);
		 * nav.travelTo(0, 2*SystemConstants.TILE); nav.travelTo(0,
		 * SystemConstants.TILE); nav.travelTo(0, 0);
		 */

		// nav.GoTo(2 * SystemConstants.TILE, 1 * SystemConstants.TILE);

		// Shoot the ball (writing 1 means shoot)
		try {
			dos.writeInt(1);
			dos.flush();
			LCD.drawString("1 sent", 0, 2);
		} catch (IOException e) {
		}
		
		try {
			dos.close();
			connection.close();
		} catch (IOException e) {
		}

		// nav.travelTo(SystemConstants.TILE, 0);
		// nav.turnTo(90);
		// RConsole.println("Finished.");

		LCD.drawString("Finished.", 0, 2);

		Button.waitForPress();

	}
}
