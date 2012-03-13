/**
 * This class is responsible for performing the localization routine
 * USlocalization is executed first to orients the robot parallel to th e Y-axis
 * LSlocalization is then executed to position the robot to (0,0.Pi/2)
 * 
 * @author Mouhyi
 */

public class Localization {

	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor lsL;
	private LightSensor lsR;
	private USPoller usp;

	/**
	 * main Localization routine
	 * 
	 * @author Mouhyi
	 */
	public static void doLocalization() {
	}

	/**
	 * US Localization routine: falling edge
	 * 
	 * @author Mouhyi
	 */
	public static void doUSLocalization() {
	}

	/**
	 * Light Localization routine
	 * 
	 * @author Mouhyi
	 */
	public static void doLightLocalization() {

	}

}
