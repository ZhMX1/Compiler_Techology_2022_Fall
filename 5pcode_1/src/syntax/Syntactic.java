package syntax;

import def.*;
import Generator.*;
import lexical.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Syntactic {
    static ArrayList<String> syn = new ArrayList<>();

    private Token curToken;
    private int tokenPos = 0;
    private int totalToken = 0;

    private HashMap<Integer, Symbols> symbols = new HashMap<>();
    private HashMap<String, Function> functions = new HashMap<>();
    private static ArrayList<Pcode> codes = new ArrayList<>();
    private int layer = -1;
    private int layerID = -1;
//    private boolean needReturn = false;
//    private int whileFlag = 0;

    private LabelGenerator labelGenerator = new LabelGenerator();
    private ArrayList<HashMap<String, String>> ifLabels = new ArrayList<>();
    private ArrayList<HashMap<String, String>> whileLabels = new ArrayList<>();
    private ArrayList<HashMap<Integer, String>> condLabels = new ArrayList<>();


    public Syntactic() {
        curToken = null;
        tokenPos = 0;
        totalToken = lexical.Lexer.tokens.size();
    }

    public void syntax() {
        next_token();
        CompUnit();
    }

    public void printPcode() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("pcode.txt"));
        System.out.println("---------------------moving into printPcode--------------------");
        for (int i = 0; i < codes.size(); i++) {
            System.out.println(codes.get(i));
            bw.write(codes.get(i) + "\n");
        }
        bw.close();
    }

    public void printSyntax() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("syntax.txt"));
        Iterator<String> it = syn.iterator();
        System.out.println("---------------------moving into printSyntax------------------");
        while (it.hasNext()) {
            String tmp = it.next();
            System.out.println(tmp);
            bw.write(tmp + "\n");
        }
        bw.close();
    }

    public void next_token() {
        if (curToken != null) {
            pushToken();
        }
        if (tokenPos < totalToken) {
            curToken = Lexer.tokens.get(tokenPos);
            tokenPos++;
        } else {
            curToken = new Token(Sym.sym.SYMEND, "\0", 0);
        }

    }
