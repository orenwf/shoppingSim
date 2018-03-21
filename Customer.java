import java.util.Random;
import java.util.Vector;

public class Customer
    extends Thread {

    Item my_item;
    int ticket_num;
    Vector<Storage_clerk> vcs = new Vector<>();

    public Customer(String s) {
	super(s);
    }

    void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-BALA.time)+"] Customer "+getName()+": "+m);
    }

    public void run() {
	try {
	    browse();
	    get_slip();
	    pay();
	    leave_BALA();
	} catch (Exception e) { msg("was abnormally interrupted."); }
    }

    void browse() throws InterruptedException {
	msg("is browsing for items in BALA.");
	Thread.sleep(2000);
	my_item = new Item();
	msg("chooses a "+my_item.weight()+" item.");
    }

    void get_slip() throws InterruptedException {
	BALA.getting_slip_c.add(this);
	msg("waiting on line for floor clerk.");
	while(BALA.getting_slip_c.indexOf(this)>0) {
	    Customer.sleep(500);
	}
	while(BALA.fc_queue.isEmpty()) ;
	BALA.fc_queue.remove(0);
	msg("getting slip from floor clerk.");
    }
    
    synchronized void pay() throws InterruptedException {
	msg("gets on line to pay.");
	setPriority(10);
	msg("pays for the "+my_item.weight+" item.");
	Customer.sleep(new Random().nextInt(10_000));
	setPriority(5);
	if(!my_item.weight().equalsIgnoreCase("light")) take_break();
    }

    void take_break() throws InterruptedException {
	msg("takes a break to eat.");
	Customer.yield();
	Customer.yield();
	Customer.sleep(new Random().nextInt(10_000));
	wait_receive();
    }

    synchronized void get_ticket() {
	ticket_num = BALA.ticket_dispenser++;
	msg("gets ticket number "+ticket_num+" from dispenser.");
    }
	
    void wait_receive() {
	msg("arrives at the storage room to receive their item.");
	get_ticket();
	msg("is in line position "+String.valueOf(ticket_num)+" to receive their item.");
	while(ticket_num != BALA.now_serving) ;
	msg("ticket number has been called.");
	get_helped();
    }

    synchronized void get_helped() {
	if(my_item.weight().equalsIgnoreCase("heavy")) {
	    while(BALA.sc_queue.size()<1) ;
	    Storage_clerk c = BALA.sc_queue.firstElement();
	    vcs.add(c);
	    c.interrupt();
	    while(BALA.sc_queue.contains(c)) ;
	    BALA.now_serving++;
	} else if(my_item.weight().equalsIgnoreCase("very heavy")) {
	    while(BALA.sc_queue.size()<2) ;
	    Storage_clerk c = BALA.sc_queue.firstElement();
	    vcs.add(c);
	    c.interrupt();
	    while(BALA.sc_queue.contains(c)) ;
	    c = BALA.sc_queue.firstElement();
	    vcs.add(c);
	    c.interrupt();
	    while(BALA.sc_queue.contains(c)) ;
	    BALA.now_serving++;
	}
	receive_item();
    }

    void receive_item() {
	if(my_item.weight().equalsIgnoreCase("heavy")) {
	    while(vcs.get(0).busy == true) ;
	} else {
	    while(vcs.get(0).busy == true || vcs.get(1).busy == true) ;
	}
	msg("has received their "+my_item.weight()+" item.");
    }
    
    void leave_BALA() {
	msg("gets on line to leave BALA.");
	if(BALA.leaving_c.isEmpty()) {
	    BALA.leaving_c.add(this);
	    while(BALA.leaving_c.size() < BALA.n_customers) ;
	    msg("shouts \"it's closing time!\" and leaves BALA.");
	    for(Floor_clerk f : BALA.floor_clerks) f.interrupt();
	    for(Storage_clerk s : BALA.storage_clerks) s.interrupt();
	    BALA.finished_Customers.add(this);
	} else if(BALA.leaving_c.lastElement().isAlive()) {
	    BALA.leaving_c.add(this);
	    try {
		BALA.leaving_c.get(BALA.leaving_c.size()-2).join();
	    } catch (InterruptedException e) { msg("was interrupted abnormally."); }
	    int sum = 0;
	    for(Customer c : BALA.customers) if(c.isAlive()) sum++;
	    if(sum == 1) msg("is the last customer to leave BALA.");
	    else msg("leaves BALA.");
	    BALA.finished_Customers.add(this);
	}
    }
}
