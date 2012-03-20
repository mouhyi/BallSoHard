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

	/**
	 * @todo : get accurate value of radii  !!!!
	 */
	public static final double LEFT_RADIUS=2.6658;
	public static final double RIGHT_RADIUS= 2.71;   // 2.6380;
	public static final double WIDTH = 17.0;
	
	// CHANGE!!!!
	public static final double LS_WIDTH = 12.8;
	//Light sensor to centre of rotation
	public static final double LS_TOCENTRE = 13.9;
	//Distance to centre of both light sensors and centre of rotation
	public static final double LS_MIDDLE = Math.sqrt(Math.pow(LS_TOCENTRE, 2)-Math.pow((LS_WIDTH/2), 2));
	//Angle between light sensor and middle of robot
	public static final double LS_ANGLE_OFFSET = Math.asin((LS_WIDTH/2)/LS_TOCENTRE);

	// END CHANGE	

	public static final double FORWARD_SPEED = 100; 	// 150		// cm/s
	public static final double ROTATION_SPEED = 40.0;
	
	// 1 foot= 30.48cm	  	
 	public static double TILE = 30.48;


}