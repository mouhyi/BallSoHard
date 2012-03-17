/**
 * This is a Wrapper class for for a polar coordinates system
 * It is designed according to a Singleton Design Pattern to ensure that only
 * on instance of this class class is created and provide a global access to it.
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
	private Coordinates() {
		x = 0;
		y = 0;
		theta = 0;
	}
	
	/**
	 * 
	 * @return an instance of this class
	 * @author Mouhyi
	 */
	public static Coordinates getInstance(){
		if(instance == null ){
			return new Coordinates();
		}
		return instance;
	}

	/**
	 * Setter
	 * @param x
	 * @param y
	 * @param theta
	 * @author Mouhyi
	 */
	public synchronized void set(double x, double y, double theta){
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	
	/**
	 * Getter
	 * @author Mouhyi
	 */
	public synchronized double getX(){
	 	return this.x;
	}
	
	/**
	 * Getter
	 * @author Mouhyi
	 */
	public synchronized double getY(){
	 	return this.y;
	}
	
	/**
	 * Getter
	 * @author Mouhyi
	 */
	public synchronized double getTheta(){
	 	return this.theta;
	}


}
