package Master;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import bluetooth.*;

/**
 * This is the main class that starts all the other threads.
 * 
 * @author Mouhyi
 */
public class Controller {

	public static void main(String[] args) {

//		// for now code
//				int startCorner = 3;
//				int job = 2;
//				int w1 = 4;
//				int w2 = 4;
//				
//				int bx = 11;
//				int by = 9;
//				int bsigma = 4;
	//	RConsole.openBluetooth((30000));
		//RConsole.openUSB(30000);
		//RConsole.open();
		Button.waitForPress(); 
		
		BluetoothConnection conn = new BluetoothConnection();
		Transmission t = conn.getTransmission();

		int w1 = 0, w2 = 0, bx = 0, by = 0, bsigma = 0;
		StartCorner corner = null;
		PlayerRole role = null;
		
		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			corner = t.startingCorner;
			role = t.role;
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
		snapper.setEnabled(false);

		
		LineDetector.init(snapper, SystemConstants.FORWARD_SPEED);
				
		ObstacleDetection us = new ObstacleDetection(new USPoller(
				SystemConstants.USL), new USPoller(SystemConstants.USR));
		

		Localization localizer = new Localization(odo, robot, new LightSensor(SensorPort.S1), new LightSensor(SensorPort.S4),
				new USPoller(new UltrasonicSensor(SensorPort.S2)), new USPoller(new UltrasonicSensor(SensorPort.S3)));
		
		

		Printer lcd = new Printer(odo);
		
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}

		int startCorner = corner.getId();
		int job = role.getId();

		// Foward
		if (job == 1) {
			Navigation nav = new Navigation(odo, robot, us, snapper, localizer,
					w1, w2);
			// Localize
			localizer.doLocalization(startCorner);
//			odo.setCoordinates(304.8, 304.8, 180, new boolean[] { true, true,
//					true });

			switch (startCorner) {
			case 1:
				nav.GoTo(2 * SystemConstants.TILE, 1 * SystemConstants.TILE);
				break;
			case 2:
				nav.GoTo(8 * SystemConstants.TILE, 1 * SystemConstants.TILE);
				break;
			case 3:
				nav.GoTo(8 * SystemConstants.TILE, 9 * SystemConstants.TILE);
				break;
			case 4:
				nav.GoTo(2 * SystemConstants.TILE, 9 * SystemConstants.TILE);
				break;
			}

			// odo.setCoordinates(8*SystemConstants.TILE, 1, 90);

			// Navigate to ball dispenser and press button
			nav.getBall(bx * SystemConstants.TILE, by * SystemConstants.TILE,
					bsigma);

			// Travel to shooting location
			nav.GoTo(2 * SystemConstants.TILE, 6 * SystemConstants.TILE);
			/*
			 * //Shoot ball try { dos.writeInt(1); dos.flush(); } catch
			 * (IOException e) { }
			 * 
			 * try { Thread.sleep(30000); } catch (InterruptedException e) { }
			 * 
			 * //Navigate to ball dispenser and press button
			 * nav.getBall(bx*SystemConstants.TILE, by*SystemConstants.TILE,
			 * bsigma);
			 * 
			 * //Travel to shooting location
			 * nav.GoTo(8*SystemConstants.TILE,6*SystemConstants.TILE);
			 * 
			 * //Shoot ball try { dos.writeInt(1); dos.flush(); } catch
			 * (IOException e) { }
			 * 
			 * try { Thread.sleep(30000); } catch (InterruptedException e) { }
			 */
		} else if (job == 2) {
			Navigation nav = new Navigation(odo, robot, us, snapper, localizer);
			
			localizer.doLocalization(startCorner);
//			odo.setCoordinates(304.8, 304.8, 180, new boolean[] { true, true,
//					true });

			switch (startCorner) {
			case 1:
				nav.GoTo(2 * SystemConstants.TILE, 1 * SystemConstants.TILE);
				break;
			case 2:
				nav.GoTo(8 * SystemConstants.TILE, 1 * SystemConstants.TILE);
				break;
			case 3:
				nav.GoTo(8 * SystemConstants.TILE, 9 * SystemConstants.TILE);
				break;
			case 4:
				nav.GoTo(2 * SystemConstants.TILE, 9 * SystemConstants.TILE);
				break;
			}
			nav.GoTo(5 * SystemConstants.TILE, 7 * SystemConstants.TILE);
			nav.turnTo(270);
			try {
				dos.writeInt(2);
				dos.flush();
			} catch (IOException e) {
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			robot.goForward(-30, 5);
		}
		RConsole.println("END Conroller ");

		Button.waitForPress();
	}

	// Defender

}