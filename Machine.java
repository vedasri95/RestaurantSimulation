
public class Machine extends Thread{
	boolean isFree;
	String name;
	int id;
	int duration;
	int currTime;
	int quantity;
	int latestAvail;
	public Machine(String name, int duration, boolean isFree, int id) {
		this.name = name;
		this.id = id;
		this.duration = duration;
		this.isFree = isFree;
		this.latestAvail = 0;
	}
	
	protected void prepareFood() {
		while (MyClock.getInstance().getCurrentTime() < currTime + (quantity*duration)) {
			try {
				synchronized (MyClock.getInstance().getClockLock()) {
					MyClock.getInstance().getClockLock().wait();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void run() {
		Restaurant ins = Restaurant.getRInstance();
		synchronized(ins.machines) {
			ins.machines[this.id - 1].latestAvail = MyClock.getInstance().getCurrentTime()+this.duration;
		}
		prepareFood();	
		synchronized(ins.machines) {
			ins.machines[this.id-1].isFree = true;
			ins.machines.notifyAll();
		}
	}
}
