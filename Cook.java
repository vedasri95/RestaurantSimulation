
import java.util.PriorityQueue;

public class Cook extends Thread{

	boolean isBusy;
	int[] orders;
	int burger_st, burger_et;
	int fries_st, fries_et;
	int coke_st, coke_et;
	int sundae_st, sundae_et;
	int burgers,fries,coke,sundae;
	int finishedOrder;
	
	public void prepareBurgers() {
		
		Machine m = new Machine("burger",5,false,1);
		m.quantity = burgers;
		m.currTime = MyClock.getInstance().getCurrentTime();
		this.burger_st = MyClock.getInstance().getCurrentTime();
		
		m.start();
		try {
			m.join();
			this.burger_et = MyClock.getInstance().getCurrentTime();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	public void prepareFries() {
		Machine m = new Machine("fries",3,false,2);
		m.quantity = fries;
		m.currTime = MyClock.getInstance().getCurrentTime();
		this.fries_st = MyClock.getInstance().getCurrentTime();	
		m.start();
		try {
			m.join();
			this.fries_et = MyClock.getInstance().getCurrentTime();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void prepareCoke() {
		Machine m = new Machine("coke", 2, false,3);
		m.quantity = 1;
		m.currTime = MyClock.getInstance().getCurrentTime();
		this.coke_st = MyClock.getInstance().getCurrentTime();		
		m.start();
		try {
			m.join();
			this.coke_et = MyClock.getInstance().getCurrentTime();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void prepareSundae() {
		Machine m  = new Machine("sundae", 1, false,4);
		m.quantity = 1;
		m.currTime = MyClock.getInstance().getCurrentTime();
		this.sundae_st = MyClock.getInstance().getCurrentTime();	
		m.start();
		try {
			m.join();
			this.sundae_et = MyClock.getInstance().getCurrentTime();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		burgers = orders[0];
		fries = orders[1];
		coke = orders[2];
		sundae = orders[3];
		//System.out.println("veda" + MyClock.getInstance().getCurrentTime());
		Restaurant r = Restaurant.getRInstance();		
		PriorityQueue<Machine> sorted = new PriorityQueue<Machine>((x,y) -> x.latestAvail - y.latestAvail);	
		synchronized(r.machines) {
		sorted.add(r.machines[0]);
        if(fries != 0) 
        	sorted.add(r.machines[1]);
        if(coke != 0) 
        	sorted.add(r.machines[2]);
        if(sundae != 0) 
        	sorted.add(r.machines[3]);
		}
		//System.out.println("ko "+MyClock.getInstance().getCurrentTime());
        while(!sorted.isEmpty()) {
        	Machine machine = sorted.poll();
        	if(machine.name.equals("burger")) { 		
        		synchronized(r.machines) {
        			while(r.machines[0].isFree == false) {
        				try {	
        					r.machines.wait();
        				} catch (InterruptedException e) {
        					e.printStackTrace();
        				}
        			}
        			r.machines[0].isFree = false;
        			r.machines.notifyAll();
        		}	        		
        		prepareBurgers();
        	}        	
        	else if(machine.name.equals("fries")) {
        		synchronized(r.machines) {
        			while(r.machines[1].isFree == false) {
        				try {
        					r.machines.wait();
        				} catch (InterruptedException e) {
        					e.printStackTrace();
        				}
        			}
        			r.machines[1].isFree = false;
        			r.machines.notifyAll();
        		}		
        		prepareFries();
        	}     	
        	else if(machine.name.equals("coke")) {
        		synchronized(r.machines) {
        			while(r.machines[2].isFree == false) {
        				try {
        					r.machines.wait();
        				} catch (InterruptedException e) {
        					e.printStackTrace();
        				}
        			}
        			r.machines[2].isFree = false;
        			r.machines.notifyAll();
        		}		
        		prepareCoke();
        	}
        	
        	else {
        		synchronized(r.machines) {
        			while(r.machines[3].isFree == false) {
        				try {
        					r.machines.wait();
        				} catch (InterruptedException e) {
        					e.printStackTrace();
        				}
        			}
        			r.machines[3].isFree = false;
        			r.machines.notifyAll();
        		}		
        		prepareSundae();
        	}     	
		}	
		this.finishedOrder = MyClock.getInstance().getCurrentTime();
		}
}
