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
    ArrayList<Expression> sol = new ArrayList<>();

    //Constructor
    public ExpressionBuilder(Integer target, ArrayList<Integer> numbers) {
        this.target = target;
        this.numbers = numbers;
        this.total = 0;
    }

    public void startBuild() {
        build1();

        buildUni(exp1, exp1, exp2);

        buildUni(exp1, exp2, exp3);
        //buildUni(exp2, exp1, exp3);

        buildUni(exp1, exp3, exp4);
        //buildUni(exp3, exp1, exp4);
        buildUni(exp2, exp2, exp4);

        buildUni(exp1, exp4, exp5);
        //buildUni(exp4, exp1, exp5);
        buildUni(exp2, exp3, exp5);
        //buildUni(exp3, exp2, exp5);

        buildUni(exp1, exp5, exp6);
        //buildUni(exp5, exp1, exp6);
        buildUni(exp2, exp4, exp6);
        //buildUni(exp4, exp2, exp6);
        buildUni(exp3, exp3, exp6);

        this.total = exp1.size() + exp2.size() + exp3.size() + exp4.size() + exp5.size() + exp6.size();
        System.out.println("Total expressions: " + this.total);
        
        //System.out.println("Total: " + this.sol.size());

        printAllSolutions();
    }

    public boolean canMerge(Expression e1, Expression e2) {
        for (int n : e1.getIndexes()) {
            if (e2.getIndexes().contains(n)) {
                return false;
            }
        }
        return true;
    }

    public void printAllSolutions() {
        System.out.println("----------------------------------");
        System.out.println("Resenja:");
        for (Expression e : sol) {
            e.formExpressionString();
            System.out.println(e);
        }
        System.out.println("Total: " + this.sol.size());
    }

    public void build1() {
        for (int i = 0; i < numbers.size(); i++) {
            Expression e = new Expression(numbers.get(i), i);
            this.exp1.add(e);

            if (e.getValue() == this.target) {
                this.sol.add(e);
            }
        }
    }
    
    public void buildUni(ArrayList<Expression> ex1, ArrayList<Expression> ex2, ArrayList<Expression> exp) {
        int num1 = ex1.get(0).getNum();
        int num2 = ex2.get(0).getNum();
        for (int i = 0; i < ex1.size(); i++) {
            for (int j = 0; j < ex2.size(); j++) {
                Expression exx1 = ex1.get(i);
                Expression exx2 = ex2.get(j);
                if (!(canMerge(exx1, exx2))) {       //ne sme se jedan broj koristiti vise puta
                    continue;
                }
                if ((num1 == num2) && (i >= j)) {    //zbog komutativnosti
                    continue;
                }

                //A+B
                Expression e1 = new Expression(exx1, exx2, '+');
                exp.add(e1);
                if (e1.getValue() == this.target) {
                    this.sol.add(e1);
                }

                //A*B
                if ((exx1.getValue() != 1) && (exx2.getValue() != 1)) {     //izbegava se mnozenje jedinicom
                    Expression e2 = new Expression(exx1, exx2, '*');
                    exp.add(e2);
                    if (e2.getValue() == this.target) {
                        this.sol.add(e2);
                    }
                }

                //A-B
                if (exx1.getValue() > exx2.getValue()) { // && ()) {        //izbegava se koriscenje 'nepozitivnih' izraza
                    /*if ((exx1.getSign().equals("-")) && (i < j)) {    //???

                    }*/
                    if (exx2.getPriority() != 1) {                          //izbegava se oduzimanje zbira ili razlike
                        Expression e3 = new Expression(exx1, exx2, '-');
                        exp.add(e3);
                        if (e3.getValue() == this.target) {
                            this.sol.add(e3);
                        }
                    }
                } else if (exx2.getValue() > exx1.getValue()) {             //B-A, ako je B>A
                    if (exx1.getPriority() != 1) {
                        Expression e33 = new Expression(exx2, exx1, '-');
                        exp.add(e33);
                        if (e33.getValue() == this.target) {
                            this.sol.add(e33);
                        }
                    }
                }
                
                //A/B
                if ((exx2.getValue() != 1) && (exx2.getPriority() != 2)) {       //izbegava se deljenje jedinicom i deljenje proizvodom ili kolicnikom
                    Expression e4 = new Expression(exx1, exx2, ':');
                    exp.add(e4);
                    if (e4.getValue() == this.target) {
                        this.sol.add(e4);
                    }
                }
                //B/A
                if ((exx1.getValue() != 1) && (exx1.getPriority() != 2)) {       //izbegava se deljenje jedinicom i deljenje proizvodom ili kolicnikom
                    Expression e5 = new Expression(exx2, exx1, ':');
                    exp.add(e5);
                    if (e5.getValue() == this.target) {
                        this.sol.add(e5);
                    }
                }
            }
        }
    }
}
