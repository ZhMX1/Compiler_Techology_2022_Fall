package def;

import lexical.Token;

import java.util.HashMap;

public class Symbols {
    private HashMap<String, Symbol> symbolHashMap;

    public Symbols() {
        symbolHashMap = new HashMap<>();
    }

    public void addSymbol(String type, int cntDim, Token token, int layerID) {
        symbolHashMap.put(token.getContent(), new Symbol(type, cntDim, token, layerID));
    }

    public boolean hasSymbol(Token token) {
        return symbolHashMap.containsKey(token.getContent());
    }

    public Symbol getSymbol(Token token) {
        return symbolHashMap.get(token.getContent());
    }

    public boolean isConst(Token token) {
        return symbolHashMap.get(token.getContent()).getType().equals("const");
    }

    @Override
    public String toString() {
        return symbolHashMap.toString();
    }
}
