package com.learn.gavrptw;

import java.io.*;
import java.util.Scanner;

/**
 * @description 储存问题的公共设置，顾客数，容量约束等,输入输出函数
 */

public class Conf {
    static int N;           // 顾客数
    static int Cap;         // 容量约束
    static String instance_name;
    static double[][] dis_matriax;  //距离矩阵
    static Customer [] customers;   //顾客

    static void readInstance() throws IOException {
        // File file_to_read = new File(".\\C101.txt");
        File file_to_read = new File("D:\\Java_Code\\Heuristic_algorithm\\src\\com\\learn\\gavrptw\\C101.txt");
        Scanner cin = new Scanner(file_to_read);
        instance_name = cin.nextLine();
        N = cin.nextInt();      // 那一行的数值
        Cap = cin.nextInt();
        customers = new Customer[N+1];  //新建数组
        dis_matriax = new double[N+1][N+1];
        while(cin.hasNext()){
            int i = cin.nextInt();
            customers[i] = new Customer();      //第几个点
            customers[i].x = cin.nextInt();
            customers[i].y = cin.nextInt();
            customers[i].demand = cin.nextInt();
            customers[i].r_time = cin.nextInt();
            customers[i].d_time = cin.nextInt();
            customers[i].s_time = cin.nextInt();
        }
        for(int i=0;i<=N;i++) // 初始化距离矩阵
            for(int j=i;j<=N;j++)
            {
                if(i==j)
                {
                    dis_matriax[i][j] = 0;
                }
                else
                {
                    dis_matriax[i][j] = Customer_Strategy.dis(customers[i],customers[j]);
                    dis_matriax[j][i] = dis_matriax[i][j];
                }
            }
    }
}
