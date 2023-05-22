import java.io.*;
import java.util.BitSet;
import java.util.ArrayList;
public class secondary extends Tclass {

    public static BRAIN ALU = new BRAIN();
    public static MEMORY RAM = new MEMORY();
    public static CONTROL UU = new CONTROL();
 
    // перевод двоичной в десятичную целое
    public static int bit_to_int(BitSet data) 
    {
        BitSet imp = new BitSet(CELL);
        imp = (BitSet) data.clone();
        if (imp.isEmpty())
            return 0;
        if (imp.get(CELL - 1))
        {
            imp.flip(0, CELL); //получение дополнительного кода
            if (imp.isEmpty())
                return -1;  //все еденицы доп кода это -1
            return -(int)imp.toLongArray()[0] - 1;
        }
        else
            return (int)imp.toLongArray()[0];   
    }
    
    // перевод десятичной в двоичную целое
    public static BitSet int_to_bit(int data) 
    {
        BitSet imp = new BitSet(CELL);
        if ((data > 2147483647)||(data < -2147483648))
            return imp;

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
        if (data == 0)
            return imp;
        if ((data > 3.40282346639e+38) || (data < -3.40282346639e+38))
            return imp;

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
        BitSet localData = new BitSet(CELL);
        localData = (BitSet) data.clone();
        if (localData.isEmpty())
            return (float)0.0;
		boolean sign_neagative = (localData.get(CELL - 1)); //false - положительное ; true - отрицательное
        BitSet tempEx = new BitSet(CELL);
		for (int i = 0; i < 8; i++)
            if (localData.get(CELL - 9 + i))
			    tempEx.set(i);

		int ex = bit_to_int(tempEx) - 127; //получение десятичного порядка и смещение
		float imp = (float)1.0;
        int bool_to_int = 0;
		for (int i = 0; i < 23; i++)
        {
            bool_to_int = localData.get(CELL - 10 - i) ? 1 : 0;
            imp += bool_to_int * Math.pow(2, -i - 1);
        }
		imp = (float)Math.pow(2.0, ex) * imp;
		if (sign_neagative) imp *= -1.0; //сделать отрицательным при необходимости

		return imp;
	}

    public static String makeIndex(int index, int zerosAmnt)
    {
        String zeros = "[";
        for (int i = 0; i < zerosAmnt; i++)
            zeros += "0";
        zeros += index + "]";        
        return zeros;
    }


