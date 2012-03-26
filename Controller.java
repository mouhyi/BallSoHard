import lejos.nxt.Button;
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
		
		Printer lcd = new Printer(odo);
		
	
		
	
		Button.waitForPress();

	}
	
	
	
}
