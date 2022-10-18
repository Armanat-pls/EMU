public class CMS {

    public static final int CELL = 32; //–азмер €чейки в бит
    public static final int MEM = 256; //  оличество €чеек пам€ти
    public static final int BMEM = 8; //  оличество бит на адрес пам€ти
    public static final String VER = "1.8.0 Java upd";


	//—правочник команд
	/*
	–азр€дность пам€ти 8 бит - 256 €чеек
	–азр€дность €чеек 32 бит
	€чейка команды  <команда> [адрес]
					0000 0000 0000 0000 0000 0000 [0000 0000]
	€чейка данных <метка знака> <31 бит под значение>
					0000 0000 0000 0000 0000 0000 0000 0000
	1 бит флага
	<0> - 1 = отрицательное число 	CELL-1


		” ‘ќ–ћџ ѕ–ќѕ»—јЌќ ќ√–јЌ»„≈Ќ»≈ Ќј ƒЋ»ЌЌ”  ќћјЌƒџ 8 —»ћ¬ќЋќ¬
		» ƒЋя  ќƒј » ƒЋя ћЌ≈ћќЌ» »
	*/

	// 0000	0000 0000 0000 0000 0001 [0000 0000]  
	//сложить €чейки RO и оп, сохранить в RO 			|	RO=RO+оп
	final static int PLUS = 1;

	// 0000	0000 0000 0000 0000 0010 [0000 0000]
	//вычесть €чейки RO и оп, сохранить в RO 			|	RO=RO-оп
	final static int MINUS = 2;

	// 0000	0000 0000 0000 0000 0011 [0000 0000]
	//умножить €чейки RO и оп, сохранить в RO   		|	RO=RO*оп
	final static int MULT = 3;

	// 0000 0000 0000 0000 0000 0100 [0000 0000]  
	// разделить €чейки RO и оп, сохранить в RO  		|	RO=RO/оп1 	
	final static int DIVIS = 4;

	// 0000	0000 0000 0000 0000 1111 [0000 0000]  
	//сложить €чейки RO и оп, сохранить в RO 	FLOAT	|	RO=RO+оп
	final static int FPLUS = 11;

	// 0000	0000 0000 0000 0000 1100 [0000 0000]
	//вычесть €чейки RO и оп, сохранить в RO 	FLOAT	|	RO=RO-оп
	final static int FMINUS = 12;

	// 0000	0000 0000 0000 0000 1101 [0000 0000]
	//умножить €чейки RO и оп, сохранить в RO   FLOAT	|	RO=RO*оп
	final static int FMULT = 13;

	// 0000 0000 0000 0000 0000 1110 [0000 0000]  
	// разделить €чейки RO и оп, сохранить в RO  FLOAT	|	RO=RO/оп1 	
	final static int FDIVIS = 14;

	// 0000 0000 0000 0000 0001 0101 [0000 0000]  
	// логическа€ операци€ RO » оп, сохранить в RO  	|	RO= RO && оп
	final static int AND = 21;

	// 0000 0000 0000 0000 0001 0110 [0000 0000]  
	// логическа€ операци€ RO »Ћ» оп, сохранить в RO  	|	RO= RO || оп
	final static int OR = 22;

	// 0000 0000 0000 0000 0001 0111 [0000 0000]  
	// логическа€ операци€ Ќ≈ оп, сохранить в RO  		|	RO= !оп
	final static int NOT = 23;

	// 0000 0000 0000 1111 1111 1001 [0000 0000]
	// перевести счЄтчик команд на €чейку		 		| оп в CANT 
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
