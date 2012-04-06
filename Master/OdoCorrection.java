package Master;

import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

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

	private Object lock ;
	private boolean correcting;

	public OdoCorrection(Odometer odo, Robot robot) {
		this.odo = odo;
		this.robot = robot;
		detected = 0;
		enabled = false;
		
		lock = new Object();
		correcting = false;

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
					/*
					 * Refresh direction before correction
					 */
					
					curDirection = odo.getDirection();
					
					double deltaTacho = tachoCount;
					tachoCount = (leftFirst) ? robot.getLeftTacho() : robot.getRightTacho();
					deltaTacho = tachoCount - deltaTacho;
					
					// compute distance traveled between line cross detections
					double distTraveled = deltaTacho / 180.0
											* Math.PI * SystemConstants.LEFT_RADIUS;
					//RConsole.println("Distance Traveled: "+String.valueOf(distTraveled));
					
					// id distance is too large
					if(distTraveled > 10){
						reset(detector);
						return;
					}
					else{
						
						synchronized(lock){
							correcting = true;
						}
						
						
						double theta = this.correctAngle(distTraveled, curDirection, leftFirst);
						Coordinates pos = odo.getCoordinates();
						
						
						if(curDirection % 2 == 0){
							double x = pos.getX();
							double axis;
							// ls.mid == robot.center???
							
							/*
							 * Axis depends on which direction robot is facing
							 * @author Ryan
							 */
							
							//Facing east
							if(curDirection == 0){
								axis = Math.round((x-SystemConstants.LS_MIDDLE) / SystemConstants.TILE ) *SystemConstants.TILE;
							}
							//Facing west
							else{
								axis = Math.round((x+SystemConstants.LS_MIDDLE) / SystemConstants.TILE ) * SystemConstants.TILE;
							}
							
							RConsole.println("Original theta: "+ String.valueOf(pos.getTheta()));
							
							RConsole.println("Original x: "+String.valueOf(pos.getX()));
							
							
							//Changed calculation
							x = axis + SystemConstants.LS_TOCENTRE*Math.sin(Math.toRadians(theta+90-SystemConstants.LS_ANGLE_OFFSET));
							
							//RConsole.println("Axis: "+String.valueOf(axis));
							
							
							double TmpX = axis + ( distTraveled)/2 * Math.cos(theta)  + SystemConstants.LS_MIDDLE  ;
							
							
							
							RConsole.println("New x: " + String.valueOf(TmpX));
							
							Sound.setVolume(50);
							Sound.beep();
									
							odo.setCoordinates(TmpX, 0, theta, new boolean[] {true, false, true});	
						}
						
						if(curDirection % 2 == 1){
							double y = pos.getY();
							double axis;
							
							// ls.mid == robot.center???
							
							/*
							 * Change axis depending on current direction
							 * @author Ryan
							 */
							
							//Facing North
							if(curDirection == 1){
								axis = Math.round((y-SystemConstants.LS_MIDDLE) / SystemConstants.TILE ) *SystemConstants.TILE;
							}
							//Facing South
							else{
								axis = Math.round((y+SystemConstants.LS_MIDDLE) / SystemConstants.TILE ) * SystemConstants.TILE;
							}
							RConsole.println("Original y: " +String.valueOf(pos.getY())); 
							
							//RConsole.println("Axis: "+axis);
							
							//Changed calculation
							y = axis + SystemConstants.LS_TOCENTRE*Math.sin(Math.toRadians(theta+90-SystemConstants.LS_ANGLE_OFFSET));
							
							double TmpY = axis + ( distTraveled)/2 * Math.sin(theta)  + SystemConstants.LS_MIDDLE  ;
							
							RConsole.println("New y: "+String.valueOf(TmpY));
							
							odo.setCoordinates(0, TmpY, theta, new boolean[] {false, true, true});
						}
						
						// reset
						RConsole.println("Odometer corrected");
						//RConsole.println("");
						Sound.setVolume(100);
						Sound.beep();
						this.reset();
						
						
					}
					
					synchronized(lock){
						correcting = false;
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
		
		//RConsole.println("Offset angle: "+String.valueOf(offsetAngle));
		
		offsetAngle = Odometer.adjustAngle(offsetAngle);

		// Subtract offsetAngle if the left sensor detects the line first, add
		// otherwise
		int coef = (leftFirst) ? -1 : 1;
		theta = direction * 90 + coef * offsetAngle;
		theta = Odometer.adjustAngle(theta);
		
		//RConsole.println("New theta: " + String.valueOf(theta));

		return theta;
	}

	public void reset() {
		this.detected = 0;
		tachoCount = 0;
		prevDirection = curDirection;
	}
	
	public void reset(LineDetector detector) {
		prevDirection = curDirection;
		detected = 1;
		leftFirst = detector.isLeft();
		tachoCount = (leftFirst) ? robot.getLeftTacho() : robot.getRightTacho();
	}

	/**
	 * Setters and Getters
	 * 
	 * @author Mouhyi
	 */
	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public synchronized boolean isEnabled() {
		return enabled;
	}
	
	public boolean isCorrecting(){
		synchronized(lock){
			return correcting;
		}
	}
}