//
//    private Token getNextWord() {
//        return Lexer.tokens.get(tokenPos);
//    }
//
//    private Token getNext2Word() {
//        return Lexer.tokens.get(tokenPos + 1);
//    }
//
//    private void getWordWithoutAddToGrammar() {
//        curToken = Lexer.tokens.get(tokenPos);
//        tokenPos++;
//    }

    public void prev_token() {
        if (tokenPos > 0) {
            tokenPos--;
            curToken = Lexer.tokens.get(tokenPos - 1);
            int tmpN = syn.size() - 1;
            while (syn.get(tmpN).charAt(0) == '<') {
                syn.remove(tmpN);
                tmpN--;
            }// remove false parser branches
            syn.remove(syn.size() - 1);
        }
    }

    public void addLayer() {
        layerID++;
        layer++;
        symbols.put(layer, new Symbols());
    }

    public void removeLayer() {
        symbols.remove(layer);
        layer--;
    }

    public void CompUnit() {
        addLayer();
//        Decl
        while (curToken.getSym() == Sym.sym.INTTK || curToken.getSym() == Sym.sym.CONSTTK) {
            if (curToken.getSym() == Sym.sym.CONSTTK) {
                ConstDecl();
            } else {
                next_token();
                if (curToken.getSym() == Sym.sym.IDENFR) {
                    next_token();
                    if (curToken.getSym() == Sym.sym.LPARENT) {
                        prev_token();
                        prev_token();
//                        FuncDef(); go to next cycle
                        break;
                    } else {
                        prev_token();
                        prev_token();
                        VarDecl();
                    }
                } else {
                    prev_token();
                    break;
                }
            }
        }

//        FuncDef
        while (curToken.getSym() == Sym.sym.INTTK || curToken.getSym() == Sym.sym.VOIDTK) {
            if (curToken.getSym() == Sym.sym.INTTK) {
                next_token();
                if (curToken.getSym() == Sym.sym.IDENFR) {
                    prev_token();
                    FuncDef();
                    continue;
                } else {
                    prev_token();
                    break; // define main function
                }
            } else {
                FuncDef();
                continue;
            }
        }

        MainFuncDef();
        removeLayer();

//        while(curToken.getSym() == Sym.sym.INTTK ||
//                curToken.getSym() == Sym.sym.CONSTTK ||
//                curToken.getSym() == Sym.sym.VOIDTK){
//            if(curToken.getSym() == Sym.sym.VOIDTK) {
//                FuncDef();
//            } else if (curToken.getSym() == Sym.sym.CONSTTK) {
//                Decl();
//            } else if (curToken.getSym() == Sym.sym.INTTK) {
//                next_token();
//                if(curToken.getSym() == Sym.sym.IDENFR) {
//                    next_token();
//                    if(curToken.getSym() == Sym.sym.LPARENT) {
//                        prev_token();
//                        prev_token();
//                        FuncDef();
//                    } else {
//                        prev_token();
//                        prev_token();
//                        Decl();
//                    }
//                }
//                else if(curToken.getSym() == Sym.sym.MAINTK) {
//                    prev_token();
//                    MainFuncDef();
////                    mainEnd = true;
//                }
//                else{
//                    // error handle
//                }
//            }
//        }

        pushSyn("CompUnit");
    }

    public void Decl() { // curToken at const or int (first decl token)
        while (curToken.getSym() == Sym.sym.CONSTTK || curToken.getSym() == Sym.sym.INTTK) {
            if (curToken.getSym() == Sym.sym.CONSTTK) {
                ConstDecl();
            } else if (curToken.getSym() == Sym.sym.INTTK) {
                next_token();
                if (curToken.getSym() == Sym.sym.IDENFR) {
                    next_token();
                    if (curToken.getSym() == Sym.sym.LPARENT) {
//                        declEnd = true;
                        prev_token();
                        prev_token();
                        break;
                    }
                    prev_token();
                    prev_token();
                    VarDecl();
                } else {
                    if (curToken.getSym() == Sym.sym.MAINTK) {
                        prev_token();
//                        declEnd = true;
                        break;
                    } else {
                        // error handle;
                    }
                }
            }
        }

    }

    private void ConstDecl() {
        is_token(Sym.sym.CONSTTK);
        is_token(Sym.sym.INTTK);
        ConstDef();

        while (curToken.getSym() == Sym.sym.COMMA) {
            next_token();
            ConstDef();
        }
        is_token(Sym.sym.SEMICN);
        pushSyn("ConstDecl");
    }

    private void ConstDef() {
        Token tmp = curToken;
        is_token(Sym.sym.IDENFR);
        codes.add(new Pcode(PcodeType.VAR, layerID + "_" + tmp.getContent()));

        int cntDim = 0;
        while (curToken.getSym() == Sym.sym.LBRACK) {
            cntDim++;
            next_token();
            ConstExp();
//            if(curToken.getSym() != Sym.sym.RBRACK) {
//                //handle error
//            } else {
//                next_token();
//            }
            is_token(Sym.sym.RBRACK);
        }

        if (cntDim > 0) {
            codes.add(new Pcode(PcodeType.ARRVAR, layerID + "_" + tmp.getContent(), cntDim));
        }

        addSymbol(tmp, "const", cntDim, layerID);

        if (curToken.getSym() == Sym.sym.ASSIGN) {
            next_token();
            ConstInitVal();
        }

        pushSyn("ConstDef");
    }

    private void ConstInitVal() {
        if (curToken.getSym() == Sym.sym.LBRACE) {
            next_token();
            if (curToken.getSym() != Sym.sym.RBRACE) {
                ConstInitVal();
                while (curToken.getSym() == Sym.sym.COMMA) {
                    next_token();
                    ConstInitVal();
                }
            }
            is_token(Sym.sym.RBRACE);
        } else {
            ConstExp();
        }
        pushSyn("ConstInitVal");
    }

    private void VarDecl() {
        is_token(Sym.sym.INTTK);
        VarDef();
        while (curToken.getSym() == Sym.sym.COMMA) {
            next_token();
            VarDef();
        }
        is_token(Sym.sym.SEMICN);
        pushSyn("VarDecl");
    }

    private void VarDef() {
        Token tmp = curToken;
        is_token(Sym.sym.IDENFR);
        codes.add(new Pcode(PcodeType.VAR, layerID + "_" + tmp.getContent()));

        int cntDim = 0;
        while (curToken.getSym() == Sym.sym.LBRACK) {
            cntDim++;
            next_token();
            ConstExp();
            is_token(Sym.sym.RBRACK);
        }

        if (cntDim > 0) {
            codes.add(new Pcode(PcodeType.ARRVAR, layerID + "_" + tmp.getContent(), cntDim));
        }
        addSymbol(tmp, "var", cntDim, layerID);

        if (curToken.getSym() == Sym.sym.ASSIGN) {
            next_token();
            InitVal();
        } else {
            codes.add(new Pcode(PcodeType.PLACEHOLDER, layerID + "_" + tmp.getContent(), cntDim));
        }
        pushSyn("VarDef");
    }

    private void InitVal() {
        if (curToken.getSym() == Sym.sym.LBRACE) {
            next_token();
            if (curToken.getSym() != Sym.sym.RBRACE) {
                InitVal();
                while (curToken.getSym() == Sym.sym.COMMA) {
                    next_token();
                    InitVal();
                }
            }
            is_token(Sym.sym.RBRACE);
        } else {
            Exp();
        }
        pushSyn("InitVal");
    }

    private int Exp() {
        int intType = AddExp();
        pushSyn("Exp");
        return intType;
    }

    private void ConstExp() {
        AddExp();
        pushSyn("ConstExp");
    }

    public void FuncDef() {
        while (curToken.getSym() == Sym.sym.VOIDTK || curToken.getSym() == Sym.sym.INTTK) {
            String returnType = "int";
            ArrayList<Integer> paras = new ArrayList<>();

            if (curToken.getSym() == Sym.sym.VOIDTK) {
                returnType = "void";
            }

            next_token();
            if (curToken.getSym() == Sym.sym.MAINTK) {
                prev_token();
                break;
            }// defining main function
            pushSyn("FuncType");

            Pcode code = new Pcode(PcodeType.FUNC, curToken.getContent());
            codes.add(code);
            Function function = new Function(curToken, returnType);
            addLayer();

            is_token(Sym.sym.IDENFR);
            is_token(Sym.sym.LPARENT);
            if (curToken.getSym() != Sym.sym.RPARENT) {
                paras = FuncFParams();
            }
            is_token(Sym.sym.RPARENT);

            function.setParas(paras);
            functions.put(function.getContent(), function);

            Block(true);

            removeLayer();
            code.setValue2(paras.size());
            codes.add(new Pcode(PcodeType.RET, 0));
            codes.add(new Pcode(PcodeType.ENDFUNC));
            pushSyn("FuncDef");
        }
    }

    public ArrayList<Integer> FuncFParams() {
        ArrayList<Integer> paras = new ArrayList<>();
        int para = FuncFParam();
        paras.add(para);
        while (curToken.getSym() == Sym.sym.COMMA) {
            next_token();
            para = FuncFParam();
            paras.add(para);
        }
        pushSyn("FuncFParams");
        return paras;
    }

    private int FuncFParam() {
        int paraType = 0;
        is_token(Sym.sym.INTTK);
        Token tmp = curToken;
        if (curToken.getSym() != Sym.sym.IDENFR) {
            // handle error
        } else {
            next_token();
            if (curToken.getSym() == Sym.sym.LBRACK) {
                paraType++;
                next_token();
                is_token(Sym.sym.RBRACK);
                while (curToken.getSym() == Sym.sym.LBRACK) {
                    paraType++;
                    next_token();
                    ConstExp();
                    is_token(Sym.sym.RBRACK);
                }
            }
        }

        codes.add(new Pcode(PcodeType.PARA, layerID + "_" + tmp.getContent(), paraType));
        addSymbol(tmp, "para", paraType, layerID);

        pushSyn("FuncFParam");
        return paraType;
    }

    public void FuncRParams(Token ident, ArrayList<Integer> fparas) {
        ArrayList<Integer> rparas = new ArrayList<>();
        int intType = Exp();
        rparas.add(intType);
        codes.add(new Pcode(PcodeType.RPARA, intType));
        while (curToken.getSym() == Sym.sym.COMMA) {
            next_token();
            intType = Exp();
            rparas.add(intType);
            codes.add(new Pcode(PcodeType.RPARA, intType));
        }
        pushSyn("FuncRParams");
    }

    public void MainFuncDef() {
        is_token(Sym.sym.INTTK);

        Function function = new Function(curToken, "int");
        function.setParas(new ArrayList<>());
        functions.put("main", function);

        codes.add(new Pcode(PcodeType.MAIN, curToken.getContent()));

        is_token(Sym.sym.MAINTK);
        is_token(Sym.sym.LPARENT);
        is_token(Sym.sym.RPARENT);
        Block(false);

        codes.add(new Pcode(PcodeType.EXIT));
        pushSyn("MainFuncDef");
    }

    public void Block(boolean fromFunc) {
        is_token(Sym.sym.LBRACE);
        if (!fromFunc) {
            addLayer();
        }
        if (curToken.getSym() != Sym.sym.RBRACE) {
            BlockItem();
        }
        is_token(Sym.sym.RBRACE);

        if (!fromFunc) {
            removeLayer();
        }
        pushSyn("Block");
    }

    private void BlockItem() {
        boolean blockEnd = false;
        while (!blockEnd) {
            if (curToken.getSym() == Sym.sym.RBRACE) {
                blockEnd = true;
            } else if (curToken.getSym() == Sym.sym.CONSTTK || curToken.getSym() == Sym.sym.INTTK) {
                Decl();
            } else {
                Stmt();
            }
        }
    }

    private void Stmt() {
//        if statement
        if (curToken.getSym() == Sym.sym.IFTK) {
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put("if", labelGenerator.getLabel("if"));
            tmp.put("else", labelGenerator.getLabel("else"));
            tmp.put("if_end", labelGenerator.getLabel("if_end"));
            tmp.put("if_block", labelGenerator.getLabel("if_block"));
//            ifLabels.add(new HashMap<>());
//            ifLabels.get(ifLabels.size() - 1).put("if", labelGenerator.getLabel("if"));
//            ifLabels.get(ifLabels.size() - 1).put("else", labelGenerator.getLabel("else"));
//            ifLabels.get(ifLabels.size() - 1).put("if_end", labelGenerator.getLabel("if_end"));
//            ifLabels.get(ifLabels.size() - 1).put("if_block", labelGenerator.getLabel("if_block"));
            ifLabels.add(tmp);
            codes.add(new Pcode(PcodeType.LABEL, ifLabels.get(ifLabels.size() - 1).get("if")));

            next_token();

            is_token(Sym.sym.LPARENT);
            Cond("if");
            is_token(Sym.sym.RPARENT);

            codes.add(new Pcode(PcodeType.JZ, ifLabels.get(ifLabels.size() - 1).get("else")));
            codes.add(new Pcode(PcodeType.LABEL, ifLabels.get(ifLabels.size() - 1).get("if_block")));

            Stmt();

            codes.add(new Pcode(PcodeType.JMP, ifLabels.get(ifLabels.size() - 1).get("if_end")));
            codes.add(new Pcode(PcodeType.LABEL, ifLabels.get(ifLabels.size() - 1).get("else")));


            if (curToken.getSym() == Sym.sym.ELSETK) {
                next_token();
                Stmt();
            }
            codes.add(new Pcode(PcodeType.LABEL, ifLabels.get(ifLabels.size() - 1).get("if_end")));
            ifLabels.remove(ifLabels.size() - 1);
        }

//        while statement
        else if (curToken.getSym() == Sym.sym.WHILETK) {
            whileLabels.add(new HashMap<>());
            whileLabels.get(whileLabels.size() - 1).put("while", labelGenerator.getLabel("while"));
            whileLabels.get(whileLabels.size() - 1).put("while_end", labelGenerator.getLabel("while_end"));
            whileLabels.get(whileLabels.size() - 1).put("while_block", labelGenerator.getLabel("while_block"));
            codes.add(new Pcode(PcodeType.LABEL, whileLabels.get(whileLabels.size() - 1).get("while")));

            next_token();

            is_token(Sym.sym.LPARENT);
            Cond("while");
            is_token(Sym.sym.RPARENT);

            codes.add(new Pcode(PcodeType.JZ, whileLabels.get(whileLabels.size() - 1).get("while_end")));
            codes.add(new Pcode(PcodeType.LABEL, whileLabels.get(whileLabels.size() - 1).get("while_block")));

            Stmt();

            codes.add(new Pcode(PcodeType.JMP, whileLabels.get(whileLabels.size() - 1).get("while")));
            codes.add(new Pcode(PcodeType.LABEL, whileLabels.get(whileLabels.size() - 1).get("while_end")));
            whileLabels.remove(whileLabels.size() - 1);
//            if(curToken.getSym() != Sym.sym.LPARENT) {
//                // handle error
//            }
//            else {
//                next_token();
//                Cond();
//
//                if(curToken.getSym() != Sym.sym.RPARENT) {
//                    //handle error
//                } else {
//                    next_token();
//                    Stmt();
//                }
//            }
        }

//        break & continue statement
        else if (curToken.getSym() == Sym.sym.BREAKTK) {
            next_token();
            codes.add(new Pcode(PcodeType.JMP, whileLabels.get(whileLabels.size() - 1).get("while_end")));
            is_token(Sym.sym.SEMICN);
        } else if (curToken.getSym() == Sym.sym.CONTINUETK) {
            next_token();
            codes.add(new Pcode(PcodeType.JMP, whileLabels.get(whileLabels.size() - 1).get("while")));
            is_token(Sym.sym.SEMICN);
        }

//        return statement
        else if (curToken.getSym() == Sym.sym.RETURNTK) {
            boolean flag = false;
            next_token();
            if (curToken.getSym() != Sym.sym.SEMICN) {
                flag = true;
                Exp();
            }
            is_token(Sym.sym.SEMICN);
            codes.add(new Pcode(PcodeType.RET, flag ? 1 : 0));
        }

//        print statement
        else if (curToken.getSym() == Sym.sym.PRINTFTK) {
            next_token();
            is_token(Sym.sym.LPARENT);
            Token strcon = curToken;
            is_token(Sym.sym.STRCON);

            int para = 0;
            while (curToken.getSym() == Sym.sym.COMMA) {
                next_token();
                Exp();
                para++;
            }

            is_token(Sym.sym.RPARENT);
            is_token(Sym.sym.SEMICN);

            codes.add(new Pcode(PcodeType.PRINT, strcon.getContent(), para));
        }

//        into block
        else if (curToken.getSym() == Sym.sym.LBRACE) {
            Block(false);
        }

//        first token is IDENFR, has 3 cases
//        LVal '=' Exp ';'
//        Exp
//        LVal '=' 'getint''('')'';'
        else if (curToken.getSym() == Sym.sym.IDENFR) {
            Token iden = curToken;
//            next_token();
//            int tokenN = tokenPos;
//            if(curToken.getSym() != Sym.sym.LPARENT) {
//                prev_token();
//                LVal();
//                if(curToken.getSym() != Sym.sym.ASSIGN) {
//                    int tmpN = tokenPos;
//                    for(int i = 0; i < (tmpN - tokenN)+1; i++) {
//                        prev_token();
//                    }
////                    tokenPos = tokenN;
////                    curToken = Lexer.tokens.get(tokenPos);
//                    Exp();
//                    is_token(Sym.sym.SEMICN);
//                }
//                else {
////                    LVal '=' Exp ';'
////                    LVal '=' 'getint''('')'';'
//                    next_token();
//                    if(curToken.getSym() == Sym.sym.GETINTTK) {
//                        next_token();
//                        is_token(Sym.sym.LPARENT);
//                        is_token(Sym.sym.RPARENT);
//                    }
//                    else {
//                        Exp();
//                    }
//                    is_token(Sym.sym.SEMICN);
//                }
//            }
////          Exp → AddExp → MulExp → UnaryExp → Ident '(' [FuncRParams] ')'
//            else {
//                prev_token();
//                Exp();
//                is_token(Sym.sym.SEMICN);
//            }
            int cnt = 0;
            boolean hasAssign = false;
            while (curToken.getSym() != Sym.sym.SEMICN) {
                if (curToken.getSym() == Sym.sym.ASSIGN) {
                    hasAssign = true;
                    break;
                }
                next_token();
                cnt++;
            }
            for (int i = 0; i < cnt; i++) {
                prev_token();
            }// return to IDENFR

            if (hasAssign) {
                int dim = LVal();
                codes.add(new Pcode(PcodeType.ADDRESS, getSymbol(iden).getLayerID() + "_" + iden.getContent(), dim));
                next_token();
                if (curToken.getSym() == Sym.sym.GETINTTK) {
                    next_token();
                    is_token(Sym.sym.LPARENT);
                    is_token(Sym.sym.RPARENT);
                    codes.add(new Pcode(PcodeType.GETINT));
                } else {
                    Exp();
                }
                is_token(Sym.sym.SEMICN);
                codes.add(new Pcode(PcodeType.POP, getSymbol(iden).getLayerID() + "_" + iden.getContent()));
            } else {
                Exp();
                is_token(Sym.sym.SEMICN);
            }
        }

//        Exp conditions start with:
//        PrimaryExp(PrimaryExp → '(' Exp ')' | Number),
        else if (curToken.getSym() == Sym.sym.LPARENT ||
                curToken.getSym() == Sym.sym.INTTK ||
                curToken.getSym() == Sym.sym.INTCON ||
                curToken.getSym() == Sym.sym.PLUS ||
                curToken.getSym() == Sym.sym.MINU ||
                curToken.getSym() == Sym.sym.NOT) {
            Exp();
            is_token(Sym.sym.SEMICN);
        }

//        only semicn
        else {
            is_token(Sym.sym.SEMICN);
        }
        pushSyn("Stmt");
    }

    private void Cond(String from) {
        LOrExp(from);
        pushSyn("Cond");
    }

    private void LOrExp(String from) {
        int cntCond = 0;
        String label = labelGenerator.getLabel("cond_" + cntCond);
        cntCond++;

        LAndExp(from, label);

        codes.add(new Pcode(PcodeType.LABEL, label));

        if (from.equals("if")) {
            codes.add(new Pcode(PcodeType.JNZ, ifLabels.get(ifLabels.size() - 1).get("if_block")));
        } else if (from.equals("while")) {
            codes.add(new Pcode(PcodeType.JNZ, whileLabels.get(whileLabels.size() - 1).get("while_block")));
        }

        pushSyn("LOrExp");

        while (curToken.getSym() == Sym.sym.OR) {
//            codes.add(new Pcode(PcodeType.OR));
            next_token();
            label = labelGenerator.getLabel("cond_" + cntCond);
            cntCond++;

            LAndExp(from, label);

            codes.add(new Pcode(PcodeType.LABEL, label));
            codes.add(new Pcode(PcodeType.OR));

            if (from.equals("if")) {
                codes.add(new Pcode(PcodeType.JNZ, ifLabels.get(ifLabels.size() - 1).get("if_block")));
            } else if (from.equals("while")) {
                codes.add(new Pcode(PcodeType.JNZ, whileLabels.get(whileLabels.size() - 1).get("while_block")));
            }

            pushSyn("LOrExp");
        }

        for (int i = codes.size() - 1; i >= 0; i--) {
            Pcode tmp = codes.get(i);
            if (tmp.getType() == PcodeType.JNZ) {
                codes.remove(i);
                break;
            }
        }//single cond remove shortcut
    }

    private void LAndExp(String from, String label) {
        EqExp();

        if (from.equals("if")) {
            codes.add(new Pcode(PcodeType.JZ, label));
        } else if (from.equals("while")) {
            codes.add(new Pcode(PcodeType.JZ, label));
        }

        pushSyn("LAndExp");

        while (curToken.getSym() == Sym.sym.AND) {
            next_token();
            EqExp();

            codes.add(new Pcode(PcodeType.AND));

            if (from.equals("if")) {
                codes.add(new Pcode(PcodeType.JZ, label));
            } else if (from.equals("while")) {
                codes.add(new Pcode(PcodeType.JZ, label));
            }

            pushSyn("LAndExp");
        }

        for (int i = codes.size() - 1; i >= 0; i--) {
            Pcode tmp = codes.get(i);
            if (tmp.getType() == PcodeType.JZ) {
                codes.remove(i);
                break;
            }
        }//single cond remove shortcut
    }

    private void EqExp() {
        RelExp();
        pushSyn("EqExp");
        while (curToken.getSym() == Sym.sym.EQL ||
                curToken.getSym() == Sym.sym.NEQ) {
            Sym.sym op = curToken.getSym();
            next_token();
            RelExp();

            switch (op) {
                case EQL:
                    codes.add(new Pcode(PcodeType.CMPEQ));
                    break;
                case NEQ:
                    codes.add(new Pcode(PcodeType.CMPNE));
                    break;
            }
            pushSyn("EqExp");
        }
    }

    private void RelExp() {
        AddExp();
        pushSyn("RelExp");
        while (curToken.getSym() == Sym.sym.LSS ||
                curToken.getSym() == Sym.sym.GRE ||
                curToken.getSym() == Sym.sym.LEQ ||
                curToken.getSym() == Sym.sym.GEQ) {
            Sym.sym op = curToken.getSym();
            next_token();
            AddExp();

            switch (op) {
                case LSS:
                    codes.add(new Pcode(PcodeType.CMPLT));
                    break;
                case GRE:
                    codes.add(new Pcode(PcodeType.CMPGT));
                    break;
                case LEQ:
                    codes.add(new Pcode(PcodeType.CMPLE));
                    break;
                case GEQ:
                    codes.add(new Pcode(PcodeType.CMPGE));
                    break;
            }
            pushSyn("RelExp");
        }
    }

    private int AddExp() {
        int dim = 0;
        dim = MulExp();
        pushSyn("AddExp");
        while (curToken.getSym() == Sym.sym.PLUS ||
                curToken.getSym() == Sym.sym.MINU) {
            Sym.sym op = curToken.getSym();
            next_token();
            MulExp();

            switch (op) {
                case PLUS:
                    codes.add(new Pcode(PcodeType.ADD));
                    break;
                case MINU:
                    codes.add(new Pcode(PcodeType.SUB));
                    break;
            }

            pushSyn("AddExp");
        }
        return dim;
    }

    private int MulExp() {
        int dim = 0;
        dim = UnaryExp();
        pushSyn("MulExp");
        while (curToken.getSym() == Sym.sym.MULT ||
                curToken.getSym() == Sym.sym.DIV ||
                curToken.getSym() == Sym.sym.MOD) {
            Sym.sym op = curToken.getSym();
            next_token();
            UnaryExp();

            switch (op) {
                case MULT:
                    codes.add(new Pcode(PcodeType.MUL));
                    break;
                case DIV:
                    codes.add(new Pcode(PcodeType.DIV));
                    break;
                case MOD:
                    codes.add(new Pcode(PcodeType.MOD));
                    break;
            }
            pushSyn("MulExp");
        }
        return dim;
    }

    private int UnaryExp() {
        int intType = 0;
        if (curToken.getSym() == Sym.sym.IDENFR) {
            Token ident = curToken;
            ArrayList<Integer> fparas = null;

            next_token();
            if (curToken.getSym() == Sym.sym.LPARENT) {
                fparas = getFunction(ident).getParas();
                next_token();
                if (curToken.getSym() != Sym.sym.RPARENT) {
                    FuncRParams(ident, fparas);
                }
                is_token(Sym.sym.RPARENT);
                codes.add(new Pcode(PcodeType.CALL, ident.getContent()));
                if (getFunction(ident).getReturnType().equals("void")) {
                    intType = -1;
                }
            } else {
                prev_token();
                intType = PrimaryExp();
            }
        } else if (curToken.getSym() == Sym.sym.PLUS ||
                curToken.getSym() == Sym.sym.MINU ||
                curToken.getSym() == Sym.sym.NOT) {
            //UnaryOp UnaryExp
            Sym.sym op = curToken.getSym();
            next_token();
            pushSyn("UnaryOp");
            UnaryExp();

            switch (op) {
                case PLUS:
                    codes.add(new Pcode(PcodeType.POS));
                    break;
                case MINU:
                    codes.add(new Pcode(PcodeType.NEG));
                    break;
                case NOT:
                    codes.add(new Pcode(PcodeType.NOT));
                    break;
            }

        } else {
            intType = PrimaryExp();
        }
        pushSyn("UnaryExp");
        return intType;
    }

    private int PrimaryExp() {
        int intType = 0;
        if (curToken.getSym() == Sym.sym.LPARENT) {
            next_token();
            Exp();
            is_token(Sym.sym.RPARENT);
        } else if (curToken.getSym() == Sym.sym.IDENFR) {
            Token ident = curToken;
            intType = LVal();

            if (intType == 0) {
                codes.add(new Pcode(PcodeType.VALUE, getSymbol(ident).getLayerID() + "_" + ident.getContent(), intType));
            } else {
                codes.add(new Pcode(PcodeType.ADDRESS, getSymbol(ident).getLayerID() + "_" + ident.getContent(), intType));
            }

        } else if (curToken.getSym() == Sym.sym.INTCON) {
            codes.add(new Pcode(PcodeType.PUSH, Integer.parseInt(curToken.getContent())));
            next_token();
            pushSyn("Number");
        }
        pushSyn("PrimaryExp");
        return intType;
    }

    private int LVal() {
        int intType = 0;
        Token ident = curToken;
        is_token(Sym.sym.IDENFR);
        codes.add(new Pcode(PcodeType.PUSH, getSymbol(ident).getLayerID() + "_" + ident.getContent()));
        while (curToken.getSym() == Sym.sym.LBRACK) {
            intType++;
            next_token();
            Exp();
            is_token(Sym.sym.RBRACK);
        }
        pushSyn("LVal");
        return getSymbol(ident).getIntType() - intType;
    }

    void pushToken() {
        syn.add(curToken.getSymName() + " " + curToken.getContent());
//        System.out.println(curToken.getSymName() + " " + curToken.getContent());
    }

    void pushSyn(String str) {
        syn.add("<" + str + ">");
//        System.out.println("<" + str + ">");
    }

    boolean is_token(Sym.sym token) {
        if (curToken.getSym() == token) {
            next_token();
            return true;
        } else {
            return false;
            //handle error
        }
    }

    private void addSymbol(Token token, String type, int cntDim, int layerID) {
        symbols.get(layer).addSymbol(type, cntDim, token, layerID);
    }

    private Symbol getSymbol(Token word) {
        Symbol symbol = null;
        for (Symbols s : symbols.values()) {
            if (s.hasSymbol(word)) {
                symbol = s.getSymbol(word);
            }
        }
        return symbol;
    }

    private Function getFunction(Token token) {
        return functions.getOrDefault(token.getContent(), null);
    }

    public static ArrayList<Pcode> getCodes() {
        return codes;
    }

