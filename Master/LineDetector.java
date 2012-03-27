package Master;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;
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

	private final int SENSOR_THRESHOLD = 15;
	private final int DETECTION_THRESHOLD = 30;
	private int[] lightValue = new int[6];
	private double robotSpeed;
	private boolean lineDetected = false;
	LightSensor ls;
	private boolean isleft;

	private OdoCorrection snapper;

	Timer timer;
	
	// instances of this class
	public static LineDetector left;
	public static LineDetector right;
	/**
	 * Constructor
	 * 
	 * @author Mouhyi
	 */
	
	/*
	 * Added an argument to the constructor to base the frequency of the light sensor readings on
	 * the speed of the robot
	 */
	private LineDetector(LightSensor ls, boolean isleft, OdoCorrection snapper, double robotSpeed) {
		this.ls = ls;
		this.isleft = isleft;
		this.snapper = snapper;
		this.robotSpeed = robotSpeed;
		
		int refreshRate = 166/(int)this.robotSpeed;				// 166/this.robotSpeed
		
		Timer timer = new Timer(refreshRate, this); 			
		timer.start();
	}
	
	public static void init(OdoCorrection snapper, double robotSpeed){
		left = new LineDetector(SystemConstants.leftLight, true, snapper, robotSpeed);
		right = new LineDetector( SystemConstants.rightLight, false, snapper, robotSpeed);
	}

	/**
	 * This method implements the run method of the Runnable interface. It calls
	 * notify when it detects a line cross
	 * 
	 * @author Mouhyi, Ryan, Josh
	 */
	public void timedOut() {
		
		int i;
		int maxIndex = lightValue.length-1;

		//puts light sensor readings in an array and shifts them for every ping
		for(i=0; i<maxIndex; i++){
			lightValue[i]=lightValue[i+1];
		}
		
		
		
		//Filters out values if the difference between subsequent values is too small
		if(!(Math.abs(lightValue[maxIndex]-ls.getNormalizedLightValue()) < SENSOR_THRESHOLD)){
			lightValue[maxIndex]=ls.getNormalizedLightValue();
		}
		
		//Detects a line if there is first a positive difference in light, then negative
		if(lightValue[0]-lightValue[1]>= DETECTION_THRESHOLD &&
		  (lightValue[1]-lightValue[2]<=-DETECTION_THRESHOLD || 
		   lightValue[2]-lightValue[3]<=-DETECTION_THRESHOLD ||
		   lightValue[3]-lightValue[4]<=-DETECTION_THRESHOLD ||
		   lightValue[4]-lightValue[5]<=-DETECTION_THRESHOLD)){
			
				
				if(this == left){
					RConsole.println("Left line detected");
				}
				else if(this == right){
					RConsole.println("Right line detected");
				}

			
				// Clear out the line condition
				for(i=0; i<maxIndex; i++){
					lightValue[i]=lightValue[i+1];
				}
				
				lineDetected = true;
				notifyListener();
				
				/*
				 * Move the delay here to accommodate localization routine
				 */
				
				/*
				// Delay to reduce risk of counting line again
				try
				{
					Thread.sleep((int)(500/this.robotSpeed));
				}
				catch (InterruptedException e){}
				*/
		}
		
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
			
			 //Changed wait time from 2000 to 500
			 Thread.sleep(500);
			LCD.drawString("LINE         ", 0, 5);
		} catch (InterruptedException e) {}
	}

	public boolean isLeft() {
		return isleft;
	}
	
	/**
	 * @author Ryan
	 * @return Boolean determining whether a line has been detected or not
	 */
	public boolean lineDetected(){
		return lineDetected;
	}
	
	/**
	 * Returns line to false
	 * @author Ryan
	 */
	public void resetLine(){
		lineDetected = false;
	}

}
