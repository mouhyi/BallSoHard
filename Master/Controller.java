package Master;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.*;
import bluetooth.*;

/**
 * This is the main class that starts all the other threads.
 * 
 * @author Mouhyi
 */
public class Controller {

	public static void main(String[] args) {

		RConsole.openBluetooth((30000));
		
		Sound.setVolume(30);
		Sound.beepSequence();
		Button.waitForPress();
		
		Robot robot = new Robot(SystemConstants.leftMotor, SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		OdoCorrection snapper = new OdoCorrection(odo, robot);
		LineDetector.init(snapper, SystemConstants.FORWARD_SPEED);
		ObstacleDetection us = new ObstacleDetection(new USPoller(SystemConstants.USL), new USPoller(SystemConstants.USR));
		Navigation nav = new Navigation(odo, robot, us, snapper);
		Printer lcd = new Printer(odo);

		//Clear out initial light sensor values
		try{
			Thread.sleep(2000);
		}catch(Exception e){}
		snapper.setEnabled(true);
		nav.setAvoidance(false);
		
	/*	nav.travelTo(2*SystemConstants.TILE, 0);
		RConsole.println("Travelled to (60,0)");
		snapper.setEnabled(false);
		RConsole.println("Disabled snapper");
		nav.turnTo(90);
		RConsole.println("Turned to 90");
		snapper.setEnabled(true);
		RConsole.println("Enabled snapper");
		nav.travelTo(2*SystemConstants.TILE, 2*SystemConstants.TILE);
		RConsole.println("Traveled to 60, 60");
		snapper.setEnabled(false);
		nav.turnTo(180);
		snapper.setEnabled(true);
		nav.travelTo(0, 2*SystemConstants.TILE);
	*/
	//	RConsole.println("Travelled to destination");
		
		nav.travelTo(2*SystemConstants.TILE, 0);
		nav.travelTo(2*SystemConstants.TILE,2*SystemConstants.TILE);
		
		
//		nav.GoTo(2*SystemConstants.TILE, 2*SystemConstants.TILE);
//		nav.GoTo(0, 0);
//		
		RConsole.println("Finished.");
		
		
	//	System.exit(0);
	}
}
