package Master;

import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;

/**
 * Movement control class
 * 
 * @author Mouhyi
 */

public class Navigation {

	private Odometer odo;
	private Robot robot;
	public ObstacleDetection us;
	OdoCorrection snapper;
	Localization localizer;
	private double w1, w2;

	private static final double ROTATION_TOLERANCE = 0.5; // in Deg
	private static final double DISTANCE_TOLERANCE = 3; // in cm

	/** minimum distance necessary for the robot to move forward */
	public static final int ObstacleDist = 50;

	/**
	 * Constructor
	 * 
	 * @param odo
	 * 
	 * @author Mouhyi
	 */
	public Navigation(Odometer odo, Robot robot, ObstacleDetection us,
			OdoCorrection snapper, Localization localizer, int w1, int w2) {
		this.odo = odo;
		this.robot = robot;
		this.us = us;
		this.snapper = snapper;
		this.localizer = localizer;
		this.w1 = w1*SystemConstants.TILE;
		this.w2 = w2*SystemConstants.TILE;
	}

	/**
	 * This method takes as arguments the x and y position in cm Will travel to
	 * designated position, while constantly updating it's heading
	 * 
	 * @author Mouhyi
	 */
	public boolean travelTo(double x, double y) {
		// double distance;
		Coordinates coords;
		double destAngle;

		/*
		 * try{ Thread.sleep(1000); }catch(Exception e){;}
		 */
		
		robot.stop();

		coords = odo.getCoordinates();
		destAngle = Math.atan2(y - coords.getY(), x - coords.getX());
		destAngle = Odometer.convertToDeg(destAngle);
		
		/*
		 * Doesn't turn if error is too small
		 * @author Ryan
		 */
		double difference = Math.abs(coords.getTheta()-destAngle);
		
		if(difference > 0.5){
			turnTo(destAngle);
			RConsole.println("Turn: from "+coords.getTheta()+" TO "+destAngle);
		}
		else{
	//		RConsole.println("TravelTo: Already facing destination");
		}

		
		/*
		 * Uncommented this to work on obstacle avoidance
		 * @author Ryan
		 
		int distance = us.getDistance();
		if(distance == 4){
			distance = 255;
		}

		if (distance < ObstacleDist) {
			
			//Don't detect obstacle if destination is beside a wall
			if(odo.getDirection() == 0 && x > 8*SystemConstants.TILE){}
			else if(odo.getDirection() == 1 && y > 10*SystemConstants.TILE){}
			else if(odo.getDirection() == 2 && x < 1*SystemConstants.TILE){}
			else if(odo.getDirection() == 3 && y < 1*SystemConstants.TILE){}
			else{
				RConsole.println("Obstacle");
				return false;
			}
			
		}
		*/
		
	//	RConsole.println("TravelTo: Advance");
		
		//robot.advance(SystemConstants.FORWARD_SPEED);

		while (true) {
			coords = odo.getCoordinates();
			
	//		RConsole.println(""+us.getDistance());
			
			/*
			 * Added a condition to only check one direction to prevent the robot from
			 * continuing to move forward even after it has reached its destination
			 * @author Ryan, Mouhyi
			 */
			
			while(snapper.isCorrecting()){
				robot.stop();
			}
				
			if(odo.getDirection()== 1){
				if ( y < coords.getY() ){
					break;
				}
			}
			
			if(odo.getDirection()==3){
				if ( y > coords.getY() ){
					break;
				}
			}
			
			if(odo.getDirection()==0){
				if ( x < coords.getX() ){
					break;
				}
			}
			
			if(odo.getDirection()==2){
				if ( x > coords.getX() ){
					break;
				}
			}
			
			
			robot.advance(SystemConstants.FORWARD_SPEED);
		}
		robot.stop();

		turnTo(odo.getDirection() * 90); // / added to align

		// RConsole.println("TravelTo: advance");
	//	RConsole.println("TravelTo ARRIVEDto: x=" + x + ",  y=" + y);

		return true;

	}

	/**
	 * This method turns the robot to the absolute heading destAngle
	 * 
	 * @author Mouhyi
	 */
	public void turnTo(double destAngle) {
		double err;
		destAngle = Odometer.adjustAngle(destAngle);

//		robot.stop();

		// save snapper state and disable
		boolean tmp = snapper.isEnabled();
		snapper.setEnabled(false);

		do {
			double curTheta = odo.getCoordinates().getTheta();
			double rotAngle = minimumAngleFromTo(curTheta, destAngle);

			robot.rotateAxis(rotAngle, (int) SystemConstants.ROTATION_SPEED);

			// and compute error
			curTheta = odo.getCoordinates().getTheta();
			err = destAngle - curTheta;

		} while (Math.abs(err) > ROTATION_TOLERANCE);
		robot.stop();

		// restore snapper state
		snapper.setEnabled(tmp);

		// RConsole.println("END TURN");

	}

