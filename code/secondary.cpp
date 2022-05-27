#include "Tclass.h"
#include "CMS.h"
#include <Windows.h>
#include <cmath>
using std::cout;
using std::cin;
using std::ifstream;
using std::ofstream;

// ������� �������� � ����������
int bin_to_int(bitset<CELL> data) 
{
	bitset<CELL-1> imp;
	for (int i=0; i<CELL-1;i++)
			imp[i]=data[i];
	if(data[CELL-1]==0)
		return imp.to_ulong();
	else
	{   //��������� ��������������� ����
		imp.flip();
		return -imp.to_ulong()-1;
	}	
}

// ������� ���������� � ��������
bitset<CELL> int_to_bit(int data) 
{
	bitset<CELL> imp;
	if (data < 0) //�������� ���������������
		imp[CELL-1] = 1;
	for (int i = CELL-2; i>=0; --i)
		imp[i]=(int)((data>>i)&1);
	return imp;
}

// ���������� ���� int � �������� ���
bitset<CELL> make_one(int com, int addr) 
{
	bitset<CELL> imp; // ���������� ��� ����������
	bitset<CELL> B_com = int_to_bit(com); //�������� ��� �������
	bitset<CELL> B_addr = int_to_bit(addr); //�������� ��� ������ ������
	for (int i = 0; i < BMEM;i++) //�������
		imp[i] = B_addr[i];
	for (int i = BMEM; i < CELL; i++)
		imp[i] = B_com[i-BMEM];
	return imp;
}

//������������
BRAIN::BRAIN()
{
	RO=0;
}

MEMORY::MEMORY()
{
	for (int i = 0; i < MEM; i++)
		for (int j = 0; j < CELL; j++)
			TABLE[i][j]=0;
		
}

CONTROL::CONTROL()
{
	CANT = 0;
	for (int i = 0;i<CELL ;i++)
		RC[i] = 0;
}

void BRAIN::write_RO(bitset<CELL> data)
{
	RO=data;
}
bitset<CELL> BRAIN::get_RO()
{
	return RO;
}

void BRAIN::showRO()
{
	cout<<"[RO]: ";
	for (int j = 0; j < CELL; j++)
	{
		cout << RO[CELL-1-j];
		if (j % 4 == 3)			
			cout <<" ";			
	}
	cout <<"\n";
}

void MEMORY::zero()
{
	for (int i = 0; i < MEM; i++)
		for (int j = 0; j < CELL; j++)
			TABLE[i][j]=0;
}

void MEMORY::write_cell(short addr, bitset<CELL> data)
{
	TABLE[addr]=data;
}

bitset<CELL> MEMORY::get_cell(short addr)
{
	return TABLE[addr];
}

//���������� RAM �� �����
void MEMORY::fill_ALL()
{
	ifstream fin("data\\memory.txt");
		if (!fin) cout << "File failure\n";
//	bool tmp=0;
	for (int i = 0; i < MEM; i++)
		fin>>TABLE[i];	//������ ����	
	fin.close();
}
//����� RAM � ����
void MEMORY::dump_ALL()
{
	ofstream fout("data\\memoryDUMP_bin.txt");
		if (!fout) cout << "File failure\n";
	for (int i = 0; i < MEM; i++)
		fout<<TABLE[i]<<"\n";	//������ �����
	fout.close();
}

//����� �� ����� RAM
void MEMORY::show()
{
	cout<<"\t\t\t============��� ������============\n";
	for (int i = 0; i < MEM; i++)
	{
		if ((i % 20 == 1) && (i > 1))
			system("pause");
		if (i < 10)		
			cout<<"[00"<<i<<"]: ";
		else if (i < 100)
			cout<<"[0"<<i<<"]: ";
		else if (i < 1000)
			cout<<"["<<i<<"]: ";
		for (int j = 0; j < CELL; j++)
		{
			cout << TABLE[i][CELL-1-j];
			if (j % 4 == 3)			
				cout <<" ";				
		}
		cout <<"\n";
	}
}

