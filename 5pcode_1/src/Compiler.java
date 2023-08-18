import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import Generator.PcodeExecutor;
import lexical.*;
import syntax.Syntactic;

public class Compiler {
    static ArrayList<String> readFile = new ArrayList<>();
    static int maxLine = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
        String str = "";
        while((str = br.readLine()) != null) {
            readFile.add(str);
            maxLine++;
        }
        br.close();

        Lexer lxr = new Lexer(maxLine, readFile);
        lxr.lexer();
//        lxr.printLexer();
        System.out.println("-----------finish lexer-----------");

        Syntactic syn = new Syntactic();
        syn.syntax();
//        syn.printSyntax();
//        syn.printPcode();
        System.out.println("-----------finish Syntax-----------");

        Scanner scanner = new Scanner(System.in);
        PcodeExecutor pe = new PcodeExecutor(Syntactic.getCodes(), scanner);
//        System.out.println("-------before run-------");
        pe.run();
        System.out.println("-------finish run-------");
        pe.print();
    }
}
