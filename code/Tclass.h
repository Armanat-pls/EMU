#ifndef CL_H
#define CL_H
#include <iostream>
#include <fstream>
#include <bitset>
#define CELL 32	//������ ������ � ���
#define MEM 256 // ���������� ����� ������
#define BMEM 8	// ���������� ��� �� ����� ������
#define VER "1.5.3"
using std::bitset;

class BRAIN
{	
	bitset<CELL> RO; //�����������, �� ���� ������
	public:
		BRAIN();		//�����������
		void write_RO(bitset<CELL> data);
		bitset<CELL> get_RO();
		void showRO();		//����� ��������
};

class MEMORY
{	
	private:
		bitset<CELL> TABLE[MEM];	// ������ ������ [����� ������][������] bitset ����������� ������ ������, ��������� ��������� ��� ������ � ���������
	public:
		MEMORY();				//�����������

		void write_cell();								//������ ������ ������ �������
		void write_cell(short addr, bitset<CELL> data); //������ ������ ������
		bitset<CELL> get_cell(short addr);				//�������� ������ �� ������

		
		void fill_ALL();	//������� RAM �� �����
		void show();	//����� ����(��� ����) RAM �� �����
		void show_cell(short addr);//����� ����� ������
		void dump_ALL();	//����� ���� RAM � ����
		void zero(); 	// ��������� ������

		
		
};
class CONTROL
{
	public:
		short CANT;			// �ר���� ������ ������
		bitset<CELL> RC;	// ������� �������� ������
		CONTROL();			//�����������

		//��������� ������� �� �������� ������
		void cut_com(bitset<CELL-BMEM> &C, bitset<BMEM> &A);

};

class CPU
{
	private:
		BRAIN ALU;
		MEMORY RAM;
		CONTROL UU;	
		short compute(); //����� ����������� �������
	public:
		void work();//�������� ����
		
	
};	
#endif
