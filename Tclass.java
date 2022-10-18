import java.util.BitSet;
public class Tclass extends CMS {

    public static String show_bitset(BitSet data)   //функци€ вывода набора битов в строку
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


    static class BRAIN{ //јЋ”

        private BitSet RO; //јккумул€тор, на одну €чейку
        
        public BRAIN(){ //конструктор
            RO = new BitSet(CELL);
        }

        public void write_RO(BitSet data){
            RO = data;
        }

        BitSet get_RO(){         //получение регистра
            return RO;
        }

        String showRO(){		//вывод регистра
            return show_bitset(RO);
        }
    }

    static class MEMORY{    //пам€ть

        private BitSet[] TABLE; //массив €чеек пам€ти

        public MEMORY(){    //конструктор
            TABLE = new BitSet[MEM];
            for (int i = 0; i < MEM; i++)
                TABLE[i] = new BitSet(CELL);
        }

        public void write_cell(int addr, BitSet data){
            TABLE[addr] = data;
        }
        public BitSet get_cell(int addr){
            return TABLE[addr];
        }
        public void fill_ALL(){};
        public void dump_all(){};
        public void zero(){
            for (int i = 0; i < MEM; i++)
                TABLE[i].clear();
        }
        public String show_cell(int addr){  
            return show_bitset(TABLE[addr]);
        };
    }

    static class CONTROL{
        int CANT;
        BitSet RC;

        public CONTROL(){
            CANT = 0;
            RC = new BitSet(CELL);
            for(int i = 0; i < CELL; i++)
                RC.clear(i);  //€вное выставление длинны CELL и заполнение нул€ми
        }
    }
}