    // склеивание двух int в двоичный код
    public static BitSet make_one(int com, int addr)
    {
        BitSet imp = new BitSet(CELL); // переменная для результата
        BitSet B_com = new BitSet(CELL);
        B_com = int_to_bit(com); //двоичный код команды
        BitSet B_addr = new BitSet(CELL);
        B_addr = int_to_bit(addr); //двоичный код адреса ячейки
        for (int i = 0; i < BMEM; i++) //склейка
            if (B_addr.get(i))
                imp.set(i);
        for (int i = BMEM; i < CELL; i++)
            if (B_com.get(i - BMEM))
                imp.set(i);
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
        BitSet localData = new BitSet(CELL);
        localData = (BitSet) data.clone();
        if (localData.isEmpty())
        {
            return imp;
        }
        for (int i = 0; i < BMEM; i++)
            if (localData.get(i))
                imp[1].set(i);
        for (int i = BMEM; i < CELL; i++)
            if (localData.get(i))
                imp[0].set(i - BMEM);
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
        if (C == (int)CMSmap.get("STOP"))
            return 666;
        int op1, op2, res;
        float fop1, fop2, fres;

        if (C == CMSmap.get("JUMP"))
        {
            UU.CANT = A;
            return 101;//Выход из выполнения без дополнительного повышения счётчика команд)
        }
        else if (C == CMSmap.get("IFJUMP"))
        {
            if (!ALU.get_RO().isEmpty())
            {
                UU.CANT = A;
                return 101;//Выход из выполнения без дополнительного повышения счётчика команд)
            }
            //если регистр пуст, повышение CANT на 1
            //если не пуст, прыжок на указанную
        }
        else if (C == CMSmap.get("LOAD"))
            ALU.write_RO(RAM.get_cell(A));//запись значения ячейки А в аккумулятор
        else if (C == CMSmap.get("SAVE"))
            RAM.write_cell(A, ALU.get_RO());//запись значения аккумуляторa в ячейку A
        else if (C == CMSmap.get("AND"))
        {   //логическое И (если регистр или операнд 0, то ложь)
            if (ALU.get_RO().isEmpty() || RAM.get_cell(A).isEmpty())
                res = 0;
            else res = 1;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("OR")) 
        {   //логическое ИЛИ (если регистр и операнд 0, то ложь)
            if (ALU.get_RO().isEmpty() && RAM.get_cell(A).isEmpty())
                res = 0;
            else res = 1;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("NOT"))
        {   //логиеское НЕ (если операнд 0 сделать 1, и наоборот)
            if (ALU.get_RO().isEmpty())
                res = 1;
            else res = 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("LESS"))
        {   //логическое МЕНЬШЕ
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = op1 < op2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("LESSOE"))
        {   //логическое МЕНЬШЕ ИЛИ РАВНО
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = op1 <= op2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("EQUAL"))
        {   //логическое РАВНО
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = op1 == op2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("NOTEQUAL"))
        {   //логическое НЕ РАВНО
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = op1 != op2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("FLESS"))
        {   //логическое МЕНЬШЕ FLOAT
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            res = fop1 < fop2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("FLESSOE"))
        {   //логическое МЕНЬШЕ ИЛИ РАВНО FLOAT
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            res = fop1 <= fop2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("FEQUAL"))
        {   //логическое РАВНО  FLOAT
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            res = fop1 == fop2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("FNOTEQUAL"))
        {   //логическое НЕ РАВНО   FLOAT
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            res = fop1 != fop2 ? 1 : 0;
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("PLUS"))
        {   //сложение целых
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = (op1 + op2);
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("MINUS"))
        {   //вычитание целых
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = (op1 - op2);
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("MULT"))
        {   //умножение целых
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            res = (op1 * op2);
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("DIVIS"))
        {   //деление целых
            op1 = bit_to_int(ALU.get_RO());
            op2 = bit_to_int(RAM.get_cell(A));
            if (op2 == 0)
                res = 0;
            else{
                try{ res = (op1 / op2); }
                catch (Throwable t){ res = 0;}
            }                    
            ALU.write_RO(int_to_bit(res));
        }
        else if (C == CMSmap.get("FPLUS"))
        {   //сложение float
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            fres = (fop1 + fop2);
            ALU.write_RO(float_to_bit(fres));
        }
        else if (C == CMSmap.get("FMINUS"))
        {   //вычитание float
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            fres = (fop1 - fop2);
            ALU.write_RO(float_to_bit(fres));
        }
        else if (C == CMSmap.get("FMULT"))
        {   //умножение float
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            fres = (fop1 * fop2);
            ALU.write_RO(float_to_bit(fres));
        }
        else if (C == CMSmap.get("FDIVIS"))
        {
            fop1 = bit_to_float(ALU.get_RO());
            fop2 = bit_to_float(RAM.get_cell(A));
            if (fop2 == 0.0)
                fres = (float)0.0;
            else{ 
                try{ fres = (fop1 / fop2);}
                catch (Throwable t){ fres = (float)0.0;} 
            }
            ALU.write_RO(float_to_bit(fres));
        }
        UU.CANT++; //повышение счётчика команд после выполнения
        return 0;
    }

    protected static class compiler {    
        enum TokenType{
            type,
            numInt,
            numFloat,
            word,
            operator,
            logic,
            struct,
            varName,
            EoI,
            error
        }
        public static class TOKEN{
            public TokenType tokenType;
            public String value;
            public int codeLine;
            public TOKEN(TokenType tokenType, String value, int codeLine){
                this.tokenType = tokenType;
                this.value = value;
                this.codeLine = codeLine;
            }
            public String toString(){
                String num = "";
                if (codeLine < 10) num = "00" + codeLine;
                else if (codeLine < 100) num = "0" + codeLine;
                else if (codeLine < 1000) num = "" + codeLine;
                return num + "| " + tokenType + " : " + value + "\n"; 
            }
        }
    
        enum VarTypes{
            floatE,
            intE,
            NULLE
        }
        public static class VARIABLE{
            public VarTypes type;
            public String name;
            public int intVal;
            public float floatVal;
            public int address;
    
            public VARIABLE(TokenType type, String name, String Val, int addrCount){
                if (type == TokenType.numInt){
                    this.type = VarTypes.intE;
                    this.intVal = Integer.valueOf(Val);
                    this.floatVal = 0.0f;
                }
                else if (type == TokenType.numFloat){
                    this.type = VarTypes.floatE;
                    this.floatVal = Float.valueOf(Val);
                    this.intVal = 0;
                }
                else{
                    this.type = VarTypes.NULLE;
                    this.intVal = 0;
                    this.floatVal = 0.0f;
                }
                this.name = name;
                this.address = addrCount;
            }
            public String toString(){
                String res = "";
                res += "type: " + this.type + "\n";
                res += "name: " + this.name + "\n";
                res += "intVal: " + this.intVal + "\n";
                res += "floatVal: " + this.floatVal + "\n";
                res += "address: " + this.address + "\n";
                return res;
            }
        }
        public static class LexicError{
            private String error;
            private TOKEN token;

            public LexicError(TOKEN token, String error){
                this.token = token;
                this.error = error;
            }
            public String toString(){
                String log = "";
                log += token.toString() + "     |    ";
                log += "Error: " + error;
                return log;
            }
        }
        enum InstrType{
            ariph,
            asign,
            whileblock,
            ifblock,
            elseblock,
            endblock,
            endprog,
        }
    
