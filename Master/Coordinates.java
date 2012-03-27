package Master;

/**
 * This is a Wrapper class for for a polar coordinates system
 * 
 * @author Mouhyi
 * 
 */
public final class Coordinates {
	
	private static Coordinates instance;

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
	 * 
	 * @author Mouhyi
	 */
	public Coordinates(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	
	/**
	 * copy's p's fields into this
	 * @param p
	 */
	public void copy(Coordinates p){
		x=p.x;
		y=p.y;
		theta=p.theta;
	}

	/**
	 * Setter
	 * @param x
	 * @param y
	 * @param theta
	 * @author Mouhyi
	 */
	public void set(double x, double y, double theta){
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	
	/**
	 * Getter & setter for x
	 * @author Mouhyi
	 */
	public double getX(){
	 	return this.x;
	}
	public void setX(double x){
	 	this.x = x;
	}
	
	/**
	 * Getter & setter for y
	 * @author Mouhyi
	 */
	public double getY(){
	 	return this.y;
	}
	public void setY(double y){
	 	this.y = y;
	}
	
	/**
	 * Getter & setter for theta
	 * @author Mouhyi
	 */
	public double getTheta(){
	 	return this.theta;
	}
	public void setTheta(double theta){
	 	this.theta = theta;
	}


}
