
public class MyClock {

	private static MyClock clockInstance;
	/**
	 * The counter for current system clock.
	 */
	private int currentTime = 0;
	/**
	 * This object will be used for locks on cooking an item, eating by the
	 * diner. i.e. wherever we need to simulate clock.sleep()
	 */
	private Object clockLock; // Synchronization primitive for SystemClock

	private MyClock() {
		clockLock = new Object();
		currentTime = 0;
	}

	public static synchronized MyClock getInstance() {
		if (clockInstance == null) {
			clockInstance = new MyClock();
		}

		return clockInstance;
	}

	public void incrementTimeUnit() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		currentTime += 1;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public Object getClockLock() {
		return clockLock;
	}
	
}
