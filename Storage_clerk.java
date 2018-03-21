import java.util.Random;

public class Storage_clerk
    extends Thread {

    volatile boolean busy;

    public Storage_clerk(String s) {
	super(s);
    }

    void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-BALA.time)+"] Storage clerk "+getName()+": "+m);
    }

    public void run() {
	msg("showed up to work.");
	while(!no_more_customers()) {
	    wait_cust();
	    help_cust();
	}
	msg("gets ready to leave work.");
	if(BALA.leaving_clerks.isEmpty()) {
	    BALA.leaving_clerks.add(this);
	    while( BALA.finished_Customers.size() < BALA.customers.size() ||
		   BALA.leaving_clerks.size()
		   < BALA.floor_clerks.size()+BALA.storage_clerks.size());
	    msg("Good night! Last one out - please lock the doors.") ;
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
	return (BALA.leaving_c.size() == BALA.n_customers) ;
    }

    void wait_cust() {
	if(no_more_customers()) { return; }
	msg("now waiting on ticket number "+BALA.now_serving+".");
	BALA.sc_queue.add(this);
	while(BALA.sc_queue.contains(this)) try {
		Storage_clerk.sleep(BALA.LONG_TIME);
	    } catch (InterruptedException e) {
		if(no_more_customers()) {
		    msg("has no more customers waiting.");
		    return;
		}
		msg("now serving customer with ticket number "+BALA.now_serving+".");
		busy = true;
		BALA.sc_queue.remove(this);
	    }
    }

    void help_cust() {
	if(no_more_customers()) { return; }
	msg("goes into the back to get the item.");
	try { Storage_clerk.sleep(new Random().nextInt(10_000)); }
	catch (InterruptedException e) { msg("was interrupted abnormally."); }
	msg("comes back with the item.");
	busy = false;
    }
   
}
