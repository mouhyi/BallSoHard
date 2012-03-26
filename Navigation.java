import lejos.nxt.LCD;

/**
 * Movement control class
 * 
 * @author Mouhyi
 */

public class Navigation {

	private Odometer odo;
	private Robot robot;

	private static final double ROTATION_TOLERANCE = 0.5; // in Deg
	private static final double DISTANCE_TOLERANCE = 1; // in cm

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
		//double distance;
		Coordinates coords;
		double destAngle;
		
		robot.stop();
		
		try{
			Thread.sleep(1000);
		}catch(Exception e){;}
		
		coords = odo.getCoordinates();
		destAngle = Math.atan2(y - coords.getY(), x - coords.getX());
		destAngle = Odometer.convertToDeg(destAngle);
		turnTo(destAngle);
		
		try{
			Thread.sleep(1000);
		}catch(Exception e){;}

		/*coords = odo.getCoordinates();
		distance = getDistance(x, y, coords.getX(), coords.getY());

		robot.goForward(distance, (int) SystemConstants.FORWARD_SPEED);*/

		while(true){
			coords = odo.getCoordinates();
			if (Math.abs(x-coords.getX()) <DISTANCE_TOLERANCE
					&& Math.abs(y-coords.getY()) <DISTANCE_TOLERANCE ){
				LCD.drawString("ARRIVED           ", 0, 5);
				break;
			}
			LCD.drawString("ADVANCE	           ", 0, 5);
			robot.advance(SystemConstants.FORWARD_SPEED);
		}	
		robot.stop();

	}

	/**
	 * This method turns the robot to the absolute heading destAngle
	 * 
	 * @author Mouhyi
	 */
	public void turnTo(double destAngle) {
		double err;
		destAngle = Odometer.adjustAngle(destAngle);
		
		robot.stop();
		
		do {
			double curTheta = odo.getCoordinates().getTheta();
			double rotAngle = minimumAngleFromTo(curTheta, destAngle);

			robot.rotateAxis(rotAngle, (int) SystemConstants.ROTATION_SPEED);

			// and compute error
			curTheta = odo.getCoordinates().getTheta();
			err = destAngle - curTheta;

		} while (Math.abs(err) > ROTATION_TOLERANCE);
		robot.stop();

	}
	
	/**
	 * Drive one TILE and correct orientation
	 * @author Mouhyi
	 */
	public void navCorrect(){
		robot.goForward(SystemConstants.TILE, (int)SystemConstants.FORWARD_SPEED);
		try{
			Thread.sleep(1000);
		}catch(Exception e){;}
		turnTo(odo.getDirection() * 90);
	}
	
	/**
	 * Assumes the robot is facing the right direction
	 * @param x
	 */
	public void TravelToX( double x){
		boolean obstacle =false;
		Coordinates coords ;
		double curX ;
		
		
		do{	
			// update coords
			coords = odo.getCoordinates();
			curX = coords.getX();
			
			// TODO update obstacle
			
			
		}
		while(Math.abs( curX- x)> SystemConstants.TILE  && !obstacle );
		
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

	public static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) - Math.pow(y1 - y2, 2));
	}
}