	/**
	 * Main navigation method
	 * 
	 * @param x
	 * @param y
	 * @author Mouhyi
	 */

	public void GoTo(double x, double y) {

		Coordinates coords;
		double curX, curY;
		boolean obstacle = false;
		boolean destinationReached = false;
		int xDiff, yDiff;

	//	RConsole.println("Going to");
		while (!destinationReached) {
			
			double xDestination = 0;
			double yDestination = 9 * SystemConstants.TILE;
			
			do {
				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();
				
				RConsole.println("Destination x: "+x);
				RConsole.println("Current x: "+curX);
				
				/* Number of tiles to move
				 * Added absolute value in case destination < current location
				 * @author Ryan
				 */
				xDiff = (int) Math.round(Math.abs(x - curX) / SystemConstants.TILE) - 1;
				if(xDiff==-1) break;

				RConsole.println("xdiff" + xDiff);

				/*
				 * Added condition to prevent entering defender zone
				 * @author Ryan
				 */
				if(x > curX){
					xDestination = x - xDiff * SystemConstants.TILE;
					if(xDestination < (5*SystemConstants.TILE + Math.round(w1/2))
							&& xDestination > (5 * SystemConstants.TILE - Math.round(w1/2))
							&& yDestination > 6 * SystemConstants.TILE
							&& yDestination < 10* SystemConstants.TILE){
						RConsole.println("Destination x in defender zone");
						break;
					}
					else{
						obstacle = !travelTo(xDestination, curY);
					}
				}
				else{
					xDestination = x + xDiff * SystemConstants.TILE;
					if(xDestination < (5*SystemConstants.TILE + Math.round(w1/2))
							&& xDestination > (5 *SystemConstants.TILE- Math.round(w1/2))
							&& yDestination > 6 * SystemConstants.TILE
							&& yDestination < 10* SystemConstants.TILE){
						break;
					}
					else{
						obstacle = !travelTo(xDestination, curY);
					}
					
				}
				RConsole.println("Traveled one tile horizontally");

				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();
				
			} while (Math.abs(curX - x) > DISTANCE_TOLERANCE && !obstacle);
			
			/*
			 * If an obstacle is detected, call avoidObstacle
			 */
			if(obstacle){
				avoidObstacle(x, y);
			}
			
	//		localizer.MidLocalization(odo.getCoordinates().getX(),odo.getCoordinates().getY(), odo.getCoordinates().getTheta());

			do {
				obstacle = false;
				
				/*
				 * Added absolute value
				 * @author Ryan
				 */				
				
				RConsole.println("Destination y: "+y);
				RConsole.println("Current y: "+curY);
				
				yDiff = (int) Math.round(Math.abs(y - curY) / SystemConstants.TILE) - 1;
				if(yDiff==-1) break;
				
				RConsole.println("ydiff" + yDiff);

				/**
				 * Added conditions
				 * @author Ryan
				 */
				if(y > curY){
					yDestination = y - yDiff * SystemConstants.TILE;
					if(yDestination < 10 * SystemConstants.TILE 
							&& yDestination > 6 * SystemConstants.TILE
							&& xDestination < (5 * SystemConstants.TILE + Math.round(w1/2))
							&& xDestination > (5 * SystemConstants.TILE- Math.round(w1/2))){
						break;
					}
					else{
						obstacle = !travelTo(curX, yDestination);
					}
				}
				else{
					yDestination = y + yDiff * SystemConstants.TILE;
					if(yDestination < 10 * SystemConstants.TILE 
							&& yDestination > 6 * SystemConstants.TILE
							&& xDestination < (5 * SystemConstants.TILE + Math.round(w1/2))
							&& xDestination > (5 * SystemConstants.TILE - Math.round(w1/2))){
						break;
					}
					else{
						obstacle = !travelTo(curX, yDestination);
					}
				}
				
				// update position
				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();

			} while (Math.abs(curY - y) > DISTANCE_TOLERANCE && !obstacle);
			
			if(obstacle){
				avoidObstacle(x, y);
			}

			localizer.MidLocalization(odo.getCoordinates().getX(),odo.getCoordinates().getY(), odo.getCoordinates().getTheta());
			
			// if(obstacle ) call obstacle avoidance
			// else if destination reached break
			// else, i.e, obstacle in destination: return -1;
			robot.stop();
			
			
			if(Math.abs(curX - x) < DISTANCE_TOLERANCE && Math.abs(curY - y) < DISTANCE_TOLERANCE){
				destinationReached = true;
			}
			
			
		}

	}
	