//    private ArrayList<Token> getExp() {
//        ArrayList<Token> exp = new ArrayList<>();
//        boolean inFunc = false;
//        int funcFlag = 0;
//        int flag1 = 0;
//        int flag2 = 0;
//        Token preWord = null;
//        Token token = Lexer.tokens.get(tokenPos);
//        while (true) {
//            if (token.getSym() == Sym.sym.SEMICN
//                    || token.getSym() == Sym.sym.ASSIGN
//                    || token.getSym() == Sym.sym.RBRACE
//                    || token.typeSymbolizeValidateStmt()) {
//                break;
//            }
//            if (token.getSym() == Sym.sym.COMMA && !inFunc) {
//                break;
//            }
//            if (preWord != null) {
//                if ((preWord.getSym() == Sym.sym.INTCON || preWord.getSym() == Sym.sym.IDENFR)
//                        && (token.getSym() == Sym.sym.INTCON || token.getSym() == Sym.sym.IDENFR)) {
//                    break;
//                }
//                if ((preWord.getSym() == Sym.sym.RPARENT || preWord.getSym() == Sym.sym.RBRACK) && (token.getSym() == Sym.sym.INTCON || token.getSym() == Sym.sym.IDENFR)) {
//                    break;
//                }
//                if (flag1 == 0 && flag2 == 0) {
//                    if (preWord.getSym() == Sym.sym.INTCON && token.getSym() == Sym.sym.LBRACK) {
//                        break;
//                    }
//                    if (preWord.getSym() == Sym.sym.INTCON && token.getSym() == Sym.sym.LBRACE) {
//                        break;
//                    }
//                }
//            }
//            if (token.typeOfNotInExp()) {
//                break;
//            }
//            if (token.getSym() == Sym.sym.IDENFR ) {
//                if (getNext2Word().getSym() == Sym.sym.LPARENT ) {
//                    inFunc = true;
//                }
//            }
//            if (token.getSym() == Sym.sym.LPARENT ) {
//                flag1++;
//                if (inFunc) {
//                    funcFlag++;
//                }
//            }
//            if (token.getSym() == Sym.sym.RPARENT ) {
//                flag1--;
//                if (inFunc) {
//                    funcFlag--;
//                    if (funcFlag == 0) {
//                        inFunc = false;
//                    }
//                }
//            }
//            if (token.getSym() == Sym.sym.LBRACK ) {
//                flag2++;
//            }
//            if (token.getSym() == Sym.sym.RBRACK ) {
//                flag2--;
//            }
//            if (flag1 < 0) {
//                break;
//            }
//            if (flag2 < 0) {
//                break;
//            }
//            getWordWithoutAddToGrammar();
//            exp.add(curToken);
//            preWord = token;
//            token = getNextWord();
//        }
//        return exp;
//    }