void MEMORY::show_cell(short addr)
{
	cout<<"["<<addr<<"]: ";
	for (int j = 0; j < CELL; j++)
	{
		cout << TABLE[addr][CELL-1-j];
		if (j % 4 == 3)			
			cout <<" ";
	}
	cout <<"\n";
}

//������� ������� �� ������ 
void CONTROL::cut_com(bitset<CELL-BMEM> &C, bitset<BMEM> &A)
{
	//0 - BMEM
	for (int i = 0; i < BMEM; i++)
		A[i] = RC[i];
	//BMEM - CELL 
	for (int i = BMEM; i < CELL; i++)
		C[i-BMEM] = RC[i];
}

void CPU::work()
{	
	cout<<"================================================================\n\n";
	cout<<"\t�������� ��� "<<VER<<"\n\n";
	cout<<"����������� - "<<CELL<<" ���\n";
	cout<<"����������� ������ - "<<BMEM<<" ��� -> "<<MEM<<" �����\n\n";
	cout<<"================================================================\n\n";
	short menu = 0; //���������� ��� ���������� ����
	short menu2 = 0; // ���������� ��� ������ ��������� � ����������� ������ ��� ����� ������
	short tmp = 0; //���������� ��� ������ �������� �������� ��� ����� ��� ������ ������ ���������
	do
	{
		cout<<"\n������� ������ ["<<UU.CANT<<"]\n";
		RAM.show_cell(UU.CANT);
		cout<<"\n";
		cout<<"================================================================\n";
		cout<<"\t\t �������� ��������\n\n";
		cout<<"\t01 - ��������� ������\n";
		cout<<"\t02 - ���������� ������ ������\n";
		cout<<"\t03 - �������� � ������������ ������\n";
		cout<<"\t04 - ����������� ������������ ������\n";
		cout<<"\t05 - ��������� ������ �� �����\n";
		cout<<"\t06 - ������� ��� ������\n";
		cout<<"\t07 - ������� ������� ���\n";
		cout<<"\t08 - �������� ������ � ����\n";
		cout<<"\t09 - �������������� ������ � ���� �����\n";
		cout<<"\t10 - �������� ������\n";
		cout<<"\t00 - EXIT\n\n";
		cout<<"================================================================\n";
		cout<<"����� �������� : ";
		cin>>menu;
		switch (menu)
		{
		case 1:	// ��������� ����������
			cout<<"\n��������� ����������\n";
			UU.RC=RAM.get_cell(UU.CANT);
			if(compute() == 666)
				cout<<"��������� ������� ����������\n";
			Sleep(250);
			break;
		case 2:	// ��������� ������� ������
			cout<<"\n��������� �������� ������\n";
			cout<<"������� ����� ������ ������\n";
			cin>>tmp;
			UU.CANT=tmp; 
			break;
		case 3:	//��� ������� ������ ������
			cout<<"\n���� ������ � ������\n\n";
			cout<<"0 - ������ ������ ������� � �������� �������;\n";
			cout<<"1 - ������ ����� � ���������� �������;\n";
			cout<<"2 - ������ ������� � ������� � ���������� �������\n";
			cin>>menu2;
			if (menu2 == 0)
			{
				bitset<CELL> tmp; // ���������� ��� ������ ������
				short ad = 0; // ���������� ��� ������ ������ ������
				cout<<"������� ����� ������ (������� ������: "<<UU.CANT<<")\n";
				cin>>ad;
				cout<<"������� 32 ���� ��� ��������\n";
				cin>>tmp;
				RAM.write_cell(ad,tmp);
			}
			else if (menu2 == 1)
			{
				int tmp = 0; 	// ���������� ��� ������ ������
				short ad = 0; // ���������� ��� ������ ������ ������
				cout<<"������� ����� ������ (������� ������: "<<UU.CANT<<")\n";
				cin>>ad;
				cout<<"������� �����\n";
				cin>>tmp;
				RAM.write_cell(ad,int_to_bit(tmp));				
			}
			else if (menu2 == 2)
			{
				int tmp = 0, oper = 0; 	// ���������� ��� ������� � ��������
				short ad = 0; // ���������� ��� ������ ������ ������
				cout<<"������� ����� ������ (������� ������: "<<UU.CANT<<")\n";
				cin>>ad;
				cout<<"������� ����� ������� � ����� ��������\n";
				cin>>tmp>>oper;
				RAM.write_cell(ad, make_one(tmp,oper));
			}
			break;
		case 4:	// ��������� �� ������
			cout<<"\n�������� ������������ ������\n";
			cout<<"������� ����� ������ ������ ��� ���������\n";
			cin>>tmp;
			RAM.show_cell(tmp);
			cout<<"\n";
			system("pause");
			break;
		case 5: // ���������� ������ �� �����
			cout<<"\n���������� ������ �� �����\n";
			RAM.fill_ALL();
			cout<<"\n������ ������� �� �����\n";
			Sleep(1000);
			break;
		case 6:	// ������ �����
			cout<<"\n������ �����\n";
			RAM.show();
			Sleep(1000);
			break;
		case 7:	// ����� �������� ���
			cout<<"\n����� �������� ���\n";
			ALU.showRO();
			Sleep(1000);
			break;
		case 8:	// ���� � ����
			cout<<"\n������ ������ � ����\n";
			RAM.dump_ALL();
			cout<<"\n������ ��������� � ����\n";
			Sleep(1000);
			break;
		case 9: // �������������� ����� �� �����
			cout<<"\n���������������� ����������\n";
			while(1)
			{
				UU.RC=RAM.get_cell(UU.CANT);
				if(compute() == 666)
				{
					cout<<"��������� ������� ����������\n";
					Sleep(1000);
					break;
				}
				if (UU.CANT == MEM)
				{
					UU.CANT--;
					break;
				}
			}	
			break;
		case 10:
			cout<<"\n������� ������\n";
			RAM.zero();
			cout<<"������ ��������\n";
			Sleep(1000);
			break;
		default:	//���������� ������
			menu = 666;
			break;
		}
	} while (menu != 666);
	cout<<"������ ��������� \n";
}

