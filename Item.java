import java.util.Random;

public class Item {
    final String weight;

    Item() {
	Random l = new Random();
	int l_num = l.nextInt(100) + 1;
	if(l_num < 61) weight = "light";
	else {
	    Random h = new Random();
	    int h_num = h.nextInt(100) + 1;
	    if(h_num < 51) weight = "heavy";
	    else weight = "very heavy";
	}
    }

    String weight() { return weight; };
}

