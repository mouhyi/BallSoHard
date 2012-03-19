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

	public static final double LEFT_RADIUS=2.6;
	public static final double RIGHT_RADIUS= 2.6;
	public static final double WIDTH = 17.2;
	public static final double LS_WIDTH = 10;

	private static final double FORWARD_SPEED = 13.0; // cm/s
	public static final double ROTATION_SPEED = 40.0;
	
	// 1 foot= 30.48cm	  	
 	private static double TILE = 30.48;

}