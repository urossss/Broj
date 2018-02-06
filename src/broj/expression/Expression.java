package broj.expression;

import java.util.*;

public final class Expression {
    
    String expression;  //izraz u formi stringa
    Double value;       //brojna vrednost izraza
    Integer num;        //broj clanova izraza
    Integer priority;   //prioritet operacija: 0 ako je samo jedan clan, 1 ako ima + ili -, 2 ako je samo * ili :
    Boolean valid;      //true ako je value ceo broj, u suprotnom false
    ArrayList<Integer> indexes = new ArrayList<>(); //indeksi ponudjenih brojeva koji su iskorisceni u ovom izrazu
    
    public Expression(Expression exp1, Expression exp2, String op) {
        if ((exp1.getPriority()==1) && (op.equals("*") || op.equals(":"))) {
            this.expression = "(" + exp1.getExpression() + ")";
        } else {
            this.expression = exp1.getExpression();
        }
        
        switch (op) {
            case "+":
                this.value = exp1.getValue() + exp2.getValue();
                this.expression = this.expression + "+" + exp2.getExpression();
                this.priority = 1;
                break;
            case "-":
                this.value = exp1.getValue() - exp2.getValue();
                this.expression += "-";
                if (exp2.getPriority() == 1) {
                    this.expression += "(" + exp2.getExpression() + ")";
                } else {
                    this.expression += exp2.getExpression();
                }   this.priority = 1;
                break;
            case "*":
                this.value = exp1.getValue() * exp2.getValue();
                this.expression += "*";
                if (exp2.getPriority() == 1) {
                    this.expression += "(" + exp2.getExpression() + ")";
                } else {
                    this.expression += exp2.getExpression();
                }   this.priority = 2;
                break;
            case ":":
                this.value = exp1.getValue() / exp2.getValue();
                this.expression += ":";
                if (exp2.getPriority()==1 || exp2.getPriority()==2) {
                    this.expression += "(" + exp2.getExpression() + ")";
                } else {
                    this.expression += exp2.getExpression();
                }   this.priority = 2;
                break;
            default:
                break;
        }
        
        this.num = exp1.getNum() + exp2.getNum();
      
        //setValidity();
        setIndexes(exp1, exp2);
        
        //System.out.println(toString());
        //printIndexes();
    }
    
    public Expression(Integer n, int i) {
        this.expression = n.toString();
        this.value = (double) n;
        this.priority = 0;
        this.num = 1;
        this.indexes.add(i);
        
        //setValidity();
        
        //System.out.println(toString());
        //printIndexes();
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public double getValue() {
        return this.value;
    }
    
    public Integer getNum() {
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
    
    public void setValidity() {
        this.valid = Math.ceil(this.value) == Math.floor(this.value);
    }
    
    public void setIndexes(Expression exp1, Expression exp2) {
        for (int n: exp1.getIndexes()) {
            this.indexes.add(n);
        }
        for (int n: exp2.getIndexes()) {
            if (!this.indexes.contains(n)) {
                this.indexes.add(n);
            }
        }
    }
    
    public void printIndexes() {
        for (int n: this.indexes) {
            System.out.print(n + " ");
        }
        System.out.println("");
    }
    
    @Override
    public String toString() {
        String s = this.expression + " = " + this.value.toString() + " ";
        if (this.valid == true) {
            s += "T ";
        }
        return s;
    }
    
}
