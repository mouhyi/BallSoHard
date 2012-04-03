package Master;

/**

 * This class is responsible for performing the localization routine
 * USlocalization is executed first to orients the robot parallel to the Y-axis
 * LSlocalization is then executed to position the robot to (0,0.Pi/2)
 *
 * @author Mouhyi, Joshua David Alfaro
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
		this.doLightLocalization3();
		robot.rotateAxis(90, 5);
		this.doLightLocalization3();
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
	 * Light Localization routine
	 * 
	 * @author Mouhyi, JDAlfaro
	 */
	private void doLightLocalization() {
		// Timer for light sensor depending on speed of robot.
		robot.rotate(40);
		LightTimer leftLight = new LightTimer(lsL);
		LightTimer rightLight = new LightTimer(lsR);

		boolean leftDone = true;
		int gridLineCountL = 0;
		boolean linePassedL = false;
		double[] angleL = { 0, 0, 0, 0 };

		boolean rightDone = true;
		int gridLineCountR = 0;
		boolean linePassedR = false;
		double[] angleR = { 0, 0, 0, 0 };

		double xOffset, yOffset, thetaOffset;

		while (leftDone || rightDone) {
			robot.rotate(40);

			if (leftLight.lineDetected()) {
				gridLineCountL = gridLineCountL + 1;
				linePassedL = true;
				Sound.setVolume(20);
				Sound.beep();
			}

			if (rightLight.lineDetected()) {
				gridLineCountR = gridLineCountR + 1;
				linePassedR = true;
				Sound.setVolume(20);
				Sound.beep();
			}

			// latch angles into memory only after a line pass
			if (linePassedL) {
				switch (gridLineCountL) {
				case 1:
					angleL[gridLineCountL - 1] = odo.getCoordinates()
							.getTheta();
					break;
				case 2:
					angleL[gridLineCountL - 1] = odo.getCoordinates()
							.getTheta();
					// To avoid wrap around which would currently ruin
					// computation
					if (angleL[gridLineCountL - 1] < angleL[gridLineCountL - 2]) {
						angleL[gridLineCountL - 1] += 360;
					}
					break;
				case 3:
					angleL[gridLineCountL - 1] = odo.getCoordinates()
							.getTheta();
					// To avoid wrap around which would currently ruin
					// computation
					if (angleL[gridLineCountL - 1] < angleL[gridLineCountL - 2]) {
						angleL[gridLineCountL - 1] += 360;
					}
					break;
				case 4:
					angleL[gridLineCountL - 1] = odo.getCoordinates()
							.getTheta();
					// To avoid wrap around which would currently ruin
					// computation
					if (angleL[gridLineCountL - 1] < angleL[gridLineCountL - 2]) {
						angleL[gridLineCountL - 1] += 360;
					}
					leftDone = false;
					break;
				default:
					break;
				}

				linePassedL = false;
				leftLight.resetLine();
			}

			// latch angles into memory only after a line pass
			if (linePassedR) {
				switch (gridLineCountR) {
				case 1:
					angleR[gridLineCountR - 1] = odo.getCoordinates()
							.getTheta();
					break;
				case 2:
					angleR[gridLineCountR - 1] = odo.getCoordinates()
							.getTheta();
					// To avoid wrap around which would currently ruin
					// computation
					if (angleR[gridLineCountR - 1] < angleR[gridLineCountR - 2]) {
						angleR[gridLineCountR - 1] += 360;
					}
					break;
				case 3:
					angleR[gridLineCountR - 1] = odo.getCoordinates()
							.getTheta();
					// To avoid wrap around which would currently ruin
					// computation
					if (angleR[gridLineCountR - 1] < angleR[gridLineCountR - 2]) {
						angleR[gridLineCountR - 1] += 360;
					}
					break;
				case 4:
					angleR[gridLineCountR - 1] = odo.getCoordinates()
							.getTheta();
					// To avoid wrap around which would currently ruin
					// computation
					if (angleR[gridLineCountR - 1] < angleR[gridLineCountR - 2]) {
						angleR[gridLineCountR - 1] += 360;
					}
					rightDone = false;
					break;
				default:
					break;
				}

				linePassedR = false;
				rightLight.resetLine();
			}
		}
		robot.stop(); // stop doing moving and leave loop to perform
						// calculations

		// Stop timers for light timer detection since it is no longer needed.
		leftLight.stop();
		rightLight.stop();

		// do trigonometry to compute with reference to real (0,0) and 0 degrees
		xOffset = ((-1) * SystemConstants.LS_TOCENTRE
				* Math.cos(Math.toRadians((angleL[2] - angleL[0]) / 2)) + (-1)
				* SystemConstants.LS_TOCENTRE
				* Math.cos(Math.toRadians((angleR[2] - angleR[0]) / 2))) / 2;
		yOffset = ((-1) * SystemConstants.LS_TOCENTRE
				* Math.cos(Math.toRadians((angleL[3] - angleL[1]) / 2)) + (-1)
				* SystemConstants.LS_TOCENTRE
				* Math.cos(Math.toRadians((angleR[3] - angleR[1]) / 2))) / 2;

		// needs work, WORSE CASE = set to 30 degrees since it seems to stop
		// around there.
		// y = mx + b
		double m = 90 / ((angleR[3] + angleL[1]) / 2 - (angleR[2] + angleL[0]) / 2);
		double b = 180 - (m) * (angleR[2] + angleL[0]) / 2;

		thetaOffset = Odometer.adjustAngle(m * odo.getCoordinates().getTheta()
				+ b);

		odo.setCoordinates(xOffset, yOffset, thetaOffset, new boolean[] { true,
				true, true });
	}

	/**
	 * Light Localization routine 2 Drive straight towards line and basically
	 * use odometry correction style correction This is potentially more
	 * accurate but more time consusming Not sure if needed if odometry
	 * correction works
	 * 
	 * @author Mouhyi, JDAlfaro
	 */
	private void doLightLocalization2() // not tested, is another idea, not
										// finished
	{
		double time_left = 0, time_right = 0;
		boolean leftNotSeen = true, rightNotSeen = true;

		robot.advance(5); // for some reason it needs to be called twice to work
		LightTimer leftLight = new LightTimer(lsL);
		LightTimer rightLight = new LightTimer(lsR);

		while (leftNotSeen || rightNotSeen) {
			if (leftLight.lineDetected()) {
				time_left = System.currentTimeMillis();
				leftNotSeen = false;
				Sound.setVolume(20);
				Sound.beep();
			}

			if (rightLight.lineDetected()) {
				time_right = System.currentTimeMillis();
				rightNotSeen = false;
				Sound.setVolume(20);
				Sound.beep();
			}
		}

		double time_difference = Math.abs(time_right - time_left);

		double theta = Math.atan(time_difference
				* SystemConstants.FORWARD_SPEED / SystemConstants.LS_WIDTH);

		if (time_right > time_left) {
			robot.rotateAxis(90 + theta, 40);
		} else {
			robot.rotateAxis(90 - theta, 40);
		}

		odo.setCoordinates(SystemConstants.LS_WIDTH * Math.tan(theta) / 2, 0.0,
				0, new boolean[] { true, false, true });
	}

	/**
	 * Light Localization routine 3 This method of localization involves driving
	 * towards the line, and stopping the left and right wheels when their
	 * respective light sensors detect a line, essentially lining up the robot
	 * on a line.
	 * 
	 * @author Anthony, Josh
	 */

	private void doLightLocalization3() {

		// Initialize two booleans that represent whether or not the right
		// and left sensors have seen lines
		boolean rightSeen = false, leftSeen = false;

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
		RConsole.print("" + currentAngle);
		if (rightSeen) {
			robot.advanceLeft(75);
			robot.advanceLeft(75);
			while (!leftSeen) {
				/*
				 * if(currentAngle - odo.getCoordinates().getTheta() > 30) {
				 * robot.advanceLeft(-25); } else { robot.advanceLeft(25); }
				 */
				// robot.advanceLeft(25);
				if (leftLight.lineDetected()) {
					leftSeen = true;
					Sound.setVolume(70);
					Sound.beep();
					robot.stopLeft();
				}
			}
		} else if (leftSeen) {
			robot.advanceRight(75);
			robot.advanceRight(75);
			while (!rightSeen) {
				/*
				 * if(currentAngle - odo.getCoordinates().getTheta() < -30) {
				 * robot.advanceRight(-25); } else { robot.advanceRight(25); }
				 */
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
