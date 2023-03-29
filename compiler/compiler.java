import java.io.*;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Locale;

import javafx.scene.control.Tab;
public class compiler {
    enum TokenType{
        type,
        numInt,
        numFloat,
        word,
        operand,
        logic,
        struct,
        varName,
        error
    }
    public static class TOKEN{
        TokenType token;
        String value;
        Boolean EOI;
        public TOKEN(TokenType token, String value, Boolean EOI){
            this.token = token;
            this.value = value;
            this.EOI = EOI;
        }
        public String toString(){
            String last = "";
            if (EOI) last = "  EoI";
            return token + " : " + value + last + "\n"; 
        }
    }
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
        static boolean isEmpty(char c){
            if (c == ' ' || c == '\n' || c == '\t') return true;
            return false;
        }
        static boolean isSpecial(char c){
            for (char item : specials) {
                if (c == item) return true;
            }
            return false;
        }
        public static String lexerAnalyse(){
            int symbol;
            BufferedReader bufferedReader;
            try{
                //bufferedReader = new BufferedReader(new FileReader("bastard.emul"));
                bufferedReader = new BufferedReader(new FileReader("test.txt"));
                String buffer = "";
                char c;
                do{
                    symbol = bufferedReader.read();
                    if (symbol == -1){
                        if (state != ReadingWhat.nothing) makeToken(buffer, true);
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
                return e.toString();
            }
            catch(IOException e){
                System.out.print(e);
                return e.toString();
            }
            Parser.fake(TableOfTokens);
            return "done";
        }
        private static void makeToken(String buffer, boolean isEOI){
            if (state == ReadingWhat.number){
                TableOfTokens.add(new TOKEN(TokenType.numInt, buffer ,isEOI));
                return;
            }else if (state == ReadingWhat.numFloat){
                TableOfTokens.add(new TOKEN(TokenType.numFloat, buffer ,isEOI));
                return;
            }else if (state == ReadingWhat.word){
                for (String type : TYPES) {
                    if (buffer.toLowerCase().equals(type)){
                        TableOfTokens.add(new TOKEN(TokenType.type, buffer ,isEOI));
                        return;
                    }
                }
                for (String word : WORDS) {
                    if (buffer.toLowerCase().equals(word)){
                        TableOfTokens.add(new TOKEN(TokenType.word, buffer ,isEOI));
                        return;
                    }
                }
                TableOfTokens.add(new TOKEN(TokenType.varName, buffer ,isEOI));
                    return;

            }else if (state == ReadingWhat.operand){
                for (String operator : OPERATORS) {
                    if (buffer.equals(operator)){
                        TableOfTokens.add(new TOKEN(TokenType.operand, buffer ,isEOI));
                        return;
                    }
                }
                for (String struct : STRUCTURE) {
                    if (buffer.equals(struct)){
                        TableOfTokens.add(new TOKEN(TokenType.struct, buffer ,isEOI));
                        return;
                    }
                }
                for (String logic : LOGIC) {
                    if (buffer.equals(logic)){
                        TableOfTokens.add(new TOKEN(TokenType.logic, buffer ,isEOI));
                        return;
                    }
                }
                TableOfTokens.add(new TOKEN(TokenType.error, "unknown operator: " + buffer ,isEOI));
                return;
            }else if (state == ReadingWhat.nothing){
                TableOfTokens.add(new TOKEN(TokenType.error, "no state" ,isEOI));
                return;
            }
            if (buffer == "" || buffer == " "){
                TableOfTokens.add(new TOKEN(TokenType.error, "no buffer" ,isEOI));
                return;
            }
        }
    }
    public static class Parser{
        public static void fake(ArrayList<TOKEN> TableOfTokens){
            for (TOKEN token : TableOfTokens) {
                System.out.print(token.toString());
            }
        }
    }
    public static void main(String[] args) {
        Lexer.lexerAnalyse();
    }
}

