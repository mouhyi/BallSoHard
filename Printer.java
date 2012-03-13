import lejos.nxt.LCD;
import lejos.util.TimerListener;

/**
 * This displays information to the LCD screen of the Lego NXT brick. It runs on
 * a timer, and updates on a constant basis . Information printed: robot's
 * Coordinates, distance detected by the US, Light value detected by the light
 * sensor
 * 
 * @author Mouhyi
 */
public class Printer implements TimerListener {

	public static Coordinates c = new Coordinates();
	private Odometer odo;
	private USPoller usPoller;
	private LightSensor lsL;
	private LightSensor lsR;

	/**
	 * Constructor
	 * @author Mouhyi
	 */
	public Printer() {
	}

	/**
	 * The timer function which calls update() to update the screen
	 * @author Mouhyi
	 */
	public void timedOut() {
	}

	/**
	 * Refreshes the screen 
	 * @author Mouhyi
	 */
	public static void update() {
	}

	/**
	 * Clears the screen
	 *  @author Mouhyi
	 */
	public static void clear() {
	}

}
