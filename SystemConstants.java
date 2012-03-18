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
 * @author Mouhyi
 */

public class SystemConstants {

	public static NXTRegulatedMotor leftMotor = Motor.A;
	public static NXTRegulatedMotor rightMotor = Motor.B;
	
	public static LightSensor leftLight = new LightSensor(SensorPort.S1);
	public static LightSensor rightLight = new LightSensor(SensorPort.S4);
	
	public static UltrasonicSensor US = new UltrasonicSensor(SensorPort.S2);
	
	/**The radii of the robot wheels . */
	public static final double LEFT_RADIUS=2.57;
	public static final double RIGHT_RADIUS= 2.56;
	
	/** The distance from on wheel to the other  */
	public static final double WIDTH = 16.1;
	public static final double LS_WIDTH = 10;
	
	private static double FORWARD_SPEED = 13.0; 	 // cm/s
	private static double ROTATION_SPEED = 40.0;	// Deg/s
	
	// 1 foot= 30.48cm
	private static double TILE = 30.48;
	
}
