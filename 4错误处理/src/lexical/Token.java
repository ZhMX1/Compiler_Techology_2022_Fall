package lexical;

public class Token {
    private Symbol.sym sym;
    private String str;
    private int lineNum;

    public Token(Symbol.sym symType, String content, int num) {
        sym = symType;
        str = content;
        lineNum = num;
    }

    public Symbol.sym getSym() {
        return sym;
    }

    public String getSymName() {
        return sym.name();
    }

    public String getStr() {
        return str;
    }

    public int getLineNum() {
        return lineNum;
    }
}
