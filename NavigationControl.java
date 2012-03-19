import lejos.nxt.*;
import lejos.util.*;
import lejos.nxt.comm.*;

//The robot is only going to travel on the gridlines
//If it sees an obstacle, it will find an alternate route 

/*
 * 
 * @Author Ryan
 * Change the lab odometer to the actual odometer
 * Use Coordinates instead of coordinates obtained from lab odometer
 */

public class NavigationControl implements TimerListener{
	
	private final int REFRESH = 50;
	private Timer ncTimer = new Timer(REFRESH, this);
	private UltrasonicTimer usLeft, usRight;
	private LightTimer lsLeft, lsRight;
	private Robot robot;
	private Odometer odometer;
	private Navigation navigation;
	private int[] destination;
	
	//Initializes the navigation control
	public NavigationControl(Odometer odometer, UltrasonicTimer usLeft,
			UltrasonicTimer usRight, LightTimer lsLeft, LightTimer lsRight, Robot robot){
		
		this.odometer = odometer;
		this.usLeft = usLeft;
		this.usRight = usRight;
		this.lsLeft = lsLeft;
		this.lsRight = lsRight;
		this.robot = robot;
		this.ncTimer.start();
	}
	
	/*
	 * Continuously polls ultrasonic sensor
	 * When obstacle is detected, the robot will turn right 90 degrees
	 */
	public void timedOut(){
		int leftDistance = usLeft.getDistance();
		
		RConsole.println("USLeft: "+String.valueOf(leftDistance));
		
		if(usLeft.obstacleDetected()){
			RConsole.println("Obstacle Detected");
			usLeft.resetObstacle();
			changeRoute();
		}
		
		if(usRight.obstacleDetected()){
			usRight.resetObstacle();
			
		}
		
	}
	
	
	/*
	 * This method also needs to be polling the sensor for more obstacles in the way
	 * Requires use of light sensor since it is going to turn then move forward until
	 * it reaches a gridline
	 * 
	 */
	private void changeRoute(){
		
		//Pause searching for obstacles
		ncTimer.stop();
		
		//Reverse until a gridline crossing is reached
		RConsole.println("Changing route");
		//robot.stop();
		
		robot.advance(-SystemConstants.FORWARD_SPEED);
		
		lsLeft.resetLine();
		lsRight.resetLine();
		
		while(!lsLeft.lineDetected()){}
		while(!lsRight.lineDetected()){}
		RConsole.println("Lines detected");
		
		//robot.stop();	
		
		//Recalibrate heading here
			
		//Rotate robot 90 degrees
		if(odometer.getTheta() == 0){
			robot.rotate(SystemConstants.ROTATION_SPEED);
			while(odometer.getTheta()<90){
			}
			//robot.stop();
		}
		
		if(odometer.getTheta() == 90){
			robot.rotate(SystemConstants.ROTATION_SPEED);
			while(odometer.getTheta()<180){
			}
			//robot.stop();
		}
		
		if(odometer.getTheta() == 180){
			robot.rotate(SystemConstants.ROTATION_SPEED);
			while(odometer.getTheta()<270){
			}
			//robot.stop();
		}
		
		if(odometer.getTheta() == 270){
			robot.rotate(SystemConstants.ROTATION_SPEED);
			while(odometer.getTheta()!=0){
			}
			//robot.stop();
		}
		
		//Resume looking for obstacles
		ncTimer.start();
		
		//Move forward until a gridline has been crossed
		robot.advance(SystemConstants.FORWARD_SPEED);
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		lsLeft.resetLine();
		lsRight.resetLine();
		
		while(!lsLeft.lineDetected()){}
		while(!lsRight.lineDetected()){}
		
		robot.stop();
			
	}
	
	public void recalculateTrajectory(){
		
		destination = navigation.getDestination();
		navigation.travelTo(destination[0], destination[1]);
				
	}
	
	public void adjustHeading(){
		
	}

}