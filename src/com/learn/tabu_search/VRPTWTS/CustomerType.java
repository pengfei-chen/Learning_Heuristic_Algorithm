package VRPTW;

public class CustomerType {
	int Number;//�ڵ�������
    int R;//�ڵ���������·�����
    double X, Y;//�ڵ��������
    double Begin, End, Service;//�ڵ㱻���ʵ�����ʱ�䣬����ʱ���Լ�����ʱ��
    double Demand;//�ڵ����������
    
    public CustomerType() {
    	this.Number=0;
    	this.R=0;
    	this.Begin =0;
    	this.End=0;
    	this.Service=0;
    	this.X=0;
    	this.Y=0;
    	this.Demand=0;
    }
    
    public CustomerType(CustomerType c1) {
    	this.Number=c1.Number;
    	this.R=c1.R;
    	this.Begin =c1.Begin;
    	this.End=c1.End;
    	this.Service=c1.Service;
    	this.X=c1.X;
    	this.Y=c1.Y;
    	this.Demand=c1.Demand;
    }
}
