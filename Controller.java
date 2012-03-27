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
	
	public static void main(String[] args){
		
		String slaveName = "T10S";
		
		//Connect bluetooth device
		RemoteDevice slave = Bluetooth.getKnownDevice(slaveName);
		
		if(slave == null){
			LCD.clear();
			LCD.drawString("Device not found",0, 0);
			LCD.refresh();
			try{
				Thread.sleep(2000);
			}catch(Exception e){}
		}
		
		BTConnection bluetooth = Bluetooth.connect(slave);
		
		
		
//		RConsole.openBluetooth(30000);

		/*Button.waitForPress();
		
		BluetoothConnection conn = new BluetoothConnection();
		Transmission t = conn.getTransmission();
		
		int w1 = 0;
		int w2 = 0;
		int bx = 0;
		int by = 0;
		int bsigma = 0;
		
		if (t == null){
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
		
		Robot robot = new Robot(SystemConstants.leftMotor, SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);
		
		LineDetector.init(snapper, SystemConstants.FORWARD_SPEED);
		
		Navigation nav = new Navigation(odo, robot);
		
		Printer lcd = new Printer(odo);
		*/
//		nav.travelTo((double)bx, (double)by);
		
		// Drive Square
		//nav.travelTo(2*SystemConstants.TILE, 0);
		/*nav.travelTo(2*SystemConstants.TILE, 3*SystemConstants.TILE);
		nav.travelTo(0, 2*SystemConstants.TILE);
		nav.travelTo(0, 0);*/
		
		/*
		nav.turnTo(10);
		robot.goForward(SystemConstants.TILE, (int) SystemConstants.FORWARD_SPEED);
		*/
		
		//LCD.drawString("ARRIVED           ", 0, 5);
	
		Button.waitForPress();

	}
	
	
	
}
