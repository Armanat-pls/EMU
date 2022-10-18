public class CMS {

    public static final int CELL = 32; //������ ������ � ���
    public static final int MEM = 256; // ���������� ����� ������
    public static final int BMEM = 8; // ���������� ��� �� ����� ������
    public static final String VER = "1.8.0 Java upd";


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

	// 0000	0000 0000 0000 0000 0001 [0000 0000]  
	//������� ������ RO � ��, ��������� � RO 			|	RO=RO+��
	final static int PLUS = 1;

	// 0000	0000 0000 0000 0000 0010 [0000 0000]
	//������� ������ RO � ��, ��������� � RO 			|	RO=RO-��
	final static int MINUS = 2;

	// 0000	0000 0000 0000 0000 0011 [0000 0000]
	//�������� ������ RO � ��, ��������� � RO   		|	RO=RO*��
	final static int MULT = 3;

	// 0000 0000 0000 0000 0000 0100 [0000 0000]  
	// ��������� ������ RO � ��, ��������� � RO  		|	RO=RO/��1 	
	final static int DIVIS = 4;

	// 0000	0000 0000 0000 0000 1111 [0000 0000]  
	//������� ������ RO � ��, ��������� � RO 	FLOAT	|	RO=RO+��
	final static int FPLUS = 11;

	// 0000	0000 0000 0000 0000 1100 [0000 0000]
	//������� ������ RO � ��, ��������� � RO 	FLOAT	|	RO=RO-��
	final static int FMINUS = 12;

	// 0000	0000 0000 0000 0000 1101 [0000 0000]
	//�������� ������ RO � ��, ��������� � RO   FLOAT	|	RO=RO*��
	final static int FMULT = 13;

	// 0000 0000 0000 0000 0000 1110 [0000 0000]  
	// ��������� ������ RO � ��, ��������� � RO  FLOAT	|	RO=RO/��1 	
	final static int FDIVIS = 14;

	// 0000 0000 0000 0000 0001 0101 [0000 0000]  
	// ���������� �������� RO � ��, ��������� � RO  	|	RO= RO && ��
	final static int AND = 21;

	// 0000 0000 0000 0000 0001 0110 [0000 0000]  
	// ���������� �������� RO ��� ��, ��������� � RO  	|	RO= RO || ��
	final static int OR = 22;

	// 0000 0000 0000 0000 0001 0111 [0000 0000]  
	// ���������� �������� �� ��, ��������� � RO  		|	RO= !��
	final static int NOT = 23;

	// 0000 0000 0000 1111 1111 1001 [0000 0000]
	// ��������� ������� ������ �� ������		 		| �� � CANT 
	final static int JUMP = 4089;

	// 0000	0000 0000 1111 1111 1101 [0000 0000]
	// �������� ����� �� �� � ������� ��������� 		| �� � RO	
	final static int LOAD = 4093;

	//0000 0000 0000 1111 1111 1110 [0000 0000]
	//�������� ����� �� �������� ��������� � �� 		| RO � ��	
	final static int SAVE = 4094;

	// 0000 0000 0000 1111 1111 1111 [0000 0000]		| STOP
	// ���������� ����������
	final static int STOP = 4095;


    
    public static int decoder(String line)
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
