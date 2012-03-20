import lejos.nxt.Button;


/**
 * This is the main class that starts all the other threads.
 *
 * @author Mouhyi 
 */
public class Controller {
	
	public static void main(String[] args){

		Robot robot = new Robot(SystemConstants.leftMotor, SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		Printer lcd = new Printer(odo);
		
		/**
		 * Testing: Drive 2 TILES
		 */
		Button.waitForPress();
		
		//robot.rotateWheels(4*360);
		for(int i=0; i<2; i++ ){
			robot.drive(SystemConstants.TILE*2);
			robot.rotateAxis(90);
		}

		Button.waitForPress();

	}
	
	
	
}
