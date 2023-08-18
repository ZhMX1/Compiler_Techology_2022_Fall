package def;

import lexical.Token;

public class Symbol {
    private String type;
    private int dim;
    private String content;
    private int layerID;

    public Symbol(String type, int dim, Token token, int layerID) {
        this.type = type;
        this.dim = dim;
        this.content = token.getContent();
        this.layerID = layerID;
    }

    public String getType() {
        return type;
    }

    public int getIntType() {
        return dim;
    }

    public int getLayerID() {
        return layerID;
    }

    @Override
    public String toString() {
        return content;
    }
}
