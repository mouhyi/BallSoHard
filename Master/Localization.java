package Master;

/**

 * This class is responsible for performing the localization routine
 * USlocalization is executed first to orients the robot parallel to the Y-axis
 * LSlocalization is then executed to position the robot to (0,0.Pi/2)
 *
 * @author Mouhyi, Joshua David Alfaro, Anthony
 */

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Localization {

	// Constants
	private final int ERROR_THRESHOLD = 100;

	// Fields
	private Odometer odo;
	private Robot robot;
	private LightSensor lsL;
	private LightSensor lsR;
	private USPoller uspL;
	private USPoller uspR;

	// Constructor
	public Localization(Odometer odo, Robot robot, LightSensor lsL,
			LightSensor lsR, USPoller uspL, USPoller uspR) {
		this.odo = odo;
		this.robot = robot;
		this.lsL = lsL;
		this.lsR = lsR;
		this.uspL = uspL;
		this.uspR = uspR;
	}

	/**
	 * main Localization routine
	 * 
	 * @author Mouhyi, JDAlfaro
	 */
	public void doLocalization(int corner) {

		// Perform the localization
		robot.setAcceleration(1500);
		this.doUSLocalization();
		this.doLightLocalization();
		robot.rotateAxis(90, 5);
		this.doLightLocalization();
		robot.rotateAxis(-90, 5);
		robot.setAcceleration(6000);

		// Adjust Odometer depending on starting location
		switch (corner) {
		case 1:
			odo.setCoordinates(0, 0, 0, new boolean[] { true, true, true });
			break;
		case 2:
			odo.setCoordinates(304.8, 0, 90, new boolean[] { true, true, true });
			break;
		case 3:
			odo.setCoordinates(304.8, 304.8, 180, new boolean[] { true, true,
					true });
			break;
		case 4:
			odo.setCoordinates(0, 304.8, 270,
					new boolean[] { true, true, true });
			break;
		default:
			break;
		}

	}
	
	/**
	 * Localization routine for after travel
	 * 
	 * @author Mouhyi, JDAlfaro, Anthony
	 * 
	 * @Note It follows the same algorithm of light localization but uses the data from the odometer to determine the closes intersection
	 */
	public void MidLocalization(double x, double y, double theta){
		robot.setAcceleration(1500);
		
		// Determine closest intersection
		double xAxis = Math.round(x / SystemConstants.TILE ) *SystemConstants.TILE;
		double yAxis = Math.round(y/ SystemConstants.TILE ) * SystemConstants.TILE;

		/*
		 * Tweaked the distances that it reverses before looking for lines
		 * @author Ryan
		 */
		
		// BOTTOM LEFT of desired intersection with respect to positive axis
		if (x < xAxis && y < yAxis) {
			// Sets up for light localization
			robot.rotateAxis(0 - theta, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(90, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(theta - 90, 5);
		}
		// BOTTOM RIGHT of desired intersection with respect to positive axis
		else if (x > xAxis && y < yAxis){
			// Sets up for light localization
			robot.rotateAxis(90 - theta, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(90, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(theta - 180, 5);
		}
		// TOP RIGHT of desired intersection with respect to positive axis
		else if (x > xAxis && y > yAxis){
			// Sets up for light localization
			robot.rotateAxis(180 - theta, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(90, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(theta - 270, 5);
		}
		// TOP LEFT of desired intersection with respect to positive axis
		else if (x < xAxis && y > yAxis){
			// Sets up for light localization
			robot.rotateAxis(270 - theta, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(90, 5);
			robot.goForward(-7, 5);
			this.doLightLocalization();
			robot.rotateAxis(theta - 0, 5);
		}
		
		odo.setCoordinates(xAxis, yAxis, theta, new boolean[] { true, true, true });
		robot.setAcceleration(6000);
	}

	/**
	 * US Localization routine: falling edge
	 * 
	 * @author Mouhyi, JDAlfaro
	 */
	private void doUSLocalization() // Falling and Rising Edge Method
	{
		robot.rotate(-50); // rotate clockwise
		robot.rotate(-50);

		// Falling and Rising Edge Method
		double angleA = 0, angleB = 0;

		int Wall = 0;
		int noWall = 0;
		boolean first = true;
		boolean second = false;
		while (first) // Checks for rising edge
		{
	//		RConsole.println("" + uspL.getDistance());
			// Reads wall
			if (uspL.getDistance() < 30 && uspR.getDistance() < 30) {
				Wall = Wall + 1;
			}

			// Reads no wall after reading wall (falling edge)
			if (Wall > 3 && uspL.getDistance() > 50 && uspR.getDistance() > 50) {
				Sound.setVolume(20);
				Sound.beep();
				first = false;
				second = true;
				Wall = 0;
				angleA = odo.getCoordinates().getTheta(); // latch angle
				// LCD.drawString("anlgeA " + angleA + " ", 0, 3);
			}
		}

		while (second) // Checks for falling edge
		{
			// Reads no wall
			if (uspL.getDistance() > 50 && uspR.getDistance() > 50) {
				noWall = noWall + 1;
			}

			// Reads wall after reading no wall (rising edge)
			if (noWall > 3 && uspL.getDistance() < 30
					&& uspR.getDistance() < 30) {
				Sound.setVolume(20);
				Sound.beep();
				second = false;
				noWall = 0;
				angleB = odo.getCoordinates().getTheta(); // latch angle
				// LCD.drawString("angleB " + angleB + " ", 0, 4);
			}

		}
		robot.stop();
		if (Odometer.adjustAngle(angleA - angleB) < ERROR_THRESHOLD) {
			this.doUSLocalization();
			return;
		}

		// Computation of new angle
		double theta = Odometer.adjustAngle(30 - Odometer.adjustAngle(angleA
				- angleB) / 2);

		LCD.drawString("newtheta " + theta + " ", 0, 5);

		robot.rotateAxis(360 - theta, 5);
	}

	/**
	 * Light Localization routine This method of localization involves driving
	 * towards the line, and stopping the left and right wheels when their
	 * respective light sensors detect a line, essentially lining up the robot
	 * on a line.
	 * 
	 * @author Anthony, Josh
	 */

	private void doLightLocalization() {

		// Initialize two booleans that represent whether or not the right
		// and left sensors have seen lines
		boolean rightSeen = false, leftSeen = false;
		
		/*
		 * Initialize another boolean for the case when one line isn't detected
		 * @author Ryan
		 */
		
		boolean lineMissed = false;

		// Initialize lightTimer objects
		LightTimer leftLight = new LightTimer(lsL);
		LightTimer rightLight = new LightTimer(lsR);

		// Advance the robot until one light sensor sees a line. Stop the
		// correct wheel.
		robot.advance(5);
		robot.advance(5);
		while (!rightSeen && !leftSeen) {

			// robot.advance(5);
			if (leftLight.lineDetected()) {
				leftSeen = true;
				Sound.setVolume(70);
				Sound.beep();
				robot.stopLeft();
			} else if (rightLight.lineDetected()) {
				rightSeen = true;
				Sound.setVolume(70);
				Sound.beep();
				robot.stopRight();
			}
		}

		// Keep making the wheel that hasn't seen a line go forward until it
		// sees a line
		double currentAngle = odo.getCoordinates().getTheta();
	//	RConsole.print("" + currentAngle);
		if (rightSeen) {
			robot.advanceLeft(75);
			robot.advanceLeft(75);
			while (!leftSeen) {
				  if(currentAngle - odo.getCoordinates().getTheta() > 30) {
					  if(!lineMissed){
						  robot.stopLeft();
						  lineMissed = true;
					  }
					  robot.advanceLeft(-75);robot.advanceLeft(-75); 
					  try{
						  Thread.sleep(3500);
					  }catch(Exception e){}
					  leftSeen = false;
				 } 
				  else if(currentAngle - odo.getCoordinates().getTheta() > 50){
					  robot.advanceLeft(-75);robot.advanceLeft(-75);
					  try{
						  Thread.sleep(3500);
					  }catch(Exception e){}
				  }
				  else { robot.advanceLeft(75);robot.advanceLeft(75); 
				  }
				
				// robot.advanceLeft(25);
				if (leftLight.lineDetected()) {
					leftSeen = true;
					Sound.setVolume(70);
					Sound.beep();
					robot.stopLeft();
				}
			}
			lineMissed = false;
			
		} else if (leftSeen) {
			robot.advanceRight(75);
			robot.advanceRight(75);
			while (!rightSeen) {
				
				  if(currentAngle - odo.getCoordinates().getTheta() < -30) {
					  if(!lineMissed){
						  robot.stopRight();
						  lineMissed = true;
					  }
					  robot.advanceRight(-75);robot.advanceRight(-75); 
					  try{
						  Thread.sleep(3500);
					  }catch(Exception e){}
					  rightSeen = false;
				  } 
				  else if(currentAngle - odo.getCoordinates().getTheta() > 50){
					  robot.advanceLeft(-75);robot.advanceLeft(-75);
					  try{
						  Thread.sleep(3500);
					  }catch(Exception e){}
				  }
				  else {
					  robot.advanceRight(75);robot.advanceRight(75);
				  }
				 
				// robot.advanceRight(25);

				if (rightLight.lineDetected()) {
					rightSeen = true;
					Sound.setVolume(70);
					Sound.beep();
					robot.stopRight();
				}
			}
		}

		robot.goForward(-1.8, 5);
		leftLight.stop();
		rightLight.stop();
	}
}
