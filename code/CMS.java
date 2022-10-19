public class CMS {

	//Справочник команд
	/*
	Разрядность памяти 8 бит - 256 ячеек
	Разрядность ячеек 32 бит
	ячейка команды  <команда> [адрес]
					0000 0000 0000 0000 0000 0000 [0000 0000]
	ячейка данных <метка знака> <31 бит под значение>
					0000 0000 0000 0000 0000 0000 0000 0000
	1 бит флага
	<0> - 1 = отрицательное число 	CELL-1


		У ФОРМЫ ПРОПИСАНО ОГРАНИЧЕНИЕ НА ДЛИННУ КОМАНДЫ 8 СИМВОЛОВ
		И ДЛЯ КОДА И ДЛЯ МНЕМОНИКИ
	*/

	// 0000	0000 0000 0000 0000 0001 [0000 0000]  
	//сложить ячейки RO и оп, сохранить в RO 			|	RO=RO+оп
	final static int PLUS = 1;

	// 0000	0000 0000 0000 0000 0010 [0000 0000]
	//вычесть ячейки RO и оп, сохранить в RO 			|	RO=RO-оп
	final static int MINUS = 2;

	// 0000	0000 0000 0000 0000 0011 [0000 0000]
	//умножить ячейки RO и оп, сохранить в RO   		|	RO=RO*оп
	final static int MULT = 3;

	// 0000 0000 0000 0000 0000 0100 [0000 0000]  
	// разделить ячейки RO и оп, сохранить в RO  		|	RO=RO/оп1 	
	final static int DIVIS = 4;

	// 0000	0000 0000 0000 0000 1111 [0000 0000]  
	//сложить ячейки RO и оп, сохранить в RO 	FLOAT	|	RO=RO+оп
	final static int FPLUS = 11;

	// 0000	0000 0000 0000 0000 1100 [0000 0000]
	//вычесть ячейки RO и оп, сохранить в RO 	FLOAT	|	RO=RO-оп
	final static int FMINUS = 12;

	// 0000	0000 0000 0000 0000 1101 [0000 0000]
	//умножить ячейки RO и оп, сохранить в RO   FLOAT	|	RO=RO*оп
	final static int FMULT = 13;

	// 0000 0000 0000 0000 0000 1110 [0000 0000]  
	// разделить ячейки RO и оп, сохранить в RO  FLOAT	|	RO=RO/оп1 	
	final static int FDIVIS = 14;

	// 0000 0000 0000 0000 0001 0101 [0000 0000]  
	// логическая операция RO И оп, сохранить в RO  	|	RO= RO && оп
	final static int AND = 21;

	// 0000 0000 0000 0000 0001 0110 [0000 0000]  
	// логическая операция RO ИЛИ оп, сохранить в RO  	|	RO= RO || оп
	final static int OR = 22;

	// 0000 0000 0000 0000 0001 0111 [0000 0000]  
	// логическая операция НЕ оп, сохранить в RO  		|	RO= !оп
	final static int NOT = 23;

	// 0000 0000 0000 1111 1111 1001 [0000 0000]
	// перевести счётчик команд на ячейку		 		| оп в CANT 
	final static int JUMP = 4089;

	// 0000	0000 0000 1111 1111 1101 [0000 0000]
	// записать адрес из оп в регистр операндов 		| оп в RO	
	final static int LOAD = 4093;

	//0000 0000 0000 1111 1111 1110 [0000 0000]
	//записать адрес из регистра операндов в оп 		| RO в оп	
	final static int SAVE = 4094;

	// 0000 0000 0000 1111 1111 1111 [0000 0000]		| STOP
	// прекратить выполнение
	final static int STOP = 4095;


    
    public static int decoder(String line)
	{
		if (line.equals("PLUS"))
			return PLUS;
		if (line.equals("MINUS"))
			return MINUS;
		if (line.equals("MULT"))
			return MULT;
		if (line.equals("DIVIS"))
			return DIVIS;
		if (line.equals("FPLUS"))
			return FPLUS;
		if (line.equals("FMINUS") )
			return FMINUS;
		if (line.equals("FMULT") )
			return FMULT;
		if (line.equals("FDIVIS"))
			return FDIVIS;
		if (line.equals("AND"))
			return AND;
		if (line.equals("OR"))
			return OR;
		if (line.equals("NOT"))
			return NOT;
		if (line.equals("JUMP"))
			return JUMP;
		if (line.equals("LOAD"))
			return LOAD;
		if (line.equals("SAVE"))
			return SAVE;
		if (line.equals("STOP"))
			return STOP;
		return 0;
	}
}
