package Master;

import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;

/**
 * Movement control class
 * 
 * @author Mouhyi
 */

public class Navigation {

	private Odometer odo;
	private Robot robot;
	private ObstacleDetection us;
	OdoCorrection snapper;

	private static final double ROTATION_TOLERANCE = 0.5; // in Deg
	private static final double DISTANCE_TOLERANCE = 1; // in cm

	/** minimum distance necessary for the robot to move forward */
	public static final int ObstacleDist = 50;

	/**
	 * Constructor
	 * 
	 * @param odo
	 * 
	 * @author Mouhyi
	 */
	public Navigation(Odometer odo, Robot robot, ObstacleDetection us,
			OdoCorrection snapper) {
		this.odo = odo;
		this.robot = robot;
		this.us = us;
		this.snapper = snapper;
	}

	/**
	 * This method takes as arguments the x and y position in cm Will travel to
	 * designated position, while constantly updating it's heading
	 * 
	 * @author Mouhyi
	 */
	public boolean travelTo(double x, double y) {
		// double distance;
		Coordinates coords;
		double destAngle;

		robot.stop();

		/*
		 * try{ Thread.sleep(1000); }catch(Exception e){;}
		 */

		coords = odo.getCoordinates();
		destAngle = Math.atan2(y - coords.getY(), x - coords.getX());
		destAngle = Odometer.convertToDeg(destAngle);

		/*
		 * Doesn't turn if error is too small
		 * @author Ryan
		 */
		
		double difference = Math.abs(coords.getTheta()-destAngle);
		
		if(difference > 3){
			turnTo(destAngle);
			RConsole.println("TravelTo: Turn completed ");
		}
		RConsole.println("TravelTo: Already facing destination");

		/*
		 * if ( us.getDistance() < ObstacleDist ){ RConsole.println("Obstacle");
		 * return false; }
		 */

		// RConsole.println("TravelTo: advance");

		while (true) {
			coords = odo.getCoordinates();
			if (Math.abs(x - coords.getX()) < DISTANCE_TOLERANCE
					&& Math.abs(y - coords.getY()) < DISTANCE_TOLERANCE) {
				break;
			}
			robot.advance(SystemConstants.FORWARD_SPEED);
		}
		robot.stop();

		turnTo(odo.getDirection() * 90); // / added to align

		// RConsole.println("TravelTo: advance");
		RConsole.println("TravelTo ARRIVEDto: x=" + x + ",  y=" + y);

		return true;

	}

	/**
	 * This method turns the robot to the absolute heading destAngle
	 * 
	 * @author Mouhyi
	 */
	public void turnTo(double destAngle) {
		double err;
		destAngle = Odometer.adjustAngle(destAngle);

		robot.stop();

		// save snapper state and disable
		boolean tmp = snapper.isEnabled();
		snapper.setEnabled(false);

		do {
			double curTheta = odo.getCoordinates().getTheta();
			double rotAngle = minimumAngleFromTo(curTheta, destAngle);

			robot.rotateAxis(rotAngle, (int) SystemConstants.ROTATION_SPEED);

			// and compute error
			curTheta = odo.getCoordinates().getTheta();
			err = destAngle - curTheta;

		} while (Math.abs(err) > ROTATION_TOLERANCE);
		robot.stop();

		// restore snapper state
		snapper.setEnabled(tmp);

		// RConsole.println("END TURN");

	}

	/**
	 * Main navigation method
	 * 
	 * @param x
	 * @param y
	 * @author Mouhyi
	 */

	public void GoTo(double x, double y) {

		Coordinates coords;
		double curX, curY;
		boolean obstacle = false;
		int xDiff, yDiff;

		while (true) {

			do {
				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();
				xDiff = (int) Math.round((x - curX) / SystemConstants.TILE) - 1;
				if(xDiff==-1) break;

				RConsole.println("xdiff" + xDiff);

				obstacle = !travelTo(x - xDiff * SystemConstants.TILE, curY);

				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();

				// xDiff--;
			} while (Math.abs(curX - x) > DISTANCE_TOLERANCE && !obstacle);

			RConsole.println("X Travel done ");

			do {
				obstacle = false;
				yDiff = (int) Math.round((y - curY) / SystemConstants.TILE) - 1;
				if(yDiff==-1) break;
				
				RConsole.println("ydiff" + yDiff);

				obstacle = !travelTo(curX, y - yDiff * SystemConstants.TILE);

				// update position
				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();

				// yDiff--;
			} while (Math.abs(curY - y) > DISTANCE_TOLERANCE && !obstacle);

			RConsole.println("Y Travel done ");

			// if(obstacle ) call obstacle avoidance
			// else if destination reached break
			// else, i.e, obstacle in destination: return -1;
			break;

		}

	}

	/**
	 * Drive one TILE and correct orientation
	 * 
	 * @author Mouhyi
	 */
	public void navCorrect() {
		robot.goForward(SystemConstants.TILE,
				(int) SystemConstants.FORWARD_SPEED);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			;
		}
		turnTo(odo.getDirection() * 90);
	}

	/**
	 * Travels along the X axis until it reachs {@param: x} or an obstacle is
	 * detected Assumes the robot is facing the right direction
	 * 
	 * @param x
	 * @return 0 if destination reached, -1 if obstacle detected
	 * @author Mouhyi
	 */
	public int TravelToX(double x) {
		boolean obstacle = false;
		Coordinates coords;
		double curX;

		while (true) {
			// update coords
			coords = odo.getCoordinates();
			curX = coords.getX();

			// TODO update obstacle

			if (Math.abs(curX - x) < DISTANCE_TOLERANCE || !obstacle)
				break;

			// go forward one tile
			navCorrect();

		}
		if (obstacle)
			return -1;
		return 0;

	}

	/**
	 * Travels along the Y axis until it reachs {@param: y} or an obstacle is
	 * detected Assumes the robot is facing the right direction
	 * 
	 * @param y
	 * @return 0 if destination reached, -1 if obstacle detected
	 * @author Mouhyi
	 */
	public int TravelToY(double y) {
		boolean obstacle = false;
		Coordinates coords;
		double curY;

		do {
			// update coords
			coords = odo.getCoordinates();
			curY = coords.getY();

			// TODO update obstacle

			// go forward one tile
			navCorrect();

		} while (Math.abs(curY - y) > DISTANCE_TOLERANCE && !obstacle);
		if (obstacle)
			return -1;
		return 0;

	}

	/**
	 * Obstacle avoidance Routine
	 * 
	 * @author mouhyi
	 */
	public void avoidObstacle() {
		int dir = odo.getDirection();
		boolean rightFree = true;
		boolean leftFree = true;

		turnLeft();
		leftFree = (us.getDistance() > ObstacleDist);
		if (leftFree) {
			navCorrect();
		} else {
			// go back to original dir
			turnRight();
			// then turn right
			turnRight();
			rightFree = (us.getDistance() > ObstacleDist);
			if (rightFree) {
				navCorrect();
			} else {
				turnRight();
				turnBack();
				navCorrect();
			}
		}
	}

	/**
	 * @author mouhyi
	 */
	public void turnLeft() {
		turnTo((odo.getDirection() + 1) * 90);
	}

	/**
	 * @author mouhyi
	 */
	public void turnRight() {
		turnTo((odo.getDirection() - 1) * 90);
	}

	/**
	 * @author mouhyi
	 */
	public void turnBack() {
		turnTo((odo.getDirection() + 2) * 90);
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return minimum angle that the robot should rotate by to get from head a
	 *         to b.
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = Odometer.adjustAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}

	public static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) - Math.pow(y1 - y2, 2));
	}
}
