/**
 * This is a Wrapper class for for a polar coordinates system
 * 
 * @author Mouhyi
 * 
 */
public class Coordinates {

	private double x;
	private double y;
	private double theta;

	/**
	 * Constructor: Initialize all the instance variables to zero
	 * 
	 * @author Mouhyi
	 */
	public Coordinates() {
		x = 0;
		y = 0;
		theta = 0;
	}

	/**
	 * Constructor: Initialize all the instance variables to the parameters
	 * values
	 * 
	 * @author Mouhyi
	 */
	public Coordinates(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}

	/**
	 * This method copies the parameter variables to this instance variables
	 * 
	 * @author Mouhyi
	 */
	public void copy(Coordinates p) {
		x = p.x;
		y = p.y;
		theta = p.theta;
	}
}
