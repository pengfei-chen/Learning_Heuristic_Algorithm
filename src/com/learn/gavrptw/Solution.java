package com.learn.gavrptw;

import java.util.*;

/**
 * @description Solution类，构成了问题的一个解,提供了转到chromosome的函数
 */

public class Solution {
    ArrayList<Route> rou_list = new ArrayList<>();          //多个route组成的列表 每一个route储存了一辆车所访问的顾客
    double fitness; // 适应度

    double getFitness()
    {
        this.fitness = 0;
        for(Route route:rou_list) {
            route.getValue();                       // 这一辆车所返回的 距离（成本）
            this.fitness += route.value;
        }
        return this.fitness;
    }

    // 把solution转化为chromosome
    Chromosome tochromosome()
    {
        Chromosome chromosome = new Chromosome();
        for(Route route :rou_list)
        {
            chromosome.cur_list.addAll(route.cus_list);
        }
        return chromosome;
    }

    //输出解
    void print()
    {
        int  i = 1;
        for(Route route :this.rou_list)
        {
            System.out.print("Route "+ i + ": 0-");
            for (int j : route.cus_list)
            {

                System.out.print(j + "-");
            }
            System.out.print("0");
            System.out.print("可行性检验："+route.check()+" ");
            System.out.print(route.value);
            System.out.println();
            i++;
        }
        System.out.println("该解的目标函数值为" + this.getFitness());
    }
}
