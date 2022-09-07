package VRPTW;

public class Parameter {

	public static double INF=Double.MAX_VALUE;
	public static int CustomerNumber=25;//�����г��ֿ�����Ĺ˿ͽڵ����
	public static int VehicleNumber = 25;
	public static int Capacity=200;//�����������
	public static int IterMax=2000;//������������
	
	public static int TabuTenure=20;//���ɲ���
	public static int[][] Tabu=new int[CustomerNumber + 10][VehicleNumber + 10];//���ɱ����ڽ��ɽڵ�������:[i][j]���ڵ�i����·��j��
	public static int[] TabuCreate=new int[CustomerNumber + 10];//���ɱ����ڽ�����չ��·����ʹ���³���

	public static double Ans;//���Ž����
	public static double Alpha = 1, Beta = 1, Sita = 0.5;//Alpha��BetaΪϵ��������Ŀ�꺯��ֵ��Sita����ϵ���ı���ٶ�
	public static double[][] Graph=new double[CustomerNumber + 10][CustomerNumber + 10];//��¼ͼ
	public static CustomerType[] customers=new CustomerType[CustomerNumber+10];//�洢�ͻ�����
	public static RouteType[] routes=new RouteType[CustomerNumber+10];//�洢��ǰ��·������
	public static RouteType[] Route_Ans=new RouteType[CustomerNumber+10];//�洢���Ž�·������
	
}
