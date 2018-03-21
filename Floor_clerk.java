public class Floor_clerk
    extends Thread {

    public Floor_clerk(String s){
	super(s);
    }

    void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-BALA.time)+"] Floor clerk "+getName()+": "+m);
    }

    public void run() {
	msg("showed up to work.");
	while(!no_more_customers()) {
	    wait_cust();
	    help_cust();
	}
	try {
	    msg("has no more customers waiting.");
	    if(!BALA.fc_queue.isEmpty()) BALA.fc_queue.remove(0);
	    Floor_clerk.sleep(BALA.LONG_TIME);
	}
	catch (InterruptedException e) { msg("gets ready to leave work."); }
	if(BALA.leaving_clerks.isEmpty()) {
	    BALA.leaving_clerks.add(this);
	    while( BALA.finished_Customers.size() < BALA.customers.size() ||
		   BALA.leaving_clerks.size()
		   < BALA.floor_clerks.size() + BALA.storage_clerks.size()) ;
	    msg("Good night! Last one out - please lock the doors.");
	} else if(BALA.leaving_clerks.lastElement().isAlive()) {
	    BALA.leaving_clerks.add(this);
	    try {
		BALA.leaving_clerks.get(BALA.leaving_clerks.size()-2).join();
	    } catch (InterruptedException e) { msg("was terminated abnormally."); }
	    int sum = 0;
	    for(Thread c : BALA.leaving_clerks) if(c.isAlive()) sum++;
	    if(sum == 1) msg("is the last clerk to leave. The day ends.");
	    else msg("leaves BALA.");
	}
    }

    synchronized boolean no_more_customers() {
	if(BALA.customers.size() == BALA.n_customers
	   && BALA.getting_slip_c.isEmpty()) {
	    return true;
	} else {
	    return false;
	}
    }

    void wait_cust() {
	if(no_more_customers()) { return; }
	msg("is waiting for browsing customers to help.");
	BALA.fc_queue.add(this);
	while(BALA.fc_queue.contains(this));
    }

    synchronized void help_cust() {
	if(no_more_customers()) { return; }
	Customer c = BALA.getting_slip_c.firstElement();
	msg("now helping customer: "+c.getName()+".");
	BALA.getting_slip_c.remove(0);
    }
    
}