        public static class INSTRUCTION{
            InstrType type;
            String writeTo;
            String operand1;
            String operator;
            String operand2;
            int blockDeep;

            public INSTRUCTION(InstrType type, String writeTo, String operand1, String operator, String operand2, int blockDeep){
                this.type = type;
                this.writeTo = writeTo;
                this.operand1 = operand1;
                this.operator = operator;
                this.operand2 = operand2;
                this.blockDeep = blockDeep;
            }
            public INSTRUCTION(InstrType type, int blockDeep){
                this.type = type;
                this.blockDeep = blockDeep;
            }
            public String toString(){
                String log = "";
                log += "Operation: " + type.toString() + "\n";
                log += "WriteTo: " + writeTo + "\n";
                log += "Operand 1: " + operand1 + "\n";
                log += "Operator: " + operator  + "\n";
                log += "Operand 2: " + operand2 + "\n";
                log += "Block deepness: " + blockDeep + "\n";
                return log;
            }
        }
    
        public static class Infoblock{
            ArrayList<VARIABLE> variablesList;
            ArrayList<LexicError> errorrsList;
            ArrayList<INSTRUCTION> instructionsList;
            
            public Infoblock(){
                variablesList = new ArrayList<VARIABLE>();
                errorrsList = new ArrayList<LexicError>();
                instructionsList = new ArrayList<INSTRUCTION>();
            }
        }
        //########### ЛЕКСЕР #############################################################################
        public static class Lexer{
            static char specials[] = {'+','-','*','/','=','!','<','>','{','}','(',')',};
            static String WORDS[] = {
                "if",
                "else",
                "while",
            };
            static String TYPES[] = {
                "int",
                "float",
            };
            static String STRUCTURE[] = {
                "{",
                "}",
                "(",
                ")",
            };
            static String OPERATORS[] = {
                "=",
                "+",
                "-",
                "*",
                "/",
            };
            static String LOGIC[] = {
                "<",
                "<=",
                "!=",
                "=="
            };
            enum ReadingWhat{
                number,
                word,
                numFloat,
                operator,
                nothing
            }
            static ArrayList<TOKEN> TableOfTokens = new ArrayList<TOKEN>();
            static ReadingWhat state = ReadingWhat.nothing;
            static int codeLine = 1;
            static boolean isEmpty(char c){
                if (c == ' ' || c == '\t') return true;
                if (c == '\n') {
                    codeLine++;
                    return true;
                }
                return false;
            }
            static boolean isSpecial(char c){
                for (char item : specials) {
                    if (c == item) return true;
                }
                return false;
            }
            public static ArrayList<TOKEN> lexerAnalyse(String filename){
                int symbol;
                BufferedReader bufferedReader;
                try{
                    bufferedReader = new BufferedReader(new FileReader(filename));
                    String buffer = "";
                    char c;
                    do{
                        symbol = bufferedReader.read();
                        if (symbol == -1){
                            if (state != ReadingWhat.nothing) makeToken(buffer, false);
                            break;
                        };
                        c = (char)symbol;
                        if (Character.isAlphabetic(symbol) || c == '_'){
                            if (state == ReadingWhat.nothing || state == ReadingWhat.word){
                                state = ReadingWhat.word;
                                buffer += c;
                            }
                            else if (state == ReadingWhat.operator){
                                makeToken(buffer, false);
                                state = ReadingWhat.word;
                                buffer = "";
                                buffer += c;
                            }
                            else{
                                System.out.print("char in number");
                                break;
                            }
                        }
                        if (Character.isDigit(c)){
                            if (state == ReadingWhat.nothing){
                                state = ReadingWhat.number;
                                buffer += c;
                            }
                            else if (state == ReadingWhat.operator){
                                makeToken(buffer, false);
                                state = ReadingWhat.number;
                                buffer = "";
                                buffer += c;
                            }
                            else if (state == ReadingWhat.word || state == ReadingWhat.number || state == ReadingWhat.numFloat){
                                buffer += c;
                            }
                            continue;
                        }
                        if (c == '.' && state == ReadingWhat.number){
                            state = ReadingWhat.numFloat;
                            buffer += c;
                            continue;
                        }
                        if (isSpecial(c)){
                            if (state == ReadingWhat.nothing || state == ReadingWhat.operator){
                                state = ReadingWhat.operator;
                                buffer += c;
                            }
                            else{
                                makeToken(buffer, false);
                                state = ReadingWhat.operator;
                                buffer = "";
                                buffer += c;
                            }
                            continue;
                        }
                        if (isEmpty(c)){
                            if (state != ReadingWhat.nothing){
                                makeToken(buffer, false);
                                state = ReadingWhat.nothing;
                                buffer = "";
                            }
                            continue;
                        }
                        if (c == ';'){
                            if (state != ReadingWhat.nothing){
                                makeToken(buffer, true);
                                state = ReadingWhat.nothing;
                                buffer = "";
                            }
                            continue;
                        } 
                    }while(symbol != -1);
                    bufferedReader.close();
                }
                catch(FileNotFoundException e){
                    System.out.print(e);
                }
                catch(IOException e){
                    System.out.print(e);
                }
                return TableOfTokens;
            }
            private static void makeToken(String buffer, boolean isEOI){
                if (state == ReadingWhat.number){
                    TableOfTokens.add(new TOKEN(TokenType.numInt, buffer, codeLine));
                    if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                    return;
                }else if (state == ReadingWhat.numFloat){
                    TableOfTokens.add(new TOKEN(TokenType.numFloat, Float.valueOf(buffer).toString(), codeLine));
                    if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                    return;
                }else if (state == ReadingWhat.word){
                    for (String type : TYPES) {
                        if (buffer.toLowerCase().equals(type)){
                            TableOfTokens.add(new TOKEN(TokenType.type, buffer, codeLine));
                            if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                            return;
                        }
                    }
                    for (String word : WORDS) {
                        if (buffer.toLowerCase().equals(word)){
                            TableOfTokens.add(new TOKEN(TokenType.word, buffer, codeLine));
                            if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                            return;
                        }
                    }
                    TableOfTokens.add(new TOKEN(TokenType.varName, buffer, codeLine));
                    if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                        return;
    
                }else if (state == ReadingWhat.operator){
                    for (String operator : OPERATORS) {
                        if (buffer.equals(operator)){
                            TableOfTokens.add(new TOKEN(TokenType.operator, buffer, codeLine));
                            if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                            return;
                        }
                    }
                    for (String struct : STRUCTURE) {
                        if (buffer.equals(struct)){
                            TableOfTokens.add(new TOKEN(TokenType.struct, buffer, codeLine));
                            if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                            return;
                        }
                    }
                    for (String logic : LOGIC) {
                        if (buffer.equals(logic)){
                            TableOfTokens.add(new TOKEN(TokenType.logic, buffer, codeLine));
                            if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                            return;
                        }
                    }
                    TableOfTokens.add(new TOKEN(TokenType.error, "unknown operator: " + buffer, codeLine));
                    if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                    return;
                }else if (state == ReadingWhat.nothing){
                    TableOfTokens.add(new TOKEN(TokenType.error, "no state", codeLine));
                    if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                    return;
                }
                if (buffer == "" || buffer == " "){
                    TableOfTokens.add(new TOKEN(TokenType.error, "no buffer", codeLine));
                    if (isEOI) TableOfTokens.add(new TOKEN(TokenType.EoI, "EoI", codeLine));
                    return;
                }
            }
        }
        public static VARIABLE getVarbyName(Infoblock ib, String name){
            for (VARIABLE var : ib.variablesList) {
                if (var.name.equals(name)) return var;
            }
            return new VARIABLE(null, "NULL", "NULL", 0);
        }
    
