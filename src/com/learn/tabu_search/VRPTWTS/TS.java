package com.learn.tabu_search.VRPTWTS;
 
import static com.learn.tabu_search.VRPTWTS.EvaluateRoute.*;
import static java.lang.Math.*;
import static com.learn.tabu_search.VRPTWTS.Parameter.*;

public class TS {
	
	public static void TabuSearch() {
		//��������
	    //��ȡ�������ӣ�����һ��·����ѡ��һ����뵽��һ��·����
	    //�ڸò������γɵ�������ѡȡʹĿ�꺯����С���Ľ�
		
	    double Temp1;
	    double Temp2;

	    //��ʼ�����ɱ�
	    for ( int i = 2; i <= CustomerNumber + 1; ++i ) {
	        for ( int j = 1; j <= VehicleNumber; ++j )
	            Tabu[i][j] = 0;
	        TabuCreate[i] = 0;
	    }

	    int Iteration = 0;
	    while ( Iteration < IterMax ) {
	        int BestC = 0;
	        int BestR = 0;
	        int BestP = 0;
	        int P=0;
	        double BestV = INF;

	        for ( int i = 2; i <= CustomerNumber + 1; ++i ) {//��ÿһ���ͻ��ڵ�
	            for ( int j = 1; j < routes[customers[i].R].V.size(); ++j ) {//��������·���е�ÿһ���ڵ�
	                if ( routes[customers[i].R].V.get(j).Number == i ) {//�ҵ��ڵ�i����·����������λ��j
	                    P = j;					//���λ�ã������ǣ��ҵ��ڵ�i����·����������λ��j
	                    break;
	                }
	            }
	          
	            removenode(customers[i].R,P,i);//���ͻ�i��ԭ·���ĵ�P��λ�����Ƴ�
	            
	            //�ҵ�һ��·������ɾȥ�Ľڵ�
	            for ( int j = 1; j <= VehicleNumber; ++j ) 
	            	 for ( int l = 1; l < routes[j].V.size(); ++l )//�ֱ�ö��ÿһ���ڵ�����λ��
	                        if ( customers[i].R != j ) {
	                        	
	                        	addnode(j,l,i);//���ͻ�l����·��j�ĵ�i��λ��
	                        	
	                            Temp1 = routes[customers[i].R].SubT;  //��¼ԭ������·����ʱ�䴰Υ���ܺ�
	                            Temp2 = routes[j].SubT;               //��¼�����·��ʱ�䴰Υ���ܺ�
	                            
	                            //����i�ڵ��Ƴ���·����
	                    	    routes[customers[i].R].SubT = 0;
	                    	    UpdateSubT(routes[customers[i].R]);
	                    	    //����i�ڵ������·��j��
	                    	    routes[j].SubT = 0;
	                    	    UpdateSubT(routes[j]);
	                            double TempV = Calculation ( routes, i, j );//����Ŀ�꺯��ֵ
	                            
	                            if((TempV < Ans)|| //����׼���������ȫ�����Ž�
	                            		(TempV < BestV &&   //����Ϊ�ֲ����Ž⣬��δ������
	                            		   ( routes[j].V.size() > 2 && Tabu[i][j] <= Iteration ) || ( routes[j].V.size() == 2 && TabuCreate[i] <= Iteration )))
									/*
									 * TODO :�ж��ˣ�����ʲôҲû��������Ϊʲô��
									 * */

	                            	//���ɲ��������ǰ��Ϊ������ɱ����ɲ������ӣ�����Ϊ������ɱ�����ʹ���µĳ���
	            	            	//·���нڵ�������2���ж��Ƿ���ɲ������ӣ�·����ֻ����㡢�յ㣬�ж��Ƿ����ʹ���³�����
	                            if ( TempV < BestV ) { //��¼�ֲ��������
	                                BestV = TempV; //best vehicle ��������
	                                BestC = i;     //best customer�ͻ�
	                                BestR = j;     //best route   ����·��
	                                BestP = l;     //best position����λ��
	                            }
	                            
	                            //�ڵ���·����ԭ
	                            routes[customers[i].R].SubT = Temp1;
	                            routes[j].SubT = Temp2;
	                            removenode(j,l,i);
	                        }
	            //�ڵ�ԭ·����ԭ
	            addnode(customers[i].R,P,i);
	        }

	        //���³������ɱ�
	        if ( routes[BestR].V.size() == 2 )
	            TabuCreate[BestC] = Iteration + 2 * TabuTenure + (int)(random() * 10);
	        //���½��ɱ�
	        Tabu[BestC][customers[BestC].R] = Iteration + TabuTenure + (int)(random() * 10);
	        //���ȫ�����ŵĽڵ��������ڵ�ǰ·������
	        for ( int i = 1; i < routes[customers[BestC].R].V.size(); ++i )
	            if ( routes[customers[BestC].R].V.get(i).Number == BestC ) {
	                P = i;
	                break;
	            }

	        //��������ѭ������ѡ�Ľ���������µ�����·���滮
	        //���θ��¸ı����·���ģ����أ����볤�ȣ�����ʱ�䴰����
	        
	        //����ԭ·��
	        removenode(customers[BestC].R,P,BestC);
	        //������·��
	        addnode(BestR,BestP,BestC);
	        //���³���ʱ��
	        routes[BestR].SubT = 0;
	        UpdateSubT(routes[BestR]);
	        routes[customers[BestC].R].SubT = 0;
	        UpdateSubT(routes[customers[BestC].R]);

	        //���±������Ľڵ�����·�����
	        customers[BestC].R = BestR;
	        
	        //�����ǰ��Ϸ��ҽ�������´洢���
	        if ( ( Check ( routes ) == true ) && ( Ans > BestV ) ) {
	        	 for ( int i = 1; i <= VehicleNumber; ++i ) {
	        	        Route_Ans[i].Load = routes[i].Load;
	        	        Route_Ans[i].V.clear();
	        	        for ( int j = 0; j < routes[i].V.size(); ++j )
	        	            Route_Ans[i].V.add ( routes[i].V.get(j) );
	        	    }
	            Ans = BestV;
	        }
	        
	        Iteration++;
	    }
	}
	
