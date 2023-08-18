package syntax;

import lexical.*;
import Error.*;

import java.io.*;
import java.util.*;

public class Syntactic {
    static ArrayList<String> ans = new ArrayList<>();

    private Token curToken;
    private int tokenPos = 0;
    private int totalToken = 0;
//    private int lineNum = 0;
    private int blockLayer = 0;

    private int inCycle = 0;
    private int returnLayer = -1;
    private boolean doBlockReturn = false;
    private int atRightBrace = 0;

    private boolean funcExist;
    private String callFunc;
    private ArrayList<Iden.param> defParams = new ArrayList<>();
    private int curDim = 0;

    public Syntactic() {
        curToken = null;
        tokenPos = 0;
        totalToken = lexical.Lexer.tokens.size();
//        declEnd = false;
    }

    public void syntax() {
        next_token();
        CompUnit();
    }

    public void printSyntax() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
        Iterator<String> it = ans.iterator();
//        System.out.println("moving into printSyntax");
        while(it.hasNext()) {
            String tmp = it.next();
//            System.out.println(tmp);
            bw.write(tmp+"\n");
        }
        bw.close();
    }

    public void next_token() {
        if(curToken != null) {
            pushToken();
        }
        if(tokenPos < totalToken) {
            curToken = Lexer.tokens.get(tokenPos);
            tokenPos++;
        }else {
            curToken = new Token(Symbol.sym.SYMEND, "\0", 0);
        }

    }

    public void prev_token(){
        if(tokenPos > 0) {
            tokenPos--;
            curToken = Lexer.tokens.get(tokenPos - 1);
            int tmpN = ans.size() - 1;
            while(ans.get(tmpN).charAt(0) == '<') {
                ans.remove(tmpN);
                tmpN--;
            }// remove false parser branches
            ans.remove(ans.size()-1);
        }
    }

    public void CompUnit(){
//        boolean mainEnd = false;
        while(curToken.getSym() == Symbol.sym.INTTK ||
                curToken.getSym() == Symbol.sym.CONSTTK ||
                curToken.getSym() == Symbol.sym.VOIDTK){
//            if(declEnd) {
//                next_token();
//                if(curToken.getSym() == Symbol.sym.MAINTK){
//                    prev_token();
//                    MainFuncDef();
//                    mainEnd = true;
//                } else {
//                    prev_token();
//                    FuncDef();
//                }
//            }
            if(curToken.getSym() == Symbol.sym.VOIDTK) {
                FuncDef();
            } else if (curToken.getSym() == Symbol.sym.CONSTTK) {
                Decl();
            } else if (curToken.getSym() == Symbol.sym.INTTK) {
                next_token();
                if(curToken.getSym() == Symbol.sym.IDENFR) {
                    next_token();
                    if(curToken.getSym() == Symbol.sym.LPARENT) {
                        prev_token();
                        prev_token();
                        FuncDef();
                    } else {
                        prev_token();
                        prev_token();
                        Decl();
                    }
                }
                else if(curToken.getSym() == Symbol.sym.MAINTK) {
                    prev_token();
                    MainFuncDef();
//                    mainEnd = true;
                }
                else{
                    // error handle
                }
            }
        }
        pushSyn("CompUnit");
    }

    public void Decl() { // curToken at const or int (first decl token)
        while (curToken.getSym() == Symbol.sym.CONSTTK || curToken.getSym() == Symbol.sym.INTTK) {
            if(curToken.getSym() == Symbol.sym.CONSTTK) {
                ConstDecl();
            }
            else if (curToken.getSym() == Symbol.sym.INTTK) {
                next_token();
                if(curToken.getSym() == Symbol.sym.IDENFR) {
                    next_token();
                    if(curToken.getSym() == Symbol.sym.LPARENT) {
//                        declEnd = true;
                        prev_token();
                        prev_token();
                        break;
                    }
                    prev_token();
                    prev_token();
                    VarDecl();
                }
                else{
                    if(curToken.getSym() == Symbol.sym.MAINTK) {
                        prev_token();
//                        declEnd = true;
                        break;
                    }
                    else {
                        // error handle;
                    }
                }
            }
        }

    }

    private void ConstDecl() {
        is_token(Symbol.sym.CONSTTK) ;
        is_token(Symbol.sym.INTTK);
//        if(curToken.getSym() == Symbol.sym.INTTK) {
//            next_token();
//            ConstDef();
//        }
//        else {
//            //handle error
//        }
        ConstDef();
        while (curToken.getSym() == Symbol.sym.COMMA) {
            next_token();
            ConstDef();
        }
        is_token(Symbol.sym.SEMICN);
        pushSyn("ConstDecl");
    }

    private void ConstDef() {
        Token tmpToken = curToken;
        is_token(Symbol.sym.IDENFR);
        int cntDim = 0;
        while(curToken.getSym() == Symbol.sym.LBRACK) {
            next_token();
            ConstExp();
//            if(curToken.getSym() != Symbol.sym.RBRACK) {
//                //handle error
//            } else {
//                next_token();
//            }
            is_token(Symbol.sym.RBRACK);
            cntDim++;
        }
        IdenList.addIden(tmpToken, blockLayer, cntDim, Iden.idenType.constVar, Iden.BType.intType);
        if (curToken.getSym() == Symbol.sym.ASSIGN) {
            next_token();
            ConstInitVal();
        }

        pushSyn("ConstDef");
    }

    private void ConstInitVal() {
        if(curToken.getSym() == Symbol.sym.LBRACE) {
            next_token();
            if(curToken.getSym() != Symbol.sym.RBRACE) {
                ConstInitVal();
                while(curToken.getSym() == Symbol.sym.COMMA) {
                    next_token();
                    ConstInitVal();
                }
            }
            is_token(Symbol.sym.RBRACE);
        }
        else {
            ConstExp();
        }
        pushSyn("ConstInitVal");
    }

    private void VarDecl() {
        is_token(Symbol.sym.INTTK);
        VarDef();
        while (curToken.getSym() == Symbol.sym.COMMA) {
            next_token();
            VarDef();
        }
        is_token(Symbol.sym.SEMICN);
        pushSyn("VarDecl");
    }

    private void VarDef() {
        Token tmpToken = curToken;
        is_token(Symbol.sym.IDENFR);
        int cntDim = 0;
        while(curToken.getSym() == Symbol.sym.LBRACK){
            next_token();
            ConstExp();
//            if(curToken.getSym() == Symbol.sym.RBRACK) {
//                next_token();
//            }else {
//                //handle error
//            }
            is_token(Symbol.sym.RBRACK);
            cntDim++;
        }
        IdenList.addIden(tmpToken, blockLayer, cntDim, Iden.idenType.var, Iden.BType.intType);
        if (curToken.getSym() == Symbol.sym.ASSIGN) {
            next_token();
            InitVal();
        }
        pushSyn("VarDef");
    }

    private void InitVal() {
        if(curToken.getSym() == Symbol.sym.LBRACE) {
            next_token();
            if(curToken.getSym() != Symbol.sym.RBRACE) {
                InitVal();
                while(curToken.getSym() == Symbol.sym.COMMA) {
                    next_token();
                    InitVal();
                }
            }
            is_token(Symbol.sym.RBRACE);
        }
        else {
            Exp();
        }
        pushSyn("InitVal");
    }

    private void Exp() {
        AddExp();
        pushSyn("Exp");
    }

    private void ConstExp() {
        AddExp();
        pushSyn("ConstExp");
    }

    public void FuncDef() {
        while (curToken.getSym() == Symbol.sym.VOIDTK || curToken.getSym() == Symbol.sym.INTTK) {
            boolean needReturn = true;
            Iden.BType rt = Iden.BType.intType;
            if(curToken.getSym() == Symbol.sym.VOIDTK) {
                needReturn = false;
                rt = Iden.BType.voidType;
            }

            next_token();
            if(curToken.getSym() == Symbol.sym.MAINTK) {
                prev_token();
                break;
            }// defining main function
            pushSyn("FuncType");
            Token tmpToken = curToken;
            is_token(Symbol.sym.IDENFR);
            is_token(Symbol.sym.LPARENT);

            ArrayList<Iden.param> params = new ArrayList<>();

            int cl = 0;
            if(curToken.getSym() != Symbol.sym.RPARENT) {
                cl = curToken.getLineNum();
                FuncFParams(params);
            }
//            if(curToken.getSym() != Symbol.sym.RPARENT) {
//                //handle error
//            }
//            next_token();
            is_token(Symbol.sym.RPARENT);
            //add function define with params into idenList
            IdenList.addIden(tmpToken, blockLayer, Iden.idenType.func, rt, params);

            //add params into idenList
            int tmpN = params.size();
            for(int i = 0; i < tmpN; i++) {
                Token tmp = new Token(Symbol.sym.INTTK,
                        params.get(i).getName(),
                        cl);
                IdenList.addIden(tmp, blockLayer + 1, params.get(i).getDim(),
                        Iden.idenType.var, Iden.BType.intType);
            }

            Block(needReturn, false);
            if(needReturn && (!doBlockReturn || returnLayer != blockLayer+1)) {
                add_error(atRightBrace, "g");
            }
            doBlockReturn = false;
            pushSyn("FuncDef");
        }
    }

    public void FuncFParams(ArrayList<Iden.param> params) {
        Iden.param tmp1 = FuncFParam();
        params.add(tmp1);
        while (curToken.getSym() == Symbol.sym.COMMA) {
            next_token();
            Iden.param tmp = FuncFParam();
            params.add(tmp);
        }
        pushSyn("FuncFParams");
    }

    private Iden.param FuncFParam() {
        is_token(Symbol.sym.INTTK);
        String name = curToken.getStr();
        int dim = 0;
        if(curToken.getSym() != Symbol.sym.IDENFR) {
            // handle error
        }
        else {
            next_token();
            if (curToken.getSym() == Symbol.sym.LBRACK) {
                next_token();
                is_token(Symbol.sym.RBRACK);
                dim++;
                while (curToken.getSym() == Symbol.sym.LBRACK) {
                    next_token();
                    ConstExp();
                    is_token(Symbol.sym.RBRACK);
                    dim++;
                }
            }
        }
        pushSyn("FuncFParam");
        Iden.param tmp = new Iden.param(name, dim);
        return tmp;
    }

    public void FuncRParams() {
        ArrayList<Iden.param> thisParams = defParams;
        Token tmp = curToken;
        int tmpPos = tokenPos;
        int size = defParams.size();
        System.out.println("-----size------"+size);
        int cnt = 0;
        boolean paramErr = false;
        Exp();
        if(funcExist && (cnt < size)) {
            System.out.println(cnt + " " + size);
            while(tmp.getSym() != Symbol.sym.INTCON && tmp.getSym() != Symbol.sym.IDENFR) {
                System.out.println(tmpPos);
                System.out.println(tmp.getSymName() + tmp.getStr());
                tmp = lexical.Lexer.tokens.get(tmpPos++);
            }
            paramErr = checkParam(tmp, thisParams.get(cnt));
        }
        while (curToken.getSym() == Symbol.sym.COMMA) {
            cnt++;
            next_token();
            tmp = curToken;
            Exp();
            if(funcExist && cnt < size && !paramErr) {
                paramErr = checkParam(tmp, thisParams.get(cnt));
            }
        }
        if(funcExist && size!= 0 && cnt+1 != size) {
            add_error(tmp.getLineNum(), "d");
        }
        pushSyn("FuncRParams");
    }

    public void MainFuncDef(){
        is_token(Symbol.sym.INTTK);

        Token tmpToken = curToken;
        is_token(Symbol.sym.MAINTK);

        is_token(Symbol.sym.LPARENT);
        is_token(Symbol.sym.RPARENT);
        IdenList.addIden(tmpToken, blockLayer, 0, Iden.idenType.func, Iden.BType.intType);
        Block(true, false);
        if(!doBlockReturn || returnLayer != blockLayer+1) {
            add_error(atRightBrace, "g");
        }
        doBlockReturn = false;
        pushSyn("MainFuncDef");
    }

    public void Block(boolean needReturn, boolean isCycle) {
        is_token(Symbol.sym.LBRACE);
        blockLayer++;
        if(curToken.getSym() != Symbol.sym.RBRACE) {
            BlockItem(needReturn, isCycle);
        }else {
//            if(needReturn && !doBlockReturn) {
//                add_error(curToken.getLineNum(), "g");
//            }
        }
        atRightBrace = curToken.getLineNum();
        is_token(Symbol.sym.RBRACE);

        IdenList.popIden(blockLayer);
        blockLayer--;
//            if(curToken.getSym() != Symbol.sym.RBRACE) {
//                //handle error
//            }
//            else {
//                next_token();
//            }
        pushSyn("Block");
    }

    private void BlockItem(boolean needReturn, boolean isCycle) {
        boolean blockEnd = false;
        while (!blockEnd) {
            if (curToken.getSym() == Symbol.sym.RBRACE) {
                blockEnd = true;
            } else if (curToken.getSym() == Symbol.sym.CONSTTK || curToken.getSym() == Symbol.sym.INTTK) {
                Decl();
            } else {
                Stmt(needReturn, isCycle);
            }
        }
    }

    private void Stmt(boolean needReturn, boolean isCycle){
//        if statement
        if(curToken.getSym() == Symbol.sym.IFTK) {
            next_token();
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                // handle error
            }
            else {
                next_token();
                Cond();
                is_token(Symbol.sym.RPARENT);
//                Stmt(false, false);
                Stmt(needReturn, isCycle);
                if (curToken.getSym() == Symbol.sym.ELSETK) {
                    next_token();
//                    Stmt(false, false);
                    Stmt(needReturn, isCycle);
                }
            }

        }

