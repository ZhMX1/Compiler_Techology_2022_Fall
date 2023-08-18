import java.io.*;
import java.util.ArrayList;

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
//        System.out.println("----------------------");

        Syntactic syn = new Syntactic();
//        System.out.println("finish 1");
        syn.syntax();
//        System.out.println("finish 2");
        syn.printSyntax();
    }
}
