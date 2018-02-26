package broj.expression;

import java.util.*;

public class PostfixCalculator {
    
    private static ArrayList<String> expression;
    private static String operators = "+-*/";
    private static Stack<String> stack = new Stack<String>();

    public static void setExpression(ArrayList<String> exp) {
        expression = exp;
    }

    public static double evaluateExpression(ArrayList<String> exp) {
        setExpression(exp);
        
        for (String s : expression) {
            if (s.equals(null)) break;
            if (operators.contains(s)) {                                //operacija
                double a = Double.valueOf(stack.pop());
                double b = Double.valueOf(stack.pop());
                
                double r = performOperation(operators.indexOf(s), a, b);
                
                stack.push(String.valueOf(r));
            } else {                                                    //broj
                stack.push(s);
            }
        }
        
        double returnValue = Double.valueOf(stack.pop());

        return returnValue;
    }

    public static double performOperation(int operation, double num1, double num2) {
        switch (operation) {
            case 0:
                return num1 + num2;
            case 1:
                return num2 - num1;
            case 2:
                return num1 * num2;
            case 3:
                return num2 / num1;
            default:
                return -1;
        }
    }
}
