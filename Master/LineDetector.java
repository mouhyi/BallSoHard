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

public class LineDetector extends Thread {

	private final int DETECTION_THRESHOLD = 40;
	private int[] lightValue = new int[6];
	private boolean lineDetected = false;
	LightSensor ls;
	private boolean isleft;

	private OdoCorrection snapper;
	
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
		this.start();
	}
	
	public static void init(OdoCorrection snapper, double robotSpeed){
		left = new LineDetector(SystemConstants.leftLight, true, snapper, robotSpeed);
		right = new LineDetector( SystemConstants.rightLight, false, snapper, robotSpeed);
	}

	/**
	 * This method implements the run method of the Runnable interface. It calls
	 * notify when it detects a line cross
	 * 
	 * @author Mouhyi, Ryan
	 */
	public void run() {
		while (true) {
			int i;
			int maxIndex = lightValue.length-1;
			int diffAB;
			int diffCD;
			int totalDiff;

			//puts light sensor readings in an array and shifts them for every ping
			for(i=0; i<maxIndex; i++){
				lightValue[i]=lightValue[i+1];
			}
					
			lightValue[maxIndex]=ls.getNormalizedLightValue();
			
			/*
			if(this==left){
				RConsole.println(String.valueOf(lightValue[maxIndex]));
			}
			*/

			/*
			 * Calculates second derivative
			 * @author Ryan
			 */
			
			diffAB = lightValue[0]-lightValue[1];
			diffCD = lightValue[2]-lightValue[3];
			totalDiff = diffAB-diffCD;
			
			if(totalDiff >= DETECTION_THRESHOLD && totalDiff <= 100){
				
					/*if(this == left){
						RConsole.println("Left line detected");
					}
					else if(this == right){
						RConsole.println("Right line detected");
					}*/

				
					// Clear out the line condition
					for(i=0; i<maxIndex; i++){
						lightValue[i]=lightValue[i+1];
					}
					
					lineDetected = true;
					notifyListener();
			}
			try { Thread.sleep(500); } catch(Exception e){}
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