	private static void addnode(int r,int pos,int Cus) {//�ڵ�����·��routes[r],�ڵ�customer[Cus],�ڵ����·����λ��pos
		//������·��r�м��Ͻڵ�Cus������
        routes[r].Load += customers[Cus].Demand;
        //������·��r�в���ڵ�Cus������ɵ�·������
        routes[r].Dis = routes[r].Dis 
        		- Graph[routes[r].V.get(pos-1).Number][routes[r].V.get(pos).Number]
                + Graph[routes[r].V.get(pos-1).Number][customers[Cus].Number] 
                + Graph[routes[r].V.get(pos).Number][customers[Cus].Number];
        //��·��r�в���ڵ�Cus
        routes[r].V.add (pos ,new CustomerType (customers[Cus]) );//����i���±�Ϊl��
	}
	
	
	private static void removenode(int r,int pos,int Cus) {//�ڵ�ȥ����·��routes[r],�ڵ�customer[cus],�ڵ�����·����λ��pos 
        //������·��r��ȥ���ڵ�Cus������
        routes[r].Load -= customers[Cus].Demand;
        //������·��r��ȥ���ڵ�Cus������ɵ�·���ľ���
        routes[r].Dis = routes[r].Dis 
        		- Graph[routes[r].V.get(pos-1).Number][routes[r].V.get(pos).Number]
	            - Graph[routes[r].V.get(pos).Number][routes[r].V.get(pos+1).Number] 
	            + Graph[routes[r].V.get(pos-1).Number][routes[r].V.get(pos+1).Number];
        //��·��r��ȥ���ڵ�Cus
        routes[r].V.remove ( pos );
	}
}