        //########### АНАЛИЗАТОР #############################################################################
        public static class SemanticAnalyser{
            private static int addrCount = 0;
    
            private static Infoblock ib = new Infoblock();
            private static TokenType lasTokenType = TokenType.EoI;
            private static TokenType curTokenType = TokenType.EoI;
            private static String lasTokenValue = "";
    
            private static TokenType typeBuffer;
            private static String nameBuffer;
    
            private static boolean varExists = false;
    
            private static ArrayList<Integer> LinesToIgnore = new ArrayList<Integer>();
            private static ArrayList<InstrType> blockLayers = new ArrayList<InstrType>();
            private static boolean expectingElse = false;
            private static TOKEN token;
            private static int i = -1;  //старт с -1, из-за повышения в методе
            private static String ErrorBuffer = "";
            private static boolean getNextToken(ArrayList<TOKEN> TableOfTokens){
                if (i + 1 < TableOfTokens.size()){
                    lasTokenValue = token.value;
                    token = TableOfTokens.get(++i);
                    lasTokenType = curTokenType;
                    curTokenType = token.tokenType;
                    return true;
                }
                else return false;
            }
            private static String CheckOperand(VarTypes targetType, boolean CreateConstant){
                VARIABLE tmpVar;
                boolean checkType = true;
                if (targetType == VarTypes.NULLE) checkType = false;
                if (token.tokenType == TokenType.numInt){
                    if (checkType && targetType != VarTypes.intE) return "Type mismatch, expected: " + targetType;
                    if (CreateConstant && getVarbyName(ib, token.value).type == VarTypes.NULLE)
                        ib.variablesList.add(new VARIABLE(TokenType.numInt, token.value, token.value, ++addrCount));
                }
                else if (token.tokenType == TokenType.numFloat){
                    if (checkType && targetType != VarTypes.floatE) return "Type mismatch, expected: " + targetType;
                    if (CreateConstant && getVarbyName(ib, token.value).type == VarTypes.NULLE)
                        ib.variablesList.add(new VARIABLE(TokenType.numFloat, token.value, token.value, ++addrCount));
                }
                else if (token.tokenType == TokenType.varName){
                    tmpVar = getVarbyName(ib, token.value);
                    if (tmpVar.type == VarTypes.NULLE) return "Variable doesn't exist";
                    if (checkType && tmpVar.type != targetType) return "Type mismatch, expected: " + targetType;
                }
                else return "Expected number or varName";
                return "";
            }
    
