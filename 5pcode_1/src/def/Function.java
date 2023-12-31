package def;

import lexical.Token;

import java.util.ArrayList;

public class Function {
    private String content;
    private String returnType;
    private ArrayList<Integer> paras;

    public Function(Token token, String returnType) {
        this.content = token.getContent();
        this.returnType = returnType;
    }

    public ArrayList<Integer> getParas() {
        return paras;
    }

    public void setParas(ArrayList<Integer> paras) {
        this.paras = paras;
    }

    public String getContent() {
        return content;
    }

    public String getReturnType() {
        return returnType;
    }

}