short CPU::compute()
{	
	bitset<CELL-BMEM> C;	//����� ��� �������
	bitset<BMEM> A;		//����� ��� ������
	UU.cut_com(C, A);
	if (C.to_ulong() == CMS::STOP)
		return 666;
	int op1, op2, res;
	op1 = bin_to_int(ALU.get_RO());
	op2 = bin_to_int(RAM.get_cell(A.to_ulong()));
	switch (C.to_ulong())
	{
	case CMS::JUMP:
		UU.CANT=A.to_ulong();
		return 101;//����� �� ���������� ��� ��������������� ��������� �������� ������
	case CMS::LOAD:
		ALU.write_RO(RAM.get_cell(A.to_ulong()));//������ �������� ������ � � �����������
		break;
	case CMS::SAVE:
		RAM.write_cell(A.to_ulong(),ALU.get_RO());//������ �������� �����������a � ������ A
		break;
	case CMS::AND:
		res = (op1 && op2);
		ALU.write_RO(int_to_bit(res));
	break;
	case CMS::OR:
		res = (op1 || op2);
		ALU.write_RO(int_to_bit(res));
	break;
	case CMS::NOT:
		res = (!op2);
		ALU.write_RO(int_to_bit(res));
	break;
	case CMS::PLUS:
		res = (op1 + op2);
		ALU.write_RO(int_to_bit(res));
		break;
	case CMS::MINUS:
		res = (op1 - op2);
		ALU.write_RO(int_to_bit(res));
		break;
	case CMS::MULT:
		res = (op1 * op2);
		ALU.write_RO(int_to_bit(res));
		break;
	case CMS::DIVIS:
		if (op2 == 0)
		{
			res = 0;
			ALU.write_RO(int_to_bit(res));
			break;
		}	
		res = (op1 / op2);
		ALU.write_RO(int_to_bit(res));
		break;
	}
	UU.CANT++;//��������� �������� ������ ����� ����������
	return 0;
}
