package com.learn.tabu_search.VRPTWTS;

import static com.learn.tabu_search.VRPTWTS.InitAndPrint.*;
import static com.learn.tabu_search.VRPTWTS.TS.*;

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
