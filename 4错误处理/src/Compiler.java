import java.io.*;
import java.util.ArrayList;

import lexical.*;
import syntax.Syntactic;
import Error.*;

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
        syn.syntax();
        syn.printSyntax();

        ErrorHandle eh = new ErrorHandle();
        eh.printError();

    }
}
