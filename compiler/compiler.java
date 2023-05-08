import java.io.*;
import java.util.ArrayList;
public class compiler {
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

        public VARIABLE(TokenType type, String name, String Val){
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
            this.address = 0;
        }
        public String toString(){
            String res = "";
            res += "======== VARIABLE ========\n";
            res += "type: " + this.type + "\n";
            res += "name: " + this.name + "\n";
            res += "intVal: " + this.intVal + "\n";
            res += "floatVal: " + this.floatVal + "\n";
            res += "address: " + this.address + "\n";
            res += "========\n";
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
            log += "======== ERROR ========\n";
            log += "On token " + token.toString() + "\n";
            log += "Error: " + error + "\n";
            log += "========\n";
            return log;
        }

    }
    enum InstrType{
        ariph,
        asign,
        whileblock,
        ifblock,
        elseblock
    }

    public static class INSTRUCTION{
        InstrType type;
        String writeTo;
        String operand1;
        String operator;
        String operand2;
        int blockDeep;
        int childrenDeep;

        public INSTRUCTION(InstrType type, String writeTo, String operand1, String operator, String operand2, int blockDeep, int childrenDeep){
            this.type = type;
            this.writeTo = writeTo;
            this.operand1 = operand1;
            this.operator = operator;
            this.operand2 = operand2;
            this.blockDeep = blockDeep;
            this.childrenDeep = childrenDeep;
        }


        public String toString(){
            String log = "";
            log += "======== INSTRUCTION ========\n";
            log += "Operation: " + type.toString() + "\n";
            log += "WriteTo: " + writeTo + "\n";
            log += "Operand 1: " + operand1 + "\n";
            log += "Operator: " + operator  + "\n";
            log += "Operand 2: " + operand2 + "\n";
            log += "Block deepnes: " + blockDeep + "\n";
            log += "Children deepnes: " + childrenDeep + "\n";
            log += "========\n";
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
        return new VARIABLE(null, "NULL", "NULL");
    }

    //########### АНАЛИЗАТОР #############################################################################
    public static class SemanticAnalyser{

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
                    ib.variablesList.add(new VARIABLE(TokenType.numInt, token.value, token.value));
            }
            else if (token.tokenType == TokenType.numFloat){
                if (checkType && targetType != VarTypes.floatE) return "Type mismatch, expected: " + targetType;
                if (CreateConstant && getVarbyName(ib, token.value).type == VarTypes.NULLE)
                    ib.variablesList.add(new VARIABLE(TokenType.numFloat, token.value, token.value));
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
                        ib.variablesList.add(new VARIABLE(typeBuffer, nameBuffer, "0"));
                        ib.instructionsList.add(new INSTRUCTION(InstrType.asign, nameBuffer, token.value, null, null, 0, 0));
                    }
                    else
                        ib.variablesList.add(new VARIABLE(typeBuffer, nameBuffer, token.value));
                
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
                        ib.errorrsList.add(new LexicError(token, "variable doesn't exist"));
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
                            ib.instructionsList.add(new INSTRUCTION(InstrType.asign, writeToBuffer, operand1Buffer, null, null, blockLayers.size(), 0));
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
                        ib.instructionsList.add(new INSTRUCTION(InstrType.ariph, writeToBuffer, operand1Buffer, operatorBuffer, operand2Buffer, blockLayers.size(), 0));
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
                        ib.instructionsList.add(new INSTRUCTION(InstrType.elseblock, null, null, null, null, blockLayers.size(), blockLayers.size() + 1));
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
                            ib.instructionsList.add(new INSTRUCTION(ItypeBuffer, null, operand1Buffer, operatorBuffer, operand2Buffer, blockLayers.size(), blockLayers.size() + 1));
                        blockLayers.add(ItypeBuffer);

                    }
                }
            }
            return ib;
        }
    }
    public static void printTokens(ArrayList<TOKEN> TableOfTokens){
        for (TOKEN token : TableOfTokens) {
            System.out.print(token.toString());
        }
    }
    public static void printErrors(ArrayList<LexicError> LexicErrors){
        if (LexicErrors.size() > 0){
            for (LexicError error : LexicErrors) {
                System.out.print(error.toString());
            }
        }
    }
    public static void printVariables(ArrayList<VARIABLE> VariablesList){
        for (VARIABLE var : VariablesList) {
            System.out.print(var.toString());
        }
    }
    public static void printInstructions(ArrayList<INSTRUCTION> InstructionsList){
        for (INSTRUCTION instruction : InstructionsList) {
            System.out.print(instruction.toString());
        }
    }
    public static void main(String[] args) {
        ArrayList<TOKEN> TableOfTokens = Lexer.lexerAnalyse("compiler\\test.txt");
        Infoblock ib = SemanticAnalyser.CheckSemantic(TableOfTokens); 

        //printErrors(ib.errorrsList);

        if (ib.errorrsList.size() == 0){
            //printTokens(TableOfTokens);
            //printVariables(ib.variablesList);
            printInstructions(ib.instructionsList);
        }

        
    }
}

