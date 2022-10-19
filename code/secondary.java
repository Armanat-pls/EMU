import java.util.BitSet;
public class secondary extends Tclass {

    public static BRAIN ALU = new BRAIN();
    public static MEMORY RAM = new MEMORY();
    public static CONTROL UU = new CONTROL();
 
    // перевод двоичной в десятичную целое
    public static int bit_to_int(BitSet data) 
    {
        if(data.isEmpty())
            return -1;
        if (data.get(CELL - 1))
        {
            data.flip(0, CELL); //получение дополнительного кода
            if (data.isEmpty())
                return 0 - 1;
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
        if (data.isEmpty())
            return (float)0.0;
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

        if (data.isEmpty())
        {
            return imp;
        }

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


    protected static int compute(){
        BitSet[] coms = new BitSet[2];
        int C;
        int A;
        coms = cut_com(UU.RC);  //вызов деления
        if (coms[0].isEmpty())
            C = 0;
        else
            C = (int)coms[0].toLongArray()[0];
        if (coms[1].isEmpty())
            A = 0;
        else
            A = (int)coms[1].toLongArray()[0];
        if (C == CMS.STOP)
            return 666;
        int op1, op2, res;
        float fop1, fop2, fres;
        switch (C)
        {
        case CMS.JUMP:
            UU.CANT = A;
            return 101;//Выход из выполнения без дополнительного повышения счётчика команд
        case CMS.LOAD:
            ALU.write_RO(RAM.get_cell(A));//запись значения ячейки А в аккумулятор
            break;
        case CMS.SAVE:
            RAM.write_cell(A, ALU.get_RO());//запись значения аккумуляторa в ячейку A
            break;
        case CMS.AND:
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            //res = (op1 && op2);
            res = 0;
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.OR:
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            //res = (op1 || op2);
            res = 0;
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.NOT:
            op2 = bit_to_int(RAM.get_cell(A));
            //res = (!op2);
            res = 0;
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.PLUS:
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = (op1 + op2);
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.MINUS:
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = (op1 - op2);
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.MULT:
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = (op1 * op2);
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.DIVIS:
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            if (op2 == 0)
            {
                res = 0;
                ALU.write_RO(int_to_bit(res));
                break;
            }
            try
            {
                res = (op1 / op2);
            }
            catch (Throwable t)
            {
                res = 0;
            }
            ALU.write_RO(int_to_bit(res));
            break;
        case CMS.FPLUS:
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            fres = (fop1 + fop2);
            ALU.write_RO(float_to_bit(fres));
            break;
        case CMS.FMINUS:
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            fres = (fop1 - fop2);
            ALU.write_RO(float_to_bit(fres));
            break;
        case CMS.FMULT:
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            fres = (fop1 * fop2);
            ALU.write_RO(float_to_bit(fres));
            break;
        case CMS.FDIVIS:
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            if (fop2 == 0.0)
            {
                fres = (float)0.0;
                ALU.write_RO(float_to_bit(fres));
                break;
            }
            try
            {
                fres = (fop1 / fop2);
            }
            catch (Throwable t)
            {
                fres = (float)0.0;
            }
            ALU.write_RO(float_to_bit(fres));
            break;
        
        }

        UU.CANT++; //повышение счётчика команд после выполнения
        return 0;
    }

}
