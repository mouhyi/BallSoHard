package Master;

import lejos.nxt.*;
import lejos.util.*;
import lejos.nxt.comm.*;

/*
 * Looks for obstacles using the USPoller, then changes direction if it sees something
 * @Author Ryan
 * 
 */

public class NavigationControl implements TimerListener {

	private final int REFRESH = 50;
	private Timer ncTimer = new Timer(REFRESH, this);
	private USPoller usLeft, usRight;
	private Odometer odo;
	private Navigation navigation;
	private int leftDistance;
	private int rightDistance;
	private Coordinates coords;
	private int direction;

	/** minimum distance necessary for the robot to move forward */
	public static final int ObstacleDist = 55;
	/*
	 * Initializes the navigation control
	 * 
	 * @param Navigation, Odometer
	 * 
	 * @author Ryan
	 */
	public NavigationControl(Navigation navigation, Odometer odo) {
		this.navigation = navigation;
		this.ncTimer.start();
		this.odo = odo;
		this.coords = odo.getCoordinates();
	}

	/*
	 * Continuously polls ultrasonic sensor When obstacle is detected, the robot
	 * will turn 90 degrees and to avoid the obstacle
	 * 
	 * @author Ryan
	 */

	public void timedOut() {
		leftDistance = usLeft.getDistance();
		rightDistance = usRight.getDistance();
		direction = odo.getDirection();

		RConsole.println("USLeft: " + String.valueOf(leftDistance));

		if (usLeft.obstacleDetected()) {
			RConsole.println("Obstacle Detected");
			usLeft.resetObstacle();
			changeRoute();
			this.ncTimer.start();
			turnRight();
		}

		if (usRight.obstacleDetected()) {
			RConsole.println("Obstacle detected");
			usRight.resetObstacle();
			changeRoute();
			this.ncTimer.start();
			turnLeft();
		}
	}

	/*
	 * Finds an alternate route to destination
	 * 
	 * @author Ryan
	 */

	private void changeRoute() {

		double nearestHorizontalLine;
		double nearestVerticalLine;

		int numHorizLines;
		int numVertLines;

		// Pause searching for obstacles
		ncTimer.stop();

		// Reverse to closest line, depending on orientation of robot
		// Facing east
		if (direction == 0) {
			numVertLines = (int) (coords.getX() / SystemConstants.TILE);
			nearestVerticalLine = SystemConstants.TILE * numVertLines;
			navigation.travelTo(nearestVerticalLine, coords.getY());
		}

		// Facing north
		else if (direction == 1) {
			numHorizLines = (int) (coords.getY() / SystemConstants.TILE);
			nearestHorizontalLine = SystemConstants.TILE * numHorizLines;
			navigation.travelTo(coords.getX(), nearestHorizontalLine);
		}

		// Facing west
		else if (direction == 2) {
			numVertLines = 1 + (int) (coords.getX() / SystemConstants.TILE);
			nearestVerticalLine = SystemConstants.TILE * numVertLines;
			navigation.travelTo(nearestVerticalLine, coords.getY());
		}

		// Facing south
		else if (direction == 3) {
			numHorizLines = 1 + (int) (coords.getY() / SystemConstants.TILE);
			nearestHorizontalLine = SystemConstants.TILE * numHorizLines;
			navigation.travelTo(coords.getX(), nearestHorizontalLine);
		}

		// Resume looking for obstacles
		ncTimer.start();

	}

	/*
	 * Turns left
	 * 
	 * @author Ryan
	 */

	public void turnLeft() {
		if (direction == 0) {
			navigation.turnTo(90);
		} else if (direction == 1) {
			navigation.turnTo(180);
		} else if (direction == 2) {
			navigation.turnTo(270);
		} else if (direction == 3) {
			navigation.turnTo(0);
		}
	}

	/*
	 * Turns right
	 * 
	 * @author Ryan
	 */

	public void turnRight() {
		if (direction == 0) {
			navigation.turnTo(270);
		} else if (direction == 1) {
			navigation.turnTo(180);
		} else if (direction == 2) {
			navigation.turnTo(90);
		} else if (direction == 3) {
			navigation.turnTo(0);
		}
	}

}