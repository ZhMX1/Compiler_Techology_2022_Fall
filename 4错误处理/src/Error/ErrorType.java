package Error;

import java.util.HashMap;
import java.util.Map;

public class ErrorType {
    int lineNum;
    String type;

    public ErrorType(int num, String str) {
        lineNum = num;
        type = str;
    }

    @Override
    public String toString() {
        return "ErrorType{" +
                "lineNum=" + lineNum +
                ", type='" + type + '\'' +
                '}';
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getType() {
        return type;
    }

    static Map<String, String> errors = new HashMap<>();

    static {
        errors.put("a", "格式字符串中出现非法字符");
        errors.put("b", "函数名或者变量名在当前作用域下重复定义");
        errors.put("c", "使用了未定义的标识符");
        errors.put("d", "参数个数与函数定义中的参数个数不匹配");
        errors.put("e", "参数类型与函数定义中对应位置的参数类型不匹配");
        errors.put("f", "无返回值的函数存在不匹配的return语句");
        errors.put("g", "有返回值的函数缺少return语句");
        errors.put("h", "不能改变常量的值");
        errors.put("i", "缺少分号");
        errors.put("j", "缺少右小括号’)’");
        errors.put("k", "缺少右中括号’]’");
        errors.put("l", "printf中格式字符与表达式个数不匹配");
        errors.put("m", "在非循环块中使用break和continue语句");
    }
}
