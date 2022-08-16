"""导入相关基础包，定义全局变量"""
import pandas as pd
import numpy as np
import copy

g_node_list=[]          #网络节点集合
g_link_list=[]          #网络节点类别集合
g_node_zone={}          #网络弧集合
g_shortest_path=[]      #最短路径集合
g_origin=None           #网络源节点
g_number_of_nodes=0     #网络节点个数
node_predecessor=[]     #前向节点集合
node_label_cost=[]      #距离标签集合
Max_label_cost=99999    #初始距离标签


