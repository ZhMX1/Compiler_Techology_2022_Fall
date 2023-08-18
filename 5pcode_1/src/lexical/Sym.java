package lexical;

import java.util.HashMap;
import java.util.Map;

public class Sym {
    public enum sym {
        IDENFR, INTCON, STRCON, MAINTK, CONSTTK,
        INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
        NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK,
        RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV,
        MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN,
        SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
        RBRACK, LBRACE, RBRACE, NONETYPE, SYMEND;

        public String toString() {
            return this.name();
        }
    }

    static Map<String, sym> reserve = new HashMap<>();

    static {
        reserve.put("main", sym.MAINTK);
        reserve.put("const", sym.CONSTTK);
        reserve.put("int", sym.INTTK);
        reserve.put("break", sym.BREAKTK);
        reserve.put("continue", sym.CONTINUETK);
        reserve.put("if", sym.IFTK);
        reserve.put("else", sym.ELSETK);
        reserve.put("!", sym.NOT);
        reserve.put("&&", sym.AND);
        reserve.put("||", sym.OR);
        reserve.put("while", sym.WHILETK);
        reserve.put("getint", sym.GETINTTK);
        reserve.put("printf", sym.PRINTFTK);
        reserve.put("return", sym.RETURNTK);
        reserve.put("+", sym.PLUS);
        reserve.put("-", sym.MINU);
        reserve.put("void", sym.VOIDTK);
        reserve.put("*", sym.MULT);
        reserve.put("/", sym.DIV);
        reserve.put("%", sym.MOD);
        reserve.put("<", sym.LSS);
        reserve.put("<=", sym.LEQ);
        reserve.put(">", sym.GRE);
        reserve.put(">=", sym.GEQ);
        reserve.put("==", sym.EQL);
        reserve.put("!=", sym.NEQ);
        reserve.put("=", sym.ASSIGN);
        reserve.put(";", sym.SEMICN);
        reserve.put(",", sym.COMMA);
        reserve.put("(", sym.LPARENT);
        reserve.put(")", sym.RPARENT);
        reserve.put("[", sym.LBRACK);
        reserve.put("]", sym.RBRACK);
        reserve.put("{", sym.LBRACE);
        reserve.put("}", sym.RBRACE);
        reserve.put("", sym.NONETYPE);
    }


}
