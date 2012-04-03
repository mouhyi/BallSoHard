package Master;

import lejos.nxt.*;

/**
 * This class contains general methods
 * 
 * @author Mouhyi
 */
public class Robot {

	private NXTRegulatedMotor leftMotor, rightMotor;

	/**
	 * Constructor
	 * 
	 * @param lMotor
	 *            The left motor of the robot.
	 * @param rMotor
	 *            The right motor of the robot.
	 * 
	 * @author Mouhyi
	 * 
	 */
	public Robot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	/**
	 * computes the total displacement of the robot relative to its original
	 * position in cm.
	 * 
	 * @author Mouhyi Validated: 03.17.2012 9:44 pm
	 */
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * SystemConstants.LEFT_RADIUS + rightMotor
				.getTachoCount() * SystemConstants.RIGHT_RADIUS)
				* Math.PI / 360.0;
	}

	/**
	 * return the robot's heading relative to its original orientation in
	 * degrees. Positive heading: counter clockwise!!
	 * 
	 * @author Mouhyi Validated: 03.17.2012 9:44 pm
	 */
	public double getHeading() {
		return (-leftMotor.getTachoCount() * SystemConstants.LEFT_RADIUS + rightMotor
				.getTachoCount() * SystemConstants.RIGHT_RADIUS)
				/ SystemConstants.WIDTH;
		// //// sign fixed
	}

	/**
	 * Sets both the forward(cm/s) and rotation(deg/s) speeds of the robot.
	 * 
	 * @author Mouhyi
	 */
	public void setSpeeds(double forwardSpeed, double rotationSpeed) {
		double leftSpeed, rightSpeed;

		/*
		 * this.forwardSpeed = forwardSpeed; this.rotationSpeed = rotationSpeed;
		 */

		leftSpeed = (forwardSpeed - rotationSpeed * SystemConstants.WIDTH
				* Math.PI / 360.0)
				* 180.0 / (SystemConstants.LEFT_RADIUS * Math.PI);
		rightSpeed = (forwardSpeed + rotationSpeed * SystemConstants.WIDTH
				* Math.PI / 360.0)
				* 180.0 / (SystemConstants.RIGHT_RADIUS * Math.PI);
		// convert forwardspeed -> deg/sec and use the formulas on Navigation
		// Tutorial

		// set motor directions
		if (leftSpeed > 0) {
			leftMotor.forward();
		} else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}

		if (rightSpeed > 0) {
			rightMotor.forward();
		} else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}

		// set motor speeds
		if (leftSpeed > 900) {
			leftMotor.setSpeed(900);
		} else {
			leftMotor.setSpeed((int) leftSpeed);
		}

		if (rightSpeed > 900) {
			rightMotor.setSpeed(900);
		} else {
			rightMotor.setSpeed((int) rightSpeed);
		}

	}

	/**
	 * Set acceleration
	 * 
	 * @author Anthony
	 */
	public void setAcceleration(int accel) {
		leftMotor.setAcceleration(accel);
		rightMotor.setAcceleration(accel);
	}

	/**
	 * Stop the robot
	 * 
	 * @author Mouhyi
	 */
	public void stop() {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}

	/**
	 * Stop the right wheel
	 * 
	 * @author Anthony
	 */
	public void stopRight() {
		rightMotor.setSpeed(0);
	}

	/**
	 * Stop the left wheel
	 * 
	 * @author Anthony
	 */
	public void stopLeft() {
		leftMotor.setSpeed(0);
	}

	/**
	 * Move forward with the left wheel only
	 * 
	 * @author Anthony
	 */
	public void advanceLeft(double speed) {

		if (speed > 0) {
			leftMotor.forward();
		} else {
			leftMotor.backward();
			speed = -speed;
		}

		if (speed > 900) {
			leftMotor.setSpeed(900);
		} else {
			leftMotor.setSpeed((int) speed);
		}
	}

	/**
	 * Move forward with the right wheel only
	 * 
	 * @author Anthony
	 */
	public void advanceRight(double speed) {

		if (speed > 0) {
			rightMotor.forward();
		} else {
			rightMotor.backward();
			speed = -speed;
		}

		if (speed > 900) {
			rightMotor.setSpeed(900);
		} else {
			rightMotor.setSpeed((int) speed);
		}
	}

	/**
	 * Drive {@param: distance} in a straight line
	 * 
	 * @param distance
	 * @author Mouhyi
	 * @todo test: wheel.rotate twice??
	 */
	public void goForward(double distance, int speed) {
		this.stop();
		leftMotor.setSpeed(convertDistance(SystemConstants.LEFT_RADIUS, speed));
		rightMotor
				.setSpeed(convertDistance(SystemConstants.RIGHT_RADIUS, speed));
		leftMotor.rotate(
				convertDistance(SystemConstants.LEFT_RADIUS, distance), true);
		rightMotor.rotate(
				convertDistance(SystemConstants.RIGHT_RADIUS, distance), false);
		this.stop();

	}

	/**
	 * Rotates the robot, relative to its current position, by the given angle
	 * 
	 * @param angle
	 */
	public void rotateAxis(double angle, int speed) {
		this.stop();
		leftMotor.setSpeed(convertDistance(SystemConstants.LEFT_RADIUS, speed));
		rightMotor
				.setSpeed(convertDistance(SystemConstants.RIGHT_RADIUS, speed));
		leftMotor.rotate(
				-convertAngle(SystemConstants.LEFT_RADIUS,
						SystemConstants.WIDTH, angle), true);
		rightMotor.rotate(
				convertAngle(SystemConstants.RIGHT_RADIUS,
						SystemConstants.WIDTH, angle), false);
		this.stop();
	}

	/**
	 * Move robot forward
	 * 
	 * @param Fspeed
	 */
	public void advance(double Fspeed) {
		this.setSpeeds(Fspeed, 0);
	}

	/**
	 * Rotates the robot wheels by the given angle
	 * 
	 * @param angle
	 *            in DEG
	 */
	public void rotateWheels(double angle) {
		leftMotor.setSpeed(convertDistance(SystemConstants.LEFT_RADIUS,
				(int) SystemConstants.FORWARD_SPEED));
		rightMotor.setSpeed(convertDistance(SystemConstants.RIGHT_RADIUS,
				(int) SystemConstants.FORWARD_SPEED));

		int angle2 = (int) angle;
		leftMotor.rotate(angle2, true);
		rightMotor.rotate(angle2, false);

		// this.stop();
	}

	public void rotateWheel(double angle, boolean left) {
		if (left) {
			leftMotor.setSpeed((int) SystemConstants.ROTATION_SPEED);
			int angle2 = (int) angle;
			leftMotor.rotate(angle2, false);
		} else {
			rightMotor.setSpeed((int) SystemConstants.ROTATION_SPEED);
			int angle2 = (int) angle;
			rightMotor.rotate(angle2, false);
		}
	}

	public void rotate(double Rspeed) {
		setSpeeds(0, Rspeed);
	}

	public double getLeftTacho() {
		return leftMotor.getTachoCount();
	}

	public double getRightTacho() {
		return rightMotor.getTachoCount();
	}

	/**
	 * @param radius
	 * @param distance
	 * @return the number of degs a motor rotate turn to advance the robot by
	 *         {@param: distance}
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * @param radius
	 * @param width
	 * @param angle
	 *            in degs
	 * @return the number of degs a motor rotate turn to turn the robot by
	 *         {@param: angle}
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return (int) ((width * angle) / (radius * 2));
	}

	/**
	 * Return speed of robot
	 */
	public double getSpeed() {
		return (Math.abs(leftMotor.getSpeed() * SystemConstants.LEFT_RADIUS
				* Math.PI / 180) + Math.abs(rightMotor.getSpeed()
				* SystemConstants.RIGHT_RADIUS * Math.PI / 180)) / 2;

	}

}
