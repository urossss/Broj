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
        System.out.println("Total: " + this.total);

        buildUni(exp1, exp1, exp2);
        System.out.println("Total: " + this.total);

        buildUni(exp1, exp2, exp3);
        System.out.println("Total: " + this.total);
        buildUni(exp2, exp1, exp3);
        System.out.println("Total: " + this.total);

        buildUni(exp1, exp3, exp4);
        System.out.println("Total: " + this.total);
        buildUni(exp3, exp1, exp4);
        System.out.println("Total: " + this.total);
        buildUni(exp2, exp2, exp4);
        System.out.println("Total: " + this.total);

        buildUni(exp1, exp4, exp5);
        System.out.println("Total: " + this.total);
        buildUni(exp4, exp1, exp5);
        System.out.println("Total: " + this.total);
        buildUni(exp2, exp3, exp5);
        System.out.println("Total: " + this.total);
        buildUni(exp3, exp2, exp5);
        System.out.println("Total: " + this.total);

        buildUni(exp1, exp5, exp6);
        System.out.println("Total: " + this.total);
        buildUni(exp5, exp1, exp6);
        System.out.println("Total: " + this.total);
        buildUni(exp2, exp4, exp6);
        System.out.println("Total: " + this.total);
        buildUni(exp4, exp2, exp6);
        System.out.println("Total: " + this.total);
        buildUni(exp3, exp3, exp6);
        System.out.println("Total: " + this.total);

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
            System.out.println(e);
        }
    }

    public void build1() {
        for (int i = 0; i < numbers.size(); i++) {
            Expression e = new Expression(numbers.get(i), i);
            this.exp1.add(e);
            this.total++;

            if (e.getValue() == this.target) {
                this.sol.add(e);
            }
        }
    }

    public void buildUni(ArrayList<Expression> ex1, ArrayList<Expression> ex2, ArrayList<Expression> exp) {
        for (int i = 0; i < ex1.size(); i++) {
            for (int j = 0; j < ex2.size(); j++) {
                if (canMerge(ex1.get(i), ex2.get(j))) {

                    if ((ex1.get(i).getNum() < ex2.get(j).getNum()) || ((ex1.get(i).getNum() == ex2.get(j).getNum()) && (i < j))) { //komutativnost + i *
                        Expression e1 = new Expression(ex1.get(i), ex2.get(j), "+");
                        exp.add(e1);
                        this.total++;
                        if (e1.getValue() == this.target) {
                            this.sol.add(e1);
                        }

                        if ((ex1.get(i).getValue() != 1) && (ex2.get(j).getValue() != 1)) {     //izbegava se mnozenje jedinicom
                            Expression e2 = new Expression(ex1.get(i), ex2.get(j), "*");
                            exp.add(e2);
                            this.total++;
                            if (e2.getValue() == this.target) {
                                this.sol.add(e2);
                            }
                        }
                    }

                    if ((ex1.get(i).getValue() > ex2.get(j).getValue()) && (ex2.get(j).getPriority() != 1)) {        //izbegava se koriscenje 'nepozitivnih' izraza i oduzimanje zbira ili razlike
                        Expression e3 = new Expression(ex1.get(i), ex2.get(j), "-");
                        exp.add(e3);
                        this.total++;
                        if (e3.getValue() == this.target) {
                            this.sol.add(e3);
                        }
                    }

                    if ((ex2.get(j).getValue() != 1) && (ex2.get(j).getPriority() != 2)) {       //izbegava se deljenje jedinicom i deljenje proizvodom ili kolicnikom
                        Expression e4 = new Expression(ex1.get(i), ex2.get(j), ":");
                        exp.add(e4);
                        this.total++;
                        if (e4.getValue() == this.target) {
                            this.sol.add(e4);
                        }
                    }
                }
            }
        }
    }
}
