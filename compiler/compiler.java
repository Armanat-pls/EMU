import java.io.*;
import java.lang.reflect.Array;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Locale;

import javax.lang.model.type.TypeKind;

import javafx.scene.control.Tab;
public class compiler {
    private static int MEM = 256;
    enum TokenType{
        type,
        numInt,
        numFloat,
        word,
        operand,
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
        public boolean isConst;

        public VARIABLE(TokenType type, String name, String Val, boolean isConst){
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
            this.isConst = isConst;
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
            res += "isConst: " + this.isConst + "\n";
            res += "========\n";
            return res;
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
            operand,
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
                        else if (state == ReadingWhat.operand){
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
                        else if (state == ReadingWhat.operand){
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
                        if (state == ReadingWhat.nothing || state == ReadingWhat.operand){
                            state = ReadingWhat.operand;
                            buffer += c;
                        }
                        else{
                            makeToken(buffer, false);
                            state = ReadingWhat.operand;
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

            }else if (state == ReadingWhat.operand){
                for (String operator : OPERATORS) {
                    if (buffer.equals(operator)){
                        TableOfTokens.add(new TOKEN(TokenType.operand, buffer, codeLine));
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
    public static ArrayList<VARIABLE> VariablesList = new ArrayList<VARIABLE>();
    public static ArrayList<Integer> LinesToIgnore = new ArrayList<Integer>();

    public static VARIABLE getVarbyName(String name){
        for (VARIABLE var : VariablesList) {
            if (var.name.equals(name)) return var;
        }
        return new VARIABLE(null, "NULL", "NULL", false);
    }

    //########### АНАЛИЗАТОР #############################################################################
    public static class SemanticAnalyser{
        private static ArrayList<LexicError> LexicErrors = new ArrayList<LexicError>();
        private static int deepLevel = 0;
        private static TokenType lasTokenType = TokenType.EoI;
        private static TokenType curTokenType = TokenType.EoI;

        private static TokenType typeBuffer;
        private static String nameBuffer;

        private static boolean varExists = false;

        private static TOKEN token;
        private static int i = -1;  //старт с -1, из-за повышения в методе
        private static boolean getNextToken(ArrayList<TOKEN> TableOfTokens){
            if (i + 1 < TableOfTokens.size()){
                token = TableOfTokens.get(++i);
                lasTokenType = curTokenType;
                curTokenType = token.tokenType;
                return true;
            }
            else return false;
        }
        public static ArrayList<LexicError> CheckSemantic(ArrayList<TOKEN> TableOfTokens){
            while (i < TableOfTokens.size()){
                varExists = false;
                if (!getNextToken(TableOfTokens)) break;
                //System.out.print(token.toString());
                if (token.tokenType == TokenType.error){
                    LexicErrors.add(new LexicError(token, "Lexic error"));
                    break;
                }
                else if (token.tokenType == TokenType.type){ //сценарий создания переменной
                    if (lasTokenType != TokenType.EoI || deepLevel > 0){
                        LexicErrors.add(new LexicError(token, "Unexpected place for type"));
                        continue;
                    }
                    if (token.value.equals("int")) typeBuffer = TokenType.numInt;
                    else if (token.value.equals("float")) typeBuffer = TokenType.numFloat;
                    if (!getNextToken(TableOfTokens)) break;
                    if (token.tokenType != TokenType.varName){
                        LexicErrors.add(new LexicError(token, "Expected varName"));
                        continue;
                    }
                    else{
                        for (VARIABLE var : VariablesList)
                            if (var.name.equals(token.value)){
                                varExists = true;
                                break;
                            }
                        if (varExists){
                            LexicErrors.add(new LexicError(token, "Variable already exists"));
                            continue;
                        }
                        else{
                            nameBuffer = token.value;
                            if (!getNextToken(TableOfTokens)) break;
                            if (token.tokenType != TokenType.operand || !token.value.equals("=")){
                                LexicErrors.add(new LexicError(token, "Expected '='"));
                                continue;
                            }
                            else{
                                if (!getNextToken(TableOfTokens)) break;
                                if (token.tokenType == TokenType.numInt || token.tokenType == TokenType.numFloat){
                                    if (typeBuffer != token.tokenType){
                                        LexicErrors.add(new LexicError(token, "type mismatch, expected " + typeBuffer));
                                        continue;
                                    }
                                    else{
                                        VariablesList.add(new VARIABLE(typeBuffer, nameBuffer, token.value, false));
                                    }
                                }
                                else if (token.tokenType == TokenType.varName){
                                    varExists = false;
                                    for (VARIABLE var : VariablesList)
                                        if (var.name.equals(token.value)){
                                            varExists = true;
                                            break;
                                        }
                                    if (!varExists){
                                        LexicErrors.add(new LexicError(token, "variable doesn't exist"));
                                        continue;
                                    }
                                    else{
                                        VarTypes tmp = getVarbyName(token.value).type;
                                        if(tmp == VarTypes.intE){
                                            if (typeBuffer != TokenType.numInt){
                                                LexicErrors.add(new LexicError(token, "type mismatch, expected " + typeBuffer));
                                                continue;
                                            }
                                            VariablesList.add(new VARIABLE(typeBuffer, nameBuffer, String.valueOf(getVarbyName(token.value).intVal) ,false));
                                        }
                                        else if (tmp == VarTypes.floatE){
                                            if (typeBuffer != TokenType.numFloat){
                                                LexicErrors.add(new LexicError(token, "type mismatch, expected " + typeBuffer));
                                                continue;
                                            }
                                            VariablesList.add(new VARIABLE(typeBuffer, nameBuffer, String.valueOf(getVarbyName(token.value).floatVal) ,false));
                                        }
                                    }
                                }
                                else{
                                    LexicErrors.add(new LexicError(token, "Expected number or varName"));
                                    continue;
                                }
                                if (!getNextToken(TableOfTokens)) break;
                                if (token.tokenType != TokenType.EoI){
                                    LexicErrors.add(new LexicError(token, "Expected ;"));
                                    continue;
                                }
                            }
                        }
                    }
                }
                else if (token.tokenType == TokenType.varName){
                    for (VARIABLE var : VariablesList)
                        if (var.name.equals(token.value)){
                            varExists = true;
                            break;
                        }
                }

            }
            return LexicErrors;
        }
    }
    public static void main(String[] args) {
        ArrayList<TOKEN> TableOfTokens = Lexer.lexerAnalyse("compiler\\test.txt");
        ArrayList<LexicError> LexicErrors = SemanticAnalyser.CheckSemantic(TableOfTokens);
        if (LexicErrors.size() > 0){
            for (LexicError error : LexicErrors) {
                System.out.print(error.toString());
            }
        }
        /* 
        for (VARIABLE var : VariablesList) {
            System.out.print(var.toString());
        }
        */
    }
}