            public static Infoblock CheckSemantic(ArrayList<TOKEN> TableOfTokens){
                token = new TOKEN(curTokenType, lasTokenValue, 0);
                while (i < TableOfTokens.size()){
                    varExists = false;
                    if (!getNextToken(TableOfTokens)) break;
                    if (token.tokenType == TokenType.error){
                        ib.errorrsList.add(new LexicError(token, "Lexic error"));
                        break;
                    }
                    else if (token.tokenType == TokenType.type){ //сценарий создания переменной
                        if (!(lasTokenType == TokenType.EoI || lasTokenType == TokenType.struct) || blockLayers.size() > 0){
                            ib.errorrsList.add(new LexicError(token, "Unexpected place for type"));
                            continue;
                        }
                        LinesToIgnore.add(token.codeLine);
                        if (token.value.equals("int")) typeBuffer = TokenType.numInt;
                        else if (token.value.equals("float")) typeBuffer = TokenType.numFloat;
                        if (!getNextToken(TableOfTokens)) break;
                        if (token.tokenType != TokenType.varName){
                            ib.errorrsList.add(new LexicError(token, "Expected varName"));
                            continue;
                        }
                        for (VARIABLE var : ib.variablesList)
                            if (var.name.equals(token.value)){
                                varExists = true;
                                break;
                            }
                        if (varExists){
                            ib.errorrsList.add(new LexicError(token, "Variable already exists"));
                            continue;
                        }
                        nameBuffer = token.value;
                        if (!getNextToken(TableOfTokens)) break;
                        if (token.tokenType != TokenType.operator || !token.value.equals("=")){
                            ib.errorrsList.add(new LexicError(token, "Expected '='"));
                            continue;
                        }
    
                        if (!getNextToken(TableOfTokens)) break;
                        VarTypes tmp = VarTypes.NULLE;
                        if (typeBuffer == TokenType.numInt) tmp = VarTypes.intE;
                        else if (typeBuffer == TokenType.numFloat) tmp = VarTypes.floatE;
                        ErrorBuffer = CheckOperand(tmp, false);
                        if (!ErrorBuffer.equals("")){
                            ib.errorrsList.add(new LexicError(token, ErrorBuffer));
                            continue;
                        }
                        if (token.tokenType == TokenType.varName){
                            ib.variablesList.add(new VARIABLE(typeBuffer, nameBuffer, "0", ++addrCount));
                            ib.instructionsList.add(new INSTRUCTION(InstrType.asign, nameBuffer, token.value, null, null, 0));
                        }
                        else
                            ib.variablesList.add(new VARIABLE(typeBuffer, nameBuffer, token.value, ++addrCount));
                    
                        if (!getNextToken(TableOfTokens)) break;
                        if (token.tokenType != TokenType.EoI){
                            ib.errorrsList.add(new LexicError(token, "Expected ;"));
                            continue;
                        }
                    }
                    else if (token.tokenType == TokenType.varName){ // сценарий проверки операции
                        if (!(lasTokenType == TokenType.EoI || lasTokenType == TokenType.struct)){
                            ib.errorrsList.add(new LexicError(token, "unexpected place for varName"));
                            continue;
                        }
                        for (VARIABLE var : ib.variablesList)
                            if (var.name.equals(token.value)){
                                varExists = true;
                                break;
                            }
                        if (!varExists){
                            ib.errorrsList.add(new LexicError(token, "Variable doesn't exist"));
                            continue;
                        }
                        String writeToBuffer = token.value;
                        String operand1Buffer = "0";
                        String operatorBuffer = "0";
                        String operand2Buffer = "0";
                        VarTypes targetType = getVarbyName(ib, token.value).type;       
                        if (!getNextToken(TableOfTokens)) break;
                        if (token.tokenType != TokenType.operator || !token.value.equals("=")){
                            ib.errorrsList.add(new LexicError(token, "Expected '='"));
                            continue;
                        }        
                        if (!getNextToken(TableOfTokens)) break;
                        ErrorBuffer = CheckOperand(targetType, true);
                        if (!ErrorBuffer.equals("")){
                            ib.errorrsList.add(new LexicError(token, ErrorBuffer));
                            continue;
                        }
                        operand1Buffer = token.value;
            
                        if (!getNextToken(TableOfTokens)) break;
                        if (token.tokenType == TokenType.EoI){
                            if (ib.errorrsList.size() == 0)
                                ib.instructionsList.add(new INSTRUCTION(InstrType.asign, writeToBuffer, operand1Buffer, null, null, blockLayers.size()));
                            continue;
                        }
    
                        if (token.tokenType != TokenType.operator || token.value.equals("=")){
                            ib.errorrsList.add(new LexicError(token, "Expected arithmetic operator"));
                            continue;
                        }
                        operatorBuffer = token.value;
    
                        if (!getNextToken(TableOfTokens)) break;
                        ErrorBuffer = CheckOperand(targetType, true);
                        if (!ErrorBuffer.equals("")){
                            ib.errorrsList.add(new LexicError(token, ErrorBuffer));
                            continue;
                        }
                        operand2Buffer = token.value;
            
                        if (!getNextToken(TableOfTokens)) break;
                        if (token.tokenType != TokenType.EoI){
                            ib.errorrsList.add(new LexicError(token, "Expected End Of Instruction"));
                            continue;
                        }
                        if (ib.errorrsList.size() == 0)
                            ib.instructionsList.add(new INSTRUCTION(InstrType.ariph, writeToBuffer, operand1Buffer, operatorBuffer, operand2Buffer, blockLayers.size()));
                    }
                    else if (token.tokenType == TokenType.struct && token.value.equals("}")){ //сценарий закрытия блока
                        int layer = blockLayers.size();
                        if (!(layer > 0)){
                            ib.errorrsList.add(new LexicError(token, "No block to close"));
                            continue;
                        }
                        if (blockLayers.get(layer - 1) == InstrType.ifblock){
                            if (i + 1 < TableOfTokens.size()){
                                TOKEN tmpToken = TableOfTokens.get(i + 1);
                                if (tmpToken.tokenType == TokenType.word && tmpToken.value.equals("else"))
                                    expectingElse = true;
                            }
                        }
                        blockLayers.remove(layer - 1);
                        ib.instructionsList.add(new INSTRUCTION(InstrType.endblock, blockLayers.size()));
                    }
                    else if (token.tokenType == TokenType.word){ // сценарий блока
                        if (token.value.equals("else")){
                            if (!expectingElse){
                                ib.errorrsList.add(new LexicError(token, "Enexpected 'else'"));
                                continue;
                            }
                            expectingElse = false;
                            if (!getNextToken(TableOfTokens)) break;
                            if (token.tokenType != TokenType.struct || !token.value.equals("{")){
                                ib.errorrsList.add(new LexicError(token, "Enexpected block opening"));
                                continue;
                            }
                            ib.instructionsList.add(new INSTRUCTION(InstrType.elseblock, null, null, null, null, blockLayers.size()));
                            blockLayers.add(InstrType.elseblock);
                        }
                        else {
                            InstrType ItypeBuffer;
                            String operand1Buffer = "0";
                            String operatorBuffer = "0";
                            String operand2Buffer = "0";
                            if (token.value.equals("while")) ItypeBuffer = InstrType.whileblock;
                            else if (token.value.equals("if")) ItypeBuffer = InstrType.ifblock;
                            else {
                                ib.errorrsList.add(new LexicError(token, "Enexpected word"));
                                continue;
                            }
                            if (!getNextToken(TableOfTokens)) break;
                            if (token.tokenType != TokenType.struct || !token.value.equals("(")){
                                ib.errorrsList.add(new LexicError(token, "Expected logic block opening"));
                                continue;
                            }
                            if (!getNextToken(TableOfTokens)) break;
                            ErrorBuffer = CheckOperand(VarTypes.NULLE, true); // первый запуск без проверки типа
                            if (!ErrorBuffer.equals("")){
                                ib.errorrsList.add(new LexicError(token, ErrorBuffer));
                                continue;
                            }
                            operand1Buffer = token.value;
                            VarTypes tmpType = VarTypes.NULLE;
                            if (token.tokenType == TokenType.numInt) tmpType = VarTypes.intE;
                            else if (token.tokenType == TokenType.numFloat) tmpType = VarTypes.floatE;
                            else if (token.tokenType == TokenType.varName) tmpType = getVarbyName(ib, token.value).type;
    
                            if (!getNextToken(TableOfTokens)) break;
                            if (token.tokenType != TokenType.logic){
                                ib.errorrsList.add(new LexicError(token, "Expected logic operator"));
                                continue;
                            }
                            operatorBuffer = token.value;
    
                            if (!getNextToken(TableOfTokens)) break;
                            ErrorBuffer = CheckOperand(tmpType, true);
                            if (!ErrorBuffer.equals("")){
                                ib.errorrsList.add(new LexicError(token, ErrorBuffer));
                                continue;
                            }
                            operand2Buffer = token.value;
    
                            if (!getNextToken(TableOfTokens)) break;
                            if (token.tokenType != TokenType.struct || !token.value.equals(")")){
                                ib.errorrsList.add(new LexicError(token, "Expected logic block closing"));
                                continue;
                            }
                            if (!getNextToken(TableOfTokens)) break;
                            if (token.tokenType != TokenType.struct || !token.value.equals("{")){
                                ib.errorrsList.add(new LexicError(token, "Expected block opening"));
                                continue;
                            }
                            if (ib.errorrsList.size() == 0)
                                ib.instructionsList.add(new INSTRUCTION(ItypeBuffer, null, operand1Buffer, operatorBuffer, operand2Buffer, blockLayers.size()));
                            blockLayers.add(ItypeBuffer);
    
                        }
                    }
                }
                ib.instructionsList.add(new INSTRUCTION(InstrType.endprog, 0));
                return ib;
            }
        }
        
