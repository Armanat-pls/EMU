import java.util.BitSet;
public class secondary extends Tclass {

    BRAIN ALU = new BRAIN();
    MEMORY RAM = new MEMORY();
    CONTROL UU = new CONTROL();

 
    // перевод двоичной в десятичную целое
    public static int bit_to_int(BitSet data) 
    {
        if (data.get(CELL - 1))
        {
            data.flip(0, CELL); //получение дополнительного кода
            return -(int)data.toLongArray()[0] - 1;
        }
        else
            return (int)data.toLongArray()[0];   
    }
    
    // перевод десятичной в двоичную целое
    public static BitSet int_to_bit(int data) 
    {
        BitSet imp = new BitSet(CELL);
        if (data < 0) //ситуация отрицательности
            imp.set(CELL - 1);
        for (int i = CELL-2; i>=0; --i)
        {
            if ((int)((data>>i)&1) == 1)
                imp.set(i);
            else
                imp.clear(i);
        }
        return imp;
    }

    //перевод float в двоичный вид
	public static BitSet float_to_bit(float data)
	{
		//при попытке перевода чисел без дробной части, кодировка упоротая, но правильная
		BitSet imp = new BitSet(CELL);

        if (data > 0) //запоминание знака числа + = 0; - = 1
            imp.clear(CELL - 1);
        else
            imp.set(CELL - 1);

		data = Math.abs(data);
		int ex = 0;	//порядок
		if (data >= 1.0)	//при числах >1 порядок растёт
		{
			while (data > 2.0)
			{
				data /= 2.0;
				ex++;
			}
		}
		else	//при числах <1 порядок убывает
		{
			while (data < 1.0)
			{
				data *= 2.0;
				ex--;
			}
		}

		ex += 127; //смещение порядка на 127, для удобного хранения отрицательных

        BitSet tempEx = new BitSet(CELL);
		tempEx = int_to_bit(ex); //временный набор для хранения двоичного порядка
		for (int i = 0; i < 8; i++)
            if (tempEx.get(i))
                imp.set(CELL - 9 + i);
		//  imp[CELL - 9 + i] = tempEx[i];	//запись порядка

		data -= (float)1.0; // от нормализованной мантисы, отсекаем целую единицу
		float tmp; //временное хранилище удвоенной мантисы
		for (int i = 0; i < 23; i++)
		{
			tmp = data * (float)2.0;
			if (tmp > 1.0)
			{
                imp.set(CELL - 10 - i);
				//imp[CELL - 10 - i] = 1;
				data = (tmp - (float)1.0);
			}
			else
			{
                imp.clear(CELL - 10 - i);
				//imp[CELL - 10 - i] = 0;
				data = tmp;
			}
		}
		return imp;
	}

    //перевод двоичного вида во float
    public static float bit_to_float(BitSet data)
	{
		boolean sign_neagative = (data.get(CELL - 1)); //false - положительное ; true - отрицательное
        BitSet tempEx = new BitSet(CELL);
		for (int i = 0; i < 8; i++)
            if (data.get(CELL - 9 + i))
			    tempEx.set(i);

		int ex = bit_to_int(tempEx) - 127; //получение десятичного порядка и смещение
		float imp = (float)1.0;
        int bool_to_int = 0;
		for (int i = 0; i < 23; i++)
        {
            bool_to_int = data.get(CELL - 10 - i) ? 1 : 0;
            imp += bool_to_int * Math.pow(2, -i - 1);
        }
		imp = (float)Math.pow(2.0, ex) * imp;
		if (sign_neagative) imp *= -1.0; //сделать отрицательным при необходимости

		return imp;
	}


    // склеивание двух int в двоичный код
    public static BitSet make_one(int com, int addr)
    {
        BitSet imp = new BitSet(CELL); // переменная для результата
        BitSet B_com = new BitSet(CELL);
        B_com = int_to_bit(com); //двоичный код команды
        BitSet B_addr = new BitSet(CELL);
        B_addr = int_to_bit(addr); //двоичный код адреса ячейки
        for (int i = 0; i < BMEM;i++) //склейка
            if (B_addr.get(i))
                imp.set(i);
            //imp[i] = B_addr[i];
        for (int i = BMEM; i < CELL; i++)
            if (B_com.get(i - BMEM))
                imp.set(i);
            //imp[i] = B_com[i-BMEM];
        return imp;
    }
 
    public static BitSet[] cut_com(BitSet data)
    {
        //        BitSet[] coms = cut_com(bastard) вызов деления
        //        coms[0].toLongArray()[0]  //операция
        //        coms[1].toLongArray()[0]  // адрес
        
        
        BitSet[] imp;   //массив с двумя кусками команд
        imp = new BitSet[2];
        imp[0] = new BitSet(CELL);
        imp[1] = new BitSet(CELL);

        //0 - BMEM
        for (int i = 0; i < BMEM; i++)
            if (data.get(i))
                imp[1].set(i);
            //A[i] = data[i];
        //BMEM - CELL 
        for (int i = BMEM; i < CELL; i++)
            if (data.get(i))
                imp[0].set(i - BMEM);
            //C[i-BMEM] = data[i];
        return imp;
    }

}
