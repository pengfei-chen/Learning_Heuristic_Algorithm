package com.learn.gavrptw;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException
    {
        Conf.readInstance();
        System.out.println("运行中");
        GA_Strategy.genetic_algoritm().toSolution().print();
    }
}
