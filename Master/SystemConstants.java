package Master;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
*
* Contains all the physical constants of the system
* All measures must be in cm and angles in DEGREES
*
* @author Mouhyi, Ryan
*/

public class SystemConstants {

	public static NXTRegulatedMotor leftMotor = Motor.A;
	public static NXTRegulatedMotor rightMotor = Motor.B;

	public static LightSensor leftLight = new LightSensor(SensorPort.S1);
	public static LightSensor rightLight = new LightSensor(SensorPort.S4);

	public static UltrasonicSensor USL = new UltrasonicSensor(SensorPort.S2);
	public static UltrasonicSensor USR = new UltrasonicSensor(SensorPort.S3);

	/** left radius is 2.7220, and the right radius is 2.7230
	 * 
	 * VERY IMPORTANT
	 * @todo : get accurate value of radii & width  !!!!
	 */
	public static final double LEFT_RADIUS=2.7220;
	public static final double RIGHT_RADIUS= 2.7230;   // 2.6380;
	public static final double WIDTH = 17.155;
	
	/*
	 * CHANGED: LS_ANGLE_OFFSET is measured in degrees
	 */
	
	//Distance between light sensors
	public static final double LS_WIDTH = 13;
	//Light sensor to centre of rotation
	public static final double LS_TOCENTRE = 15.3;
	//Distance to centre of both light sensors and centre of rotation
	public static final double LS_MIDDLE =  16.86;
	//Angle between light sensor and middle of robot
	public static final double LS_ANGLE_OFFSET = Math.toDegrees(Math.asin((LS_WIDTH/2)/LS_TOCENTRE));   		//In DEGREES
		
	/*		   
	 * 		   						|-----*-----|			Centre of rotation = *
	 *									 / \			
	 * 			 					    / | \       		LS_ANGLE_OFFSET = o
	 *  			LS_CENTRE----->    /  |  \			
	 *           					  /   |   \
	 *          					 /o   | <---------------LS_MIDDLE
	 *   						    #-----------#        	Light sensors = #  
	 * 			 					  LS_WIDTH
	 */
	
	public static final double FORWARD_SPEED = 10; 	// 150		// cm/s
	public static final double ROTATION_SPEED = 8.0;
	
	// 1 foot= 30.48cm	  	
 	public static double TILE = 30.48;
 	public static int MAX_X=8;
 	public static int MAX_Y=8;


}