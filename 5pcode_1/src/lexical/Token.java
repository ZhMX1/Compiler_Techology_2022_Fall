package lexical;

public class Token {
    private Sym.sym sym;
    private String content;
    private int lineNum;

    public Token(Sym.sym symType, String content, int num) {
        this.sym = symType;
        this.content = content;
        this.lineNum = num;
    }

    public Sym.sym getSym() {
        return sym;
    }

    public String getSymName() {
        return sym.name();
    }

    public String getContent() {
        return content;
    }

    public int getLineNum() {
        return lineNum;
    }

//    public boolean typeSymbolizeStmt() {
//        return this.sym == Sym.sym.IDENFR
//                || this.sym == Sym.sym.LBRACE
//                || this.sym == Sym.sym.IFTK
//                || this.sym == Sym.sym.ELSETK
//                || this.sym == Sym.sym.WHILETK
//                || this.sym == Sym.sym.BREAKTK
//                || this.sym == Sym.sym.CONTINUETK
//                || this.sym == Sym.sym.RETURNTK
//                || this.sym == Sym.sym.PRINTFTK
//                || this.sym == Sym.sym.SEMICN
//                || typeSymbolizeBeginOfExp();
//    }
//
//    public boolean typeSymbolizeValidateStmt() {
//        return this.sym == Sym.sym.IFTK
//                || this.sym == Sym.sym.ELSETK
//                || this.sym == Sym.sym.WHILETK
//                || this.sym == Sym.sym.BREAKTK
//                || this.sym == Sym.sym.CONTINUETK
//                || this.sym == Sym.sym.RETURNTK
//                || this.sym == Sym.sym.PRINTFTK
//                || this.sym == Sym.sym.SEMICN;
//    }
//
//    public boolean typeSymbolizeBeginOfExp() {
//        return this.sym == Sym.sym.LPARENT
//                || this.sym == Sym.sym.IDENFR
//                || this.sym == Sym.sym.INTCON
//                || this.sym == Sym.sym.NOT
//                || this.sym == Sym.sym.PLUS
//                || this.sym == Sym.sym.MINU;
//    }
//
//    public boolean typeOfUnary() {
//        return this.sym == Sym.sym.PLUS
//                || this.sym == Sym.sym.MINU
//                || this.sym == Sym.sym.NOT;
//    }
//
//    public boolean typeOfNotInExp() {
//        return this.sym== Sym.sym.CONSTTK
//                || this.sym== Sym.sym.INTTK
//                || this.sym== Sym.sym.BREAKTK
//                || this.sym== Sym.sym.CONTINUETK
//                || this.sym== Sym.sym.IFTK
//                || this.sym== Sym.sym.ELSETK
//                || this.sym== Sym.sym.WHILETK
//                || this.sym== Sym.sym.GETINTTK
//                || this.sym== Sym.sym.PRINTFTK
//                || this.sym== Sym.sym.RETURNTK;
//    }
}
