import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class Compiler {
    static ArrayList<String> file = new ArrayList<>();
    static int totalLine = 0;
    static Vector<Token> tokens = new Vector<>();
    static boolean isAnnotation = false;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
        String str = "";
        while((str = br.readLine()) != null) {
            file.add(str);
            totalLine++;
        }
        br.close();
//        System.out.println(file);
        Lexer lxr = new Lexer("textfile.txt");
        lxr.print();
//        BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
//        Iterator<Token> it = Compiler.tokens.iterator();
//        while(it.hasNext()) {
//            Token tmp = it.next();
//            String ans = tmp.getSym() + " " + tmp.getStr()+"\n";
//            System.out.print(ans);
//            bw.write(ans);
//        }
//        bw.close();
    }
}
