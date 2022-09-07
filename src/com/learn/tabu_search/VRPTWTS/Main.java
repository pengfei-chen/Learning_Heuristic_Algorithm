package VRPTW;

import static VRPTW.InitAndPrint.*;  
import static VRPTW.TS.*;

public class Main {
	public static void main (String arg[]) {
		
		long begintime = System.nanoTime();
		
		ReadIn();
	    Construction();
	    TabuSearch();
	    Output();
	    CheckAns();
		
	    long endtime = System.nanoTime();
		double usedTime= (endtime - begintime)/(1e9);
		System.out.println();
		System.out.println("Total run time £º"+usedTime+"s");
	}
}
