package Master;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import bluetooth.*;

/**
 * This is the main class that starts all the other threads.
 * 
 * @author Mouhyi
 */
public class Controller {

	public static void main(String[] args) {

		RConsole.openBluetooth((30000));

		Button.waitForPress();
		

		/*
		 * BluetoothConnection conn = new BluetoothConnection(); Transmission t
		 * = conn.getTransmission();
		 * 
		 * int w1 = 0; int w2 = 0; int bx = 0; int by = 0; int bsigma = 0;
		 * 
		 * if (t == null){ LCD.drawString("Failed to read transmission", 0, 5);
		 * } else { StartCorner corner = t.startingCorner; PlayerRole role =
		 * t.role; w1 = t.w1; w2 = t.w2; bx = t.bx; by = t.by; bsigma =
		 * t.bsigma;
		 * 
		 * // print out the transmission information conn.printTransmission(); }
		 */

		Robot robot = new Robot(SystemConstants.leftMotor,
				SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);

		
		LineDetector.init(snapper, SystemConstants.FORWARD_SPEED);
		
		
		ObstacleDetection us = new ObstacleDetection(new USPoller(
				SystemConstants.USL), new USPoller(SystemConstants.USR));
		
		Navigation nav = new Navigation(odo, robot, us, snapper);
		
		Localization localizer = new Localization(odo, robot, new LightSensor(SensorPort.S1), new LightSensor(SensorPort.S4),
				new USPoller(new UltrasonicSensor(SensorPort.S2)), new USPoller(new UltrasonicSensor(SensorPort.S3)));

		

		Printer lcd = new Printer(odo);

		// Drive Square
		
		/* nav.travelTo(SystemConstants.TILE, 0);
		 
		 nav.travelTo(2*SystemConstants.TILE, 0);
		 
		 nav.travelTo(2*SystemConstants.TILE, SystemConstants.TILE);
		 nav.travelTo(2*SystemConstants.TILE, 2*SystemConstants.TILE);
		 nav.travelTo(SystemConstants.TILE, 2*SystemConstants.TILE);
		 nav.travelTo(0, 2*SystemConstants.TILE); nav.travelTo(0,SystemConstants.TILE);
		 nav.travelTo(0, 0);*/
		 

		//nav.GoTo(SystemConstants.TILE, 1 * SystemConstants.TILE);
		//nav.travelTo(2* SystemConstants.TILE, 0);
		
		nav.travelTo(1*SystemConstants.TILE, 0);
		nav.travelTo(2*SystemConstants.TILE, 0);
		nav.travelTo(3*SystemConstants.TILE, 0);
		nav.travelTo(4*SystemConstants.TILE, 0);
		nav.travelTo(5*SystemConstants.TILE, 0);
		
		
		nav.travelTo(5*SystemConstants.TILE, 1*SystemConstants.TILE);
		nav.travelTo(5*SystemConstants.TILE, 2*SystemConstants.TILE);
		nav.travelTo(5*SystemConstants.TILE, 3*SystemConstants.TILE);
		nav.travelTo(5*SystemConstants.TILE, 4*SystemConstants.TILE);
		nav.travelTo(5*SystemConstants.TILE, 5*SystemConstants.TILE);
		
		localizer.MidLocalization(odo.getCoordinates().getX(),odo.getCoordinates().getY(), odo.getCoordinates().getTheta());
		
		//nav.travelTo(4*SystemConstants.TILE, 4*SystemConstants.TILE );
		//nav.turnTo(90);
		
		
		RConsole.println("END Conroller ");
		
		Button.waitForPress();

	}

}