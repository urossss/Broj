package broj.gui;

import broj.expression.*;
import java.util.*;
import javax.swing.*;

public class Screen extends javax.swing.JFrame {

    private final int X = 50;
    private Random rand = new Random();

    private boolean set;
    private boolean thread_run;
    private boolean stop_all = true;

    private Thread t1;

    private int target;                                         //trazeni broj
    private ArrayList<Integer> numbers = new ArrayList<>();     //ponudjeni brojevi
    private ArrayList<JLabel> numbersLabs = new ArrayList<>();  //labele sa ponudjenim brojevima
    private ArrayList<String> exp = new ArrayList<>();          //izraz koji koirisnik unosi
    private String expString;                                   //izraz koji koirisnik unosi, u formi stringa

    /**
     * Creates new form Screen
     */
    public Screen() {
        initComponents();
        //
        addLabs();
        restart();
    }

    public void restart() {
        thread_run = true;
        set = false;

        numbers.clear();
        exp.clear();
        userSolution.setText("");

        lab1.setText(" ");
        lab2.setText(" ");
        lab3.setText(" ");
        /*lab4.setText(" ");
        lab5.setText(" ");
        lab6.setText(" ");
        lab7.setText(" ");
        lab8.setText(" ");
        lab9.setText(" ");*/

        enableLabs();

        t1 = new Thread() {
            public void run() {
                initialize(this);
            }
        };
        t1.start();
    }

    public void initialize(Thread t) {
        countdown(lab1, t, 0);
        countdown(lab2, t, 0);
        countdown(lab3, t, 0);
        countdown(lab4, t, 1);
        countdown(lab5, t, 1);
        countdown(lab6, t, 1);
        countdown(lab7, t, 1);
        countdown(lab8, t, 10);
        countdown(lab9, t, 25);

        if (!t.isInterrupted()) {
            target = Integer.parseInt(lab1.getText()) * 100
                    + Integer.parseInt(lab2.getText()) * 10
                    + Integer.parseInt(lab3.getText());

            System.out.println(target);

            numbers.add(Integer.parseInt(lab4.getText()));
            numbers.add(Integer.parseInt(lab5.getText()));
            numbers.add(Integer.parseInt(lab6.getText()));
            numbers.add(Integer.parseInt(lab7.getText()));
            numbers.add(Integer.parseInt(lab8.getText()));
            numbers.add(Integer.parseInt(lab9.getText()));

            for (int n : numbers) {
                System.out.print(n + " ");
            }
            System.out.println("");
        }
    }

    public void countdown(JLabel lab, Thread t, int mode) {
        int i = mode;
        if (!t.isInterrupted()) {
            do {
                if (mode == 0) {
                    i = rand.nextInt(10);
                } else if (mode == 1) {
                    i = rand.nextInt(9) + 1;
                } else if (mode == 10) {
                    i = 5 * (rand.nextInt(3) + 2);
                } else {
                    i = 25 * (rand.nextInt(4) + 1);
                }
                lab.setText("" + i);
                try {
                    t.sleep(X);
                } catch (InterruptedException e) {
                    //ignore exception
                    System.out.println("x");
                    t.interrupt();
                }
            } while (!set && thread_run && !t.isInterrupted());
        }

        if (!stop_all) {
            set = false;
        }
    }

    public void addLabs() {
        numbersLabs.add(lab4);
        numbersLabs.add(lab5);
        numbersLabs.add(lab6);
        numbersLabs.add(lab7);
        numbersLabs.add(lab8);
        numbersLabs.add(lab9);
    }

    public void enableLabs() {
        for (JLabel l : numbersLabs) {
            l.setText(" ");
            l.setEnabled(true);
        }
    }

