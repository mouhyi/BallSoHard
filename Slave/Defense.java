package Slave;

import lejos.nxt.NXTRegulatedMotor;

/**
 * This class allows the robot to block an opponent's balls by deploying a
 * mechanical defense mechanism.
 * 
 * @author Anthony
 * 
 */

public class Defense implements Runnable {

	private NXTRegulatedMotor defMotor;

	/**
	 * Constructor for Defense
	 * 
	 * @author Anthony
	 */
	public Defense(NXTRegulatedMotor defMotor) {
		this.defMotor = defMotor;
	}

	/**
	 * This method implements run method of the Runnable interface. Deploys the
	 * mechanical defense mechanism.
	 * 
	 * @author Anthony
	 */

	public void run() {
		// Rotate defensive motor 180 degrees to raise the defense mechanism
		defMotor.rotate(180);
	}

}
