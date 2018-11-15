
public class ClockThread extends Thread {
	public void run() {
		Restaurant r = Restaurant.getRInstance();
		while( true) {
			synchronized(r.lock) {
				if(r.currDiners == 0 && MyClock.getInstance().getCurrentTime() >= 120)
					break;
			}
			MyClock.getInstance().incrementTimeUnit();
			synchronized(r.lock) {
				if(r.currDiners == 0) {
					break;
				}
			}
		synchronized (MyClock.getInstance().getClockLock()) {
			MyClock.getInstance().getClockLock().notifyAll();
		}
		}
	}
}
