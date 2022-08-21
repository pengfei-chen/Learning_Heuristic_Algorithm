package com.learn.gavrptw;

import java.util.*;

/**
 * @description Route类，每一个route储存了一辆车所访问的顾客
 */

public class Route {
    ArrayList<Integer> cus_list = new ArrayList<>();// route列表

    double value;
    boolean if_feasible;

    boolean check()//检查可行性
    {
        return (    check_c() && check_t());
    }

    //容量检查
    boolean check_c()
    {
        int ans = 0;
        for(int i:this.cus_list)
        {
            ans +=Conf.customers[i].demand;
        }

        return ans <= Conf.Cap;        // 这里不能等于嘛？
    }

    //时间检查
    boolean check_t()
    {
        double time = 0;
        time += Conf.dis_matriax[0][this.cus_list.get(0)];  //起点到当前列表的第一个点需要时间
        if(time>Conf.customers[cus_list.get(0)].d_time)return false;
        for(int i=1;i<=this.cus_list.size()-1;i++)
        {
            // TODO ：要好好理解这里，检验看还有没有错误。
            // 比较当前列表前一个点的最早开始时间。
            // 本次循环，右边的time，不会比上一个循环中的开始时间小。
            // time 一直在变大。
            time = Math.max(Conf.customers[this.cus_list.get(i-1)].r_time,  time+Conf.dis_matriax[this.cus_list.get(i-1)][this.cus_list.get(i)]);
            if(time > Conf.customers[cus_list.get(i)].d_time)return  false;
        }
        return true;
    }

    //获得route的dis
    // 获取路径的距离？
    double getValue()
    {
        this.value = 0;
        value += Conf.dis_matriax[0][cus_list.get(0)];//开始
        value += Conf.dis_matriax[0][cus_list.get(cus_list.size()-1)];
        if(cus_list.size()>1) {
            for (int i = 1; i < cus_list.size(); i++) {
                value += Conf.dis_matriax[cus_list.get(i)][cus_list.get(i - 1)];
            }
        }
        return value;
    }

}
