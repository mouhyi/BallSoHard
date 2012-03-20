/**
 * 
 * This Class implements the odometer correction routine. it listens to LineDetector 
 * <b>
 * It is applicable only when navigating in straight lines along the grid lines.
 * <b>
 * 
 * @author Mouhyi
 * 
 * @see LineDetector
 * @see Odometer
 */

public class OdoCorrection {

	private Odometer odo;
	private Robot robot;
	private boolean enabled;
	private boolean leftFirst;
	// detected: number of line cross detected
	private int detected;
	private int curDirection, prevDirection;
	private double tachoCount;

	private long startTime;
	private static int timedout = 1000;

	public OdoCorrection(Odometer odo, Robot robot) {
		this.odo = odo;
		this.robot = robot;
		detected = 0;
		enabled = false;

	}

	/**
	 * Gets notified after a new line cross and executes the Odometry correction
	 * routine.
	 * 
	 * @param detector
	 *            indicates the detector which is calling this method
	 */
	public synchronized void lineDetected(LineDetector detector) {

		if (!enabled)
			return;

		// Get current direction
		curDirection = odo.getDirection();

		// if heading has changed: reset
		if (curDirection != prevDirection) {
			reset();
		}

		// If this is the first line
		if (detected == 0) {
			reset(detector);
			
			/*detected = 1;
			leftFirst = detector.isLeft();
			tachoCount = (leftFirst) ? robot.getLeftTacho() : robot.getRightTacho();
			startTime = System.currentTimeMillis();*/
		}
		// if same line
		else if (detected == 1) {
			if (leftFirst == detector.isLeft()) {
				reset(detector);
				return;
			}
			// If it's a different line
			else {
				// new line	 //REMOVE!
				if (System.currentTimeMillis() - startTime > timedout) {
					//reset(detector);
				}
				// second line
				else {
					double deltaTacho = tachoCount;
					tachoCount = (leftFirst) ? robot.getLeftTacho() : robot.getRightTacho();
					deltaTacho = tachoCount - deltaTacho;
					
					// compute distance traveled between line cross detections
					double distTraveled = deltaTacho / 180.0
											* Math.PI * SystemConstants.LEFT_RADIUS;
					// id distance is too large
					if(distTraveled > 10){
						reset(detector);
						return;
					}
					else{
						double theta = this.correctAngle(distTraveled, curDirection, leftFirst);
						Coordinates pos = odo.getCoordinates();
						
						if(curDirection % 2 == 0){
							double x = pos.getX();
							// ls.mid == robot.center???
							double axis = Math.round(x / SystemConstants.TILE ) *SystemConstants.TILE;
							x = axis + ( distTraveled - SystemConstants.LS_MIDDLE )/2 * Math.cos(theta);
							// x-= LS_Offset
							
							odo.setCoordinates(x, 0, theta, new boolean[] {true, false, true});	
						}
						
						if(curDirection % 2 == 1){
							double y = pos.getY();
							// ls.mid == robot.center???
							double axis = Math.round(y / SystemConstants.TILE ) *SystemConstants.TILE;
							y = axis + ( distTraveled - SystemConstants.LS_MIDDLE  )/2 * Math.sin(theta);
							// x-= LS_Offset
							
							odo.setCoordinates(0, y, theta, new boolean[] {false, true, true});	
						}
						
						// reset
						this.reset();
						
					}
				}
			}
		}
	}

	/**
	 * This method computes the corrected value of the polar angle theta of the
	 * system.
	 * 
	 * @author Mouhyi
	 */
	public double correctAngle(double distTraveled, int direction,
			boolean leftFirst) {

		double offsetAngle, x, y, theta;

		offsetAngle = Math.atan(distTraveled / SystemConstants.LS_WIDTH);
		offsetAngle = Odometer.convertToDeg(offsetAngle);
		offsetAngle = Odometer.adjustAngle(offsetAngle);

		// Subtract offsetAngle if the left sensor detects the line first, add
		// otherwise
		int coef = (leftFirst) ? -1 : 1;
		theta = direction * 90 + coef * offsetAngle;
		theta = Odometer.adjustAngle(theta);

		return theta;
	}

	public void reset() {
		this.detected = 0;
		tachoCount = 0;
		startTime = 0;
		prevDirection = curDirection;
	}
	
	public void reset(LineDetector detector) {
		prevDirection = curDirection;
		detected = 1;
		leftFirst = detector.isLeft();
		tachoCount = (leftFirst) ? robot.getLeftTacho() : robot.getRightTacho();
		startTime = System.currentTimeMillis();
	}

	/**
	 * Setters and Getters
	 * 
	 * @author Mouhyi
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