//        while statement
        else if(curToken.getSym() == Symbol.sym.WHILETK) {
            inCycle++;
            next_token();
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                // handle error
            }
            else {
                next_token();
                Cond();
//                if(curToken.getSym() != Symbol.sym.RPARENT) {
//                    //handle error
//                } else {
//                    next_token();
                is_token(Symbol.sym.RPARENT);
                Stmt(needReturn, true);
                inCycle--;
            }
        }

//        break & continue statement
        else if(curToken.getSym() == Symbol.sym.BREAKTK
        || curToken.getSym() == Symbol.sym.CONTINUETK) {
            if(inCycle == 0) {
                add_error(curToken.getLineNum(), "m");
            }
            next_token();
            is_token(Symbol.sym.SEMICN);
        }

//        return statement
        else if(curToken.getSym() == Symbol.sym.RETURNTK) {
            doBlockReturn = true;
            int cl = curToken.getLineNum();
            returnLayer = blockLayer;
            next_token();
            if(needReturn && curToken.getSym() != Symbol.sym.SEMICN) {
                Exp();
                is_token(Symbol.sym.SEMICN);
            }
            else {
                if(curToken.getSym() != Symbol.sym.SEMICN) {
                    // judge whether it is missing a SEMICN or an extra RETURNTK
                    if(curToken.getLineNum() - cl == 0) {
                        add_error(curToken.getLineNum(), "f");
                        Exp();
                    }
                }
                is_token(Symbol.sym.SEMICN);
            }
        }

