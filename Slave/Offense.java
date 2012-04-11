package Slave;

import lejos.nxt.*;

/**
 * This class allows the robot to shoot balls that it has obtained from the
 * dispenser.
 * 
 * @author Anthony, Ze
 * 
 */

public class Offense implements Runnable {

	private NXTRegulatedMotor loadMotor, holdMotor;

	/**
	 * Constructor for Offfense
	 * 
	 * @author Anthony
	 */
	public Offense(NXTRegulatedMotor loadMotor, NXTRegulatedMotor holdMotor) {
		this.loadMotor = loadMotor;
		this.holdMotor = holdMotor;
	}

	/**
	 * This method implements run method of the Runnable interface. Shoots the
	 * ball 3 times.
	 * 
	 * @author Anthony, Ze
	 */

	public void run() {
		int numShots = 3;

		for (int i = 0; i < numShots; i++) {
			shoot();
		}
	}

	/**
	 * This method controls the motors to shoot the ball.
	 * 
	 * @author Anthony, Ze
	 */

	private void shoot() {
		// Motor.A is the lock
		// Motor.B pulls down the catapult's arm

		// unlock
		holdMotor.rotate(-55);

		// pull down the arm
		loadMotor.backward();

		// wait while the arm is being pulled down
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		// lock
		holdMotor.forward();

		// stop pulling down the arm
		loadMotor.stop();

		// release the string
		loadMotor.rotate(180);

		// unlock&launch the ball
		holdMotor.stop();
		holdMotor.rotate(-40);

		// move back to original position
		holdMotor.rotate(40);
	}
}
