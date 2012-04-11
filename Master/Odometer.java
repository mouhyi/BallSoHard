package Master;

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class keeps track of the location of the robot and updates constantly
 * the robot's coordinates.
 * 
 * This implement a polar coordinates system which have positive angles in the
 * counterclockwise direction. Angles are expressed Degrees. Distances are
 * expressed in cm.
 * 
 * @author Mouhyi
 * 
 */

public class Odometer extends Thread {

	private Robot robot;
	private Coordinates coords;

	// variable to keep track of the robot's total displacement
	private double displacement;
	// variable to keep track of the robot's total heading: continuous: not
	// bounded to [0, 360)
	private double heading;

	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 25;

	/**
	 * Constructor
	 * 
	 * @author Mouhyi
	 */
	public Odometer(Robot robot) {

		this.robot = robot;
		coords = new Coordinates();
		// start timer

		/*
		 * Changed timer to thread
		 * 
		 * @author Ryan
		 */
		// Timer timer = new Timer(ODOMETER_PERIOD, this);
		this.start();
	}

	/**
	 * Recompute the odometer values using the displacement and heading changes
	 * 
	 * @author Mouhyi
	 */
	public void run() {
		double dHeading, dDisplacement;
		double x, y, theta;

		// double dTheta2 = (dTheta < 3 || dTheta > 358) ? 0 :
		// adjustAngle(dTheta); // /// CHANGE!

		while (true) {

			synchronized (coords) {
				// get cuurrent coords
				x = coords.getX();
				y = coords.getY();
				theta = coords.getTheta();

				dHeading = robot.getHeading() - heading; // theta <-> heading ,
															// if not heading,
															// manual correction
															// doesn't work

				double dTheta = NegativeMap(dHeading);
				// if(dTheta < 1 || dTheta > 359) dTheta = 0;

				dDisplacement = robot.getDisplacement() - displacement; // adjust??

				// Formulas from Tutorial: problem angles sum & /2
				x += dDisplacement
						* Math.cos(convertToRadians(theta + dTheta / 2));
				y += dDisplacement
						* Math.sin(convertToRadians(theta + dTheta / 2));
				theta += dTheta; // not sure if this correction is correct
				theta = adjustAngle(theta);

				// update displacement
				displacement += dDisplacement;
				heading += dHeading;

				// update coordinates
				coords.set(x, y, theta);
			}
			try {
				Thread.sleep(25);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Set robot's coordinates to the given parameters (Demeter's rule)
	 * 
	 * @param x
	 * @param y
	 * @param theta
	 * @author Mouhyi
	 */
	public void setCoordinates(double x, double y, double theta) {
		synchronized (coords) {
			coords.set(x, y, theta);
		}
	}

	public void setCoordinates(double x, double y, double theta,
			boolean[] update) {
		synchronized (coords) {
			if (update[0])
				coords.setX(x);
			if (update[1])
				coords.setY(y);
			if (update[2])
				coords.setTheta(theta);
		}
	}

	/**
	 * @return a Coordinates object with the coordinates of the robot
	 * @author Mouhyi
	 */
	public Coordinates getCoordinates() {
		Coordinates coordsClone = new Coordinates();
		synchronized (coords) {
			coordsClone.copy(coords);
		}
		return coordsClone;
	}

	/**
	 * Compute robot's direction
	 * 
	 * @return int direction: 0:E, 1:N, 2:W, 3:S
	 * @author Mouhyi
	 */
	public int getDirection() {
		double theta;
		synchronized (coords) {
			theta = coords.getTheta();
		}
		int direction = ((int) (theta + 45) / 90) % 4; // round then divide
		return direction;
	}

	public Robot getRobot() {
		return robot;
	}

	// static 'helper' methods

	/**
	 * This method converts angles is degrees to radians
	 * 
	 * @param angle
	 * @return corresponding angle in radians
	 * @author Mouhyi
	 */
	public static double convertToRadians(double angle) {
		return (angle * Math.PI) / (180.0);
	}

	/**
	 * This method converts angles is degrees to radians
	 * 
	 * @param angle
	 * @return corresponding angle in radians
	 * @author Mouhyi
	 */
	public static double convertToDeg(double angle) {
		return angle * (180.0) / Math.PI;
	}

	/**
	 * Map angle to [0,360)
	 * 
	 * @param angle
	 *            in degrees
	 * @return value congruent to 'angle' modulos 360 deg
	 * @author Mouhyi
	 */
	public static double adjustAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		return angle % 360.0;
	}

	/**
	 * Map angle to [-180,180)
	 * 
	 * @param angle
	 *            in degrees
	 * @return corresponding angle in [-180,180)
	 * @author Mouhyi
	 */
	public static double NegativeMap(double angle) {
		double d = adjustAngle(angle);
		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}

}
