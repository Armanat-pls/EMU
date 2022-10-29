import java.util.BitSet;
public class Tclass extends CMS {
    public static final int CELL = 32; //Размер ячейки в бит
    public static final int MEM = 256; // Количество ячеек памяти
    public static final int BMEM = 8; // Количество бит на адрес памяти
    public static final String VER = "1.8.0 Java upd";
    public static final boolean comands = initialiseCMS(); // Инициализация словаря команд
    public static int MEMzeros = (int)Math.ceil(Math.log10(MEM + 1));

    public static String show_bitset(BitSet data)   //функция вывода набора битов в строку
    {
        String S = "";
        for (int i = CELL - 1; i >= 0; i--)
        {
            if (i % 4 == 3)			
                S += " ";	
            if (data.get(i))
                S += "1";
            else
                S += "0";
        }    
        return S;
    }


    static class BRAIN{ //АЛУ

        private BitSet RO; //Аккумулятор, на одну ячейку
        
        public BRAIN(){ //конструктор
            RO = new BitSet(CELL);
        }

        public void write_RO(BitSet data){
            RO = (BitSet)data.clone();
        }

        BitSet get_RO(){         //получение регистра
            return (BitSet)RO.clone();
        }

        String showRO(){		//вывод регистра
            return show_bitset(RO);
        }

        void clearRO(){
            RO.clear();
        }
    }

    static class MEMORY{    //память

        private BitSet[] TABLE; //массив ячеек памяти

        public MEMORY(){    //конструктор
            TABLE = new BitSet[MEM];
            for (int i = 0; i < MEM; i++)
                TABLE[i] = new BitSet(CELL);
        }

        public void write_cell(int addr, BitSet data){
            TABLE[addr] = (BitSet)data.clone();
        }
        public BitSet get_cell(int addr){
            return (BitSet)TABLE[addr].clone();
        }
        public void zero(){
            for (int i = 0; i < MEM; i++)
                TABLE[i].clear();
        }
        public String show_cell(int addr){  
            return show_bitset(TABLE[addr]);
        }

        //обработка строки и ввод в память
        public boolean file_RAMfill(int addr, String line){
            BitSet writeline = new BitSet(CELL);
            boolean success = true;
            line = line.replace(" ", "");
            if (line.length() < CELL) //удлиннение строки при необходимости
            {
                String temp = "";
                for (int i = 0; i < CELL - line.length(); i++)
                    temp += "0";
                line = temp + line;
            }   
            for (int i = 0; i < CELL; i++)
            {
                if (line.charAt(i) == '0')
                    writeline.clear(CELL - 1 - i);
                else if (line.charAt(i) == '1')
                    writeline.set(CELL - 1 - i);
                else 
                {
                    writeline.clear();
                    success = false;
                }
            }
            if (success)
                write_cell(addr, writeline);
            return success;
        }
    }

    static class CONTROL{
        int CANT;
        BitSet RC;

        public CONTROL(){
            CANT = 0;
            RC = new BitSet(CELL);
            for(int i = 0; i < CELL; i++)
                RC.clear(i);  //явное выставление длинны CELL и заполнение нулями
        }
    }
}
