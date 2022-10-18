#ifndef CMS_H
#define CMS_H
using std::string;

//���������� ������
/*
����������� ������ 8 ��� - 256 �����
����������� ����� 32 ���
������ �������  <�������> [�����]
				0000 0000 0000 0000 0000 0000 [0000 0000]
������ ������ <����� �����> <31 ��� ��� ��������>
				0000 0000 0000 0000 0000 0000 0000 0000
1 ��� �����
<0> - 1 = ������������� ����� 	CELL-1


	� ����� ��������� ����������� �� ������ ������� 8 ��������
	� ��� ���� � ��� ���������
*/
namespace CMS // ���������� ������
{

	// 0000	0000 0000 0000 0000 0001 [0000 0000]  
	//������� ������ RO � ��, ��������� � RO 			|	RO=RO+��
	const short PLUS = 1;

	// 0000	0000 0000 0000 0000 0010 [0000 0000]
	//������� ������ RO � ��, ��������� � RO 			|	RO=RO-��
	const short MINUS = 2;

	// 0000	0000 0000 0000 0000 0011 [0000 0000]
	//�������� ������ RO � ��, ��������� � RO   		|	RO=RO*��
	const short MULT = 3;

	// 0000 0000 0000 0000 0000 0100 [0000 0000]  
	// ��������� ������ RO � ��, ��������� � RO  		|	RO=RO/��1 	
	const short DIVIS = 4;

	// 0000	0000 0000 0000 0000 1111 [0000 0000]  
	//������� ������ RO � ��, ��������� � RO 	FLOAT	|	RO=RO+��
	const short FPLUS = 11;

	// 0000	0000 0000 0000 0000 1100 [0000 0000]
	//������� ������ RO � ��, ��������� � RO 	FLOAT	|	RO=RO-��
	const short FMINUS = 12;

	// 0000	0000 0000 0000 0000 1101 [0000 0000]
	//�������� ������ RO � ��, ��������� � RO   FLOAT	|	RO=RO*��
	const short FMULT = 13;

	// 0000 0000 0000 0000 0000 1110 [0000 0000]  
	// ��������� ������ RO � ��, ��������� � RO  FLOAT	|	RO=RO/��1 	
	const short FDIVIS = 14;

	// 0000 0000 0000 0000 0001 0101 [0000 0000]  
	// ���������� �������� RO � ��, ��������� � RO  	|	RO= RO && ��
	const short AND = 21;

	// 0000 0000 0000 0000 0001 0110 [0000 0000]  
	// ���������� �������� RO ��� ��, ��������� � RO  	|	RO= RO || ��
	const short OR = 22;

	// 0000 0000 0000 0000 0001 0111 [0000 0000]  
	// ���������� �������� �� ��, ��������� � RO  		|	RO= !��
	const short NOT = 23;

	// 0000 0000 0000 1111 1111 1001 [0000 0000]
	// ��������� ������� ������ �� ������		 		| �� � CANT 
	const short JUMP = 4089;

	// 0000	0000 0000 1111 1111 1101 [0000 0000]
	// �������� ����� �� �� � ������� ��������� 		| �� � RO	
	const short LOAD = 4093;

	//0000 0000 0000 1111 1111 1110 [0000 0000]
	//�������� ����� �� �������� ��������� � �� 		| RO � ��	
	const short SAVE = 4094;

	// 0000 0000 0000 1111 1111 1111 [0000 0000]		| STOP
	// ���������� ����������
	const short STOP = 4095;

		short decoder(string line)
	{
		if (line == "PLUS")
			return PLUS;
		if (line == "MINUS")
			return MINUS;
		if (line == "MULT")
			return MULT;
		if (line == "DIVIS")
			return DIVIS;
		if (line == "FPLUS")
			return FPLUS;
		if (line == "FMINUS")
			return FMINUS;
		if (line == "FMULT")
			return FMULT;
		if (line == "FDIVIS")
			return FDIVIS;
		if (line == "AND")
			return AND;
		if (line == "OR")
			return OR;
		if (line == "NOT")
			return NOT;
		if (line == "JUMP")
			return JUMP;
		if (line == "LOAD")
			return LOAD;
		if (line == "SAVE")
			return SAVE;
		if (line == "STOP")
			return STOP;
		
		return 0;
	}

}
#endif