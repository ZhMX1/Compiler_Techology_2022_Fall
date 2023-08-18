package syntax;

import lexical.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Syntactic {
    static ArrayList<String> ans = new ArrayList<>();
    static Vector<Token> synUsed = new Vector<>();

    private Token curToken;
    private int tokenPos = 0;
    private int totalToken = 0;
//    private boolean declEnd = false;

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
        System.out.println("moving into printSyntax");
        while(it.hasNext()) {
            String tmp = it.next();
            System.out.println(tmp);
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
        if(curToken.getSym() == Symbol.sym.INTTK) {
            next_token();
            ConstDef();
        }
        else {
            //handle error
        }
        while (curToken.getSym() == Symbol.sym.COMMA) {
            next_token();
            ConstDef();
        }
        is_token(Symbol.sym.SEMICN);
        pushSyn("ConstDecl");
    }

    private void ConstDef() {
        if(curToken.getSym() != Symbol.sym.IDENFR) {
            //handle error
        }
        else {
            next_token();
            while(curToken.getSym() == Symbol.sym.LBRACK) {
                next_token();
                ConstExp();
                if(curToken.getSym() != Symbol.sym.RBRACK) {
                    //handle error
                } else {
                    next_token();
                }
            }

            if (curToken.getSym() == Symbol.sym.ASSIGN) {
                next_token();
                ConstInitVal();
            }
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
        if(curToken.getSym() != Symbol.sym.INTTK) {
            //error handle
        }
        else {
            next_token();
            VarDef();
            while (curToken.getSym() == Symbol.sym.COMMA) {
                next_token();
                VarDef();
            }
            if(curToken.getSym() == Symbol.sym.SEMICN) {
                next_token();
            }
            else {
                //error handle
            }
        }
        pushSyn("VarDecl");
    }

    private void VarDef() {
        if(curToken.getSym() == Symbol.sym.IDENFR) {
            next_token();
            while(curToken.getSym() == Symbol.sym.LBRACK){
                next_token();
                ConstExp();
                if(curToken.getSym() == Symbol.sym.RBRACK) {
                    next_token();
                }else {
                    //handle error
                }
            }
            if (curToken.getSym() == Symbol.sym.ASSIGN) {
                next_token();
                InitVal();
            }
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
        while (curToken.getSym() == Symbol.sym.VOIDTK || curToken.getSym() == Symbol.sym.INTTK){
            next_token();
            if(curToken.getSym() == Symbol.sym.MAINTK) {
                prev_token();
                break;
            }// defining main function
            pushSyn("FuncType");
            is_token(Symbol.sym.IDENFR);
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                //handle error
            }
            else {
                next_token();
                if(curToken.getSym() != Symbol.sym.RPARENT) {
                    FuncFParams();
                }
                if(curToken.getSym() != Symbol.sym.RPARENT) {
                    //handle error
                }
                next_token();
                Block();
            }
            pushSyn("FuncDef");
        }
    }

    private void FuncFParam() {
        if(curToken.getSym() != Symbol.sym.INTTK) {
            //handle error
        }
        else {
            next_token();
            if(curToken.getSym() != Symbol.sym.IDENFR) {
                // handle error
            }
            else {
                next_token();
                if (curToken.getSym() == Symbol.sym.LBRACK) {
                    next_token();
                    if(curToken.getSym() != Symbol.sym.RBRACK) {
                        // handle error
                    } else {
                        next_token();
                        while (curToken.getSym() == Symbol.sym.LBRACK) {
                            next_token();
                            ConstExp();
                            if(curToken.getSym() != Symbol.sym.RBRACK) {
                                //handle error
                            } else {
                                next_token();
                            }
                        }
                    }
                }
            }
        }
        pushSyn("FuncFParam");
    }

    public void FuncFParams() {
        FuncFParam();
        while (curToken.getSym() == Symbol.sym.COMMA) {
            next_token();
            FuncFParam();
        }
        pushSyn("FuncFParams");
    }

    public void FuncRParams() {
        Exp();
        while (curToken.getSym() == Symbol.sym.COMMA) {
            next_token();
            Exp();
        }
        pushSyn("FuncRParams");
    }

    public void MainFuncDef(){
        is_token(Symbol.sym.INTTK);
        is_token(Symbol.sym.MAINTK);
        is_token(Symbol.sym.LPARENT);
        is_token(Symbol.sym.RPARENT);
        Block();
        pushSyn("MainFuncDef");
    }

    public void Block() {
        if(curToken.getSym() == Symbol.sym.LBRACE) {
            next_token();
            if(curToken.getSym() != Symbol.sym.RBRACE) {
                BlockItem();
            }

            if(curToken.getSym() != Symbol.sym.RBRACE) {
                //handle error
            }
            else {
                next_token();
            }
        }
        pushSyn("Block");
    }

    private void BlockItem() {
        boolean blockEnd = false;
        while (!blockEnd) {
            if (curToken.getSym() == Symbol.sym.RBRACE) {
                blockEnd = true;
            } else if (curToken.getSym() == Symbol.sym.CONSTTK || curToken.getSym() == Symbol.sym.INTTK) {
                Decl();
            } else {
                Stmt();
            }
        }
    }

    private void Stmt(){
        if(curToken.getSym() == Symbol.sym.IFTK) {
            next_token();
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                // handle error
            }
            else {
                next_token();
                Cond();

                if(curToken.getSym() != Symbol.sym.RPARENT) {
                    //handle error
                }
                else {
                    next_token();
                    Stmt();
                    if (curToken.getSym() == Symbol.sym.ELSETK) {
                        next_token();
                        Stmt();
                    }
                }
            }

        }
        else if(curToken.getSym() == Symbol.sym.WHILETK) {
            next_token();
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                // handle error
            }
            else {
                next_token();
                Cond();

                if(curToken.getSym() != Symbol.sym.RPARENT) {
                    //handle error
                } else {
                    next_token();
                    Stmt();
                }
            }
        }
        else if(curToken.getSym() == Symbol.sym.BREAKTK
        || curToken.getSym() == Symbol.sym.CONTINUETK) {
            next_token();
            if(curToken.getSym() != Symbol.sym.SEMICN) {
                //handle error
            } else {
                next_token();
            }
        }
        else if(curToken.getSym() == Symbol.sym.RETURNTK) {
            next_token();
            if(curToken.getSym() != Symbol.sym.SEMICN) {
                Exp();
            }
            if(curToken.getSym() != Symbol.sym.SEMICN) {
                //handle error
            }
            else {
                next_token();
            }

        }
        else if(curToken.getSym() == Symbol.sym.PRINTFTK) {
            next_token();
            is_token(Symbol.sym.LPARENT);
            is_token(Symbol.sym.STRCON);
            while(curToken.getSym() == Symbol.sym.COMMA) {
                next_token();
                Exp();
            }
            is_token(Symbol.sym.RPARENT);
            is_token(Symbol.sym.SEMICN);
        }
        else if(curToken.getSym() == Symbol.sym.LBRACE) {
            Block();
        }
        else if(curToken.getSym() == Symbol.sym.IDENFR) {
            next_token();
            int ansN = ans.size();
            int tokenN = tokenPos;
            if(curToken.getSym() != Symbol.sym.LPARENT) {
                //Unfinish
                prev_token();
                LVal();
                if(curToken.getSym() != Symbol.sym.ASSIGN) {
                    int tmpN = tokenPos;
                    for(int i = 0; i < (tmpN - tokenN)+1; i++) {
                        prev_token();
                    }
//                    tokenPos = tokenN;
//                    curToken = Lexer.tokens.get(tokenPos);
                    Exp();
                    is_token(Symbol.sym.SEMICN);
                }
                else {
                    //LVal '=' Exp ';'
                    //LVal '=' 'getint''('')'';'
                    next_token();
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
            else {
                prev_token();
                Exp();
                is_token(Symbol.sym.SEMICN);
            }
        }
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
            next_token();
            if(curToken.getSym() == Symbol.sym.LPARENT){
                next_token();
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
            if(curToken.getSym() == Symbol.sym.RPARENT) {
                next_token();
            } else {
                // handle error
            }
        }else if (curToken.getSym() == Symbol.sym.IDENFR) {
            LVal();
        }else if(curToken.getSym() == Symbol.sym.INTCON){
            next_token();
            pushSyn("Number");
        }
        pushSyn("PrimaryExp");
    }

    private void LVal() {
        is_token(Symbol.sym.IDENFR);
        while (curToken.getSym() == Symbol.sym.LBRACK){
            next_token();
            Exp();
            if(curToken.getSym() != Symbol.sym.RBRACK) {
                //handle_error
            }
            else {
                next_token();
            }
        }
        pushSyn("LVal");
    }

//    public String curTokenName() {
//        return curToken.getSymName();
//    }

    void pushToken(){
        ans.add( curToken.getSymName()+" "+curToken.getStr());
        System.out.println(curToken.getSymName()+" "+curToken.getStr());
    }

    void pushSyn(String syn) {
        ans.add("<"+syn+">");
        System.out.println("<"+syn+">");
    }

    void is_token(Symbol.sym token) {
        if(curToken.getSym() == token) {
            next_token();
        }
        else{
            //handle error
        }
    }

}
