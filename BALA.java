import java.util.Vector;

public class BALA {

    static int n_customers;
    static int n_floor_clerks;
    static int n_storage_clerks;

    static Vector<Customer> customers = new Vector<>();
    static Vector<Floor_clerk> floor_clerks = new Vector<>();
    static Vector<Storage_clerk> storage_clerks = new Vector<>();

    static Vector<Customer> getting_slip_c = new Vector<>(); // Customers waiting on the Floor
    static Vector<Customer> leaving_c = new Vector<>(); // Customers waiting to leave
    static Vector<Floor_clerk> fc_queue = new Vector<>();
    static Vector<Storage_clerk> sc_queue = new Vector<>();
    static Vector<Thread> leaving_clerks = new Vector<>();
    static Vector<Customer> finished_Customers = new Vector<>();

    volatile static int ticket_dispenser = 1;
    volatile static int now_serving = 1;
	
    static long time = System.currentTimeMillis();
    static final long LONG_TIME = 999_999_999;

    public static void main(String[] args) {


	if(args.length < 1) {
	    n_customers = 18;
	    n_floor_clerks = 2;
	    n_storage_clerks = 4;
	} else if(args.length < 2) {
	    n_floor_clerks = 2;
	    n_storage_clerks = 4;
	} else if(args.length < 3) {
	    n_storage_clerks = 4;
	} else {
	    n_customers = Integer.parseInt(args[0]);
	    n_floor_clerks = Integer.parseInt(args[1]);
	    n_storage_clerks = Integer.parseInt(args[2]);
	}

	for(int i = 0; i < n_floor_clerks; i++) {
	    floor_clerks.add(new Floor_clerk("FC"+String.valueOf(i)));
	    floor_clerks.lastElement().start();
	}
	for(int i = 0; i < n_storage_clerks; i++) {
	    storage_clerks.add(new Storage_clerk("SC"+String.valueOf(i)));
	    storage_clerks.lastElement().start();
	}
	for(int i = 0; i < n_customers; i++) {
	    customers.add(new Customer("C"+String.valueOf(i)));
	    customers.lastElement().start();
	}
    }
}