        //########### ТРАНСЛЯТОР #############################################################################
        public static class Translator{
            private static ArrayList<String> errors;
            private static int MEMcount = 0;

            private static BitSet[] BitSets = new BitSet[MEM];
            private static ArrayList<BlockHead> blocks;
            private static boolean checkIf = false;
            private static class BlockHead{
                InstrType type;
                //поля для while и if-else
                int conditionPos;   //  Адрес ячейки с првоеркой условия 
                int jumpPos;        //  Адрес ячейки с прыжком через блок, если условие не выполнено

                //поля для if-else
                public int blockEndPos;    //  Адрес ячейки в конце блока, если нужно перепрыгнуть else. Заполняется по завершению блока.

                public BlockHead(InstrType type, int conditionPos, int jumpPos){
                    this.type = type;
                    this.conditionPos = conditionPos;
                    this.jumpPos = jumpPos;
                }

            }
            private static boolean MEMcntUP(){
                if (++MEMcount < MEM) return true;
                else{
                    errors.add("Memory overload");
                    return false;
                }
            }
            public static ArrayList<String> Compile(Infoblock ib){
                blocks = new ArrayList<BlockHead>();
                errors = new ArrayList<String>();
                if (ib.variablesList.size() + ib.instructionsList.size() >= MEM + 1){
                    errors.add("Memory overload. varCount: " + ib.variablesList.size() + ", instrCount: " + ib.instructionsList.size());
                    return errors;
                }
                else if(ib.instructionsList.size() >= MEM + 1){
                    errors.add("Memory overload. instrCount: " + ib.instructionsList.size());
                    return errors;
                }
                else if(ib.variablesList.size() >= MEM + 1){
                    errors.add("Memory overload. varCount: " + ib.variablesList.size());
                    return errors;
                }
                
                //заполнение данных
                for (VARIABLE var : ib.variablesList){
                    if (!MEMcntUP()) break;
                    if (var.type == VarTypes.intE)
                        BitSets[MEMcount] = int_to_bit(var.intVal);
                    else if (var.type == VarTypes.floatE)
                        BitSets[MEMcount] = float_to_bit(var.floatVal);
                    else errors.add("Type NULL, shouldn't be impossible");      
                }
                if (!MEMcntUP()) return errors;
                BitSets[0] = make_one( CMSmap.get("JUMP"), MEMcount);
    
                for (INSTRUCTION instr : ib.instructionsList) {
                    if (MEMcount >= MEM){
                        errors.add("Memory overload");
                        break;
                    }
                    if (checkIf && instr.type != InstrType.elseblock){
                        MEMcount--;
                        blocks.remove(blocks.size() - 1);
                    }
                    checkIf = false;
                    if (instr.type == InstrType.asign){
                        BitSets[MEMcount] = make_one(CMSmap.get("LOAD"), Integer.valueOf(getVarbyName(ib, instr.operand1).address));
                        if (!MEMcntUP()) break;
                        BitSets[MEMcount] = make_one(CMSmap.get("SAVE"), Integer.valueOf(getVarbyName(ib, instr.writeTo).address));
                        if (!MEMcntUP()) break;
                    }
                    else if (instr.type == InstrType.ariph){
                        BitSets[MEMcount] = make_one(CMSmap.get("LOAD"), Integer.valueOf(getVarbyName(ib, instr.operand1).address));
                        if (!MEMcntUP()) break;
                        String F = getVarbyName(ib, instr.writeTo).type == VarTypes.intE ? "" : "F";
                        String COMM = "";
                        if (instr.operator.equals("+"))
                            COMM = "PLUS";
                        else if (instr.operator.equals("-"))
                            COMM = "MINUS";
                        else if (instr.operator.equals("*"))
                            COMM = "MULT";
                        else if (instr.operator.equals("/"))
                            COMM = "DIVIS";
                        BitSets[MEMcount] = make_one(CMSmap.get(F + COMM), Integer.valueOf(getVarbyName(ib, instr.operand2).address));
                        if (!MEMcntUP()) break;
                        BitSets[MEMcount] = make_one(CMSmap.get("SAVE"), Integer.valueOf(getVarbyName(ib, instr.writeTo).address));
                        if (!MEMcntUP()) break;
                    }
                    else if (instr.type == InstrType.whileblock || instr.type == InstrType.ifblock){
                        int conditionPosBuffer;
                        int jumpPosBuffer;

                        conditionPosBuffer = MEMcount;  // На этот адрес нужно будет возвращатсья в конце while блока;
                        BitSets[MEMcount] = make_one(CMSmap.get("LOAD"), Integer.valueOf(getVarbyName(ib, instr.operand1).address));
                        if (!MEMcntUP()) break;
                        String F = getVarbyName(ib, instr.operand1).type == VarTypes.intE ? "" : "F";
                        String COMM = "";
                        if (instr.operator.equals("<"))
                            COMM = "LESS";
                        else if (instr.operator.equals("<="))
                            COMM = "LESSOE";
                        else if (instr.operator.equals("=="))
                            COMM = "EQUAL";
                        else if (instr.operator.equals("!="))
                            COMM = "NOTEQUAL";
                        BitSets[MEMcount] = make_one(CMSmap.get(F + COMM), Integer.valueOf(getVarbyName(ib, instr.operand2).address));
                        if (!MEMcntUP()) break;
                        BitSets[MEMcount] = make_one(CMSmap.get("NOT"), 0);
                        if (!MEMcntUP()) break;
                        jumpPosBuffer = MEMcount;   //  По этому адресу нужно будет расположить команду команду прыжка через блок.
                        if (!MEMcntUP()) break;
                        blocks.add(new BlockHead(instr.type, conditionPosBuffer, jumpPosBuffer));                        
                    }
                    else if (instr.type == InstrType.elseblock){
                        blocks.add(new BlockHead(instr.type, 0, 0));
                    }
                    else if (instr.type == InstrType.endblock){
                        BlockHead lastBlockHead = blocks.get(blocks.size() - 1);
                        if (lastBlockHead.type == InstrType.whileblock){
                            BitSets[MEMcount] = make_one(CMSmap.get("JUMP"), lastBlockHead.conditionPos);
                            if (!MEMcntUP()) break;
                            BitSets[lastBlockHead.jumpPos] = make_one(CMSmap.get("IFJUMP"), MEMcount);
                            blocks.remove(blocks.size() - 1);
                        }
                        else if (lastBlockHead.type == InstrType.ifblock){
                            lastBlockHead.blockEndPos = MEMcount;
                            blocks.set(blocks.size() - 1, lastBlockHead);
                            if (!MEMcntUP()) break;
                            BitSets[lastBlockHead.jumpPos] = make_one(CMSmap.get("IFJUMP"), MEMcount);
                            // если за if не идёт else, этот блок будет удалён. Удаление происходит в начале цикла.
                            checkIf = true;
                        }
                        else if (lastBlockHead.type == InstrType.elseblock){
                            lastBlockHead = blocks.get(blocks.size() - 2);
                            BitSets[lastBlockHead.blockEndPos] = make_one(CMSmap.get("JUMP"), MEMcount);
                            blocks.remove(blocks.size() - 1);
                            blocks.remove(blocks.size() - 1);
                        }
                    }
                    else if (instr.type == InstrType.endprog){
                        BitSets[MEMcount] = make_one(CMSmap.get("STOP"), 0);
                        break;
                    }
                }
                if (errors.size() == 0){
                    FileWriter writer;
                    try{
                        writer = new FileWriter("compiler\\result.txt");
                        for (int i = 0; i < MEM; i++)
                            writer.write(show_bitset(BitSets[i]) + "\n");
                        writer.close();
                    }
                    catch(IOException e){
                        System.out.println(e);
                    }
                }
                return errors;
            }
        }
        
        public static ArrayList<String> printTokens(ArrayList<TOKEN> TableOfTokens){
            ArrayList<String> tokens = new ArrayList<String>();
            for (TOKEN token : TableOfTokens) {
                tokens.add(token.toString());
            }
            return tokens;
        }
        public static ArrayList<String> printErrors(ArrayList<LexicError> LexicErrors){
            ArrayList<String> errors = new ArrayList<String>();
            if (LexicErrors.size() > 0){
                for (LexicError error : LexicErrors) {
                    errors.add(error.toString());
                }
            }
            return errors;
        }
        public static ArrayList<String> printVariables(ArrayList<VARIABLE> VariablesList){
            ArrayList<String> variables = new ArrayList<String>();
            for (VARIABLE var : VariablesList) {
                variables.add(var.toString());
            }
            return variables;
        }
        public static ArrayList<String> printInstructions(ArrayList<INSTRUCTION> InstructionsList){
            ArrayList<String> instructions = new ArrayList<String>();
            for (INSTRUCTION instruction : InstructionsList) {
                instructions.add(instruction.toString());
            }
            return instructions;
        }        
    }   

}
