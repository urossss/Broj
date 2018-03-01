package broj.expression;

import java.util.*;

public final class Expression {

    private String expression;  //izraz u formi stringa
    private Double value;       //brojna vrednost izraza
    private int num;        //broj clanova izraza
    private int priority;   //prioritet operacija: 0 ako je samo jedan clan, 1 ako ima + ili -, 2 ako je samo * ili /
    private Boolean valid;      //true ako je value ceo broj, u suprotnom false
    private ArrayList<Integer> indexes = new ArrayList<>(); //indeksi ponudjenih brojeva koji su iskorisceni u ovom izrazu
    private Expression left;
    private Expression right;
    private char sign;

    public Expression(Expression exp1, Expression exp2, char op) {
        this.left = exp1;
        this.right = exp2;
        this.sign = op;
        this.expression = "";

        switch (op) {
            case '+':
                this.value = exp1.getValue() + exp2.getValue();
                this.priority = 1;
                break;
            case '-':
                this.value = exp1.getValue() - exp2.getValue();
                this.priority = 1;
                break;
            case '*':
                this.value = exp1.getValue() * exp2.getValue();
                this.priority = 2;
                break;
            case '/':
                this.value = exp1.getValue() / exp2.getValue();
                this.priority = 2;
                break;
            default:
                break;
        }

        this.num = exp1.getNum() + exp2.getNum();

        //setValidity();
        setIndexes(exp1, exp2);
    }

    public Expression(Integer n, int i) {
        this.expression = n.toString();
        this.value = (double) n;
        this.priority = 0;
        this.num = 1;
        this.indexes.add(i);
        this.sign = 0;

        //setValidity();
    }

    public String getExpression() {
        formExpressionString();
        return this.expression;
    }

    public double getValue() {
        return this.value;
    }

    public int getNum() {
        return this.num;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean getValidity() {
        return this.valid;
    }

    public ArrayList<Integer> getIndexes() {
        return this.indexes;
    }

    public char getSign() {
        return this.sign;
    }
    
    public Expression getLeft() {
        return this.left;
    }
    
    public Expression getRight() {
        return this.right;
    }
    
    public void printExpression() {
        formExpressionString();
        System.out.println(this);
    }

    public void formExpressionString() {
        if (this.sign == 0) {
            return;
        }
        this.expression = "";
        switch (this.sign) {
            case '+':
                this.expression = left.getExpression() + "+" + right.getExpression();
                break;
            case '-':
                this.expression = left.getExpression() + "-";
                if (right.getPriority() == 1) {
                    this.expression += "(" + right.getExpression() + ")";
                } else {
                    this.expression += right.getExpression();
                }
                break;
            case '*':
                if (left.getPriority() == 1) {
                    this.expression += "(" + left.getExpression() + ")";
                } else {
                    this.expression += left.getExpression();
                }
                this.expression += "*";
                if (right.getPriority() == 1) {
                    this.expression += "(" + right.getExpression() + ")";
                } else {
                    this.expression += right.getExpression();
                }
                break;
            case '/':
                if (left.getPriority() == 1) {
                    this.expression += "(" + left.getExpression() + ")";
                } else {
                    this.expression += left.getExpression();
                }
                this.expression += "/";
                if (right.getPriority() != 0) {
                    this.expression += "(" + right.getExpression() + ")";
                } else {
                    this.expression += right.getExpression();
                }
                break;
            default:
                break;
        }
    }

    public void setValidity() {
        this.valid = Math.ceil(this.value) == Math.floor(this.value);
    }

    public void setIndexes(Expression exp1, Expression exp2) {
        for (int n : exp1.getIndexes()) {
            this.indexes.add(n);
        }
        for (int n : exp2.getIndexes()) {
            if (!this.indexes.contains(n)) {
                this.indexes.add(n);
            }
        }
    }

    public void printIndexes() {    //
        for (int n : this.indexes) {
            System.out.print(n + " ");
        }
        System.out.println("");
    }

    public void printParents() {    //
        System.out.println("     = #" + left.toString() + "# " + sign + " #" + right.toString() + "#");
    }

    @Override
    public String toString() {
        String s = this.expression + " = " + this.value.toString();
        //if (this.valid == true) {
        //    s += "T ";
        //}
        return s;
    }
    
    //proveriti ovo malo i dodati override za hashcode() ako zatreba
    /*@Override
    public boolean equals(Object o) {
        return this.expression.equals(((Expression) o).getExpression());
    }*/

}
