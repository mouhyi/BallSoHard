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
	
	private USPoller left;
	private USPoller right;
	private Odometer odo;
	private Coordinates coords;

	/**
	 * Constructor
	 * 
	 * @author Mouhyi, Ryan
	 */
	public ObstacleDetection (USPoller uspL, USPoller uspR, Odometer odo) {
		this.left = uspL;
		this.right = uspR;
		this.odo = odo;
	}
	
	/**
	 * Sets a mode for determining whether or an obstacle has been detected
	 * @author Ryan
	 **/
	public synchronized boolean obstacleDetected() {
		coords = odo.getCoordinates();
		int direction = odo.getDirection();
		double x = coords.getX();
		double y = coords.getY();
		
		/*
		 * Filters out wall readings
		 * @author Ryan
		 */
		//Facing south wall
		if(x < TOLERANCE && direction == 3){
			return false;
		}
		else if(y < TOLERANCE && direction == 0){
			return false;
		}
	/*	
		else if(y > 8 * SystemConstants.TILE && direction == 2){
			return false;
		}
	
		//Facing north wall
		else if(x > 8*SystemConstants.TILE && direction == 1){
			return false;
		}
	*/
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
