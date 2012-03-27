package Master;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.util.Timer;
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

	private Odometer odo;
	//private USPoller usPoller;
	private LightSensor lsL;
	private LightSensor lsR;
	Coordinates coords;
	private static final int LCD_PERIOD = 100;
	private Timer timer;
	
	
	/**
	 * Constructor
	 * @author Mouhyi
	 */
	public Printer(Odometer odo) {
		this.odo = odo;
		Timer timer = new Timer(LCD_PERIOD, this);
		timer.start();
	}

	/**
	 * The timer function which calls update() to update the screen
	 * @author Mouhyi
	 */
	public void timedOut() {
		//LCD.clearDisplay();
		this.update();
	}

	/**
	 * Refreshes the screen 
	 * @author Mouhyi
	 */
	public void update() {
		coords = odo.getCoordinates();
		
		// clear the lines for displaying odometry information
		LCD.drawString("X:              ", 0, 0);
		LCD.drawString("Y:              ", 0, 1);
		LCD.drawString("0:              ", 0, 2);
		
		// print coordinates
		LCD.drawString(""+coords.getX(), 2, 0);
	    LCD.drawString(""+coords.getY(), 2, 1);
	    LCD.drawString(""+coords.getTheta(), 2, 2);
	}

}
