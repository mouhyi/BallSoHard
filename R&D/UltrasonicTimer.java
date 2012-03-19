/*
 * Ultrasonic Poller Class
 * Author: Ryan Singzon
 * V2.00
 */

import lejos.nxt.*;
import lejos.util.*;
import lejos.nxt.comm.*;

public class UltrasonicTimer extends Exception implements TimerListener{
	
	private final int REFRESH = 50;
	private final int TOLERANCE = 30;
	private int[] usDistances = new int[5];
	private UltrasonicSensor us;
	private Timer usTimer = new Timer(REFRESH, this);	
	private int median;
	private boolean obstacleDetected;
	
	//Initializes sensor and starts collecting data
	public UltrasonicTimer(UltrasonicSensor us){
		this.us = us;
		this.usTimer.start();
	}
	
	//Data is collected, sorted, and filtered here
	public void timedOut(){
		int i;
		int maxIndex = usDistances.length-1;
		int middle = maxIndex/2;
		
		//Inserts sensor readings into an array and shifts them for every ping
		for(i=0; i<maxIndex; i++){
			usDistances[i]=usDistances[i+1];
		}
		usDistances[maxIndex]=us.getDistance();
		
		//Sorts data and returns median
		sort(usDistances, 0);
		
		median = usDistances[middle];	
				
		if(median < TOLERANCE){
			obstacleDetected = true;
		}
	}
	
	//Sorts ultrasonic sensor readings in ascending order
	private void sort (int data[], int n) { 
		int i, j; 
		for (i = 0; i < n; i = i ++){
			j = i-1;
			for ( j=j; j >= 0 && data[j] > data[j+1]; j = j - 1){ 
				swap(data, j); 
			} 
		}
	}
	
	//Swaps values in array
	private void swap (int data[], int k) { 
		int temp; 
		temp = data[k];
		data[k] = data[k+1];
		data[k+1] = temp;
	}
		
	//Returns the filtered ultrasonic sensor reading
	public int getDistance(){
		return median;
	}
	
	//Sets a mode for determining whether or an obstacle has been detected
	public boolean obstacleDetected(){
		
		return obstacleDetected;
	}
	
	public void resetObstacle(){
		obstacleDetected = false;
	}
}