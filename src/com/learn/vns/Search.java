package com.learn.vns;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Search {
    int[][] Delta1 = new int[TSPData.CITY_SIZE][TSPData.CITY_SIZE];
    int currentvalue;
    int bestvalue;

    int distance_2city(int[] city1, int[] city2){
        int distance = 0;
        distance = (int) Math.sqrt((double) Math.pow(city1[0] - city2[0], 2) + Math.pow(city1[1] - city2[1], 2) );
        return distance;
    }

    //根据产生的城市序列，计算旅游总距离
    //所谓城市序列，就是城市先后访问的顺序，比如可以先访问ABC，也可以先访问BAC等等
    //访问顺序不同，那么总路线长度也是不同的
    //p_perm 城市序列参数
    int cost_total(int[] cities_permutation, int[][] cities){
        int total_distance = 0;
        int c1,c2;
        /*逛一圈，看看最后的总距离*/
        for (int i=0; i<TSPData.CITY_SIZE; i++){
            c1 = cities_permutation[i];
            if (i == TSPData.CITY_SIZE-1){ //最后一个城市和第一个城市计算距离
                c2 = cities_permutation[0];
            }else {
                c2 = cities_permutation[i+1];
            }
            total_distance += distance_2city(cities[c1], cities[c2]);
        }
        return total_distance;
    }

    //获取随机城市排列
    int[] random_permutation(){
        int[] route = new int[TSPData.CITY_SIZE];
        ArrayList<Integer> cities = new ArrayList<>();
        for (int i =0; i<TSPData.CITY_SIZE; i++){
            cities.add(i);
        }
        Collections.shuffle(cities);
        Integer[] routefake = cities.toArray(new Integer[cities.size()]);
        for (int i=0; i<routefake.length; i++){
            route[i] = routefake[i];
        }
        return route;
    }

    //对应two_opt_swap的去重
    int calc_delta1(int i, int k, int tmp[], int[][] cities){
        /*
        以下计算说明：
        对于每个方案，翻转以后没必要再次重新计算总距离
        只需要在翻转的头尾做个小小处理

        比如：
        有城市序列   1-2-3-4-5 总距离 = d12 + d23 + d34 + d45 + d51 = A
        翻转后的序列 1-4-3-2-5 总距离 = d14 + d43 + d32 + d25 + d51 = B
        由于 dij 与 dji是一样的，所以B也可以表示成 B = A - d12 - d45 + d14 + d25
        下面的优化就是基于这种原理

        这里可以尝试代入，理解一下。
        */
        int delta = 0;
        if (i==0){
            if (k==TSPData.CITY_SIZE-1)
                delta = 0;
            else{
                delta = 0 - distance_2city(cities[tmp[k]], cities[tmp[k+1]])
                        + distance_2city(cities[tmp[i]], cities[tmp[k+1]])
                        - distance_2city(cities[tmp[TSPData.CITY_SIZE-1]], cities[tmp[i]])
                        + distance_2city(cities[tmp[TSPData.CITY_SIZE-1]], cities[tmp[k]]);
                }
        } else {
            if (k==TSPData.CITY_SIZE-1)
                delta = 0 - distance_2city(cities[tmp[i-1]], cities[tmp[i]])
                        + distance_2city(cities[tmp[i-1]], cities[tmp[k]])
                        - distance_2city(cities[tmp[0]], cities[tmp[k]])
                        + distance_2city(cities[tmp[i]], cities[tmp[0]]);
            else
                delta = 0 - distance_2city(cities[tmp[i-1]], cities[tmp[i]])
                        + distance_2city(cities[tmp[i-1]], cities[tmp[k]])
                        - distance_2city(cities[tmp[k]], cities[tmp[k+1]])
                        + distance_2city(cities[tmp[i]], cities[tmp[k+1]]);
        }
        return delta;
    }



    /*
    去重处理，对于Delta数组来说，对于城市序列1-2-3-4-5-6-7-8-9-10，如果对3-5应用了邻域操作2-opt ， 事实上对于
    7-10之间的翻转是不需要重复计算的。 所以用Delta提前预处理一下。

    当然由于这里的计算本身是O（1） 的，事实上并没有带来时间复杂度的减少（更新操作反而增加了复杂度）
    如果delta计算 是O（n）的，这种去重操作效果是明显的。
    */

    //对应two_opt_swap的去重更新
    // 目的是为了更新 新的，每个位置 到 其他位置的 减少值。
    void Update1(int i, int k, int[] tmp, int[][] cities, int[][] Delta){
        if (i!=0 && (k!= (TSPData.CITY_SIZE-1))) {
            i--;
            k++;
            for (int j = i ; j <= k; j++)
                for (int l = j + 1; l < TSPData.CITY_SIZE; l++)
                    Delta[j][l] = calc_delta1(j, l, tmp, cities);
            for (int j = 0; j < k; j++) {
                for (int l = i; l <= k; l++) {
                    if (j >= l)
                        continue;
                    Delta[j][l] = calc_delta1(j, l, tmp, cities);
                }
            }
            /*经过以上两步，Delta 这个二维数组基本被填满，任意两点间的 delta 被得到？  */
        } else { // 如果不是边界，更新(i-1, k + 1)之间的
            for (i=0; i<TSPData.CITY_SIZE; i++){
                for (k=i+1; k<TSPData.CITY_SIZE; k++)
                    Delta[i][k] = calc_delta1(i,k,tmp,cities);
            }
        }
    }

    void two_opt_swap(int[] cities_permutation, int b, int c){
        ArrayList<Integer> cities = new ArrayList<>();
        for (int i=0; i<b; i++)
            cities.add(cities_permutation[i] );
        for (int i=c; i>=b; i--)                            //就这里实现了反转
            cities.add(cities_permutation[i] );
        for (int i= c+1; i<TSPData.CITY_SIZE; i++)
            cities.add(cities_permutation[i] );
        for (int i=0; i<TSPData.CITY_SIZE; i++)
            cities_permutation[i] = cities.get(i);
    }


    //邻域结构1 使用two_opt_swap算子
    void neighborhood_one(int[] solution, int[][] cities){
        int i,k,count = 0;
        int max_no_improve = 60;
        int initial_cost = cost_total(solution, cities); //初始花费
        int now_cost = 0;
        // SOLUTION current_solution = solution;
        for (i=0; i<TSPData.CITY_SIZE-1; i++)
            for (k=i+1; k<TSPData.CITY_SIZE; k++)
                Delta1[i][k] = calc_delta1(i,k,solution, cities);
        do{
            count++;
            for (i=0; i<TSPData.CITY_SIZE-1; i++){
                for (k=i+1; k<TSPData.CITY_SIZE; k++){
                    if (Delta1[i][k] <0){
                        two_opt_swap(solution,i,k);
                        now_cost = initial_cost +Delta1[i][k];
                        currentvalue = now_cost;
                        initial_cost = currentvalue;
                        Update1(i,k,solution, cities, Delta1);  // 目的是更新delta的值
                        count = 0; //复位count
                    }
                }
            }
        }while (count <= max_no_improve);
    }

    //two_h_opt_swap的去重
    //随机产生两点，塞进新排列头部。其余的按第一个被选中点开始的顺序往后逐个排列：
    int calc_delta2(int i,int k, int[] cities_permutation, int[][] cities){
        int delta = 0;
        if(i==0){
            if(k==i+1)
                delta=0;
            else if (k==TSPData.CITY_SIZE-1)
                delta = 0 - distance_2city(cities[cities_permutation[i]],cities[cities_permutation[i+1]])
                        - distance_2city(cities[cities_permutation[k]],cities[cities_permutation[k-1]])
                        + distance_2city(cities[cities_permutation[k]],cities[cities_permutation[i+1]])
                        + distance_2city(cities[cities_permutation[k-1]],cities[cities_permutation[i]]);
            else
                delta = 0 - distance_2city(cities[cities_permutation[i]],cities[cities_permutation[i+1]])
                        - distance_2city(cities[cities_permutation[k]],cities[cities_permutation[k-1]])
                        - distance_2city(cities[cities_permutation[k]],cities[cities_permutation[k+1]])
                        + distance_2city(cities[cities_permutation[k-1]],cities[cities_permutation[k+1]])
                        + distance_2city(cities[cities_permutation[i]],cities[cities_permutation[k]])
                        + distance_2city(cities[cities_permutation[k]],cities[cities_permutation[i+1]]);
        }
        else {
            if (k==i+1)
                delta = 0;
            else if (k==TSPData.CITY_SIZE-1)
                delta = 0 - distance_2city(cities[cities_permutation[i]],cities[cities_permutation[i+1]])
                        - distance_2city(cities[cities_permutation[0]],cities[cities_permutation[k]])
                        - distance_2city(cities[cities_permutation[k]],cities[cities_permutation[k-1]])
                        + distance_2city(cities[cities_permutation[k]],cities[cities_permutation[i+1]])
                        + distance_2city(cities[cities_permutation[k-1]],cities[cities_permutation[0]])
                        + distance_2city(cities[cities_permutation[i]],cities[cities_permutation[k]]);
            else
                delta = 0 - distance_2city(cities[cities_permutation[i]],cities[cities_permutation[i+1]])
                        - distance_2city(cities[cities_permutation[k]],cities[cities_permutation[k+1]])
                        - distance_2city(cities[cities_permutation[k]],cities[cities_permutation[k-1]])
                        + distance_2city(cities[cities_permutation[i]],cities[cities_permutation[k]])
                        + distance_2city(cities[cities_permutation[k]],cities[cities_permutation[i+1]])
                        + distance_2city(cities[cities_permutation[k-1]],cities[cities_permutation[k+1]]);
        }
        return delta;
    }

    //two_h_opt_swap算子
    void two_h_opt_swap(int[] cities_permutation, int a, int d){
        int n = TSPData.CITY_SIZE;
        ArrayList<Integer> cities = new ArrayList<>();
        cities.add(cities_permutation[a]);
        cities.add(cities_permutation[d]);
        for (int i=1 ; i<n; i++ ){
            int idx = (a+i) % n;   // 结合前面例子来看，这里就理解了。
            if (idx != d)
                cities.add(cities_permutation[idx]);
        }
        for (int i=0; i<cities.size(); i++)
            cities_permutation[i] = cities.get(i);
    }

    void neighborhood_two(int[] solution, int[][] cities){
        int i,k,count = 0;
        int max_no_improve = 60;
        int inital_cost = cost_total(solution, cities);
        // 初始花费
        int now_cost, delta = 0;
        do{
            count++;
            for (i=0; i<TSPData.CITY_SIZE-1; i++)
                for (k=i+1; k< TSPData.CITY_SIZE; k++){
                    delta = calc_delta2(i,k,solution, cities);
                    if (delta < 0){
                        two_h_opt_swap(solution,i,k);
                        now_cost = inital_cost + delta;
                        currentvalue = now_cost;
                        inital_cost = currentvalue;
                        count = 0;
                    }
                }
        } while (count<= max_no_improve);
    }


    int[] variable_neighborhood_descent(int[] solution, int[][] cities){
        int[] current_solution = solution;
        int l = 1;
        System.out.println("=====================VariableNeighborhoodDescent=====================");
        while (true){
            if (l==1){
                neighborhood_one(current_solution,cities);
                System.out.println("Now in neighborhood_one , current_solution = "
                        + cost_total(current_solution, cities) + "  solution = " + cost_total(solution, cities));
                if (cost_total(current_solution, cities) < cost_total(solution, cities)) {
                    solution = current_solution;
                    l = 0;
                    }

        } else if (l == 2) {
                neighborhood_two(current_solution, cities);
                System.out.println("Now in neighborhood_two , current_solution = "
                        + cost_total(current_solution, cities) + "  solution = " + cost_total(solution, cities));
                if (cost_total(current_solution, cities) < cost_total(solution, cities)) {
                    solution = current_solution;
                    l = 0;
                }
            } else
                return solution;// 跳出循环体所在的方法，相当于结束该方法;
            l ++ ;
        }
    }

    //将城市序列分成4块，然后按块重新打乱顺序。
    //用于扰动函数
    void double_bridge_move (int[] cities_permutation){
            int pos1 = 1 + (int)Math.random() * (TSPData.CITY_SIZE / 4);
            int pos2 = pos1 + 1 + (int)Math.random() * (TSPData.CITY_SIZE / 4);
            int pos3 = pos2 + 1 + (int)Math.random() * (TSPData.CITY_SIZE / 4);
            int i;
            ArrayList<Integer> cities = new ArrayList<>();
            for(i=0; i<pos1; i++){
                cities.add(cities_permutation[i]);
            }
            for(i=pos3; i<TSPData.CITY_SIZE; i++){
                cities.add(cities_permutation[i]);
            }
            for(i=pos2; i<pos3; i++){
                cities.add(cities_permutation[i]);
            }
            for(i=pos1; i<pos2; i++){
                cities.add(cities_permutation[i]);
            }
        }

    // 抖动
    void shaking(int[] solution, int[][] cities){
            double_bridge_move(solution);
            currentvalue = cost_total(solution, cities);
        }

    void variable_neighborhood_search(int[] best_solution, int[][] cities){
            int max_iterations = 20;
            int count = 0, it = 0;
            int[] current_solution = best_solution;
            // 算法开始
            do{
                System.out.println("Algorithm VNS iterated  " + (it+1) + "times"); // \t是补全当前字符串长度到8的整数倍
                count++;
                it++;
                shaking(current_solution, cities);
                current_solution = variable_neighborhood_descent(current_solution, cities);
                bestvalue = cost_total(current_solution, cities);
                if (currentvalue < bestvalue){
                    best_solution = current_solution;
                    count = 0;
                }
                System.out.println("全局best_solution = " + bestvalue);
            } while (count<= max_iterations);
        }

}
