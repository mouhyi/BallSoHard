package Master;

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * Wrapper class for the ultrasonic sensors
 * 
 * @author Mouhyi, Ryan
 * @see Printer
 */

public class USPoller extends Thread{

	private int[] usDistances = new int[5];
	public UltrasonicSensor us;
	public int median;
	private Object lock;

	/**
	 * Constructor
	 * 
	 * @author Mouhyi, Ryan
	 */
	public USPoller (UltrasonicSensor us) {
		this.us = us;
		this.start();
		lock = new Object();
	}
	
	/**
     * This method implements run method of the Runnable interface.
     *
     * @author Mouhyi, Ryan
     */
	public void run() {
	
		int i;
		int maxIndex = usDistances.length-1;
		int middle = maxIndex/2;
		
		while(true){
			//Inserts sensor readings into an array and shifts them for every ping
	//		for(i=0; i<maxIndex; i++){
	//			usDistances[i]=usDistances[i+1];
	//		}
			
	//		usDistances[maxIndex]=us.getDistance();
		
			//Sorts data and returns median
	//		filter(usDistances, 0);
		
			synchronized(lock){
				median = us.getDistance();
	//			RConsole.println(String.valueOf(this.getDistance()));
			}		
		
			try{
				Thread.sleep(50);
			}catch(Exception e){}
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
	
	
	public synchronized int getDistance(){
		return median;
		
	}
}	
