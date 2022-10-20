import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
	*/

	//словарь мнемоник и команд
	public static Map<String, Integer> CMSmap = new HashMap<String, Integer>();
	public CMS(){

		//сложить ячейки RO и оп, сохранить в RO 
		CMSmap.put("PLUS", 1);

		//вычесть ячейки RO и оп, сохранить в RO
		CMSmap.put("MINUS", 2);

		//умножить ячейки RO и оп, сохранить в RO 
		CMSmap.put("MULT", 3);

		//разделить ячейки RO и оп, сохранить в RO 
		CMSmap.put("DIVIS", 4);

		//сложить ячейки RO и оп, сохранить в RO	FLOAT
		CMSmap.put("FPLUS", 11);

		//вычесть ячейки RO и оп, сохранить в RO	FLOAT
		CMSmap.put("FMINUS", 12);

		//умножить ячейки RO и оп, сохранить в RO	FLOAT
		CMSmap.put("FMULT", 13);

		//разделить ячейки RO и оп, сохранить в RO	FLOAT 
		CMSmap.put("FDIVIS", 14);

		//логическая операция RO И оп, сохранить в RO 
		CMSmap.put("AND", 21);

		//логическая операция RO ИЛИ оп, сохранить в RO 
		CMSmap.put("OR", 22);

		//логическая операция НЕ оп, сохранить в RO 
		CMSmap.put("NOT", 23);

		//перевести счётчик команд на ячейку
		CMSmap.put("JUMP", 4089);
		
		//записать адрес из оп в регистр операндов 
		CMSmap.put("LOAD", 4093);

		//записать адрес из регистра операндов в оп 
		CMSmap.put("SAVE", 4094);

		//прекратить выполнение
		CMSmap.put("STOP", 4095);

		//блокировка изменений
		CMSmap = Collections.unmodifiableMap(CMSmap);
	}




    /*/
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
	*/
}
