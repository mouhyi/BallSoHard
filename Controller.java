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
		//Odometer odo = new Odometer(robot);
		/*OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);
		
		LineDetector.init(snapper);*/
		
		//Printer lcd = new Printer(odo);
		
		/**
		 * Testing: Tweek SystemConstants.WIDTH
		 * Rotate the robot 'turnsNumber' times about its center and change the value
		 * of SystemConstants.WIDTH Accordingly until it comes back exactly
		 * to its starting position.
		 * if robot stops beyond the starting position : decrement WIDTH
		 * otherwise, increment WIDTH
		 */
		
		int turnsNumber = 6;  // start with 6 and until 10
		robot.rotateAxis(turnsNumber *360, (int) SystemConstants.ROTATION_SPEED);
	

		
	
		Button.waitForPress();

	}
	
	
	
}
