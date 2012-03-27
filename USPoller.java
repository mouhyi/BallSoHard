import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * Wrapper class for the ultrasonic sensors
 * 
 * @author Mouhyi, Ryan
 * @see Printer
 */

public class USPoller implements TimerListener{

	private final int TOLERANCE = 30;
	private final int REFRESH = 50;
	private int[] usDistances = new int[5];
	private UltrasonicSensor us;
	private int median;
	private boolean obstacleDetected;
	private Object lock;
	
	public static USPoller left;
	public static USPoller right;

	/**
	 * Constructor
	 * 
	 * @author Mouhyi, Ryan
	 */
	public USPoller (UltrasonicSensor us) {
		this.us = us;
		Timer timer = new Timer(REFRESH, this);
		timer.start();
	}
	
	/**
	 * This method initializes the USPollers and starts collecting US sensor readings
	 * @param LeftUS, RightUS
	 * @author Ryan
	 */
	
	public static void startUS(UltrasonicSensor usLeft, UltrasonicSensor usRight){
		left = new USPoller(usLeft);
		right = new USPoller(usRight);
	}
	
	
	/**
     * This method implements run method of the Runnable interface.
     *
     * @author Mouhyi, Ryan
     */
	public void timedOut() {
	
		int i;
		int maxIndex = usDistances.length-1;
		int middle = maxIndex/2;
		
		//Inserts sensor readings into an array and shifts them for every ping
		for(i=0; i<maxIndex; i++){
			usDistances[i]=usDistances[i+1];
		}
		usDistances[maxIndex]=us.getDistance();
		
		//Sorts data and returns median
		filter(usDistances, 0);
		
		median = usDistances[middle];	
				
		if(median < TOLERANCE){
			obstacleDetected = true;
		}
	
	}
	
	/*
	 * Sorts ultrasonic sensor readings in ascending order
	 * @author Ryan
	 */
	private void filter(int data[], int size) { 
		int i, j; 
		for (i = 0; i < size; i++){
			j = i-1;
			for (j=j; j>=0 && data[j] > data[j+1]; j--){ 
				int temp;
				temp = data[j];
				data[j] = data[j+1];
				data[j+1] = temp; 
			} 
		}
	}
	
	/*
	 * Sets a mode for determining whether or an obstacle has been detected
	 * @author Ryan
	 */
	public synchronized boolean obstacleDetected(){
		return obstacleDetected;
	}
	
	/*
	 * Returns the filtered ultrasonic sensor reading
	 * @author Ryan
	 */
	public int getDistance(){
		return median;
	}
	
	public void resetObstacle(){
		obstacleDetected = false;
	}
}	
