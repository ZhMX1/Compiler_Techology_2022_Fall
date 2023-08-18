public class Token {
    private String sym;
    private String str;
    private int lineNum;

    public Token(String symType, String content, int num) {
        sym = symType;
        str = content;
        lineNum = num;
    }

    public String getSym() {
        return sym;
    }

    public String getStr() {
        return str;
    }

    public int getLineNum() {
        return lineNum;
    }
}
