package Master;

import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;

/**
 * Movement control class
 * 
 * @author Mouhyi
 */

public class NavigationRoundThree {

	private Odometer odo;
	private Robot robot;
	public ObstacleDetection us;
	OdoCorrection snapper;
	Localization localizer;
	private double w1, w2;
	private USPoller usL, usR;

	private static final double ROTATION_TOLERANCE = 0.5; // in Deg
	private static final double DISTANCE_TOLERANCE = 3; // in cm
	private int travelCount = 0;
	private static final int OBSTACLE_THRESHOLD = 45;
	
	/*
	 *CHANGED: Rotation tolerance from 10-5
	 */
	private static final double RESTRICTION_TOLERANCE = 5;

	/** minimum distance necessary for the robot to move forward */
	

	/**
	 * Constructor for Attacker
	 * 
	 * @param odo
	 * 
	 * @author Mouhyi, Ryan
	 */
	public NavigationRoundThree(Odometer odo, Robot robot, ObstacleDetection us,
			OdoCorrection snapper, Localization localizer, int w1, int w2, USPoller usL, USPoller usR) {
		this.odo = odo;
		this.robot = robot;
		this.us = us;
		this.snapper = snapper;
		this.localizer = localizer;
		this.w1 = w1*SystemConstants.TILE;
		this.w2 = w2*SystemConstants.TILE;
		this.usL = usL;
		this.usR = usR;
	}
	
