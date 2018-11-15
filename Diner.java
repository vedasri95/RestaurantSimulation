public class Diner extends Thread implements Comparable<Diner>{

	int arrival;
	int exit;
	int[] orders;
	int tableNum;
	int tableTime;
	int cookNum;
	int id;
	int burger_st, burger_et;
	int fries_st, fries_et;
	int coke_st, coke_et;
	int sundae_st, sundae_et;
	int foodbrought;
	
	
	public Diner() {	
		
		
	}
	
	public void eatFood() {
		int currTime = MyClock.getInstance().getCurrentTime();

		while (MyClock.getInstance().getCurrentTime() < (currTime + 30)) {
			synchronized (MyClock.getInstance().getClockLock()) {
				try {
					MyClock.getInstance().getClockLock().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void run() {
		Restaurant r = Restaurant.getRInstance();
		
		while(this.arrival > MyClock.getInstance().getCurrentTime() ) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		if(tableNum == -1) 
			r.assignTable(this);


		if(cookNum == -1)
			r.assignCook(this);
			Cook c  = new Cook();
			c.orders = this.orders;
			c.start();
			try {
				c.join();
				synchronized(r.cooks) {
					r.cooks[cookNum] = false;
					r.freeCooks++;
					r.cooks.notifyAll();
					}
				this.burger_st = c.burger_st;
				this.burger_et = c.burger_et;
				this.fries_st = c.fries_st;
				this.fries_et = c.fries_et;
				this.coke_st = c.coke_st;
				this.coke_et = c.coke_et;
				this.sundae_st = c.sundae_st;
				this.sundae_et = c.sundae_et;
				this.foodbrought = c.finishedOrder;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		StringBuffer buff = new StringBuffer();
		int seating = this.tableTime;
		
		int burg = burger_st ;
		String s1 = " COKE: ";
		
		if(this.orders[2] != 0) {
			int coke = coke_st ;	
			s1 = s1 + coke;
		}
		else {
			s1= s1+ "N/A  ";
		}
		String s2 = " SUNDAE: ";
		if(this.orders[3] != 0) {
			int sun = sundae_st ;	
			s2 = s2 + sun;
			
		}
		else {
			s2 = s2+ "N/A ";
		}
		String s3 = " FRIES: ";
		if(this.orders[1] != 0) {
			
			s3 = s3 + fries_st;
		}
		else
			s3 = s3+ "N/A ";
		int id = this.id+1;
		int table = this.tableNum+1;
		int cookNum = this.cookNum+1;
		buff.append("DINER ID: " + id + "\t" + " SEATING: " + seating + "\t" + " TABLENUM: " + table + "\t");
		buff.append(" COOKNUM: "+ cookNum + "\t");
		buff.append(" BURGER: " + burg + "\t");
		buff.append(s3 + "\t");
		buff.append(s1 + "\t");
		buff.append(s2 + "\t");
		buff.append(" FOOD SERVED: " + this.foodbrought + "\t");
		System.out.println(buff.toString());
		eatFood();		
		r.freeTable(this);	
		synchronized(r.lock) {
			r.currDiners--;
		}
	}

	@Override
	public int compareTo(Diner arg0) {
		// TODO Auto-generated method stub
		return this.arrival - arg0.arrival;
	}
}