//    private Exps divideExp(ArrayList<Token> exp, ArrayList<String> symbol) {
//        ArrayList<ArrayList<Token>> exps = new ArrayList<>();
//        ArrayList<Token> exp1 = new ArrayList<>();
//        ArrayList<Token> symbols = new ArrayList<>();
//        boolean unaryFlag = false;
//        int flag1 = 0;
//        int flag2 = 0;
//        for (int i = 0; i < exp.size(); i++) {
//            Token word = exp.get(i);
//            if (word.getSym() == Sym.sym.LPARENT) {
//                flag1++;
//            }
//            if (word.getSym() == Sym.sym.RPARENT) {
//                flag1--;
//            }
//            if (word.getSym() == Sym.sym.LBRACK) {
//                flag2++;
//            }
//            if (word.getSym() == Sym.sym.RBRACK) {
//                flag2--;
//            }
//            if (symbol.contains(word.getSym()) && flag1 == 0 && flag2 == 0) {
//                //UnaryOp
//                if (word.typeOfUnary()) {
//                    if (!unaryFlag) {
//                        exp1.add(word);
//                        continue;
//                    }
//                }
//                exps.add(exp1);
//                symbols.add(word);
//                exp1 = new ArrayList<>();
//            } else {
//                exp1.add(word);
//            }
//            unaryFlag = (word.getSym() == Sym.sym.IDENFR || word.getSym() == Sym.sym.RPARENT ||
//                    word.getSym() == Sym.sym.INTCON || word.getSym() == Sym.sym.RBRACK);
//        }
//        exps.add(exp1);
//        return new Exps(exps, symbols);
//    }

}
