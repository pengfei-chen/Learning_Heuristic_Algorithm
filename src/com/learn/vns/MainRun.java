package com.learn.vns;

public class MainRun{
    public  static  void main(String[] args){
        Search abc = new Search();
        int[] best_solution = new int[TSPData.CITY_SIZE];
        best_solution = abc.random_permutation();
        abc.bestvalue = abc.cost_total(best_solution, TSPData.berlin52);
        System.out.println("初始总路线长度 = " + abc.bestvalue);
        abc.variable_neighborhood_search(best_solution, TSPData.berlin52);
        System.out.println("搜索完成！ 最优路线总长度 = " + abc.bestvalue);
        System.out.println("最优访问城市序列如下：");
        for (int i = 0; i < TSPData.CITY_SIZE; i++)
            System.out.print(best_solution[i] + "  ");
    }
}