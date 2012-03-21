/**
 * Movement control class
 * 
 * @author Mouhyi
 */

public class Navigation {

	private Odometer odo;
	private Robot robot;

	private static final int ROTATION_TOLERANCE = 1; // in Deg
	private static final int DISTANCE_TOLERANCE = 1; // in Deg

	/**
	 * Constructor
	 * 
	 * @param odo
	 * 
	 * @author Mouhyi
	 */
	public Navigation(Odometer odo, Robot robot) {
		this.odo = odo;
		this.robot = robot;
	}

	/**
	 * This method takes as arguments the x and y position in cm Will travel to
	 * designated position, while constantly updating it's heading
	 * 
	 * @author Mouhyi
	 */
	public void travelTo(double x, double y) {
		double distance;
		Coordinates coords;
		double destAngle;
		
		coords = odo.getCoordinates();
		destAngle = Math.atan2(y - coords.getY() , x - coords.getX());
		destAngle = Odometer.convertToDeg(destAngle);
		turnTo(destAngle);
		
		do{
			coords = odo.getCoordinates();
			distance = getDistance(x, y, coords.getX(), coords.getY());
			
			robot.goForward(distance, (int)SystemConstants.FORWARD_SPEED);
			
			coords = odo.getCoordinates();
			distance = getDistance(x, y, coords.getX(), coords.getY());
			
		}while(distance > DISTANCE_TOLERANCE);
		
	}

	/**
	 * This method turns the robot to the absolute heading destAngle
	 * 
	 * @author Mouhyi
	 */
	public void turnTo(double destAngle) {
		double err;
		do{
			destAngle = Odometer.adjustAngle(destAngle);
			double curTheta = odo.getCoordinates().getTheta();
			double rotAngle = minimumAngleFromTo(destAngle, curTheta);

			robot.rotateAxis(rotAngle, (int) SystemConstants.ROTATION_SPEED);
			
			// and compute error
			curTheta = odo.getCoordinates().getTheta();
			err = destAngle - curTheta;
			
		}while (Math.abs(err) > ROTATION_TOLERANCE);
		
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return minimum angle that the robot should rotate by to get from head a
	 *         to b.
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = Odometer.adjustAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
	
	public static double getDistance(double x1, double y1, double x2, double y2 ){
		return Math.sqrt ( Math.pow(x1- x2, 2) - Math.pow( y1 - y2 , 2) );
	}
}
