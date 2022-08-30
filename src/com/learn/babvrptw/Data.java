package com.learn.babvrptw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

//定义参数
class Data{
    int vertex_num;			//所有点集合n（包括配送中心和客户点，首尾（0和n）为配送中心）
    double E;	      		//配送中心时间窗开始时间
    double	L;	     		//配送中心时间窗结束时间
    int veh_num;    		//车辆数
    double cap;     		//车辆载荷
    int[][] vertexs;		//所有点的坐标x,y
    int[] demands;			//需求量
    int[] vehicles;			//车辆编号
    double[] a;				//时间窗开始时间【a[i],b[i]】
    double[] b;				//时间窗结束时间【a[i],b[i]】
    double[] s;				//客户点的服务时间
    int[][] arcs;			//arcs[i][j]表示i到j点的弧
    double[][] dist;		//距离矩阵，满足三角关系,暂用距离表示花费 C[i][j]=dist[i][j]
    double gap= 1e-6;
    double big_num = 100000;
    //截断小数3.26434-->3.2
    public double double_truncate(double v){
        int iv = (int) v;
        if(iv+1 - v <= gap)
            return iv+1;
        double dv = (v - iv) * 10;
        int idv = (int) dv;
        double rv = iv + idv / 10.0;
        return rv;
    }
    public Data() {
        super();
    }
    //函数功能：从txt文件中读取数据并初始化参数
    public void Read_data(String path,Data data,int vertexnum) throws Exception{
        String line = null;
        String[] substr = null;
        Scanner cin = new Scanner(new BufferedReader(new FileReader(path)));  //读取文件
        for(int i =0; i < 4;i++){
            line = cin.nextLine();  //读取一行
        }
        line = cin.nextLine();
        line.trim(); //返回调用字符串对象的一个副本，删除起始和结尾的空格
        substr = line.split(("\\s+")); //以空格为标志将字符串拆分
        //初始化参数
        data.vertex_num = vertexnum;
        data.veh_num = Integer.parseInt(substr[1]);
        data.cap = Integer.parseInt(substr[2]);
        data.vertexs =new int[data.vertex_num][2];				//所有点的坐标x,y
        data.demands = new int[data.vertex_num];					//需求量
        data.vehicles = new int[data.veh_num];					//车辆编号
        data.a = new double[data.vertex_num];						//时间窗开始时间
        data.b = new double[data.vertex_num];						//时间窗结束时间
        data.s = new double[data.vertex_num];						//服务时间
        data.arcs = new int[data.vertex_num][data.vertex_num];
        //距离矩阵,满足三角关系,用距离表示cost
        data.dist = new double[data.vertex_num][data.vertex_num];
        for(int i =0; i < 4;i++){
            line = cin.nextLine();
        }
        //读取vetexnum-1行数据
        for (int i = 0; i < data.vertex_num - 1; i++) {
            line = cin.nextLine();
            line.trim();
            substr = line.split("\\s+");
            data.vertexs[i][0] = Integer.parseInt(substr[2]);
            data.vertexs[i][1] = Integer.parseInt(substr[3]);
            data.demands[i] = Integer.parseInt(substr[4]);
            data.a[i] = Integer.parseInt(substr[5]);
            data.b[i] = Integer.parseInt(substr[6]);
            data.s[i] = Integer.parseInt(substr[7]);
        }
        cin.close();//关闭流
        //初始化配送中心参数
        data.vertexs[data.vertex_num-1] = data.vertexs[0];
        data.demands[data.vertex_num-1] = 0;
        data.a[data.vertex_num-1] = data.a[0];
        data.b[data.vertex_num-1] = data.b[0];
        data.E = data.a[0];
        data.L = data.b[0];
        data.s[data.vertex_num-1] = 0;
        double min1 = 1e15;
        double min2 = 1e15;
        //距离矩阵初始化
        for (int i = 0; i < data.vertex_num; i++) {
            for (int j = 0; j < data.vertex_num; j++) {
                if (i == j) {
                    data.dist[i][j] = 0;
                    continue;
                }
                data.dist[i][j] =
                        Math.sqrt((data.vertexs[i][0]-data.vertexs[j][0])
                                *(data.vertexs[i][0]-data.vertexs[j][0])+
                                (data.vertexs[i][1]-data.vertexs[j][1])
                                        *(data.vertexs[i][1]-data.vertexs[j][1]));
                data.dist[i][j]=data.double_truncate(data.dist[i][j]);
            }
        }
        data.dist[0][data.vertex_num-1] = 0;
        data.dist[data.vertex_num-1][0] = 0;
        //距离矩阵满足三角关系
        for (int  k = 0; k < data.vertex_num; k++) {
            for (int i = 0; i < data.vertex_num; i++) {
                for (int j = 0; j < data.vertex_num; j++) {
                    if (data.dist[i][j] > data.dist[i][k] + data.dist[k][j]) {
                        data.dist[i][j] = data.dist[i][k] + data.dist[k][j];
                    }
                }
            }
        }
        //初始化为完全图
        for (int i = 0; i < data.vertex_num; i++) {
            for (int j = 0; j < data.vertex_num; j++) {
                if (i != j) {
                    data.arcs[i][j] = 1;
                }
                else {
                    data.arcs[i][j] = 0;
                }
            }
        }
        //除去不符合时间窗和容量约束的边
        for (int i = 0; i < data.vertex_num; i++) {
            for (int j = 0; j < data.vertex_num; j++) {
                if (i == j) {
                    continue;
                }
                if (data.a[i]+data.s[i]+data.dist[i][j]>data.b[j] ||
                        data.demands[i]+data.demands[j]>data.cap) {
                    data.arcs[i][j] = 0;
                }
                if (data.a[0]+data.s[i]+data.dist[0][i]+data.dist[i][data.vertex_num-1]>
                        data.b[data.vertex_num-1]) {
                    System.out.println("the calculating example is false");

                }
            }
        }
        for (int i = 1; i < data.vertex_num-1; i++) {
            if (data.b[i] - data.dist[0][i] < min1) {
                min1 = data.b[i] - data.dist[0][i];
            }
            if (data.a[i] + data.s[i] + data.dist[i][data.vertex_num-1] < min2) {
                min2 = data.a[i] + data.s[i] + data.dist[i][data.vertex_num-1];
            }
        }
        if (data.E > min1 || data.L < min2) {
            System.out.println("Duration false!");
            System.exit(0);//终止程序
        }
        //初始化配送中心0，n+1两点的参数
        data.arcs[data.vertex_num-1][0] = 0;
        data.arcs[0][data.vertex_num-1] = 1;
        for (int i = 1; i < data.vertex_num-1; i++) {
            data.arcs[data.vertex_num-1][i] = 0;
        }
        for (int i = 1; i < data.vertex_num-1; i++) {
            data.arcs[i][0] = 0;
        }
    }
}
