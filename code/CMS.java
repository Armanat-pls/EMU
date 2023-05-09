import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

	//словарь мнемоник и команд НЕИЗМЕНЯЕМЫЙ ИЗВНЕ
	public static Map<String, Integer> CMSmap;
	public static boolean initialiseCMS(){
		CMSmap = new HashMap<String, Integer>();

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

		//логическая операция НЕ RO, сохранить в RO 
		CMSmap.put("NOT", 23);

		//логическая операция МЕНЬШЕ оп, сохранить в RO 
		CMSmap.put("LESS", 31);

		//логическая операция МЕНЬШЕ ИЛИ РАВНО оп, сохранить в RO 
		CMSmap.put("LESSOE", 32);

		//логическая операция МЕНЬШЕ оп, сохранить в RO 	FLOAT
		CMSmap.put("FLESS", 41);

		//логическая операция МЕНЬШЕ ИЛИ РАВНО оп, сохранить в RO 	FLOAT
		CMSmap.put("FLESSOE", 42);

		//перевести счётчик команд на ячейку
		CMSmap.put("JUMP", 4089);

		//перевести счётчик команд на ячейку (ЕСЛИ РЕГИСТР НЕ 0)
		CMSmap.put("IFJUMP", 4090);
		
		//записать адрес из оп в регистр операндов 
		CMSmap.put("LOAD", 4093);

		//записать адрес из регистра операндов в оп 
		CMSmap.put("SAVE", 4094);

		//прекратить выполнение
		CMSmap.put("STOP", 4095);

		//записывается в АЛУ в ошибках
		CMSmap.put("ERROR", 606060);

		//блокировка изменений
		CMSmap = Collections.unmodifiableMap(CMSmap);
		return true;
	}

	public static int decoder(String line)
	{
		if (CMSmap.get(line) != null)
			return CMSmap.get(line);
		else
			return 0;
	}

	public static String decoder(int com)
	{	
		Iterator<Map.Entry<String, Integer>> it = CMSmap.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Integer> pair = it.next();
			if (pair.getValue() == com)
				return pair.getKey();
		}
		return "";
	}	
}


