package broj.expression;

import java.util.*;

public class ExpressionBuilder {

    Integer target;
    ArrayList<Integer> numbers = new ArrayList<>();
    ArrayList<Expression> exp1 = new ArrayList<>();
    ArrayList<Expression> exp2 = new ArrayList<>();
    ArrayList<Expression> exp3 = new ArrayList<>();
    ArrayList<Expression> exp4 = new ArrayList<>();
    ArrayList<Expression> exp5 = new ArrayList<>();
    ArrayList<Expression> exp6 = new ArrayList<>();
    Integer total;  //ukupan broj izraza u exp1, exp2... exp6

    //Constructor
    public ExpressionBuilder(Integer target, ArrayList<Integer> numbers) {
        this.target = target;
        this.numbers = numbers;
        this.total = 0;
    }

    public void startBuild() {
        build1();  System.out.println("Total: " + this.total);
        //build1();
        buildUni(exp1, exp1, exp2); System.out.println("Total: " + this.total);
        
        buildUni(exp1, exp2, exp3); System.out.println("Total: " + this.total);
        buildUni(exp2, exp1, exp3); System.out.println("Total: " + this.total);
        
        buildUni(exp1, exp3, exp4); System.out.println("Total: " + this.total);
        buildUni(exp3, exp1, exp4); System.out.println("Total: " + this.total);
        buildUni(exp2, exp2, exp4); System.out.println("Total: " + this.total);
        
        buildUni(exp1, exp4, exp5); System.out.println("Total: " + this.total);
        buildUni(exp4, exp1, exp5); System.out.println("Total: " + this.total);
        buildUni(exp2, exp3, exp5); System.out.println("Total: " + this.total);
        buildUni(exp3, exp2, exp5); System.out.println("Total: " + this.total);
        
        buildUni(exp1, exp5, exp6); System.out.println("Total: " + this.total);
        buildUni(exp5, exp1, exp6); System.out.println("Total: " + this.total);
        buildUni(exp2, exp4, exp6); System.out.println("Total: " + this.total);
        buildUni(exp4, exp2, exp6); System.out.println("Total: " + this.total);
        buildUni(exp3, exp3, exp6); System.out.println("Total: " + this.total);
    }

    public boolean canMerge(Expression e1, Expression e2) {
        boolean b = true;
        for (int n : e1.getIndexes()) {
            if (e2.getIndexes().contains(n)) {
                b = false;
            }
        }
        return b;
    }

    //Mozda moze jedna f-ja za sve buildove, samo da se prosledjuju razlicite liste izraza...
    //Proveriti da li se izrazi od 3 clana i vise moraju praviti i od 3 pojedinacna broja ili je to obuhvaceno u exp2?
    public void build1() {
        for (int i = 0; i < numbers.size(); i++) {
            Expression e = new Expression(numbers.get(i), i);
            this.exp1.add(e);
            this.total++;
        }
    }

    public void buildUni(ArrayList<Expression> ex1, ArrayList<Expression> ex2, ArrayList<Expression> exp) {
        for (int i = 0; i < ex1.size(); i++) {
            for (int j = 0; j < ex2.size(); j++) {
                if (canMerge(ex1.get(i), ex2.get(j))) {
                    Expression e1 = new Expression(ex1.get(i), ex2.get(j), "+");
                    Expression e2 = new Expression(ex1.get(i), ex2.get(j), "-");
                    Expression e3 = new Expression(ex1.get(i), ex2.get(j), "*");
                    Expression e4 = new Expression(ex1.get(i), ex2.get(j), ":");

                    exp.add(e1);
                    exp.add(e2);
                    exp.add(e3);
                    exp.add(e4);
                    this.total += 4;
                }
            }
        }
    }
}
