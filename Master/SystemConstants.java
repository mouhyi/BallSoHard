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
	 * Updated for new sensor position
	 * @author Ryan
	 */
	//Distance between light sensors
	public static final double LS_WIDTH = 24.2;

	//Light sensor to centre of rotation
	public static final double LS_TOCENTRE = 12.2;
	
	//Distance to centre of both light sensors and centre of rotation
	// @Mouhyi: I don't know I added a 1 to the formula but it seems to improve things
	public static final double LS_MIDDLE = Math.sqrt(Math.pow(LS_TOCENTRE,2)-Math.pow(LS_WIDTH/2, 2)) ;
	
	//Angle between light sensor and middle of robot
	public static final double LS_ANGLE_OFFSET = Math.toDegrees(Math.asin((LS_WIDTH/2)/LS_TOCENTRE));   		//In DEGREES
	
	public static final double FORWARD_SPEED = 15; 	// 150		// cm/s
	public static final double ROTATION_SPEED = 5.0;  //8
	
	// 1 foot= 30.48cm	  	
 	public static double TILE = 30.48;
 	public static int MAX_X=8;
 	public static int MAX_Y=8;


}