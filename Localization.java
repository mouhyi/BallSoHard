/**
* This class is responsible for performing the localization routine
* USlocalization is executed first to orients the robot parallel to the Y-axis
* LSlocalization is then executed to position the robot to (0,0.Pi/2)
*
* @author Mouhyi, Joshua David Alfaro
*/

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Localization {

	// Fields
	private Odometer odo;
	private Robot robot;
	private LightSensor lsL;
	private LightSensor lsR;
	private USPoller uspL;
	private USPoller uspR;

	// Constructor
	public Localization(Odometer odo, Robot robot, LightSensor lsL, LightSensor lsR, USPoller uspL, USPoller uspR) {
		this.odo = odo;
		this.robot = robot;
		this.lsL = lsL;
		this.lsR = lsR;
		this.uspL = uspL;
		this.uspR = uspR;
	}
	
	
	/**
	 * main Localization routine
	 *
	 * @author Mouhyi, JDAlfaro
	 */
	public void doLocalization() {
		this.doUSLocalization();
		robot.rotateAxis(45);
		odo.setCoordinates(0, 0, 0);
		this.doLightLocalization();
	}

	/**
	 * US Localization routine: falling edge
	 *
	 * @author Mouhyi, JDAlfaro
	 */
	private  void doUSLocalization()	// Falling and Rising Edge Method
	{
		robot.rotate(-SystemConstants.ROTATION_SPEED);	//rotate clockwise
		
		// Falling and Rising Edge Method
		double angleA = 0, angleB = 0;
		
		int Wall = 0;
		int noWall = 0;
		boolean first = true;
		boolean second = false;
		while (first)	// Checks for rising edge
		{
			uspR.run();

			// Reads no wall
			if (uspR.getDistance() < 30)
			{
				Wall = Wall + 1;
			}

			// Reads wall after reading no wall (rising edge)
			if (Wall >  3 && uspR.getDistance() > 50)
			{
				Sound.beep();
				first = false;
				second = true;
				noWall = 0;
				angleA = odo.getCoordinates().getTheta(); // latch angle
			}
		}

		while (second)	// Checks for falling edge
		{	
			uspR.run();
			// Reads no wall
			if (uspR.getDistance() < 50)
			{
				noWall = noWall + 1;
			}

			// Reads wall after reading no wall (falling edge)
			if (noWall > 3 && uspR.getDistance() < 30)
			{
				Sound.beep();
				second = false;
				noWall = 0;
				angleB = odo.getCoordinates().getTheta(); // latch angle
			}
			
			
		}
		robot.stop();
		
		// Computation of new angle
		double theta = Odometer.adjustAngle(90 - Odometer.adjustAngle(angleA - angleB)/2);
		LCD.drawString("anlgeA "+ angleA +" ", 0, 3);
		LCD.drawString("angleB "+ angleB +" ", 0, 4);
		LCD.drawString("newtheta "+ theta +" ", 0, 5);
		
		odo.setCoordinates(0, 0, theta, new boolean[] { false, false, true });
	}

	/**
	 * Light Localization routine
	 *
	 * @author Mouhyi, JDAlfaro
	 */
	private void doLightLocalization()
	{
		// Timer for light sensor depending on speed of robot.
		robot.rotate(SystemConstants.ROTATION_SPEED);
		LightTimer leftLight = new LightTimer(lsL, robot.getSpeed());
		LightTimer rightLight = new LightTimer(lsR, robot.getSpeed());

		boolean leftDone = true;
		int gridLineCountL = 0;
		boolean linePassedL = false;
		double[] angleL = {0, 0, 0, 0};
		
		boolean rightDone = true;
		int gridLineCountR = 0;
		boolean linePassedR = false;
		double[] angleR = {0, 0, 0, 0};
		
		double xOffset, yOffset, thetaOffset;
		
		while(leftDone || rightDone)
		{
			robot.rotate(SystemConstants.ROTATION_SPEED);
			
			if(leftLight.lineDetected())
			{
				gridLineCountL = gridLineCountL + 1;
				linePassedL = true;
				Sound.setVolume(20);
				Sound.beep();
			}
				
			if(rightLight.lineDetected())
			{
				gridLineCountR = gridLineCountR + 1;
				linePassedR = true;
				Sound.setVolume(20);
				Sound.beep();
			}
			
			// latch angles into memory only after a line pass
			if(linePassedL)
			{
				switch(gridLineCountL)	
				{	
				case 1:
					angleL[0] = odo.getCoordinates().getTheta();
					RConsole.println("angleL[0]  " + angleL[0]);
					break;
				case 2:
					angleL[1] = odo.getCoordinates().getTheta();
					RConsole.println("angleL[1]  " + angleL[1]);
					break;
				case 3:
					angleL[2] = odo.getCoordinates().getTheta();
					RConsole.println("angleL[2]  " + angleL[2]);
					break;
				case 4:
					angleL[3] = odo.getCoordinates().getTheta();
					RConsole.println("angleL[3]  " + angleL[3]);
					leftDone = false;
					break;
				default:
					break;
				}
				
				linePassedL = false;
				leftLight.resetLine();
			}
			
			// latch angles into memory only after a line pass
			if(linePassedR)
			{
				switch(gridLineCountR)	
				{	
				case 1:
					angleR[0] = odo.getCoordinates().getTheta();
					RConsole.println("angleR[0]  " + angleR[0]);
					break;
				case 2:
					angleR[1] = odo.getCoordinates().getTheta();
					RConsole.println("angleR[1]  " + angleR[1]);
					break;
				case 3:
					angleR[2] = odo.getCoordinates().getTheta();
					RConsole.println("angleR[2]  " + angleR[2]);
					break;
				case 4:
					angleR[3] = odo.getCoordinates().getTheta();
					RConsole.println("angleR[3]  " + angleR[3]);
					rightDone = false;
					break;
				default:
					break;
				}
				
				linePassedR = false;
				rightLight.resetLine();
			}			
		}
		robot.stop();	// stop doing moving and leave loop to perform calculations
		
		// Stop timers for light timer detection since it is no longer needed.
		leftLight.stop();
		rightLight.stop();
		
		
		// do trigonometry to compute with reference to real (0,0) and 0 degrees
		xOffset = ((-1)*SystemConstants.LS_TOCENTRE*Math.cos(Math.toRadians((angleL[2] - angleL[0])/2))
				+ (-1)*SystemConstants.LS_TOCENTRE*Math.cos(Math.toRadians((angleR[2] - angleR[0])/2)))/2;
		yOffset = ((-1)*SystemConstants.LS_TOCENTRE*Math.cos(Math.toRadians((angleL[3] - angleL[1])/2))
				+ (-1)*SystemConstants.LS_TOCENTRE*Math.cos(Math.toRadians((angleR[3] - angleR[1])/2)))/2;
		
		// needs work, WORSE CASE = set to 30 degrees since it seems to stop around there.
		thetaOffset = (Math.abs(angleL[3] - angleL[0]) - 90 - Math.abs(angleL[2] - angleL[0])/2 
				+ Math.abs(angleR[3] - angleR[0]) - 90 - Math.abs(angleR[2] - angleR[0])/2)/2;
		
		odo.setCoordinates(xOffset, yOffset, thetaOffset, new boolean[] {true, true, true});
	}
	
	/**
	 * Light Localization routine 2
	 *	Drive straight towards line and basically use odometry correction style correction
	 * @author Mouhyi, JDAlfaro
	 */
	private void doLightLocalization2()	// not tested, is another idea, not finished
	{
		double time_left = 0, time_right = 0;
		boolean leftNotSeen = true, rightNotSeen = true;

		robot.advance(5);		// for some reason it needs to be called twice to work
		LightTimer leftLight = new LightTimer(lsL, robot.getSpeed());
		LightTimer rightLight = new LightTimer(lsR, robot.getSpeed());
		
		while(leftNotSeen || rightNotSeen)
		{
			if(leftLight.lineDetected())
			{
				time_left = System.currentTimeMillis();
				leftNotSeen = false;
				Sound.setVolume(20);
				Sound.beep();	
			}
				
			if(rightLight.lineDetected())
			{
				time_right = System.currentTimeMillis();
				rightNotSeen = false;
				Sound.setVolume(20);
				Sound.beep();
			}
		}
		
		double time_difference = Math.abs(time_right - time_left);
		
		double theta = Math.atan(time_difference*SystemConstants.FORWARD_SPEED/SystemConstants.LS_WIDTH);
		
		if(time_right > time_left)
		{
			robot.rotateAxis(90 + theta);
		}
		else
		{
			robot.rotateAxis(90 - theta);
		}
		
		odo.setCoordinates(SystemConstants.LS_WIDTH*Math.tan(theta)/2, 0.0, 0, new boolean[] {true, false, true });
	}
}

