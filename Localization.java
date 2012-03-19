/**
* This class is responsible for performing the localization routine
* USlocalization is executed first to orients the robot parallel to the Y-axis
* LSlocalization is then executed to position the robot to (0,0.Pi/2)
*
* @author Mouhyi, Joshua David Alfaro
*/

import lejos.nxt.*;

public class Localization {

	// Fields
	private Coordinates coords;
	private Robot robot;
	private LightTimer lsL;
	private LightTimer lsR;
	private USPoller uspL;
	private USPoller uspR;
	private Navigation navigation;

	// Constructor
	public Localization(Coordinates coords, Robot robot, LightTimer lsL, LightTimer lsR, USPoller uspL, USPoller uspR, Navigation navigation) {
		this.coords = coords;
		this.robot = robot;
		this.lsL = lsL;
		this.lsR = lsR;
		this.uspL = uspL;
		this.uspR = uspR;
		this.navigation = navigation;
	}
	
	
	/**
	 * main Localization routine
	 *
	 * @author Mouhyi
	 */
	public void doLocalization() {
		this.doUSLocalization();
	}

	/**
	 * US Localization routine: falling edge
	 *
	 * @author Mouhyi, Joshua David Alfaro
	 */
	private  void doUSLocalization() {
		// Falling Edge Method
		double angleA = 0, angleB = 0;
		
		int noWall = 0;
		boolean first_rot = true;
		boolean second_rot = false;
		while (first_rot)
		{
			robot.rotate(SystemConstants.ROTATION_SPEED);	//rotate counterclockwise

			// Reads no wall
			if (this.uspL.filter() > 50)
			{
				noWall = noWall + 1;
			}

			// Reads wall after reading no wall (falling edge)
			if (noWall >  3 && this.uspL.filter() < 30)
			{
				first_rot = false;
				second_rot = true;
				noWall = 0;
				angleA = coords.getTheta(); // latch angle
			}
		}

		while (second_rot)
		{
			robot.rotate(-SystemConstants.ROTATION_SPEED);		// rotate clockwise
			
			// Reads no wall
			if (this.uspR.filter() > 50)
			{
				noWall = noWall + 1;
			}

			// Reads wall after reading no wall (falling edge)
			if (noWall > 3 && this.uspR.filter() < 30)
			{
				second_rot = false;
				noWall = 0;
				angleB = coords.getTheta(); // latch angle
			}
			
			
		}
		robot.stop();
		double theta = 45 + Odometer.adjustAngle(angleA - angleB)/2;
		
		coords.set(0,0,theta);
	}
	
	/**
	 * Preparation for light localization
	 * 
	 * @author Ryan
	 */
	
	private void positionRobot(){
		
		//Moves the robot to face wall to the left
		navigation.turnTo(90, true);
		
		//Reverse until the robot is near the gridline
		while(uspL.filter() < 25){
			robot.advance(-SystemConstants.FORWARD_SPEED);
		}
		
		//Moves the robot to face the bottom wall
		navigation.turnTo(180, true);
		
		while(uspL.filter() < 25){
			robot.advance(-SystemConstants.FORWARD_SPEED);
		}
		
	}

	/**
	 * Light Localization routine
	 *
	 * @author Mouhyi, Ryan
	 */
	private void doLightLocalization() {
		
		double[] yIntercepts = new double[2];
		double[] xIntercepts = new double[2];
		double thetaY;
		double thetaX;
		double changeInTheta;
		double x, y;
		
		// start rotating and clock all 4 gridlines
		robot.setSpeeds(0, -40);
				
		//While no line is detected, wait
		lsL.resetLine();
						
		while (!lsL.lineDetected()) {}
			
		Sound.beep();
		//Record angle at which line is detected
		yIntercepts[0] = coords.getTheta();
		
		//Wait before resetting line to prevent multiple lines from being detected consecutively
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
					
		lsL.resetLine();

		//repeat the above 3 more times to read the other lines
		while (!lsL.lineDetected()) {}
		Sound.beep();
		xIntercepts[0] = coords.getTheta();
				
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
					
		lsL.resetLine();
		
		//Detect third line	
		while (!lsL.lineDetected()) {}
		
		Sound.beep();
		yIntercepts[1] = coords.getTheta();
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
					
		lsL.resetLine();

		//Detect fourth line
		while (!lsL.lineDetected()) {}
		Sound.beep();
		xIntercepts[1] = coords.getTheta();
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
					
		lsL.resetLine();
				
		// Calculate point (0,0) and 0 degrees
		thetaY = yIntercepts[1]-yIntercepts[0]-SystemConstants.LS_ANGLE_OFFSET;
		thetaX = xIntercepts[1]-xIntercepts[0]-SystemConstants.LS_ANGLE_OFFSET;
		x = -SystemConstants.LS_MIDDLE*Math.cos(Math.toRadians(thetaY/2));
		y = -SystemConstants.LS_MIDDLE*Math.cos(Math.toRadians(thetaX/2));
		
		//compute the correction for angle (delta theta)
		changeInTheta = ((90-(yIntercepts[0]-SystemConstants.LS_ANGLE_OFFSET-180-thetaY/2))+(90-(xIntercepts[1]-180-thetaX/2)))/2;
	
		coords.set(x, y, changeInTheta);
	}
}

