package com.learn.bapvrptw;

import java.io.IOException;
import java.util.ArrayList;

/*
* 2022.09.10 ���Ե�ߣ��һ����룬ĿǰӦ����һ֪����״̬��
* ��ʱ��ع��£�������˼·��
* */

public class Main {

	public static void main(String[] args) throws IOException {
		branchandbound bp = new branchandbound();
		paramsVRP instance = new paramsVRP();
		instance.initParams("src\\com\\learn\\bapvrptw\\dataset\\R101.TXT");		// ������Ҫ�޸�·��
		ArrayList<route> initRoutes = new ArrayList<route>();
		ArrayList<route> bestRoutes = new ArrayList<route>();
		
		bp.BBnode(instance, initRoutes, null, bestRoutes, 0);
		double optCost = 0;
		System.out.println();
		System.out.println("solution >>>");
		for(int i = 0; i < bestRoutes.size(); ++i) {
			System.out.println(bestRoutes.get(i).path);
			optCost+=bestRoutes.get(i).cost;
		}

		System.out.println("\nbest Cost = "+optCost);
	}

}