	/**
	 * Constructor for Defender
	 * 
	 * @param odo
	 * 
	 * @author Mouhyi, Josh
	 */
	public NavigationRoundThree(Odometer odo, Robot robot, ObstacleDetection us,
			OdoCorrection snapper, Localization localizer) {
		this.odo = odo;
		this.robot = robot;
		this.us = us;
		this.snapper = snapper;
		this.localizer = localizer;
		// Arbitrary Large Value to Allow Defender to enter the real D-Zone
		this.w1 = 0*SystemConstants.TILE;
		this.w2 = 1*SystemConstants.TILE;
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

		travelCount = travelCount+1;
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
	//		RConsole.println(""+usR.getDistance());
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
		if(travelCount % 4 == 0){
			localizer.MidLocalization(odo.getCoordinates().getX(), odo.getCoordinates().getY(), odo.getCoordinates().getTheta()); 
		}
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
		// I don't thiink you need to initialize anymore but leaving for not for good measure
		double xDestination;
		double yDestination;
		// Boolean to determine circumstance of travel
		boolean doneX = false, doneY = false, cantX = false, cantY = false;
		
		// Helps for special Case
		double specialX;
		double specialY;
		if (odo.getCoordinates().getX() < 5*SystemConstants.TILE) {
			specialX = 11*SystemConstants.TILE;
		}
		else {
			specialX = -1*SystemConstants.TILE;
		}
		if (odo.getCoordinates().getY() < 5*SystemConstants.TILE) {
			specialY = 11*SystemConstants.TILE;
		}
		else {
			specialY = -1*SystemConstants.TILE;
		}
		
		RConsole.println("Special X: "+ specialX);
		RConsole.println("Special Y: "+ specialY);
	//	RConsole.println("Going to");
		while (!destinationReached) {
			
			do {
				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();
				
				//RConsole.println("Destination x: "+x);
				//RConsole.println("Current x: "+curX);
				
				/* Number of tiles to move
				 * Added absolute value in case destination < current location
				 * @author Ryan
				 */
				xDiff = (int) Math.round(Math.abs(x - curX) / SystemConstants.TILE) - 1;
				if(xDiff==-1 || doneX) {
					doneX = true;
					break;
				}

				//RConsole.println("xdiff" + xDiff);

				/*
				 * Added condition to prevent entering defender zone
				 * @author Ryan
				 */
				
				if(x > curX){
					xDestination = x - xDiff * SystemConstants.TILE;
					yDestination = curY;
					if(xDestination <= (5*SystemConstants.TILE + Math.round(w1/2) + RESTRICTION_TOLERANCE)
							&& xDestination >= (5 * SystemConstants.TILE - Math.round(w1/2) - RESTRICTION_TOLERANCE)
							&& yDestination <= 9 * SystemConstants.TILE + 1*SystemConstants.TILE + RESTRICTION_TOLERANCE
							&& yDestination >= 9 * SystemConstants.TILE - (w2 - 1*SystemConstants.TILE) -RESTRICTION_TOLERANCE){
						RConsole.println("Destination x in defender zone (going right)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						cantX = true;
						break;
					}
					else{
						RConsole.println("No problem (going right)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						turnTo(Odometer.convertToDeg(Math.atan2(yDestination - coords.getY(), xDestination - coords.getX())));
						RConsole.println("usL.getDistance() = " + usL.getDistance() + "\nusR.getDistance()" + usR.getDistance());
						if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
							|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
							cantX = true;
							break;
						}
						doneY = false;
						obstacle = !travelTo(xDestination, curY);
					}
				}
				else{
					xDestination = x + xDiff * SystemConstants.TILE;
					yDestination = curY;
					if(xDestination <= (5*SystemConstants.TILE + Math.round(w1/2) + RESTRICTION_TOLERANCE)
							&& xDestination >= (5 * SystemConstants.TILE - Math.round(w1/2) - RESTRICTION_TOLERANCE)
							&& yDestination <= 9 * SystemConstants.TILE + 1*SystemConstants.TILE + RESTRICTION_TOLERANCE
							&& yDestination >= 9* SystemConstants.TILE - (w2 - 1*SystemConstants.TILE) -RESTRICTION_TOLERANCE){
						RConsole.println("Destination x in defender zone (going left)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						cantX = true;
						break;
					}
					else{
						RConsole.println("No problem (going left)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						turnTo(Odometer.convertToDeg(Math.atan2(yDestination - coords.getY(), xDestination - coords.getX())));
						RConsole.println("usL.getDistance() = " + usL.getDistance() + "\nusR.getDistance()" + usR.getDistance());
						if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
								|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
							cantX = true;
							break;
						}
						doneY = false;
						obstacle = !travelTo(xDestination, curY);
					}
					
				}
				//RConsole.println("Traveled one tile horizontally");

				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();
				
			} while (true/*Math.abs(curX - x) > DISTANCE_TOLERANCE && !obstacle*/);
			
			/*
			 * If an obstacle is detected, call avoidObstacle
			 */
			if(obstacle){
				avoidObstacle(x, y);
			}

			do {
				obstacle = false;
				
				/*
				 * Added absolute value
				 * @author Ryan
				 */				
				
				yDiff = (int) Math.round(Math.abs(y - curY) / SystemConstants.TILE) - 1;
				if(yDiff==-1 || doneY) {
					doneY = true;
					break;
				}
				
				//RConsole.println("ydiff" + yDiff);

				/**
				 * Added conditions
				 * @author Ryan
				 */
				if(y > curY){
					yDestination = y - yDiff * SystemConstants.TILE;
					xDestination = curX;
					if(xDestination <= (5*SystemConstants.TILE + Math.round(w1/2) + RESTRICTION_TOLERANCE)
							&& xDestination >= (5 * SystemConstants.TILE - Math.round(w1/2) - RESTRICTION_TOLERANCE)
							&& yDestination <= 9 * SystemConstants.TILE + 1*SystemConstants.TILE + RESTRICTION_TOLERANCE
							&& yDestination >= 9* SystemConstants.TILE - (w2 - 1*SystemConstants.TILE) - RESTRICTION_TOLERANCE){
						RConsole.println("Destination y in defender zone (going right)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						cantY = true;
						break;
					}
					else{
						RConsole.println("No problem (going right)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						turnTo(Odometer.convertToDeg(Math.atan2(yDestination - coords.getY(), xDestination - coords.getX())));
						RConsole.println("usL.getDistance() = " + usL.getDistance() + "\nusR.getDistance()" + usR.getDistance());
						if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
								|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
							cantY = true;
							break;
						}
						doneX = false;
						obstacle = !travelTo(curX, yDestination);
					}
				}
				else{
					yDestination = y + yDiff * SystemConstants.TILE;
					xDestination = curX;
					if(xDestination <= (5*SystemConstants.TILE + Math.round(w1/2) + RESTRICTION_TOLERANCE)
							&& xDestination >= (5 * SystemConstants.TILE - Math.round(w1/2) - RESTRICTION_TOLERANCE)
							&& yDestination <= 9 * SystemConstants.TILE + 1*SystemConstants.TILE + RESTRICTION_TOLERANCE
							&& yDestination >= 9* SystemConstants.TILE - (w2 - 1*SystemConstants.TILE) -RESTRICTION_TOLERANCE){
						RConsole.println("Destination x in defender zone (going left)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						cantY = true;
						break;
					}
					else{
						RConsole.println("No problem (going left)\nxDestination: " + xDestination + "\nyDestination: " + yDestination);
						turnTo(Odometer.convertToDeg(Math.atan2(yDestination - coords.getY(), xDestination - coords.getX())));
						RConsole.println("usL.getDistance() = " + usL.getDistance() + "\nusR.getDistance()" + usR.getDistance());
						if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
								|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
							cantY = true;
							break;
						}
						doneX = false;
						obstacle = !travelTo(curX, yDestination);
					}
				}
				
				// update position
				coords = odo.getCoordinates();
				curX = coords.getX();
				curY = coords.getY();

			} while (true/*Math.abs(curY - y) > DISTANCE_TOLERANCE && !obstacle*/);
			
			if(obstacle){
				avoidObstacle(x, y);
			}

			//localizer.MidLocalization(odo.getCoordinates().getX(),odo.getCoordinates().getY(), odo.getCoordinates().getTheta());
			
			// if(obstacle ) call obstacle avoidance
			// else if destination reached break
			// else, i.e, obstacle in destination: return -1;
			robot.stop();
			
			// If X is stop by a constraint and Y is happy, then change Y
			if (cantX && doneY)
			{
				RConsole.println("Move in Y");
				if(specialY > curY){
					turnTo(Odometer.convertToDeg(Math.atan2(curY + 1*SystemConstants.TILE - coords.getY(), curX - coords.getX())));
					RConsole.println("specialY > curY");
					// replace this with obstacle checking
					if(usL.getDistance() < OBSTACLE_THRESHOLD || usR.getDistance() < OBSTACLE_THRESHOLD){
						//cantY = true;
						if (specialY == -1*SystemConstants.TILE) {
							specialY = 11*SystemConstants.TILE;
						}
						else {
							specialY = -1*SystemConstants.TILE;
						}
					}
					else{
						cantX = false;
						obstacle = !travelTo(curX, curY + 1*SystemConstants.TILE);
					}
				}
				else{
					turnTo(Odometer.convertToDeg(Math.atan2(curY - 1*SystemConstants.TILE - coords.getY(), curX - coords.getX())));
					RConsole.println("specialY < curY");
					// replace this with obstacle checking
					if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
							|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
						//cantY = true;
						if (specialY == -1*SystemConstants.TILE) {
							specialY = 11*SystemConstants.TILE;
						}
						else {
							specialY = -1*SystemConstants.TILE;
						}
					}
					else{
						cantX = false;
						RConsole.println("Moving in Y");
						obstacle = !travelTo(curX, curY - 1*SystemConstants.TILE);
					}
					
				}
			}
			else if (doneX && cantY) {

				if(specialX > curX){
					turnTo(Odometer.convertToDeg(Math.atan2(curY - coords.getY(), curX + 1*SystemConstants.TILE - coords.getX())));
					// replace this with obstacle checking
					if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
							|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
						//cantX = true;
						if (specialX == -1*SystemConstants.TILE) {
							specialX = 11*SystemConstants.TILE;
						}
						else {
							specialX = -1*SystemConstants.TILE;
						}
					}
					else{
						cantY = false;
						obstacle = !travelTo(curX + 1*SystemConstants.TILE, curY);
					}
				}
				else{
					turnTo(Odometer.convertToDeg(Math.atan2(curY - coords.getY(), curX - 1*SystemConstants.TILE - coords.getX())));
					// replace this with obstacle checking
					if ((usL.getDistance() < OBSTACLE_THRESHOLD && usL.getDistance() != 4 )
							|| (usR.getDistance() < OBSTACLE_THRESHOLD && usR.getDistance() != 4)) {
						//cantX = true;
						if (specialX == -1*SystemConstants.TILE) {
							specialX = 11*SystemConstants.TILE;
						}
						else {
							specialX = -1*SystemConstants.TILE;
						}
					}
					else{
						cantY = false;
						obstacle = !travelTo(curX - 1*SystemConstants.TILE, curY);
					}
					
				}
			
			}
			else if (cantX && cantY) {
				// Not yet determined but should be extrmely rare
			}
			else {
				cantX = false;
				cantY = false;
			}

			
			
			/*if(Math.abs(curX - x) < DISTANCE_TOLERANCE && Math.abs(curY - y) < DISTANCE_TOLERANCE){
				destinationReached = true;
			}*/
			if (doneX && doneY) {
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
		 * From specs: {1,2,3,4} corresponds to the cardinal directions N, E, S, W.
		 * 
		 * Convert orientation to our convention 0:E, 1:N, 2:W, 3:S
		 */

		double startX = x, startY = y;
		int turnDirection;
		RConsole.println("Dispenser x: " + x);
		RConsole.println("Dispenser y: " + y);

		// North
		if (orientation == 1) {
			startY = y + 2 * SystemConstants.TILE;
			turnDirection = 1;
		}
		// East
		else if (orientation == 2) {
			startX = x + 2 * SystemConstants.TILE;
			turnDirection = 0;
		}
		// South
		else if (orientation == 3) {
			startY = y - 1 * SystemConstants.TILE;
			turnDirection = 3;
		}
		// West
		else {
			startX = x - 1 * SystemConstants.TILE;
			turnDirection = 2;
		}

		// Move to one node away from the dispenser
		RConsole.println("GoTo: " + startX + ", " + startY);
		GoTo(startX, startY);
		
		localizer.MidLocalization(startX,  startY, odo.getCoordinates().getTheta());
		
		//East
		if(orientation == 2){
			turnTo(90);
		}
		//North
		else if(orientation == 1){
			turnTo(0);
		}
		//West
		else if(orientation == 4){
			turnTo(90);
		}
		//South
		else{
			turnTo(0);
		}
		
		// Align the back of the robot with the button
		if(orientation == 2 || orientation == 3){
			robot.goForward(7,5);
			RConsole.println("Distance to move: 7");
		}
		else if(orientation == 4 || orientation == 1){
			robot.goForward(23, 5);
			RConsole.println("Distance to move: 23:");
		}
		
		
		turnTo(turnDirection * 90);

		robot.goForward(-32, 5);
		for (int i = 0; i < 2; i++) {
			robot.goForward(5, 5);
			robot.goForward(-5, 5);
		}
		
		try{
			Thread.sleep(500);
		} catch(Exception e){}
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
		leftFree = (us.getDistance() > OBSTACLE_THRESHOLD);
		if (leftFree) {
			navCorrect();
		} else {
			// go back to original dir
			turnRight();
			// then turn right
			turnRight();
			rightFree = (us.getDistance() > OBSTACLE_THRESHOLD);
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
