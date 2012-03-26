import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;


/**
 * This is the main class that starts all the other threads.
 *
 * @author Mouhyi 
 */
public class Controller {
	
	public static void main(String[] args){
		
		//RConsole.openBluetooth(30000);

		Button.waitForPress();
		
		Robot robot = new Robot(SystemConstants.leftMotor, SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);
		
		LineDetector.init(snapper, SystemConstants.FORWARD_SPEED);
		
		Navigation nav = new Navigation(odo, robot);
		
		Printer lcd = new Printer(odo);
		
		// Drive Square
		//nav.travelTo(2*SystemConstants.TILE, 0);
		/*nav.travelTo(2*SystemConstants.TILE, 3*SystemConstants.TILE);
		nav.travelTo(0, 2*SystemConstants.TILE);
		nav.travelTo(0, 0);*/
		
		robot.goForward(2*SystemConstants.TILE, (int) SystemConstants.FORWARD_SPEED);
		
		//LCD.drawString("ARRIVED           ", 0, 5);
	
		Button.waitForPress();

	}
	
	
	
}
