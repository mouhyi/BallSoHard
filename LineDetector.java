import lejos.nxt.LightSensor;

/**
 * This is class uses light sensors to detect line crossing and notifies
 * OdoCorretion accordignly
 * 
 * @author Mouhyi
 */

public class LineDetector implements Runnable {

	static final int THRESHOLD = 450;
	private boolean running;
	int last_reading;
	private boolean left;

	/**
	 * This method implemets run method of the Runnable interface.
	 * 
	 * @author Mouhyi
	 */
	public void run() {
	}

	/**
	 * Notify OdoCorrection that a line has been crossed: semaphore
	 * 
	 * Mouhyi
	 */
	public void notifyListner() {
	}
	
	public boolean isLeft(){
		return left;
	}

}