	/**
	 * Retreives balls from the dispenser:
	 * Localizes one tile away from the dispenser then backs into the button
	 * @params x, y, omega
	 * @author Ryan
	 * 
	 */
public void getBall(double x, double y, int orientation){
		
		/*
		 * From specs:
		 * {1,2,3,4} corresponds to the cardinal directions N, E, S, W.
		 *
		 * Convert orientation to our convention
		 * 0:E, 1:N, 2:W, 3:S
		 */
				
		double startX = x, startY = y;
		
		RConsole.println("Dispenser x: "+x);
		RConsole.println("Dispenser y: "+y);
				
		// North
		if (orientation == 1) {
			startY = y + 2*SystemConstants.TILE;
		}
		// East
		else if (orientation == 2) {
			startX = x + 2*SystemConstants.TILE;
			orientation = 0;
		}
		// South
		else if (orientation == 3) {
			startY = y - 2*SystemConstants.TILE;
		}
		// West
		else {
			startX = x - 2*SystemConstants.TILE;
			orientation = 2;
		}
		
		int alignDirection = orientation - 1;
		if(alignDirection == -1){
			alignDirection = 3;
		}
		
		//Move to one node away from the dispenser
		RConsole.println("GoTo: "+startX+", "+startY);
		GoTo(startX, startY);
		
		//Align the back of the robot with the button
		turnTo(90 * alignDirection);
		robot.goForward(7,5);
		
		turnTo(90 * orientation);
		
		robot.goForward(-33,5);
		for(int i = 0; i < 4; i++){
			robot.goForward(5,5);
			robot.goForward(-5,5);
		}
	}

	/**
	 * Drive one TILE and correct orientation
	 * 
	 * @author Mouhyi
	 */
	public void navCorrect() {
		robot.goForward(SystemConstants.TILE,
				(int) SystemConstants.FORWARD_SPEED);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			;
		}
		turnTo(odo.getDirection() * 90);
	}

	/**
	 * Travels along the X axis until it reachs {@param: x} or an obstacle is
	 * detected Assumes the robot is facing the right direction
	 * 
	 * @param x
	 * @return 0 if destination reached, -1 if obstacle detected
	 * @author Mouhyi
	 */
	public int TravelToX(double x) {
		boolean obstacle = false;
		Coordinates coords;
		double curX;

		while (true) {
			// update coords
			coords = odo.getCoordinates();
			curX = coords.getX();

			// TODO update obstacle

			if (Math.abs(curX - x) < DISTANCE_TOLERANCE || !obstacle)
				break;

			// go forward one tile
			navCorrect();

		}
		if (obstacle)
			return -1;
		return 0;

	}

	/**
	 * Travels along the Y axis until it reachs {@param: y} or an obstacle is
	 * detected Assumes the robot is facing the right direction
	 * 
	 * @param y
	 * @return 0 if destination reached, -1 if obstacle detected
	 * @author Mouhyi
	 */
	public int TravelToY(double y) {
		boolean obstacle = false;
		Coordinates coords;
		double curY;

		do {
			// update coords
			coords = odo.getCoordinates();
			curY = coords.getY();

			// TODO update obstacle

			// go forward one tile
			navCorrect();

		} while (Math.abs(curY - y) > DISTANCE_TOLERANCE && !obstacle);
		if (obstacle)
			return -1;
		return 0;

	}

	/**
	 * Obstacle avoidance Routine
	 * 
	 * @author mouhyi
	 */
	
	/*
	 * Added parameters x and y to recursively call the GoTo method after
	 * avoiding obstacles until it has reached its destination
	 * @author Ryan
	 */
	public void avoidObstacle(double x, double y) {
		int dir = odo.getDirection();
		boolean rightFree = true;
		boolean leftFree = true;

		turnLeft();
		leftFree = (us.getDistance() > ObstacleDist);
		if (leftFree) {
			navCorrect();
		} else {
			// go back to original dir
			turnRight();
			// then turn right
			turnRight();
			rightFree = (us.getDistance() > ObstacleDist);
			if (rightFree) {
				navCorrect();
			} else {
				turnRight();
				turnBack();
				navCorrect();
			}
		}
		
		this.GoTo(x, y);
	}

	/**
	 * @author mouhyi
	 */
	public void turnLeft() {
		turnTo((odo.getDirection() + 1) * 90);
	}

	/**
	 * @author mouhyi
	 */
	public void turnRight() {
		turnTo((odo.getDirection() - 1) * 90);
	}

	/**
	 * @author mouhyi
	 */
	public void turnBack() {
		turnTo((odo.getDirection() + 2) * 90);
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
