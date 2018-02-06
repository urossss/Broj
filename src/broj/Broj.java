package broj;

import broj.expression.ExpressionBuilder;
import java.util.*;

public class Broj {

    public static void main(String[] args) {

        Integer target;
        ArrayList<Integer> numbers = new ArrayList<>();

        Scanner reader = new Scanner(System.in);

        System.out.print("Enter a target number: ");
        target = reader.nextInt();

        Integer n;
        System.out.print("Enter 6 numbers: ");
        for (int i = 0; i < 6; i++) {
            n = reader.nextInt();
            numbers.add(n);
        }
        
        ExpressionBuilder builder = new ExpressionBuilder(target, numbers);
        
        builder.startBuild();

    }

}
