package Error;

import lexical.Token;
import java.util.*;
import static Error.ErrorHandle.*;

public class IdenList {
    public static ArrayList<Iden> idenList = new ArrayList<>();

    public static void addIden(Token t, int layer, int d,
                               Iden.idenType it, Iden.BType bt) {
        Iden newIden = new Iden(t.getStr(), layer, d, it, bt);
        if(checkIden(newIden, layer, t.getLineNum())) {
            idenList.add(newIden);
        }
    }

    public static void addIden(Token t, int layer,
                               Iden.idenType it, Iden.BType rt,
                               ArrayList<Iden.param> params) {
        Iden newIden = new Iden(t.getStr(), layer, it, rt, params);
        if(checkIden(newIden, layer, t.getLineNum())) {
            idenList.add(newIden);
        }
//        idenList.add(newIden);
    }

    public static boolean checkIden(Iden iden, int layer, int lineNum) {
        int size = idenList.size();
        for(int i = size-1; i >= 0; i--) {
            Iden tmp = idenList.get(i);
            System.out.println("------check---------------"+tmp.str);
            if(tmp.layerID < layer) {
                break;
            }
            // 仅在同一个作用域下找重复定义
            if(tmp.str.equals(iden.str) &&
                    tmp.layerID >= iden.layerID) {
                errors.add(new ErrorType(lineNum, "b"));
                return false;
            }
        }
        return true;
    }

    public static boolean checkIdenExist(Iden iden, int lineNum, Iden.idenType it) {
        int size = idenList.size();
        for(int i = size-1; i >= 0; i--) {
            Iden tmp = idenList.get(i);
            if(tmp.str.equals(iden.str) &&
                    /*tmp.getType() == it &&*/
                    tmp.layerID <= iden.layerID) {
                    int n = tmp.getParams().size();
                    return true;
            }
        }
        errors.add(new ErrorType(lineNum, "c"));
        return false;
    }

    public static boolean checkConst(String name, int lineNum) {
        int size = idenList.size();
        for(int i = size-1; i >= 0; i--) {
            Iden tmp = idenList.get(i);
            if(tmp.str.equals(name)) {
                if(tmp.getType() == Iden.idenType.constVar) {
                    errors.add(new ErrorType(lineNum, "h"));
                    return false;
                }else if(tmp.getType() == Iden.idenType.var) {
                    return true;
                }
            }
        }
//        errors.add(new ErrorType(lineNum, "c"));
        return false;
    }

    public static void popIden(int layer) {
        Iterator<Iden> iterator = idenList.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().layerID >= layer){
                iterator.remove();
            }
        }
    }

}
