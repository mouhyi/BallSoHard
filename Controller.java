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
		OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);
		
		LineDetector.init(snapper);
		
		Printer lcd = new Printer(odo);
		
		/**
		 * Testing: Drive 2 TILES
		 */
		Button.waitForPress();
		
		//robot.rotateWheels(4*360);
		for(int i=0; i<4; i++ ){
			snapper.setEnabled(true);
			robot.drive(SystemConstants.TILE*2);
			snapper.setEnabled(false);
			robot.rotateAxis(90);
			snapper.setEnabled(true);
			
			/*LCD.drawString("TILE         ", 0, 6);
			try {
				Thread.sleep(3000);
				LCD.drawString("          ", 0, 6);
			} catch (InterruptedException e) {}*/
		}

		Button.waitForPress();

	}
	
	
	
}
