import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.comm.*;

import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This is class uses light sensors to detect line crossing and notifies
 * OdoCorretion accordignly
 * 
 * @author Mouhyi, Ryan
 */

public class LineDetector implements TimerListener {

	private final int SENSOR_THRESHOLD = 4;
	private final int DETECTION_THRESHOLD = 5;
	private int[] lightValue = new int[6];
	LightSensor ls;
	static final int THRESHOLD = 450;
	int last_reading;
	private boolean isleft;

	private OdoCorrection snapper;

	Timer timer;
	// update period, in ms
	private static final int PERIOD = 25;
	
	// instances of this class
	public static LineDetector left;
	public static LineDetector right;
	/**
	 * Constructor
	 * 
	 * @author Mouhyi
	 */
	private LineDetector(LightSensor ls, boolean isleft, OdoCorrection snapper) {
		this.ls = ls;
		this.isleft = isleft;
		this.snapper = snapper;
		
		Timer timer = new Timer(PERIOD, this);
		timer.start();
	}
	
	public static void init(OdoCorrection snapper){
		left = new LineDetector(SystemConstants.leftLight, true, snapper);
		right = new LineDetector( SystemConstants.rightLight, false, snapper);
	}

	/**
	 * This method implements the run method of the Runnable interface. It calls
	 * notify when it detects a line cross
	 * 
	 * @author Mouhyi, Ryan
	 */
	public void timedOut() {
		
		int i;
		int maxIndex = lightValue.length-1;

		//puts light sensor readings in an array and shifts them for every ping
		for(i=0; i<maxIndex; i++){
			lightValue[i]=lightValue[i+1];
		}
		
		//Filters out values if the difference between subsequent values is too small
		if(!(Math.abs(lightValue[maxIndex]-ls.getLightValue()) < SENSOR_THRESHOLD)){
			lightValue[maxIndex]=ls.getLightValue();
		}
				
		//Detects a line if there is first a positive difference in light, then negative
		if(lightValue[0]-lightValue[1]>= DETECTION_THRESHOLD &&
		  (lightValue[1]-lightValue[2]<=-DETECTION_THRESHOLD || 
		   lightValue[2]-lightValue[3]<=-DETECTION_THRESHOLD ||
		   lightValue[3]-lightValue[4]<=-DETECTION_THRESHOLD ||
		   lightValue[4]-lightValue[5]<=-DETECTION_THRESHOLD)){
			
				notifyListener();				
		}
		
		last_reading = lightValue[maxIndex];
		

		/*int new_reading = ls.getNormalizedLightValue();
		if (new_reading < THRESHOLD && last_reading > THRESHOLD)
			notifyListener();
		last_reading = new_reading;
		*/
	}

	/**
	 * Registers a listener to this LineDetector to be notified of line cross
	 * events.
	 * 
	 * @param listener
	 *            the grid snapper
	 * @author Mouhyi           
	 */
	public void setListener(OdoCorrection snapper) {
		this.snapper = snapper;
	}

	/**
	 * Notify OdoCorrection that a line has been crossed: semaphore
	 * 
	 * @author Mouhyi
	 */
	public void notifyListener() {
		snapper.lineDetected(this);
		
		
		LCD.drawString("LINE "+isleft, 0, 5);
		try {
			Thread.sleep(2000);
			LCD.drawString("LINE         ", 0, 5);
		} catch (InterruptedException e) {}
	}

	public boolean isLeft() {
		return isleft;
	}

}
