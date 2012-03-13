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
	private static Coordinates coords;

	/**
	* Constructor
	*
	* @author Mouhyi
	*/
	public Odometer(Robot robot) {
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
	
	/*
	 * Recompute the odometer values using the displacement and heading changes
	 */
	public void timedOut() {
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
	
	public Robot getRobot(){
		return robot;
	}

}