//        print statement
        else if(curToken.getSym() == Symbol.sym.PRINTFTK) {
            int cl = curToken.getLineNum();
            next_token();
            is_token(Symbol.sym.LPARENT);
            int declNum = 0, realNum = 0;
            if(curToken.getSym() != Symbol.sym.STRCON) {
                ErrorHandle.error(curToken.getLineNum());
            } else {
                declNum = ErrorHandle.typeA(curToken.getStr(), curToken.getLineNum());
            }

            next_token();
            while(curToken.getSym() == Symbol.sym.COMMA) {
                realNum++;
                next_token();
                Exp();
            }
            if(realNum != declNum) {
                add_error(cl, "l");
            }
            is_token(Symbol.sym.RPARENT);
            is_token(Symbol.sym.SEMICN);
        }

//        into block
        else if(curToken.getSym() == Symbol.sym.LBRACE) {
            Block(needReturn, isCycle);
        }

//        first token is IDENFR, has 3 cases
//        LVal '=' Exp ';'
//        Exp
//        LVal '=' 'getint''('')'';'
        else if(curToken.getSym() == Symbol.sym.IDENFR) {
            Token tmp = curToken;
            next_token();

            int tokenN = tokenPos;
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                prev_token();
                int errCnt1 = ErrorHandle.errors.size();
                LVal();
                if(curToken.getSym() != Symbol.sym.ASSIGN) {
                    int tmpN = tokenPos;
                    for(int i = 0; i < (tmpN - tokenN)+1; i++) {
                        prev_token();
                    }
//                    tokenPos = tokenN;
//                    curToken = Lexer.tokens.get(tokenPos);
                    Exp();
                    int errCnt2 = ErrorHandle.errors.size();
//                    System.out.println("-------redundant check-----------"+(errCnt2 - errCnt1));
//                    System.out.println(ErrorHandle.errors.get(errCnt1).toString());
//                    System.out.println(ErrorHandle.errors.get(errCnt1+1).toString());
                    if(errCnt1 != errCnt2){
                        ErrorHandle.errors.remove(errCnt2 - 1);
                    }
                    is_token(Symbol.sym.SEMICN);
                }
                else {
                    System.out.println("---check const----"+tmp.getStr());
                    //LVal '=' Exp ';'
                    //LVal '=' 'getint''('')'';'
                    next_token();
                    IdenList.checkConst(tmp.getStr(), tmp.getLineNum());
                    if(curToken.getSym() == Symbol.sym.GETINTTK) {
                        next_token();
                        is_token(Symbol.sym.LPARENT);
                        is_token(Symbol.sym.RPARENT);
                    }
                    else {
                        Exp();
                    }
                    is_token(Symbol.sym.SEMICN);
                }
            }
