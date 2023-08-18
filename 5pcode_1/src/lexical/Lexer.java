package lexical;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class Lexer {
    public static boolean fileEnd = false;
    public static boolean formatString = false;
    public static Vector<Token> tokens = new Vector<>();
    static ArrayList<String> file = new ArrayList<>();

    private String curLine;
    private char curChar;
    private int lineNum = 0;
    private int totalLine = 0;
    private int charPos = 0;
    private int length = 0;

    public Lexer(int tl, ArrayList<String> al){
        file = (ArrayList<String>) al.clone();
        curLine = "";
        curChar = ' ';
        lineNum = 0;
        totalLine = tl;
        charPos = 0;
        length = 0;
        fileEnd = false;

//        System.out.println(file);
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

    public void printLexer() throws IOException {
//        while (!fileEnd) {
//            get_sym();
//        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
        Iterator<Token> it = tokens.iterator();
        while(it.hasNext()) {
            Token tmp = it.next();
            String ans = tmp.getSym() + " " + tmp.getContent()+"\n";
            System.out.print(ans);
            bw.write(ans);
        }
        bw.close();
    }

    public void lexer() {
        while (!fileEnd) {
            get_sym();
        }
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
            tokens.add(new Token(Sym.sym.INTCON, sec, lineNum));
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
                Sym.sym iden = is_reserve(sec);//.getValue();
                if (iden.equals("PRINTFTK")) {
                    formatString = true;
                }
                tokens.add(new Token(iden, sec, lineNum));
            } else {
                tokens.add(new Token(Sym.sym.IDENFR, sec, lineNum));
            }
        } else if (curChar == '!') {
            next_char();
            if (curChar == '=') {
                next_char();
                tokens.add(new Token(Sym.sym.NEQ, "!=", lineNum));
            } else {
                tokens.add(new Token(Sym.sym.NOT, "!", lineNum));
            }
        } else if (curChar == '&') {
            next_char();
            if (curChar == '&') {
                next_char();
                tokens.add(new Token(Sym.sym.AND, "&&", lineNum));
            }
        } else if (curChar == '|') {
            next_char();
            if (curChar == '|') {
                next_char();
                tokens.add(new Token(Sym.sym.OR, "||", lineNum));
            }
        } else if (curChar == '+') {
            next_char();
            tokens.add(new Token(Sym.sym.PLUS, "+", lineNum));
        } else if (curChar == '-') {
            next_char();
            tokens.add(new Token(Sym.sym.MINU, "-", lineNum));
        } else if (curChar == '*') {
            next_char();
            tokens.add(new Token(Sym.sym.MULT, "*", lineNum));
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
                tokens.add(new Token(Sym.sym.DIV, "/", lineNum));
            }
        } else if (curChar == '%') {
            next_char();
            tokens.add(new Token(Sym.sym.MOD, "%", lineNum));
        } else if (curChar == '<') {
            next_char();
            if (curChar == '=') {
                next_char();
                tokens.add(new Token(Sym.sym.LEQ, "<=", lineNum));
            } else {
                tokens.add(new Token(Sym.sym.LSS, "<", lineNum));
            }
        } else if (curChar == '>') {
            next_char();
            if (curChar == '=') {
                next_char();
                tokens.add(new Token(Sym.sym.GEQ, ">=", lineNum));
            } else {
                tokens.add(new Token(Sym.sym.GRE, ">", lineNum));
            }
        } else if (curChar == '=') {
            next_char();
            if (curChar == '=') {
                next_char();
                tokens.add(new Token(Sym.sym.EQL, "==", lineNum));
            } else {
                tokens.add(new Token(Sym.sym.ASSIGN, "=", lineNum));
            }
        } else if (curChar == ';') {
            next_char();
            tokens.add(new Token(Sym.sym.SEMICN, ";", lineNum));
        } else if (curChar == ',') {
            next_char();
            tokens.add(new Token(Sym.sym.COMMA, ",", lineNum));
        } else if (curChar == '(') {
            next_char();
            tokens.add(new Token(Sym.sym.LPARENT, "(", lineNum));
        } else if (curChar == ')') {
            next_char();
            tokens.add(new Token(Sym.sym.RPARENT, ")", lineNum));
        } else if (curChar == '[') {
            next_char();
            tokens.add(new Token(Sym.sym.LBRACK, "[", lineNum));
        } else if (curChar == ']') {
            next_char();
            tokens.add(new Token(Sym.sym.RBRACK, "]", lineNum));
        } else if (curChar == '{') {
            next_char();
            tokens.add(new Token(Sym.sym.LBRACE, "{", lineNum));
        } else if (curChar == '}') {
            next_char();
            tokens.add(new Token(Sym.sym.RBRACE, "}", lineNum));
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
            tokens.add(new Token(Sym.sym.STRCON, sec, lineNum));
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
        if(charPos < length) {
//            if(charPos == 0) {
//                lineNum++;
//            }
            curChar = curLine.charAt(charPos);
//            System.out.println(curChar);
            charPos++;
//            System.out.println("charPos:"+charPos+" curChar:"+curChar+" cuLine:"+curLine);
        }
        else if(lineNum == totalLine) {
            fileEnd = true;
        } else {
            System.out.println("lineNum" + lineNum);
            curLine = file.get(lineNum);
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

    Sym.sym is_reserve(String str) {
        Sym.sym symbol;
        symbol = Sym.reserve.get(str);
//        if (symbol == null) {
//            Pair<Boolean, Sym.sym> ret = new Pair<>(false, symbol);
//            return ret;
//        }
//        Pair<Boolean, Sym.sym> ret = new Pair<>(true, symbol);
//        return ret;
        return symbol;
    }

}
