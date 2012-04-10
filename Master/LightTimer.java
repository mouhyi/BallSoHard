package Master;

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.util.*;

public class LightTimer implements TimerListener
{
	
	private final int SENSOR_THRESHOLD = 40;
	private final int DETECTION_THRESHOLD = 30;
	private int[] lightValue = new int[6];
	private LightSensor sensor; 
	private Timer lightTimer;
	private double robotSpeed;
	
	private boolean lineDetected = false;
	
	//Initializes timer and starts reading light values
	public LightTimer(LightSensor ls)		// robot speed in cm/s
	{
		this.sensor = ls;
		lightTimer = new Timer(25, this); // 166/this.robotSpeed
		lightTimer.start();
	}
		
	//Collects light sensor data
	public void timedOut(){
		int i;
		int maxIndex = lightValue.length-1;

		//puts light sensor readings in an array and shifts them for every ping
		for(i=0; i<maxIndex; i++){
			lightValue[i]=lightValue[i+1];
		}
		
			lightValue[maxIndex]=sensor.getNormalizedLightValue();
	//		RConsole.println("light = "+ lightValue[maxIndex]);
			/*
			if(this==left){
				RConsole.println(String.valueOf(lightValue[maxIndex]));
			}
			*/

			/*
			 * Calculates second derivative
			 * @author Ryan
			 */
			
			int diffAB = lightValue[0]-lightValue[1];
			int diffCD = lightValue[2]-lightValue[3];
			int totalDiff = diffAB-diffCD;
	//		RConsole.println("totalDiff = "+ totalDiff);
			
			// The total diff will be incorrect at first since the array is inialized at 0.
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
			}
			//try { Thread.sleep(10); } catch(Exception e){}
			
				
				
	}

	//Returns true if a line is detected
	public boolean lineDetected(){
		return lineDetected;
	}
	
	//Sets sets line detection to false
	public void resetLine(){
		lineDetected = false;
	}
	
	public void stop(){
		lightTimer.stop();
	}
	
	public int getLightValue()
	{
		return lightValue[0];
	}
	
	public LightSensor getSensor()
	{
		return sensor;
	}
}