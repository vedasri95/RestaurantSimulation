import java.util.Scanner;


import java.util.Comparator;
import java.util.PriorityQueue;

public class Restaurant {

	int currDiners;
	 boolean[] tables;
	 Machine[] machines;
	 boolean[] cooks;
	 Object lock;
	 PriorityQueue<Diner> diners;
	 private static Scanner scan;
	 int freeTables;
	 int freeCooks;
	static Restaurant r;
	
	public void assignCook(Diner diner) {		
		synchronized(r.cooks){		
			while(r.freeCooks == 0) {
				try {
					r.cooks.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for(int i=0; i<r.cooks.length; i++) {
				if(r.cooks[i] == false) {
					r.cooks[i] = true;	
					diner.cookNum = i;
					r.freeCooks--;
					cooks.notifyAll();
					break;
				}
				}
		}
	}
	
	public void addDiner(Diner diner) {
		synchronized(Restaurant.getRInstance().diners) {
			Restaurant.getRInstance().diners.add(diner);
		}
	}
	
	public Diner pollDiner() {
		synchronized(Restaurant.getRInstance().diners) {
			return Restaurant.getRInstance().diners.poll();
		}
	}
	
	
	public void assignTable(Diner diner) {
		Restaurant r = Restaurant.getRInstance();
		synchronized(r.tables){	
			while( r.diners.peek()!=diner || Restaurant.getRInstance().freeTables == 0) {
				try {
					r.tables.wait();
				}
				catch(InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}				
			if(!r.diners.isEmpty()) {
			for(int i=0; i<r.tables.length; i++) {
				if(r.tables[i] == false) {
					r.tables[i] = true;	
					r.freeTables--;
					pollDiner();
					diner.tableNum = i;	
					diner.tableTime = MyClock.getInstance().getCurrentTime();
					r.tables.notifyAll();
					break;
				}
				
			}
			}
		}
	}
	
	
	public void freeTable(Diner diner) {
		synchronized(Restaurant.getRInstance().tables) {
			Restaurant.getRInstance().tables[diner.tableNum]=false;
			Restaurant.getRInstance().freeTables++;
			Restaurant.getRInstance().tables.notifyAll();
		}
	}
		
	public static Restaurant getRInstance() {
		if(r == null) {
			r =  new Restaurant();
		}
		return r;
	}
	
	public static void main(String[] args) {
		scan = new Scanner(System.in);
		int numDiners = scan.nextInt();
		int numTables = scan.nextInt();
		int numCooks = scan.nextInt();
		Restaurant r = Restaurant.getRInstance();
		r.lock = new Object();
		r.freeTables = numTables;
		r.freeCooks = numCooks;
		r.currDiners = 0;
		r.diners = new PriorityQueue<Diner>(new Comparator<Diner>() {
			@Override
			public int compare(Diner arg0, Diner arg1) {
				// TODO Auto-generated method stub
				return arg0.arrival - arg1.arrival;
			}
			
		});	
		r.tables = new boolean[numTables];
		r.cooks = new boolean[numCooks];
		Restaurant.getRInstance().machines = new Machine[4];
		synchronized(r.machines) {
		r.machines[0] = new Machine("burger", 5, true,1);
		r.machines[1] = new Machine("fries", 3, true,2);
		r.machines[2] = new Machine("coke", 2, true,3);
		r.machines[3] = new Machine("sundae", 1, true,4);
		}
		ClockThread ct = new ClockThread();
		
		for(int i=0; i<numDiners; i++) {
			
			Diner diner = new Diner();
			diner.arrival = scan.nextInt();
			int[] orders = new int[4];
			orders[0] = scan.nextInt();
			orders[1] = scan.nextInt();
			orders[2] = scan.nextInt();
			orders[3] = scan.nextInt();
			diner.tableNum = -1;
			diner .id = i;
			diner.cookNum = -1;
			diner.exit = -1;
			diner.orders = orders;
			r.addDiner(diner);
			synchronized(r.lock) {
				r.currDiners++;
			}
		}
		
		java.util.Iterator<Diner> it = r.diners.iterator();
		while(it.hasNext()) {
			Diner diner = it.next();
			diner.start();	
		}
		ct.start();
		try {
			ct.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("TIME WHEN LAST DINER LEFT: " + MyClock.getInstance().getCurrentTime());
		System.out.println("Restaurant Closed");
	}
}
