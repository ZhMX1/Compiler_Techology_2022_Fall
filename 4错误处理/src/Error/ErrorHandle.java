package Error;

import java.io.*;
import java.util.*;

public class ErrorHandle {
    public static ArrayList<ErrorType> errors = new ArrayList<>();
    public static void error(int lineNum) {
        System.out.println("error occuring in line " + lineNum);
    }

    public void printError() throws IOException {
        CompareLine cl = new CompareLine();
        Collections.sort(errors,cl);
        BufferedWriter bw = new BufferedWriter(new FileWriter("error.txt"));
        Iterator<ErrorType> it = errors.iterator();
        while(it.hasNext()) {
            ErrorType tmp = it.next();
            String ans = tmp.getLineNum() + " " + tmp.getType()+"\n";
            System.out.print(ans);
            bw.write(ans);
        }
        bw.close();
    }

    public static int typeA(String str, int lineNum) {
        int declNum = 0;
        boolean added = false;
        // ignore the '"' at the start and end position of string
        for(int i = 1; i < str.length() - 1; i++) {
            if(str.charAt(i) == '%') {
                if(str.charAt(i + 1) != 'd') {
                    if(!added) {
                        errors.add(new ErrorType(lineNum, "a"));
                        added = true;
//                        System.out.println("----1111---- "+str.charAt(i)+str.charAt(i+1)+str.charAt(i+2));
//                        break;
                    }
                }
                else {
                    i++;
                    declNum++;
                }
            }
            else if(str.charAt(i) == '\\') {
                if(str.charAt(i + 1) != 'n' && !added) {
                    errors.add(new ErrorType(lineNum, "a"));
                    added = true;
//                    System.out.println("----22222---- "+str.charAt(i)+str.charAt(i+1)+str.charAt(i+2));
//                    break;
                }
            }
            else if(!added && !((str.charAt(i) >= 40 && str.charAt(i) <= 126)
                    || str.charAt(i) == 32 || str.charAt(i) == 33)) {
                errors.add(new ErrorType(lineNum, "a"));
                added = true;
//            System.out.println("----33333333---- "+str.charAt(i)+str.charAt(i+1)+str.charAt(i+2));
//                break;
            }

        }
        return declNum;
    }
}

class CompareLine implements Comparator<ErrorType>{
    //按照姓名进行排序
    @Override
    public int compare(ErrorType e1, ErrorType e2) {
        if(e1.getLineNum() > e2.getLineNum())
            return 1;
        else return -1;
    }
}