import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.util.*;

public class LightTimer implements TimerListener{
	
	private final int REFRESH = 5;
	private final int SENSOR_THRESHOLD = 4;
	private final int DETECTION_THRESHOLD = 4;
	private int[] lightValue = new int[6];
	private LightSensor sensor; 
	private Timer lightTimer = new Timer(REFRESH, this);
	private boolean lineDetected = false;
	
	//Initializes timer and starts reading light values
	public LightTimer(LightSensor lsl){
		this.sensor = lsl;
		this.lightTimer.start();
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
		if(!(Math.abs(lightValue[maxIndex]-sensor.getLightValue()) < SENSOR_THRESHOLD)){
			lightValue[maxIndex]=sensor.getLightValue();
		}
		
		//Prints the filtered light value
		RConsole.println(String.valueOf(lightValue[maxIndex]));
		
		//Detects a line if there is first a positive difference in light, then negative
		if(lightValue[0]-lightValue[1]>= DETECTION_THRESHOLD &&
		  (lightValue[1]-lightValue[2]<=-DETECTION_THRESHOLD || 
		   lightValue[2]-lightValue[3]<=-DETECTION_THRESHOLD ||
		   lightValue[3]-lightValue[4]<=-DETECTION_THRESHOLD ||
		   lightValue[4]-lightValue[5]<=-DETECTION_THRESHOLD) &&
		   lineDetected==false){
			
				lineDetected=true;				
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
	
}