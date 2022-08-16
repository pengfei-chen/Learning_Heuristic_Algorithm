package com.learn.aca;

import java.util.Random;

public class AntColonyProgram {
    private static final Random random = new Random(1);   // 改变种子值，结果值也会不同
    /**
     * influence of pheromone on direction
     */
    private static final int alpha = 3;
    /**
     * influence of adjacent node distance
     */
    private static final int beta = 2;
    /**
     * pheromone decrease factor
     */
    private static final double rho = 0.01;
    /**
     * pheromone increase factor
     */
    private static final double Q = 2.0;

    public static void main(String[] args){
        try{
            System.out.println("\nBegin Ant Colony Optimization demo\n");

            int numCities = 60;
            int numAnts = 4;
            int maxTime = 1000;

            System.out.println("Number cities in problem = " + numCities);
            System.out.println("\nNumber ants = " + numAnts);
            System.out.println("Maximum time = " + maxTime);
            System.out.println("\nAlpha (pheromone influence) = " + alpha);
            System.out.println("Beta (local node influence) = " + beta);
            System.out.println("Rho (pheromone evaporation coefficient) = " + rho);
            System.out.println("Q (pheromone deposit factor) = " + Q);
            System.out.println("\nInitialing dummy graph distances");

            int [][] dists = MakeGraphDistances(numCities);
            System.out.println("\nInitialing ants to random trails\n");
            int[][] ants = InitAnts(numAnts, numCities);
            // initialize ants to random trails
            ShowAnts(ants, dists);

            //determine the best initial trail
            int[] bestTrail = AntColonyProgram.BestTrail(ants, dists);
            // the length of the best trail
            double bestlength = length(bestTrail, dists);

            System.out.print("\nBest initial trail length: " + bestlength + "\n");
            System.out.println("\nInitializing pheromones on trails");
            double [][] pheromones = InitPheromones(numCities);

            int time = 0;
            System.out.println("\nEntering UpdateAnts - UpdatePheromones loop\n");
            while(time < maxTime){
//                System.out.println("______111_______");
//                System.out.println("bestlength: " + bestlength);
                UpdateAnts(ants, pheromones, dists);
                UpdatePheromones(pheromones, ants, dists);

                int[] currBestTrail = AntColonyProgram.BestTrail(ants, dists);
                double currBestlength = length(currBestTrail, dists);
//                System.out.println("currBestlength: " + currBestlength);
//                System.out.println("bestlength: " + bestlength);
                if (currBestlength < bestlength){
                    bestlength = currBestlength;
                    bestTrail = currBestTrail;
                    System.out.println("New best length of " + bestlength + " found at time " + time);
                }
                time += 1;
            }

            System.out.println("\nTime complete");
            System.out.println("\nBest trail found:");
            Display(bestTrail);
            System.out.println("\nlength of best trail found: " + bestlength);
            System.out.println("\nEnd Ant Colony Optimization demo\n");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    //Main
    // -----------------------------------------------------------------------------------------
    private static int[][] InitAnts(int numAnts, int numCities) throws Exception{
        int[][] ants = new int [numAnts][];
        for(int k = 0; k <= numAnts-1; k++){
            int start = random.nextInt(numCities);
            ants[k] = RandomTrail(start, numCities);
        }
        return ants;
    }

    public static int[] RandomTrail(int start, int numCities) throws Exception{
        // helper for InitAnts
        int[] trail = new int [numCities];

        // sequential
        for (int i = 0; i <= numCities - 1; i++ ){
            trail[i] = i;
        }
        // Fisher-Yates shuffle
        for (int i = 0;i <= numCities-1; i++){
            int r = random.nextInt(numCities-i) + i;
            int tmp = trail[r];
            trail[r] = trail[i];
            trail[i] = tmp;
        }

        int idx = IndexOfTarget(trail, start);
        // put start at [0]
        int temp = trail[0];
        trail[0] = trail[idx];
        trail[idx] = temp;

        return trail;
    }

    private static int IndexOfTarget(int[] trail, int target) throws Exception {
        // helper for RandomTrail
        for (int i = 0; i <= trail.length - 1; i++) {
            if (trail[i] == target) {
                return i;
            }
        }
        throw new Exception("Target not found in IndexOfTarget");
    }

    private static double length(int[] trail, int[][] dists){
        // total length of a trail
        double result = 0.0;
        for (int i = 0; i<= trail.length-2; i++){
                result += Distance(trail[i], trail[i+1], dists);
        }
        return result;
    }

    public static int[] BestTrail(int[][] ants, int[][] dists){
        // best trail has shortest total length
        // 对比每一只蚂蚁最好的路
        double bestlength = length(ants[0], dists);
        int idxBestlength = 0;
        for (int k=1; k <= ants.length - 1; k++ ){
            double len = length(ants[k], dists);
            if (len < bestlength){
                bestlength = len;
                idxBestlength = k;
            }
        }

        int numCities = ants[0].length;
        /*
        * INSTANT VB NOTE: The local variable bestTrail was renamed since Visual Basic
        * will not allow local variables with the same name as their enclosing function or property
        * */
        int[] bestTrail_Renamed = new int[numCities];
        bestTrail_Renamed = ants[idxBestlength].clone();

        return bestTrail_Renamed;
    }

    private static double [][] InitPheromones(int numCities){
        double[][] pheromones = new double[numCities][];
        for(int i = 0; i <= numCities - 1; i++)
            pheromones[i] = new double[numCities];
        for (int i =0; i<= pheromones.length - 1; i++){
            for (int j = 0 ; j <= pheromones[i].length - 1; j++)
                pheromones[i][j] = 0.01;
            /*
            *  otherwise first call to UpdateAnts -> BuiuldTrail -> NextNode -> MoveProbs => all 0.0 => throws
            * */
        }

        return pheromones;
    }

    private static void UpdateAnts(int[][] ants, double[][] pheromones, int[][] dists) throws Exception{
        int numCities = pheromones.length;
        for (int k=0; k <= ants.length - 1; k++){
            int start = random.nextInt(numCities);
            int[] newTrail = BuildTrail(k, start, pheromones, dists);
            ants[k] = newTrail;
        }
    }


    private static int[] BuildTrail(int k, int start, double[][] pheromones, int[][] dists) throws Exception{
        int numCities = pheromones.length;
        int[] trail = new int[numCities];
        boolean[] visited = new boolean[numCities];
        trail[0] = start;  //第一次写遗漏掉了
        visited[start] = true;
        // 根据信息素选择下一个城市
        for(int i = 0; i<= numCities - 2; i++){
            int cityX = trail[i];
            int next = NextCity(k, cityX, visited, pheromones, dists);
            trail[i+1] = next;
            visited[next] = true;
        }
        return trail;
    }

    private static int NextCity(int k, int cityX, boolean[] visited, double[][] pheromones,
                                int[][] dists) throws Exception{
        // for ant k (with visited[]), at nodeX, what is next node in trail?
        double[] probs = MoveProbs(k,cityX, visited, pheromones, dists);

        double[] cumul = new double[probs.length + 1];
        for (int i = 0; i <= probs.length - 1; i++){
            cumul[i+1] = cumul[i] + probs[i];
            // consider setting cumul[cuml.length-1] to 1.00
            // 这里使用 cumul ，保证了其肯定可以返回一个点。
        }

        double p = random.nextDouble();
        for (int i = 0; i <= cumul.length - 2; i++){
            if (p>=cumul[i] && p < cumul[i+1]){
                return i;
            }
        }
        throw new Exception("Failure to return valid city in NextCity");
    }

    private static double[] MoveProbs(int k, int cityX, boolean[] visited, double[][] pheromones, int[][] dists){
        // for ant k, located at nodeX, with visited[], return the prob of moving to each city
        int numCities = pheromones.length;
        double[] taueta = new double[numCities];
        double sum = 0.0;
        // sum of all status
        // i is the adjacent city
        for (int i = 0; i <= taueta.length - 1; i++){
            if (i == cityX){
                taueta[i] = 0.0;
                // prob of moving to self is 0
            } else if (visited[i]){
                taueta[i] = 0.0;
            } else {
                taueta[i] = Math.pow(pheromones[cityX][i], alpha) * Math.pow((1.0 / Distance(cityX, i ,dists)), beta);
                if (taueta[i] < 0.0001){
                    taueta[i] = 0.0001;
                } else if (taueta[i] > (Double.MAX_VALUE / (numCities * 100))){
                    taueta[i] = Double.MAX_VALUE / (numCities * 100);
                }
            }
            sum += taueta[i];
        }

        double[] probs = new double[numCities];
        for (int i = 0; i <= probs.length - 1; i++){
            probs[i] = taueta[i] / sum;
            // big trouble if sum = 0.0
        }

        return probs;
    }

    private static void UpdatePheromones(double[][] pheromones, int[][] ants, int[][] dists) throws Exception {
        for (int i = 0; i <= pheromones.length - 1; i++)
            for (int j = i + 1; j <= pheromones[i].length - 1; j++)
                for (int k = 0; k <= ants.length - 1; k++){
                    double length = AntColonyProgram.length(ants[k], dists);
                    // length of ant k trail
                    double decrease = (1.0 - rho) * pheromones[i][j];
                    double increase = 0.0;
                    if (EdgeInTrail(i,j,ants[k])){  //是否相连
                        increase = (Q/length);
                    }

                    pheromones[i][j] = decrease + increase;

                    if (pheromones[i][j] < 0.0001){
                        pheromones[i][j] = 0.0001;
                    } else if (pheromones[i][j] > 100000.0){
                        pheromones[i][j] = 100000.0;
                    }

                    pheromones[j][i] = pheromones[i][j];
                }
    }

    private static boolean EdgeInTrail(int cityX, int cityY, int[] trail) throws Exception{
        // are cityX and cityY adjacent to each other in trail[]?
        int lastIndex = trail.length - 1;
        int idx = IndexOfTarget(trail, cityX);

        if (idx == 0 && trail[1] == cityY) {
            return true;
        } else if (idx == 0 && trail[lastIndex] == cityY) {
            return true;
        } else if (idx == 0) {
            return false;
        } else if (idx == lastIndex && trail[lastIndex - 1] == cityY) {
            return true;
        } else if (idx == lastIndex && trail[0] == cityY) {
            return true;
        } else if (idx == lastIndex) {
            return false;
        } else if (trail[idx - 1] == cityY) {
            return true;
        } else return trail[idx + 1] == cityY;
    }

    private static int[][] MakeGraphDistances(int numCities){
        int [][] dists = new int[numCities][];
        for(int i = 0; i <= dists.length - 1; i++){
            dists[i] = new int[numCities];
        }

        for (int i = 0; i <= numCities - 1; i++){
            for (int j = i + 1; j <= numCities - 1; j++){
                int d = random.nextInt(8) + 1; //[1,8]
                dists[i][j] = d;
                dists[j][i] = d;
            }
        }

        return dists;
    }

    private static double Distance(int cityX, int cityY, int[][] dists) {
        return dists[cityX][cityY];
    }

    private static void Display(int[] trail){
        for (int i = 0; i <= trail.length - 1 ; i++){
            System.out.print(trail[i] + " ");
            if (i > 0 && i % 20 == 0){
                System.out.println();
            }
        }
        System.out.println();
    }


    private static void ShowAnts(int[][] ants, int[][] dists){
        for (int i = 0; i <= ants.length - 1; i++){
            System.out.print(i + ": [ ");

            for (int j = 0; j <= 3; j++){
                System.out.print(ants[i][j] + " ");
                }
            System.out.print("... ");
            for (int j  = ants[i].length - 4; j <= ants[i].length - 1; j ++){
                System.out.print(ants[i][j] + " " );
            }

            System.out.print("] len = ");
            double len = length(ants[i], dists);
            System.out.printf("%.1f", len);
            System.out.println();
        }
    }

    private static void  Display(double[][] pheromones){
        for (int i = 0; i <= pheromones.length - 1; i++){
            System.out.print(i + ": ");
            for (int j = 0; j <= pheromones[i].length -1; j++){
                String p = String.format("%.4f", pheromones[i][j]);
                System.out.print(String.format("%1$-8s",pheromones[i][j]) + " ");
            }
            System.out.println();
        }
    }

}
