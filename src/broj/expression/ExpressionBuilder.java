package broj.expression;

import java.util.*;

public class ExpressionBuilder {

    private int target; // trazeni broj
    private ArrayList<Integer> numbers = new ArrayList<>(); // ponudjeni brojevi
    private ArrayList<Expression> exp1 = new ArrayList<>();
    private ArrayList<Expression> exp2 = new ArrayList<>();
    private ArrayList<Expression> exp3 = new ArrayList<>();
    private ArrayList<Expression> exp4 = new ArrayList<>();
    private ArrayList<Expression> exp5 = new ArrayList<>();
    private ArrayList<Expression> exp6 = new ArrayList<>();
    private int total;                                      //ukupan broj izraza u exp1, exp2... exp6
    private ArrayList<Expression> sol = new ArrayList<>();  //lista svih resenja - tacnih ako postoje ili pribliznih ako nema tacnih
    //private ArrayList<String> solStr = new ArrayList<>();   //lista svih resenja, samo stringovi za ispis   //PROBATI OVO!!!
    private boolean findAll;    //true ako je potrebno naci sva resenja
    private boolean found;      //true ako je neko resenje vec pronadjeno
    private int minDif;

    //Constructors
    public ExpressionBuilder() {

    }

    public ExpressionBuilder(int target, ArrayList<Integer> numbers) {
        reset(target, numbers);
    }

    public void reset(int target, ArrayList<Integer> numbers) {
        this.target = target;
        this.numbers = numbers;
        this.total = 0;
        this.findAll = false;
        this.minDif = 999999;
        this.found = false;
        clearLists();
    }

    public void findAll(boolean b) {
        this.findAll = b;
    }

    public void findAll() {
        this.findAll = true;
    }

    public void clearLists() {
        exp1.clear();
        exp2.clear();
        exp3.clear();
        exp4.clear();
        exp5.clear();
        exp6.clear();
        sol.clear();
    }

    public void startBuild() {
        //sortiramo brojeve da bi se izbegla neka dupla resenja
        Collections.sort(numbers);
        //obrcemo listu da bi (mozda) bili lepsi izrazi, tipa 100+1, a ne 1+100
        Collections.reverse(numbers);

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

        removeDuplicates();
    }

    // proverave da li se od data 2 izraza moze formirati novi, tj. da li su izrazi sastavljeni od razlicitih ponudjenih brojeva
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

    public void removeDuplicates() {
        for (int i = 0; i < sol.size(); i++) {
            for (int j = i+1; j < sol.size(); j++) {
                if (sol.get(j).getExpression().equals(sol.get(i).getExpression())) {
                    sol.remove(j);
                }
            }
        }
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

    public boolean newSolution(Expression e) {      // vraca true ako je tacno resenje pronadjeno i ne treba traziti dalje
        if (e.getValue() == this.target) {
            if (this.minDif != 0) {
                this.sol.clear();
                this.minDif = 0;
            }
            this.found = true;
            if (!this.findAll) {
                //e.printExpression();
                this.sol.add(e);
                return true;
            }
            this.sol.add(e);
        } else if (Math.abs(e.getValue() - this.target) < minDif) {
            e.setValidity();
            if (e.isValid()) {      // uzimamo samo cele brojeve za resenja
                this.minDif = (int) (Math.abs(e.getValue() - this.target));
                this.sol.clear();
                this.sol.add(e);
            }

        } else if (Math.abs(e.getValue() - this.target) == minDif) {
            this.sol.add(e);
        }
        return false;
    }

    // formira izraze od 1 clana, tj. konvertuje sam broj u izraz
    public void build1() {
        for (int i = 0; i < numbers.size(); i++) {
            Expression e = new Expression(numbers.get(i), i);
            this.exp1.add(e);

            if (newSolution(e)) {
                return;
            }
        }
    }

    // formira sve odgovarajuce kombinacije izraza (jednog iz ex1 i jednog iz ex2) i smesta ih u exp
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
                if (!(canMerge(exx1, exx2))) {       // ne sme se jedan ponudjeni broj koristiti vise puta
                    continue;
                }
                if ((num1 == num2) && (i >= j)) {    // zbog komutativnosti
                    continue;
                }

                //A+B
                if ((exx1.getSign() != '-') && (exx2.getPriority() != 1)) {     // da se ne dodaje zbir ili razlika, vec ako se vec pravi neki zbir, da se dodaje samo jedan po jedan
                    if (!asoc(exx1, exx2, '+')) {
                        Expression e1 = new Expression(exx1, exx2, '+');
                        exp.add(e1);
                        if (newSolution(e1)) {
                            return;
                        }
                    }
                }

                //A*B
                if (((exx1.getValue() != 1) && (exx2.getValue() != 1)) // izbegava se mnozenje jedinicom
                        && (exx1.getSign() != '/') && (exx2.getPriority() != 2)) {  // slicno kao kod sabiranja...
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
                    if (!asoc(exx1, exx2, '/')) {
                        Expression e4 = new Expression(exx1, exx2, '/');
                        exp.add(e4);
                        if (newSolution(e4)) {
                            return;
                        }
                    }
                }
                //B/A
                if ((exx1.getValue() != 1) && (exx1.getPriority() != 2)) {       //izbegava se deljenje jedinicom i deljenje proizvodom ili kolicnikom
                    if (!asoc(exx2, exx1, '/')) {
                        Expression e5 = new Expression(exx2, exx1, '/');
                        exp.add(e5);
                        if (newSolution(e5)) {
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public ArrayList<Expression> getSol() {
        return this.sol;
    }

    public int getTotal() {
        return this.total;
    }
}