    public boolean allNumbersUsed() {
        for (JLabel l : numbersLabs) {
            if (l.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    public boolean isOp(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    public boolean isParentheses(String s) {
        return s.equals("(") || s.equals(")");
    }

    public void makeExp() {
        expString = "";
        for (String s : exp) {
            expString += s;
        }
    }

    public boolean addNumber(String num) {
        if (exp.isEmpty() || isOp(exp.get(exp.size() - 1)) || exp.get(exp.size() - 1).equals("(")) {
            exp.add(num);
            makeExp();
            userSolution.setText(expString);
            return true;
        }
        return false;
    }

    public void addOperation(String op) {
        if (!(exp.isEmpty() || allNumbersUsed() || isOp(exp.get(exp.size() - 1)) || exp.get(exp.size() - 1).equals("("))) {
            exp.add(op);
            makeExp();
            userSolution.setText(expString);
        }
    }

    public void addOpenPar() {
        if (exp.isEmpty() || exp.get(exp.size() - 1).equals("(") || isOp(exp.get(exp.size() - 1))) {
            exp.add("(");
            makeExp();
            userSolution.setText(expString);
        }
    }

    public void addClosedPar() {
        if (!(exp.isEmpty() || isOp(exp.get(exp.size() - 1)) || exp.get(exp.size() - 1).equals("("))) {
            int open = 0;
            int closed = 0;
            for (String s : exp) {
                if (s.equals("(")) {
                    open++;
                } else if (s.equals(")")) {
                    closed++;
                }
            }
            if (open > closed) {
                exp.add(")");
                makeExp();
                userSolution.setText(expString);
            }
        }
    }

    public void calculate() {
        
        InfixToPostfix converter = new InfixToPostfix();
        //converter.printExpression();
        System.out.println(" = " + PostfixCalculator.evaluateExpression(converter.convert(userSolution.getText())));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        _stop = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lab7 = new javax.swing.JLabel();
        lab5 = new javax.swing.JLabel();
        lab6 = new javax.swing.JLabel();
        lab4 = new javax.swing.JLabel();
        number = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        lab2 = new javax.swing.JLabel();
        lab3 = new javax.swing.JLabel();
        lab8 = new javax.swing.JLabel();
        lab9 = new javax.swing.JLabel();
        _restart = new javax.swing.JButton();
        userSolution = new javax.swing.JTextField();
        ops = new javax.swing.JPanel();
        _op_plus = new javax.swing.JButton();
        _op_minus1 = new javax.swing.JButton();
        _op_puta = new javax.swing.JButton();
        _op_podeljeno = new javax.swing.JButton();
        _op_otv_zagrada = new javax.swing.JButton();
        _op_zatv_zagrada = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(43, 155, 243));
        setForeground(new java.awt.Color(0, 153, 255));

        jPanel1.setBackground(new java.awt.Color(51, 153, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        _stop.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        _stop.setText("STOP");
        _stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _stopActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(51, 153, 255));

        lab7.setBackground(new java.awt.Color(255, 51, 51));
        lab7.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab7.setForeground(new java.awt.Color(255, 255, 255));
        lab7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab7.setText("0");
        lab7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab7MouseClicked(evt);
            }
        });

        lab5.setBackground(new java.awt.Color(255, 51, 51));
        lab5.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab5.setForeground(new java.awt.Color(255, 255, 255));
        lab5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab5.setText("0");
        lab5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab5MouseClicked(evt);
            }
        });

        lab6.setBackground(new java.awt.Color(255, 51, 51));
        lab6.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab6.setForeground(new java.awt.Color(255, 255, 255));
        lab6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab6.setText("0");
        lab6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab6MouseClicked(evt);
            }
        });

        lab4.setBackground(new java.awt.Color(255, 51, 51));
        lab4.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab4.setForeground(new java.awt.Color(255, 255, 255));
        lab4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab4.setText("0");
        lab4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lab5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lab6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        number.setBackground(new java.awt.Color(51, 153, 255));

        lab1.setBackground(new java.awt.Color(255, 255, 255));
        lab1.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab1.setForeground(new java.awt.Color(255, 255, 255));
        lab1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab1.setText("0");
        lab1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lab2.setBackground(new java.awt.Color(255, 51, 51));
        lab2.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab2.setForeground(new java.awt.Color(255, 255, 255));
        lab2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab2.setText("0");
        lab2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lab3.setBackground(new java.awt.Color(255, 51, 51));
        lab3.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab3.setForeground(new java.awt.Color(255, 255, 255));
        lab3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab3.setText("0");
        lab3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab3.setPreferredSize(new java.awt.Dimension(50, 100));

        javax.swing.GroupLayout numberLayout = new javax.swing.GroupLayout(number);
        number.setLayout(numberLayout);
        numberLayout.setHorizontalGroup(
            numberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(numberLayout.createSequentialGroup()
                .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        numberLayout.setVerticalGroup(
            numberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, numberLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(numberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lab8.setBackground(new java.awt.Color(255, 51, 51));
        lab8.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab8.setForeground(new java.awt.Color(255, 255, 255));
        lab8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab8.setText("0");
        lab8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab8MouseClicked(evt);
            }
        });

        lab9.setBackground(new java.awt.Color(255, 51, 51));
        lab9.setFont(new java.awt.Font("Tahoma", 0, 90)); // NOI18N
        lab9.setForeground(new java.awt.Color(255, 255, 255));
        lab9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lab9.setText("0");
        lab9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        lab9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lab9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lab9MouseClicked(evt);
            }
        });

        _restart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _restartActionPerformed(evt);
            }
        });

        userSolution.setEditable(false);
        userSolution.setBackground(new java.awt.Color(51, 153, 255));
        userSolution.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        userSolution.setForeground(new java.awt.Color(255, 255, 255));
        userSolution.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        ops.setBackground(new java.awt.Color(51, 153, 255));

        _op_plus.setBackground(new java.awt.Color(51, 153, 255));
        _op_plus.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_plus.setForeground(new java.awt.Color(255, 255, 255));
        _op_plus.setText("+");
        _op_plus.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_plus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_plusActionPerformed(evt);
            }
        });

        _op_minus1.setBackground(new java.awt.Color(51, 153, 255));
        _op_minus1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_minus1.setForeground(new java.awt.Color(255, 255, 255));
        _op_minus1.setText("-");
        _op_minus1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_minus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_minus1ActionPerformed(evt);
            }
        });

        _op_puta.setBackground(new java.awt.Color(51, 153, 255));
        _op_puta.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_puta.setForeground(new java.awt.Color(255, 255, 255));
        _op_puta.setText("*");
        _op_puta.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_puta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_putaActionPerformed(evt);
            }
        });

        _op_podeljeno.setBackground(new java.awt.Color(51, 153, 255));
        _op_podeljeno.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_podeljeno.setForeground(new java.awt.Color(255, 255, 255));
        _op_podeljeno.setText("/");
        _op_podeljeno.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_podeljeno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_podeljenoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout opsLayout = new javax.swing.GroupLayout(ops);
        ops.setLayout(opsLayout);
        opsLayout.setHorizontalGroup(
            opsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(_op_plus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(_op_minus1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(_op_puta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(_op_podeljeno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        opsLayout.setVerticalGroup(
            opsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(_op_plus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_minus1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_puta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_podeljeno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        _op_otv_zagrada.setBackground(new java.awt.Color(51, 153, 255));
        _op_otv_zagrada.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_otv_zagrada.setForeground(new java.awt.Color(255, 255, 255));
        _op_otv_zagrada.setText("(");
        _op_otv_zagrada.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_otv_zagrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_otv_zagradaActionPerformed(evt);
            }
        });

        _op_zatv_zagrada.setBackground(new java.awt.Color(51, 153, 255));
        _op_zatv_zagrada.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_zatv_zagrada.setForeground(new java.awt.Color(255, 255, 255));
        _op_zatv_zagrada.setText(")");
        _op_zatv_zagrada.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_zatv_zagrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_zatv_zagradaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(336, 336, 336)
                                .addComponent(_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(userSolution, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ops, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(48, 48, 48)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(51, 51, 51)
                                                .addComponent(lab9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(_op_otv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(_op_zatv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addGap(0, 105, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(257, 257, 257)
                        .addComponent(_restart, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(_restart, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ops, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(_op_otv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(_op_zatv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addComponent(userSolution, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void _op_putaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_putaActionPerformed
        addOperation("*");
    }//GEN-LAST:event__op_putaActionPerformed

    private void _op_plusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_plusActionPerformed
        addOperation("+");
    }//GEN-LAST:event__op_plusActionPerformed

    private void _restartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__restartActionPerformed
        thread_run = false;
        t1.interrupt();
        restart();
    }//GEN-LAST:event__restartActionPerformed

    private void lab9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab9MouseClicked
        if (lab9.isEnabled()) {
            if (addNumber(lab9.getText())) {
                lab9.setEnabled(false);
            }
        }
    }//GEN-LAST:event_lab9MouseClicked

    private void lab8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab8MouseClicked
        if (lab8.isEnabled()) {
            if (addNumber(lab8.getText())) {
                lab8.setEnabled(false);
            }
        }
    }//GEN-LAST:event_lab8MouseClicked

    private void lab4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab4MouseClicked
        if (lab4.isEnabled()) {
            if (addNumber(lab4.getText())) {
                lab4.setEnabled(false);
            }
        }
    }//GEN-LAST:event_lab4MouseClicked

    private void lab6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab6MouseClicked
        if (lab6.isEnabled()) {
            if (addNumber(lab6.getText())) {
                lab6.setEnabled(false);
            }
        }
    }//GEN-LAST:event_lab6MouseClicked

    private void lab5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab5MouseClicked
        if (lab5.isEnabled()) {
            if (addNumber(lab5.getText())) {
                lab5.setEnabled(false);
            }
        }
    }//GEN-LAST:event_lab5MouseClicked

    private void lab7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab7MouseClicked
        if (lab7.isEnabled()) {
            if (addNumber(lab7.getText())) {
                lab7.setEnabled(false);
            }
        }
    }//GEN-LAST:event_lab7MouseClicked

    private void _stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__stopActionPerformed
        set = true;
    }//GEN-LAST:event__stopActionPerformed

    private void _op_minus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_minus1ActionPerformed
        addOperation("-");
    }//GEN-LAST:event__op_minus1ActionPerformed

    private void _op_podeljenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_podeljenoActionPerformed
        addOperation("/");
    }//GEN-LAST:event__op_podeljenoActionPerformed

    private void _op_otv_zagradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_otv_zagradaActionPerformed
        addOpenPar();
    }//GEN-LAST:event__op_otv_zagradaActionPerformed

    private void _op_zatv_zagradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_zatv_zagradaActionPerformed
        addClosedPar();
    }//GEN-LAST:event__op_zatv_zagradaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Screen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Screen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Screen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Screen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Screen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _op_minus1;
    private javax.swing.JButton _op_otv_zagrada;
    private javax.swing.JButton _op_plus;
    private javax.swing.JButton _op_podeljeno;
    private javax.swing.JButton _op_puta;
    private javax.swing.JButton _op_zatv_zagrada;
    private javax.swing.JButton _restart;
    private javax.swing.JButton _stop;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab3;
    private javax.swing.JLabel lab4;
    private javax.swing.JLabel lab5;
    private javax.swing.JLabel lab6;
    private javax.swing.JLabel lab7;
    private javax.swing.JLabel lab8;
    private javax.swing.JLabel lab9;
    private javax.swing.JPanel number;
    private javax.swing.JPanel ops;
    private javax.swing.JTextField userSolution;
    // End of variables declaration//GEN-END:variables
}
