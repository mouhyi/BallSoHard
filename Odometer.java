import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class keeps track of the location of the robot and updates constantly
 * the robot's coordinates
 * 
 * @author Mouhyi
 * 
 */

public class Odometer implements TimerListener {

	private Timer timer;
	private Robot robot;
	private Coordinates coords;
	private double displacement; // variable to keep track of the robot's total displacement
	// private Coordinates oldCoords ;

	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 25;

	/**
	 * Constructor
	 * 
	 * @author Mouhyi
	 */
	public Odometer(Robot robot) {

		coords = Coordinates.getInstance();
		// start timer
		Timer timer = new Timer(ODOMETER_PERIOD, this);
		timer.start();
	}

	/**
	 * Recompute the odometer values using the displacement and heading changes
	 * 
	 * @author Mouhyi
	 */
	public void timedOut() {
		double dTheta, dDisplacement;
		double x, y, theta;
		
		// get cuurrent coords
		x = coords.getX();
		y = coords.getY();
		theta = coords.getTheta();

		dTheta = robot.getHeading() - theta; // ///
		double dTheta2 = (dTheta < 3 || dTheta > 358) ? 0 : adjustAngle(dTheta); 		// ///   CHANGE!

		dDisplacement = robot.getDisplacement() - displacement;
		
		// Formulas from Tutorial
		x += dDisplacement * Math.cos( adjustAngle ( convertToRadians(theta + dTheta2 / 2) ) );			////
		y += dDisplacement * Math.sin ( convertToRadians(theta + dTheta2 / 2) );							////
		theta += dTheta;
		theta = adjustAngle(theta);
		displacement += dDisplacement;
		
		// update coordinates
		coords.set(x, y, theta);

	}

	/**
	 * method to stop the timerlistener
	 * 
	 */
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}

	/**
	 * method to start the timerlistener
	 * 
	 */
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}

	// static 'helper' methods
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}

	// This method converts angles is degrees to angles in radians
	public static double convertToRadians(double angle) {
		return (angle * Math.PI) / (180.0);
	}

	// Map theta to [0,360)
	public static double adjustAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		return angle % 360.0;
	}

	public Robot getRobot() {
		return robot;
	}

}
