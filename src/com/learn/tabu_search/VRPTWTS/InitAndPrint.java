package com.learn.tabu_search.VRPTWTS;

import static java.lang.Math.*;
import static com.learn.tabu_search.VRPTWTS.Parameter.*;
import static com.learn.tabu_search.VRPTWTS.EvaluateRoute.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import com.learn.tabu_search.VRPTWTS.RouteType;

public class InitAndPrint {
	
	//����ͼ�ϸ��ڵ��ľ���
	private static double Distance ( CustomerType C1, CustomerType C2 ) {
	    return sqrt ( ( C1.X - C2.X ) * ( C1.X - C2.X ) + ( C1.Y - C2.Y ) * ( C1.Y - C2.Y ) );
	}
	
	
	//��ȡ����
	public static void ReadIn(){

		for(int i=0;i<CustomerNumber+10;i++) {
			customers[i]=new CustomerType();
			routes[i]=new RouteType();
			Route_Ans[i]=new RouteType();
		}
		
		try {	
			Scanner in = new Scanner(new FileReader("src\\com\\learn\\tabu_search\\VRPTWTS\\c101.txt"));
			
			 for ( int i = 1; i <= CustomerNumber + 1; ++i ) {
				 customers[i].Number=in.nextInt()+1;
				 customers[i].X=in.nextDouble();
				 customers[i].Y=in.nextDouble();
				 customers[i].Demand=in.nextDouble();
				 customers[i].Begin=in.nextDouble();
				 customers[i].End=in.nextDouble();
				 customers[i].Service=in.nextDouble();
			 }
			
			in.close();
		}catch (FileNotFoundException e) {
			// File not found
			System.out.println("File not found!");
			System.exit(-1);
		}
		
		for ( int i = 1; i <= VehicleNumber; ++i ) {
	        if ( routes[i].V.size()!=0 )
	            routes[i].V.clear();
	        
	        routes[i].V.add ( new CustomerType (customers[1]) );//�������������һ�����ƣ�����Ҳ��Ҫ�ġ�
	        routes[i].V.add ( new CustomerType (customers[1]) );
	        routes[i].V.get(0).End=routes[i].V.get(0).Begin;//���
	        routes[i].V.get(1).Begin=routes[i].V.get(1).End;//�յ�
	        //�����и����ڵ�0����ʼʱ��0����ֹʱ�䣬�������ϸ�ֵ��
	        routes[i].Load = 0;
	    }
		
		Ans = INF;

	    for ( int i = 1; i <= CustomerNumber + 1; ++i )
	        for ( int j = 1; j <= CustomerNumber + 1; ++j )
	            Graph[i][j] = Distance ( customers[i], customers[j] );
	   
	}
	
	
	//�����ʼ��
	public static void Construction() {
	    int[] Customer_Set=new int[CustomerNumber + 10];
	    for ( int i = 1; i <= CustomerNumber; ++i )
	        Customer_Set[i] = i + 1;

	    int Sizeof_Customer_Set = CustomerNumber;
	    int Current_Route = 1;

	    //����������Լ��ΪĿ�ĵ������ʼ��
	    //�������ѡһ���ڵ���뵽��m��·���У�����������Լ����������m+1��·��
	    //�Ҳ���·����λ���ɸ�·�����Ѵ��ڵĸ��ڵ������ʱ�����
	    while ( Sizeof_Customer_Set > 0 ) {
			int K = (int) (random() * Sizeof_Customer_Set + 1);
			int C = Customer_Set[K];
			Customer_Set[K] = Customer_Set[Sizeof_Customer_Set];
			Sizeof_Customer_Set--;//����ǰ������Ľڵ㸳ֵΪ��ĩ�ڵ�ֵ,����������1
			//�����ȡ��һ���ڵ㣬���Ʋ�������������еĴ���

	        if ( routes[Current_Route].Load + customers[C].Demand > Capacity )
	            Current_Route++;
	        //����������Լ������һ������·��
	        
	        for ( int i = 0; i < routes[Current_Route].V.size() - 1; i++ )//��·����ÿһ�Խڵ���ң����Ƿ��ܲ����½ڵ�
	            if ( ( routes[Current_Route].V.get(i).Begin <= customers[C].Begin ) && ( customers[C].Begin <= routes[Current_Route].V.get(i + 1).Begin ) ) {
	            	routes[Current_Route].V.add ( i + 1, new CustomerType (customers[C]) );
	            	//�ж�ʱ�䴰��ʼ���֣����㣬�����ýڵ㡣
	            	routes[Current_Route].Load += customers[C].Demand;
	            	customers[C].R = Current_Route;
	            	//����·���������ڵ��ࡣ
	                break;
	            }
	    }
	    
	    
	    //��ʼ�����㳬��ʱ�䴰Լ��������
	    for ( int i = 1; i <= VehicleNumber; ++i ) {
	    	routes[i].SubT = 0;
	        routes[i].Dis = 0;
	        
	        for(int j = 1; j < routes[i].V.size(); ++j) {
	        	routes[i].Dis += Graph[routes[i].V.get(j-1).Number][routes[i].V.get(j).Number];
	        }
	        
	        UpdateSubT(routes[i]);
	    }
	    
	}

	
	public static void Output () {//������
	    System.out.println("************************************************************");
	    System.out.println("The Minimum Total Distance = "+ Ans);
	    System.out.println("Concrete Schedule of Each Route as Following : ");

	    int M = 0;
	    for ( int i = 1; i <= VehicleNumber; ++i )
	        if ( Route_Ans[i].V.size() > 2 ) {
	            M++;
	            System.out.print("No." + M + " : ");
	            
	            for ( int j = 0; j < Route_Ans[i].V.size() - 1; ++j )
	            	System.out.print( Route_Ans[i].V.get(j).Number + " -> ");
	            System.out.println( Route_Ans[i].V.get(Route_Ans[i].V.size() - 1).Number);
	        }
	    System.out.println("************************************************************");
	}
	
	public static void CheckAns() {
		//�����������Ƿ���ȷ
	    double Check_Ans = 0;
	    for ( int i = 1; i <= VehicleNumber; ++i )
	        for ( int j = 1; j < Route_Ans[i].V.size(); ++j )
	            Check_Ans += Graph[Route_Ans[i].V.get(j-1).Number][Route_Ans[i].V.get(j).Number];

	    System.out.println("Check_Ans="+Check_Ans );
	    
	    //�����Ƿ�����ʱ�䴰Լ��
	    boolean flag=true;
	    for (int i=1;i<=VehicleNumber;i++){
	    	UpdateSubT(Route_Ans[i]);
	    	if( Route_Ans[i].SubT>0 )
	    		flag=false;
	    }
	    if (flag) 
	    	System.out.println("Solution satisfies time windows construction");
	    else 
	    	System.out.println("Solution not satisfies time windows construction");
	    
	}
}
