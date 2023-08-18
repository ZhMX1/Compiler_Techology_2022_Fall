package Error;

import java.util.ArrayList;

public class Iden {
    String str;                 // content
    int layerID;                // function layers number
    int dim = 0;                // recorde array dimension
    idenType type = idenType.NULL;      // big type
    BType btype = BType.unknown;        // 变量类型
//    BType returnType = BType.unknown;   // return type for function
    ArrayList<param> params = new ArrayList<>(); //save params

    public idenType getType() {
        return type;
    }

    public BType getBtype() {
        return btype;
    }

    public String getStr() {
        return str;
    }

    public int getDim() {
        return dim;
    }

    public ArrayList<param> getParams() {
        return params;
    }

    public Iden(String s, int layer, idenType it, BType bt) {
        str = s;
        layerID = layer;
        type = it;
        btype = bt;
    } // normal var & constVar

    public Iden(String s, int layer, int d, idenType it, BType bt) {
        str = s;
        layerID = layer;
        dim = d;
        type = it;
        btype = bt;
    } // array var & constVar

    public Iden(String s, int layer, idenType it, BType bt, ArrayList<param> p) {
        str = s;
        layerID = layer;
        type = it;
        btype = bt; // record return type
        params = (ArrayList<param>) p.clone();
    } // for function

    public enum idenType {
        var, constVar, func, returnParam, NULL
    };
    public enum BType {
        intType, voidType, unknown
    };

    public static class param {
        String name;
        int dim = 0;

        public param(String n, int d) {
            name = n;
            dim = d;
        }

        public String getName() {
            return name;
        }

        public int getDim() {
            return dim;
        }
    }
}
