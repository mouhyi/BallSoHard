package Master;

import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * Wrapper class for the ultrasonic sensors
 * 
 * @author Mouhyi, Ryan
 * @see Printer
 */

public class ObstacleDetection
{

	private final int TOLERANCE = 45;
	private boolean obstacleDetected;
	private Object lock;
	
	public USPoller left;
	public USPoller right;

	/**
	 * Constructor
	 * 
	 * @author Mouhyi, Ryan
	 */
	public ObstacleDetection (USPoller uspL, USPoller uspR) {
		this.left = uspL;
		this.right = uspR;
	}
	
	/**
	 * Sets a mode for determining whether or an obstacle has been detected
	 * @author Ryan
	 **/
	public synchronized boolean obstacleDetected()
	{
		boolean leftObstacle = false;
		boolean rightObstacle = false;
		if(left.getDistance() < TOLERANCE)
		{
			leftObstacle = true;
		}
		if(right.getDistance() < TOLERANCE)
		{
			rightObstacle = true;
		}
		
		if(leftObstacle || rightObstacle){
			obstacleDetected = true;
		}
	
		return obstacleDetected;
	}
	
	/**
	 * Returns the minimum of both filtered ultrasonic sensor readings
	 * @author Ryan
	 **/
	public int getDistance(){
		if(left.getDistance() < right.getDistance()){
			return left.getDistance();
		}
		else{
			return right.getDistance();
		}
		
	}
	
	/**
	 * Resets the obstacle to false
	 * @author Ryan
	 */
	public void resetObstacle(){
		obstacleDetected = false;
	}
}	
