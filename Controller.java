import lejos.nxt.Button;
import lejos.nxt.comm.RConsole;


/**
 * This is the main class that starts all the other threads.
 *
 * @author Mouhyi 
 */
public class Controller {
	
	public static void main(String[] args){
		
		RConsole.openBluetooth(30000);

		Button.waitForPress();
		
		Robot robot = new Robot(SystemConstants.leftMotor, SystemConstants.rightMotor);
		Odometer odo = new Odometer(robot);
		/*OdoCorrection snapper = new OdoCorrection(odo, robot);
		snapper.setEnabled(true);
		
		LineDetector.init(snapper);*/
		
		Printer lcd = new Printer(odo);
		
		/**
		 * Testing: Drive 2 TILES
		 */
		
		
		//robot.rotateWheels(4*360);
		for(int i=0; i<4; i++ ){
			//snapper.setEnabled(true);
			robot.goForward(SystemConstants.TILE*2, (int)SystemConstants.FORWARD_SPEED);
			//snapper.setEnabled(false);
			robot.rotateAxis(90, (int) SystemConstants.ROTATION_SPEED);
			//snapper.setEnabled(true);
			
			/*LCD.drawString("TILE         ", 0, 6);
			try {
				Thread.sleep(3000);
				LCD.drawString("          ", 0, 6);
			} catch (InterruptedException e) {}*/
		}
		
		
	
		Button.waitForPress();

	}
	
	
	
}
