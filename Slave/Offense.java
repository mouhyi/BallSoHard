package Slave;

import lejos.nxt.*;

/**
 * This class allows the robot to shoot balls that it has obtained from the
 * dispenser.
 * 
 * @author Anthony
 * 
 */

public class Offense implements Runnable {

	private NXTRegulatedMotor loadMotor, holdMotor;

	/**
	 * This method implements run method of the Runnable interface. Shoots the
	 * ball.
	 * 
	 * @author Anthony
	 */

	public Offense(NXTRegulatedMotor loadMotor, NXTRegulatedMotor holdMotor) {
		this.loadMotor = loadMotor;
		this.holdMotor = holdMotor;
	}

	public void run() {
		// Motor.A is the lock
		// Motor.B pulls down the catapult's arm

		// unlock
		holdMotor.rotate(-35);

		// pull down the arm
		loadMotor.rotate(-250);

		// lock
		holdMotor.rotate(35);

		// release the string
		loadMotor.rotate(250);

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		// unlock&launch the ball
		holdMotor.rotate(-45);

		// move back to original position
		holdMotor.rotate(45);

	}

}
