package com.learn.gavrptw;

public class Customer {
    int x;
    int y;
    int demand;
    int r_time;//开始时间
    int d_time;//结束时间
    int s_time;//服务时间
}

abstract class Customer_Strategy
{
    public static double dis(Customer a,Customer b)
    {
        return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y));//返回两个顾客的欧式距离
    }
}
