package broj.expression;

import java.util.*;

public class ExpressionBuilder {

    private int target;
    private ArrayList<Integer> numbers = new ArrayList<>();
    private ArrayList<Expression> exp1 = new ArrayList<>();
    private ArrayList<Expression> exp2 = new ArrayList<>();
    private ArrayList<Expression> exp3 = new ArrayList<>();
    private ArrayList<Expression> exp4 = new ArrayList<>();
    private ArrayList<Expression> exp5 = new ArrayList<>();
    private ArrayList<Expression> exp6 = new ArrayList<>();
    private int total;                                      //ukupan broj izraza u exp1, exp2... exp6
    private ArrayList<Expression> sol = new ArrayList<>();  //lista svih resenja - tacnih ako postoje ili pribliznih ako nema tacnih
    private ArrayList<String> solStr = new ArrayList<>();   //lista svih resenja, samo stringovi za ispis   //PROBATI OVO!!!
    private boolean findAll;    //true ako je potrebno naci sva resenja
    private boolean found;      //true ako je neko resenje vec pronadjeno
    private int minDif;

    //Constructor
    public ExpressionBuilder(Integer target, ArrayList<Integer> numbers) {
        this.target = target;
        this.numbers = numbers;
        this.total = 0;
        this.findAll = true;
        this.minDif = 999999;
    }

    public void startBuild() {
        build1();

        buildUni(exp1, exp1, exp2);

        buildUni(exp2, exp1, exp3);

        buildUni(exp3, exp1, exp4);
        buildUni(exp2, exp2, exp4);

        buildUni(exp4, exp1, exp5);
        buildUni(exp3, exp2, exp5);

        buildUni(exp5, exp1, exp6);
        buildUni(exp4, exp2, exp6);
        buildUni(exp3, exp3, exp6);

        this.total = exp1.size() + exp2.size() + exp3.size() + exp4.size() + exp5.size() + exp6.size();
        System.out.println("Total expressions: " + this.total);

        printAllSolutions();
    }

    public void findAll() {
        this.findAll = true;
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
            e.printExpression();
            //e.printParents();
        }
        System.out.println("Total: " + this.sol.size());
    }

    public boolean asoc(Expression left, Expression right, char znak) {
        if ((left.getNum() == 1) && (right.getNum() == 1)) {
            return false;
        }
        if (left.getSign() != znak) {
            return false;
        }
        if (left.getRight().getIndexes().get(0) < right.getIndexes().get(0)) {
            return false;
        }
        return true;
    }

    public boolean newSolution(Expression e) {      //vraca true ako je tacno resenje pronadjeno i ne treba traziti dalje
        if (e.getValue() == this.target) {
            if (this.minDif != 0) {
                this.sol.clear();
                this.minDif = 0;
            }
            this.found = true;
            if (!this.findAll) {
                e.printExpression();
                return true;
            }
            this.sol.add(e);
        } else if (Math.abs(e.getValue() - this.target) < minDif) {
            e.setValidity();
            if (e.getValidity()) {      //uzimamo samo cele brojeve za resenja
                this.minDif = (int) (Math.abs(e.getValue() - this.target));
                this.sol.clear();
                this.sol.add(e);
            }

        } else if (Math.abs(e.getValue() - this.target) == minDif) {
            this.sol.add(e);
        }
        return false;
    }

    public void build1() {
        for (int i = 0; i < numbers.size(); i++) {
            Expression e = new Expression(numbers.get(i), i);
            this.exp1.add(e);

            if (newSolution(e)) {
                return;
            }
        }
    }

    public void buildUni(ArrayList<Expression> ex1, ArrayList<Expression> ex2, ArrayList<Expression> exp) { //num1>num2 uvek vazi, tako smo pozivali f-ju
        if (found && !this.findAll) {
            return;
        }
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
                if ((exx1.getSign() != '-') && (exx2.getPriority() != 1)) {     //da se ne dodaje zbir ili razlika, vec ako se vec pravi neki zbir, da se dodaje samo jedan po jedan
                    if (!asoc(exx1, exx2, '+')) {
                        Expression e1 = new Expression(exx1, exx2, '+');
                        exp.add(e1);
                        if (newSolution(e1)) {
                            return;
                        }
                    }
                }

                //A*B
                if (((exx1.getValue() != 1) && (exx2.getValue() != 1)) //izbegava se mnozenje jedinicom
                        && (exx1.getSign() != ':') && (exx2.getPriority() != 2)) {
                    if (!asoc(exx1, exx2, '*')) {
                        Expression e2 = new Expression(exx1, exx2, '*');
                        exp.add(e2);
                        if (newSolution(e2)) {
                            return;
                        }
                    }
                }

                //A-B
                if (exx1.getValue() > exx2.getValue()) { // && ()) {        //izbegava se koriscenje 'nepozitivnih' izraza
                    if (exx2.getPriority() != 1) {                          //izbegava se oduzimanje zbira ili razlike
                        if (!asoc(exx1, exx2, '-')) {
                            Expression e3 = new Expression(exx1, exx2, '-');
                            exp.add(e3);
                            if (newSolution(e3)) {
                                return;
                            }
                        }
                    }
                } else if (exx2.getValue() > exx1.getValue()) {             //B-A, ako je B>A
                    if (exx1.getPriority() != 1) {
                        if (!asoc(exx2, exx1, '-')) {
                            Expression e33 = new Expression(exx2, exx1, '-');
                            exp.add(e33);
                            if (newSolution(e33)) {
                                return;
                            }
                        }
                    }
                }

                //A/B
                if ((exx2.getValue() != 1) && (exx2.getPriority() != 2)) {       //izbegava se deljenje jedinicom i deljenje proizvodom ili kolicnikom
                    if (!asoc(exx1, exx2, ':')) {
                        Expression e4 = new Expression(exx1, exx2, ':');
                        exp.add(e4);
                        if (newSolution(e4)) {
                            return;
                        }
                    }
                }
                //B/A
                if ((exx1.getValue() != 1) && (exx1.getPriority() != 2)) {       //izbegava se deljenje jedinicom i deljenje proizvodom ili kolicnikom
                    if (!asoc(exx2, exx1, ':')) {
                        Expression e5 = new Expression(exx2, exx1, ':');
                        exp.add(e5);
                        if (newSolution(e5)) {
                            return;
                        }
                    }
                }
            }
        }
    }
}
