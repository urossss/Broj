package broj.expression;

import java.util.*;

public class InfixToPostfix {

    private static ArrayList<String> exp;
    private static String operators = "+-*/";
    private static Stack<String> helper = new Stack<>();

    public static ArrayList<String> convert(String s) {
        setExpression(s);
        return exp;
    }

    public static void setExpression(String expression) {
        exp = new ArrayList<String>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') {
                helper.push("(");
            } else if (c == ')') {
                String elem = helper.pop();
                while (!elem.equals("(")) {
                    exp.add(elem);
                    elem = helper.pop();
                }
            } else if (operators.contains("" + c)) {   //operacija
                if (!helper.empty()) {
                    if (c == '+' || c == '-') {
                        String s = "";
                        while (!helper.empty() && !zagrada(s)) {
                            if (!s.equals("")) {
                                exp.add(s);
                            }
                            s = helper.pop();
                        }
                        if (zagrada(s)) {
                            helper.push(s);
                        } else if (!s.equals("")) {
                            exp.add(s);
                        }
                    } else {
                        String s = "";
                        while (!helper.empty() && !zagrada(s) && !(s.equals("+") || s.equals("-"))) {
                            if (!s.equals("")) {
                                exp.add(s);
                            }
                            s = helper.pop();
                        }
                        if ((s.equals("+") || s.equals("-")) || (zagrada(s))) {
                            helper.push(s);
                        } else if (!s.equals("")) {
                            exp.add(s);
                        }
                    }
                }
                helper.push("" + c);
            } else {                                        //broj
                String num = "";
                while (c >= '0' && c <= '9') {
                    num += c;
                    i++;
                    if (i < expression.length()) {
                        c = expression.charAt(i);
                    } else {
                        break;
                    }
                }
                i--;
                exp.add(num);
            }
        }
        while (!helper.empty()) {
            exp.add(helper.pop());
        }
    }
    
    private static boolean zagrada(String s) {
        return (s.equals("(")) || (s.equals(")"));
    }

    private static void printHelper() {
        System.out.print("Helper: ");
        for (String s : helper) {
            System.out.print(s + " ");
        }
        System.out.println("");
    }

    private static void printExpression() {
        for (String s : exp) {
            System.out.print(s);
        }
    }

}
