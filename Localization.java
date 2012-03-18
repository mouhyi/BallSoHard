/**
* This class is responsible for performing the localization routine
* USlocalization is executed first to orients the robot parallel to the Y-axis
* LSlocalization is then executed to position the robot to (0,0.Pi/2)
*
* @author Mouhyi, Joshua David Alfaro
*/

import lejos.nxt.*;

public class Localization {

	// Fields
	private Coordinates coords;
	private Robot robot;
	private LightSensor lsL;
	private LightSensor lsR;
	private USPoller uspL;
	private USPoller uspR;

	// Constructor
	public Localization(Coordinates coords, Robot robot, LightSensor lsL, LightSensor lsR, USPoller uspL, USPoller uspR) {
		this.coords = coords;
		this.robot = robot;
		this.lsL = lsL;
		this.lsR = lsR;
		this.uspL = uspL;
		this.uspR = uspR;
	}
	
	
	/**
	 * main Localization routine
	 *
	 * @author Mouhyi
	 */
	public void doLocalization() {
		this.doUSLocalization();
	}

	/**
	 * US Localization routine: falling edge
	 *
	 * @author Mouhyi, Joshua David Alfaro
	 */
	private  void doUSLocalization() {
		// Falling Edge Method
		double angleA = 0, angleB = 0;
		
		int noWall = 0;
		boolean first_rot = true;
		boolean second_rot = false;
		while (first_rot)
		{
			robot.rotate(SystemConstants.ROTATION_SPEED);	//rotate counterclockwise

			// Reads no wall
			if (this.uspL.filter() > 50)
			{
				noWall = noWall + 1;
			}

			// Reads wall after reading no wall (falling edge)
			if (noWall >  3 && this.uspL.filter() < 30)
			{
				first_rot = false;
				second_rot = true;
				noWall = 0;
				angleA = coords.getTheta(); // latch angle
			}
		}

		while (second_rot)
		{
			robot.rotate(-SystemConstants.ROTATION_SPEED);		// rotate clockwise
			
			// Reads no wall
			if (this.uspR.filter() > 50)
			{
				noWall = noWall + 1;
			}

			// Reads wall after reading no wall (falling edge)
			if (noWall > 3 && this.uspR.filter() < 30)
			{
				second_rot = false;
				noWall = 0;
				angleB = coords.getTheta(); // latch angle
			}
			
			
		}
		robot.stop();
		double theta = 45 + Odometer.adjustAngle(angleA - angleB)/2;
		
		coords.set(0,0,theta);
	}

	/**
	 * Light Localization routine
	 *
	 * @author Mouhyi
	 */
	private void doLightLocalization() {

	}
}