//          Exp → AddExp → MulExp → UnaryExp → Ident '(' [FuncRParams] ')'
            else {
                prev_token();
                Exp();
                is_token(Symbol.sym.SEMICN);
            }
        }

//      Exp conditions start with:
//      PrimaryExp(PrimaryExp → '(' Exp ')' | Number),
        else if (curToken.getSym() == Symbol.sym.LPARENT ||
                curToken.getSym() == Symbol.sym.INTTK ||
                curToken.getSym() == Symbol.sym.INTCON ||
                curToken.getSym() == Symbol.sym.PLUS ||
                curToken.getSym() == Symbol.sym.MINU ||
                curToken.getSym() == Symbol.sym.NOT) {
            Exp();
            is_token(Symbol.sym.SEMICN);
        } else {
            is_token(Symbol.sym.SEMICN);
        }
        pushSyn("Stmt");
    }

    private void Cond() {
        LOrExp();
        pushSyn("Cond");
    }

    private void LOrExp() {
        LAndExp();
        pushSyn("LOrExp");
        while(curToken.getSym() == Symbol.sym.OR) {
            next_token();
            LAndExp();
            pushSyn("LOrExp");
        }
    }

    private void LAndExp() {
        EqExp();
        pushSyn("LAndExp");
        while(curToken.getSym() == Symbol.sym.AND) {
            next_token();
            EqExp();
            pushSyn("LAndExp");
        }
    }

    private void EqExp() {
        RelExp();
        pushSyn("EqExp");
        while(curToken.getSym() == Symbol.sym.EQL ||
                curToken.getSym() == Symbol.sym.NEQ) {
            next_token();
            RelExp();
            pushSyn("EqExp");
        }
    }

    private void RelExp() {
        AddExp();
        pushSyn("RelExp");
        while(curToken.getSym() == Symbol.sym.LSS ||
                curToken.getSym() == Symbol.sym.GRE ||
                curToken.getSym() == Symbol.sym.LEQ ||
                curToken.getSym() == Symbol.sym.GEQ ) {
            next_token();
            AddExp();
            pushSyn("RelExp");
        }
    }

    private void AddExp(){
        MulExp();
        pushSyn("AddExp");
        while(curToken.getSym() == Symbol.sym.PLUS ||
                curToken.getSym() == Symbol.sym.MINU) {
            next_token();
            MulExp();
            pushSyn("AddExp");
        }
    }

    private void MulExp() {
        UnaryExp();
        pushSyn("MulExp");
        while(curToken.getSym() == Symbol.sym.MULT ||
                curToken.getSym() == Symbol.sym.DIV ||
                curToken.getSym() == Symbol.sym.MOD) {
            next_token();
            UnaryExp();
            pushSyn("MulExp");
        }
    }

    private void UnaryExp() {
        if(curToken.getSym() == Symbol.sym.IDENFR) {
            Token tmpToken = curToken;
            next_token();
            if(curToken.getSym() == Symbol.sym.LPARENT){
                Iden newIden = new Iden(tmpToken.getStr(), blockLayer,
                        Iden.idenType.func, Iden.BType.intType);
                funcExist = IdenList.checkIdenExist(newIden, tmpToken.getLineNum(), Iden.idenType.func);
                callFunc = tmpToken.getStr();
                int paramN = getDefParam();
                next_token();
                if(funcExist && paramN != 0 && curToken.getSym() == Symbol.sym.RPARENT) {
                    add_error(tmpToken.getLineNum(), "d");
                }
                if(funcExist && paramN == 0 && curToken.getSym() != Symbol.sym.RPARENT) {
                    add_error(tmpToken.getLineNum(), "d");
                }
                if (curToken.getSym() != Symbol.sym.RPARENT) {
                    FuncRParams();
                }

                is_token(Symbol.sym.RPARENT);
            }else {
                prev_token();
                PrimaryExp();
            }
        }
        else if (curToken.getSym() == Symbol.sym.PLUS ||
                curToken.getSym() == Symbol.sym.MINU ||
                curToken.getSym() == Symbol.sym.NOT) {
            next_token();
            pushSyn("UnaryOp");
            UnaryExp();
        } else {
            PrimaryExp();
        }
        pushSyn("UnaryExp");
    }

    private void PrimaryExp() {
        if(curToken.getSym() == Symbol.sym.LPARENT) {
            next_token();
            Exp();
//            if(curToken.getSym() == Symbol.sym.RPARENT) {
//                next_token();
//            } else {
//                // handle error
//            }
            is_token(Symbol.sym.RPARENT);
        }else if (curToken.getSym() == Symbol.sym.IDENFR) {
            LVal();
        }else if(curToken.getSym() == Symbol.sym.INTCON){
            next_token();
            pushSyn("Number");
        }
        pushSyn("PrimaryExp");
    }

    private void LVal() {
        Token tmpToken = curToken;
        is_token(Symbol.sym.IDENFR);
        int cntDim = 0;
        curDim = cntDim;
        while (curToken.getSym() == Symbol.sym.LBRACK){
            next_token();
            Exp();
            is_token(Symbol.sym.RBRACK);
            cntDim++;
        }
        curDim = cntDim;
        Iden newIden = new Iden(tmpToken.getStr(), blockLayer, cntDim,
                Iden.idenType.NULL, Iden.BType.intType);
        IdenList.checkIdenExist(newIden, tmpToken.getLineNum(), Iden.idenType.NULL);
        pushSyn("LVal");
    }

    void pushToken(){
        ans.add( curToken.getSymName()+" "+curToken.getStr());
        System.out.println(curToken.getSymName()+" "+curToken.getStr());
    }

    void pushSyn(String syn) {
        ans.add("<"+syn+">");
//        System.out.println("<"+syn+">");
    }

    boolean is_token(Symbol.sym token) {
        if(curToken.getSym() == token) {
            next_token();
            return true;
        }
        else{
            lexical.Token tmp = Lexer.tokens.get(tokenPos - 2);
            if (token == Symbol.sym.SEMICN) {
                add_error(tmp.getLineNum(), "i");
//                next_token();
            }
            else if(token == Symbol.sym.RPARENT) {
                add_error(tmp.getLineNum(), "j");
//                next_token();
            }
            else if(token == Symbol.sym.RBRACK) {
                add_error(tmp.getLineNum(), "k");
//                next_token();
            }
            return false;

        }
    }

    void add_error(int num, String type) {
        ErrorHandle.errors.add(new ErrorType(num, type));
    }

    int getDefParam() {
        int size = IdenList.idenList.size();
        for(int i = size-1; i >= 0; i--) {
            Iden tmp = IdenList.idenList.get(i);
            if(tmp.getType() == Iden.idenType.func && tmp.getStr().equals(callFunc)){
                defParams = tmp.getParams();
                return tmp.getParams().size();
            }
        }
        return 0;
    }

    boolean checkParam(Token token, Iden.param param){
        if(token.getSym() == Symbol.sym.INTCON ){
            if(param.getDim() != 0 ) {
                add_error(token.getLineNum(), "e");
                return false;
            }
            return true;
        }

        int size = IdenList.idenList.size();
        for(int i = 0; i < size; i++) {
            Iden tmp = IdenList.idenList.get(i);
            System.out.println("--------------checkParam--------------");
            System.out.println(tmp.getStr()+" "+tmp.getBtype());
            if(tmp.getStr().equals(token.getStr())) {
                if(tmp.getBtype() != Iden.BType.intType ||
                        tmp.getDim()-curDim != param.getDim()) {
                    add_error(token.getLineNum(), "e");
                    return false;
                }else {

                    return true;
                }
            }
        }
//        add_error(token.getLineNum(), "e");
        return false;
    }
}
