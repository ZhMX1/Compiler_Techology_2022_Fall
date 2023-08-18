import java.io.*;
import java.util.Iterator;

public class Lexer {
    public static boolean fileEnd = false;
    public static boolean formatString = false;

    private String fileName;
    private String curLine;
    private char curChar;
    private int lineNum = 0;
    private int charPos = 0;
    private int length = 0;

    public Lexer(String fileStr){
        fileName = fileStr;
        curLine = "";
        curChar = '\0';
        lineNum = 0;
        charPos = 0;
        length = 0;
        fileEnd = false;

        next_char();
    }

    @Override
    public String toString() {
        return "Line{" +
                "curLine='" + curLine + '\'' +
                ", curChar=" + curChar +
                ", lineNum=" + lineNum +
                ", charPos=" + charPos +
                ", length=" + length +
                ", fileEnd=" + fileEnd +
                '}';
    }

    public void print() throws IOException {
        while (!fileEnd) {
            get_sym();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
        Iterator<Token> it = Compiler.tokens.iterator();
        while(it.hasNext()) {
            Token tmp = it.next();
            String ans = tmp.getSym() + " " + tmp.getStr()+"\n";
            System.out.print(ans);
            bw.write(ans);
        }
        bw.close();
    }

    public void get_sym() {
        String sec = "";
        while (isSpace(curChar) && !fileEnd) {
                next_char();
        }
        if (isDigit(curChar)) {
            sec += curChar;
            next_char();
            while (isDigit(curChar)) {
                sec += curChar;
                next_char();
            }
            Compiler.tokens.add(new Token("INTCON", sec, lineNum));
        } else if (isLetter(curChar) || curChar == '_') {
            sec += curChar;
            next_char();
            while (isDigit(curChar) || isLetter(curChar) || curChar == '_') {
                sec += curChar;
                next_char();
            }

//            System.out.println(sec);
//            System.out.println(is_reserve(sec).getKey());
//            String iden = is_reserve(sec).getValue().getClass().getSimpleName();
            if (is_reserve(sec) != null) {
                Symbol.sym iden = is_reserve(sec);//.getValue();
                if (iden.equals("PRINTFTK")) {
                    formatString = true;
                }
                Compiler.tokens.add(new Token(iden.toString(), sec, lineNum));
            } else {
                Compiler.tokens.add(new Token("IDENFR", sec, lineNum));
            }
        } else if (curChar == '!') {
            next_char();
            if (curChar == '=') {
                next_char();
                Compiler.tokens.add(new Token("NEQ", "!=", lineNum));
            } else {
                Compiler.tokens.add(new Token("NOT", "!", lineNum));
            }
        } else if (curChar == '&') {
            next_char();
            if (curChar == '&') {
                next_char();
                Compiler.tokens.add(new Token("AND", "&&", lineNum));
            }
        } else if (curChar == '|') {
            next_char();
            if (curChar == '|') {
                next_char();
                Compiler.tokens.add(new Token("OR", "||", lineNum));
            }
        } else if (curChar == '+') {
            next_char();
            Compiler.tokens.add(new Token("PLUS", "+", lineNum));
        } else if (curChar == '-') {
            next_char();
            Compiler.tokens.add(new Token("MINU", "-", lineNum));
        } else if (curChar == '*') {
            next_char();
            Compiler.tokens.add(new Token("MULT", "*", lineNum));
        } else if (curChar == '/') {
            next_char();
            if (curChar == '/') {
                charPos = length;
                next_char();
//                for (int i = 0; i < curLine.length() - charPos + 1; i++) {
//                    next_char();
//                }
            } else if (curChar == '*') {
                while (!fileEnd) {
                    next_char();
                    if(curChar == '*') {
                        next_char();
                        if(curChar == '/') {
                            next_char();
                            break;
                        }
                        roll_back();// in case /***/
                    }
                }
            } else {
                Compiler.tokens.add(new Token("DIV", "/", lineNum));
            }
        } else if (curChar == '%') {
            next_char();
            Compiler.tokens.add(new Token("MOD", "%", lineNum));
        } else if (curChar == '<') {
            next_char();
            if (curChar == '=') {
                next_char();
                Compiler.tokens.add(new Token("LEQ", "<=", lineNum));
            } else {
                Compiler.tokens.add(new Token("LSS", "<", lineNum));
            }
        } else if (curChar == '>') {
            next_char();
            if (curChar == '=') {
                next_char();
                Compiler.tokens.add(new Token("GEQ", ">=", lineNum));
            } else {
                Compiler.tokens.add(new Token("GRE", ">", lineNum));
            }
        } else if (curChar == '=') {
            next_char();
            if (curChar == '=') {
                next_char();
                Compiler.tokens.add(new Token("EQL", "==", lineNum));
            } else {
                Compiler.tokens.add(new Token("ASSIGN", "=", lineNum));
            }
        } else if (curChar == ';') {
            next_char();
            Compiler.tokens.add(new Token("SEMICN", ";", lineNum));
        } else if (curChar == ',') {
            next_char();
            Compiler.tokens.add(new Token("COMMA", ",", lineNum));
        } else if (curChar == '(') {
            next_char();
            Compiler.tokens.add(new Token("LPARENT", "(", lineNum));
        } else if (curChar == ')') {
            next_char();
            Compiler.tokens.add(new Token("RPARENT", ")", lineNum));
        } else if (curChar == '[') {
            next_char();
            Compiler.tokens.add(new Token("LBRACK", "[", lineNum));
        } else if (curChar == ']') {
            next_char();
            Compiler.tokens.add(new Token("RBRACK", "]", lineNum));
        } else if (curChar == '{') {
            next_char();
            Compiler.tokens.add(new Token("LBRACE", "{", lineNum));
        } else if (curChar == '}') {
            next_char();
            Compiler.tokens.add(new Token("RBRACE", "}", lineNum));
        } else if (curChar == '\"') {
            sec += curChar;
            next_char();
            while (curChar != '\"') {
                sec += curChar;
                next_char();
            }
            sec += curChar;
            next_char();
            formatString = false;
            Compiler.tokens.add(new Token("STRCON", sec, lineNum));
        }
//        if(charPos == length) {
//            System.out.println("charPos == length");
//            next_char();
//        }
//        if(!fileEnd) {
//            System.out.println("1:line"+lineNum+" charPos"+charPos+" line length "+length+" curChar is "+curChar);
//            roll_back();
//            lexer();
//        }
    }

    public void next_char() {
        String str = "";
        if(charPos < length) {
//            if(charPos == 0) {
//                lineNum++;
//            }
            curChar = curLine.charAt(charPos);
            System.out.println(curChar);
            charPos++;
//            System.out.println("charPos:"+charPos+" curChar:"+curChar+" cuLine:"+curLine);
        }
        else if(lineNum == Compiler.totalLine) {
            fileEnd = true;
        } else {
            System.out.println("lineNum" + lineNum);
            curLine = Compiler.file.get(lineNum);
            lineNum++;

            curChar = ' ';
            charPos = 0;
            length = curLine.length();

            System.out.println("charPos:"+charPos+" lineLength:"+length+" curLine:"+curLine);
        }
    }


    public void roll_back() {
        if (charPos > 0) {
            charPos--;
            curChar = curLine.charAt(charPos);
            /* charPos is head of the actual number in array pos for 1 */
        }
    }

    boolean isSpace(char c) {
        if (c == ' ' || c == '\t' || c == '\n') {
            return true;
        }
        return false;
    }

    boolean isLetter(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        }
        return false;
    }

    boolean isDigit(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        }

        return false;
    }

    Symbol.sym is_reserve(String str) {
        Symbol.sym symbol;
        symbol = Symbol.reserve.get(str);
//        if (symbol == null) {
//            Pair<Boolean, Symbol.sym> ret = new Pair<>(false, symbol);
//            return ret;
//        }
//        Pair<Boolean, Symbol.sym> ret = new Pair<>(true, symbol);
//        return ret;
        return symbol;
    }

}
