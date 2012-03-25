import lejos.nxt.*;
import lejos.util.*;

public class LightTimer implements TimerListener
{
	
	private final int SENSOR_THRESHOLD = 16;
	private final int DETECTION_THRESHOLD = 16;
	private int[] lightValue = new int[6];
	private LightSensor sensor; 
	private Timer lightTimer;
	private double robotSpeed;
	
	private boolean lineDetected = false;
	
	//Initializes timer and starts reading light values
	public LightTimer(LightSensor ls, double robotSpeed)		// robot speed in cm/s
	{
		this.sensor = ls;
		this.robotSpeed = robotSpeed;
		lightTimer = new Timer((int)(166/this.robotSpeed), this); // 166/this.robotSpeed
		LCD.drawString("" + this.robotSpeed, 0 , 7);
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
		
		//Filters out values if the difference between subsequent values is too small
		if(!(Math.abs(lightValue[maxIndex]-sensor.getNormalizedLightValue()) < SENSOR_THRESHOLD)){
			lightValue[maxIndex]=sensor.getNormalizedLightValue();
		}
		
		//Detects a line if there is first a positive difference in light, then negative
		if(lightValue[0]-lightValue[1]>= DETECTION_THRESHOLD &&
		  (lightValue[1]-lightValue[2]<=-DETECTION_THRESHOLD || 
		   lightValue[2]-lightValue[3]<=-DETECTION_THRESHOLD ||
		   lightValue[3]-lightValue[4]<=-DETECTION_THRESHOLD ||
		   lightValue[4]-lightValue[5]<=-DETECTION_THRESHOLD) &&
		   lineDetected==false){
			
				lineDetected=true;
				// Clear out the line condition
				for(i=0; i<maxIndex; i++){
					lightValue[i]=lightValue[i+1];
				}
				// Delay to reduce risk of counting line again
				try
				{
					Thread.sleep((int)(500/this.robotSpeed));
				}
				catch (InterruptedException e)
				{
				
				}
		}
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
	
}