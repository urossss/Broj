package broj.gui;

import broj.expression.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Screen extends javax.swing.JFrame {

    private final int X = 50;           //broj milisekundi izmedju 2 broja pri zaustavljanju brojaca
    private Random rand = new Random();

    private boolean set;                //indikator za zaustavljanje brojaca
    private boolean thread_run;         //flag za zaustavljanje brojaca kad se svi brojevi izaberu ili se ide reset
    private boolean stop_all;           //true ako hocemo automatsko postavljanje brojeva - random
    private boolean started;        //true ako su brojevi zaustavljeni, moze se poceti racunanje
    //private boolean done;           //true ako je korisnik odigrao svoje (kliknuo je da se izracuna uneti izraz)
    private boolean comp;

    private Thread t;      //nit koja obavlja zaustavljanje brojeva

    private int target;                                         //trazeni broj
    private ArrayList<Integer> numbers = new ArrayList<>();     //ponudjeni brojevi
    private ArrayList<JLabel> numbersLabs = new ArrayList<>();  //labele sa ponudjenim brojevima
    private ArrayList<JLabel> disabledLabs = new ArrayList<>(); //labele sa iskoriscenim ponudjenim brojevima
    private ArrayList<String> exp = new ArrayList<>();          //izraz koji korisnik unosi
    private String expString;                                   //izraz koji korisnik unosi, u formi stringa, za ispis

    private InfixToPostfix converter = new InfixToPostfix();        //konvertuje izraz koji korisnik unese u postfiksnu notaciju
    private ExpressionBuilder builder = new ExpressionBuilder();    //nalazi tacan broj
    private ArrayList<Expression> sol = new ArrayList<>();          //lista resenja koje racunar nadje
    private int total;                                              //ukupan broj izracunatih izraza

    /**
     * Creates new form Screen
     */
    public Screen() {
        initComponents();

        Image im = Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/icon_numbers2.png"));
        setIconImage(im);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        //
        addLabs();
        _findAll.doClick();
        _showStats.doClick();

        restart();
    }

    public void restart() {
        //indikatori se resetuju
        thread_run = true;
        set = false;
        stop_all = false;
        started = false;
        comp = false;

        //prazne se liste
        numbers.clear();
        exp.clear();
        disabledLabs.clear();
        sol.clear();

        //brisu se ispisi sa ekrana
        _sol_user.setText("");
        _res_user.setText("");
        _sol_comp.setText("");
        _res_comp.setText("");
        _sol_comp_area.setText("");
        _stats.setText("");
        lab1.setText(" ");
        lab2.setText(" ");
        lab3.setText(" ");

        computer.setEnabled(true);
        yes.setEnabled(true);
        no.setEnabled(true);
        enableLabs();
        expString = "";

        if (_compCanCalc.isSelected()) {
            _compCanCalc.doClick();
        }

        //pokrecu se brojaci za zaustavljanje brojeva
        t = new Thread() {
            public void run() {
                initialize(this);
            }
        };
        t.start();
    }

    public void initialize(Thread t) {
        //zaustavljanje jednog po jednog broja
        countdown(lab1, t, 0);
        countdown(lab2, t, 0);
        countdown(lab3, t, 0);
        countdown(lab4, t, 1);
        countdown(lab5, t, 1);
        countdown(lab6, t, 1);
        countdown(lab7, t, 1);
        countdown(lab8, t, 10);
        countdown(lab9, t, 25);

        //ako je zaustavljanje uspesno (nije resetovano) formiraju se trazeni i ponudjeni brojevi
        if (!t.isInterrupted()) {
            target = Integer.parseInt(lab1.getText()) * 100
                    + Integer.parseInt(lab2.getText()) * 10
                    + Integer.parseInt(lab3.getText());

            numbers.add(Integer.parseInt(lab4.getText()));
            numbers.add(Integer.parseInt(lab5.getText()));
            numbers.add(Integer.parseInt(lab6.getText()));
            numbers.add(Integer.parseInt(lab7.getText()));
            numbers.add(Integer.parseInt(lab8.getText()));
            numbers.add(Integer.parseInt(lab9.getText()));

            started = true;
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
                    t.interrupt();
                }
            } while (!set && thread_run && !t.isInterrupted());
        }

        if (!stop_all) {
            set = false;
        }
    }

    //samo pravi listu sa labelama gde su ponudjeni brojevi
    public void addLabs() {
        numbersLabs.add(lab4);
        numbersLabs.add(lab5);
        numbersLabs.add(lab6);
        numbersLabs.add(lab7);
        numbersLabs.add(lab8);
        numbersLabs.add(lab9);
    }

    //resetuje ponudjene brojeve
    public void enableLabs() {
        for (JLabel l : numbersLabs) {
            l.setText(" ");
            l.setEnabled(true);
        }
    }

    //proverava da li su vec iskorisceni svi ponudjeni brojevi
    public boolean allNumbersUsed() {
        for (JLabel l : numbersLabs) {
            if (l.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    //proverava da li je string operacija
    public boolean isOp(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    //proverava da li je string zagrada
    public boolean isParentheses(String s) {
        return s.equals("(") || s.equals(")");
    }

    //formira string za ispis unetog izraza
    public void makeExp() {
        expString = "";
        for (String s : exp) {
            expString += s;
        }
    }

    //kad se klikne ponudjeni broj
    public boolean addNumber(String num) {
        if (started) {
            if (exp.isEmpty() || isOp(exp.get(exp.size() - 1)) || exp.get(exp.size() - 1).equals("(")) {
                exp.add(num);
                makeExp();
                _sol_user.setText(expString);
                return true;
            }
        }
        return false;
    }

    //kad se klikne operacija
    public void addOperation(String op) {
        if (started) {
            if (!(exp.isEmpty() || allNumbersUsed() || isOp(exp.get(exp.size() - 1)) || exp.get(exp.size() - 1).equals("("))) {
                exp.add(op);
                makeExp();
                _sol_user.setText(expString);
            }
        }
    }

    //kad se klikne otvorena zagrada
    public void addOpenPar() {
        if (started) {
            if (exp.isEmpty() || exp.get(exp.size() - 1).equals("(") || isOp(exp.get(exp.size() - 1))) {
                exp.add("(");
                makeExp();
                _sol_user.setText(expString);
            }
        }
    }

    //kad se klikne zatvorena zagrada
    public void addClosedPar() {
        if (started) {
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
                    _sol_user.setText(expString);
                }
            }
        }
    }

    //kad se klikne dugme za brisanje (kao backspace)
    public void erase() {
        if (!exp.isEmpty()) {
            String last = exp.get(exp.size() - 1);
            if (!(isOp(last) || isParentheses(last))) {
                disabledLabs.get(disabledLabs.size() - 1).setEnabled(true);
                disabledLabs.remove(disabledLabs.size() - 1);
            }
            exp.remove(exp.size() - 1);
            makeExp();
            _sol_user.setText(expString);

            _res_user.setText("");
        }
    }

    //proverava da li je uneti izraz validan za racunanje
    public boolean validExpression() {
        if (expString.isEmpty()) {
            return false;
        }
        char last = expString.charAt(expString.length() - 1);
        if (isOp(last + "") || last == '(') {
            return false;
        }
        int otv = 0;
        int zatv = 0;
        for (int i = 0; i < expString.length(); i++) {
            if (expString.charAt(i) == '(') {
                otv++;
            } else if (expString.charAt(i) == ')') {
                zatv++;
            }
        }
        return (otv == zatv);
    }

    //racunanje izraza koji korisnik unese
    public void calculate() {
        double result = PostfixCalculator.evaluateExpression(converter.convert(_sol_user.getText()));
        String resString;
        if (Math.floor(result) == Math.ceil(result)) {
            resString = Math.round(result) + "";
        } else {
            result = Math.round(result * 100);
            result /= 100.0;
            Math.round(result);
            resString = result + "";
        }
        _res_user.setText(resString);
    }

    //resenje kompjutera
    public void solve() {
        builder.reset(target, numbers);
        builder.findAll(_findAll.isSelected());  //da li nalazi sva resenja ili samo jedno
        long startTime = System.nanoTime();     //za racunanje za koliko je kompjuter nasao resenje
        builder.startBuild();
        long endTime = System.nanoTime();
        total = builder.getTotal();
        sol = builder.getSol();

        writeSol();
        _stats.setText(" Izracunato za " + (endTime - startTime) / 1000000 + "ms. Ukupno " + total + " izraza izracunato.");
        if (_findAll.isSelected()) {
            _stats.setText(_stats.getText() + " Ukupno " + sol.size() + " resenja.");
        }
    }

    //ispis (jednog ili svih) resenja u odgovarajuca polja
    public void writeSol() {
        if (_findAll.isSelected()) {
            for (Expression e : sol) {
                String newLine = Math.round(e.getValue()) + " = " + e.getExpression() + "\n";
                _sol_comp_area.setText(_sol_comp_area.getText() + newLine);
            }
        } else {
            String s = " " + sol.get(0).getExpression();
            String r = "" + Math.round(sol.get(0).getValue());
            _sol_comp.setText(s);
            _res_comp.setText(r);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _panel_form = new javax.swing.JPanel();
        _stop = new javax.swing.JButton();
        _panel_numbers = new javax.swing.JPanel();
        lab4 = new javax.swing.JLabel();
        lab5 = new javax.swing.JLabel();
        lab6 = new javax.swing.JLabel();
        lab7 = new javax.swing.JLabel();
        _panel_target = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        lab2 = new javax.swing.JLabel();
        lab3 = new javax.swing.JLabel();
        lab8 = new javax.swing.JLabel();
        lab9 = new javax.swing.JLabel();
        _restart = new javax.swing.JButton();
        _sol_user = new javax.swing.JTextField();
        _panel_ops = new javax.swing.JPanel();
        _op_plus = new javax.swing.JButton();
        _op_minus = new javax.swing.JButton();
        _op_puta = new javax.swing.JButton();
        _op_podeljeno = new javax.swing.JButton();
        _res_user = new javax.swing.JLabel();
        _panel_parentheses = new javax.swing.JPanel();
        _op_otv_zagrada = new javax.swing.JButton();
        _op_zatv_zagrada = new javax.swing.JButton();
        _panel_buttons = new javax.swing.JPanel();
        no = new javax.swing.JButton();
        yes = new javax.swing.JButton();
        computer = new javax.swing.JButton();
        _sol_comp = new javax.swing.JTextField();
        _sol_comp_area = new java.awt.TextArea();
        _auto = new javax.swing.JButton();
        _res_comp = new javax.swing.JLabel();
        _stats = new javax.swing.JLabel();
        _menu = new javax.swing.JMenuBar();
        _igra = new javax.swing.JMenu();
        _newGame = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        _exit = new javax.swing.JMenuItem();
        _opcije = new javax.swing.JMenu();
        _findAll = new javax.swing.JCheckBoxMenuItem();
        _showStats = new javax.swing.JCheckBoxMenuItem();
        _compCanCalc = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Moj broj");
        setBackground(new java.awt.Color(43, 155, 243));
        setForeground(new java.awt.Color(0, 153, 255));
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);

        _panel_form.setBackground(new java.awt.Color(51, 153, 255));
        _panel_form.setForeground(new java.awt.Color(255, 255, 255));

        _stop.setBackground(new java.awt.Color(51, 153, 255));
        _stop.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        _stop.setText("STOP");
        _stop.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _stopActionPerformed(evt);
            }
        });

        _panel_numbers.setBackground(new java.awt.Color(51, 153, 255));

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

        javax.swing.GroupLayout _panel_numbersLayout = new javax.swing.GroupLayout(_panel_numbers);
        _panel_numbers.setLayout(_panel_numbersLayout);
        _panel_numbersLayout.setHorizontalGroup(
            _panel_numbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_numbersLayout.createSequentialGroup()
                .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        _panel_numbersLayout.setVerticalGroup(
            _panel_numbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_numbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lab5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lab6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        _panel_target.setBackground(new java.awt.Color(51, 153, 255));

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

        javax.swing.GroupLayout _panel_targetLayout = new javax.swing.GroupLayout(_panel_target);
        _panel_target.setLayout(_panel_targetLayout);
        _panel_targetLayout.setHorizontalGroup(
            _panel_targetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_targetLayout.createSequentialGroup()
                .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        _panel_targetLayout.setVerticalGroup(
            _panel_targetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _panel_targetLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(_panel_targetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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

        _restart.setBackground(new java.awt.Color(51, 153, 255));
        _restart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/broj/gui/icons/restart.png"))); // NOI18N
        _restart.setText("");
        _restart.setToolTipText("");
        _restart.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _restart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _restartActionPerformed(evt);
            }
        });

        _sol_user.setEditable(false);
        _sol_user.setBackground(new java.awt.Color(51, 153, 255));
        _sol_user.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        _sol_user.setForeground(new java.awt.Color(255, 255, 255));
        _sol_user.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _sol_user.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        _panel_ops.setBackground(new java.awt.Color(51, 153, 255));

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

        _op_minus.setBackground(new java.awt.Color(51, 153, 255));
        _op_minus.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _op_minus.setForeground(new java.awt.Color(255, 255, 255));
        _op_minus.setText("-");
        _op_minus.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _op_minus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _op_minusActionPerformed(evt);
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

        javax.swing.GroupLayout _panel_opsLayout = new javax.swing.GroupLayout(_panel_ops);
        _panel_ops.setLayout(_panel_opsLayout);
        _panel_opsLayout.setHorizontalGroup(
            _panel_opsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_opsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(_op_plus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(_op_minus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(_op_puta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(_op_podeljeno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        _panel_opsLayout.setVerticalGroup(
            _panel_opsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_opsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(_op_plus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_minus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_puta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_podeljeno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        _res_user.setBackground(new java.awt.Color(255, 255, 255));
        _res_user.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        _res_user.setForeground(new java.awt.Color(255, 255, 255));
        _res_user.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        _res_user.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _res_user.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        _panel_parentheses.setBackground(new java.awt.Color(51, 153, 255));

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

        javax.swing.GroupLayout _panel_parenthesesLayout = new javax.swing.GroupLayout(_panel_parentheses);
        _panel_parentheses.setLayout(_panel_parenthesesLayout);
        _panel_parenthesesLayout.setHorizontalGroup(
            _panel_parenthesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_parenthesesLayout.createSequentialGroup()
                .addComponent(_op_otv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(_op_zatv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        _panel_parenthesesLayout.setVerticalGroup(
            _panel_parenthesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_parenthesesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(_op_otv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(_op_zatv_zagrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        _panel_buttons.setBackground(new java.awt.Color(51, 153, 255));

        no.setBackground(new java.awt.Color(51, 153, 255));
        no.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        no.setForeground(new java.awt.Color(255, 255, 255));
        no.setIcon(new javax.swing.ImageIcon(getClass().getResource("/broj/gui/icons/cross.png"))); // NOI18N
        no.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        no.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noActionPerformed(evt);
            }
        });

        yes.setBackground(new java.awt.Color(51, 153, 255));
        yes.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        yes.setForeground(new java.awt.Color(255, 255, 255));
        yes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/broj/gui/icons/tick.png"))); // NOI18N
        yes.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        yes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesActionPerformed(evt);
            }
        });

        computer.setBackground(new java.awt.Color(51, 153, 255));
        computer.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        computer.setForeground(new java.awt.Color(255, 255, 255));
        computer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/broj/gui/icons/computer.png"))); // NOI18N
        computer.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        computer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                computerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout _panel_buttonsLayout = new javax.swing.GroupLayout(_panel_buttons);
        _panel_buttons.setLayout(_panel_buttonsLayout);
        _panel_buttonsLayout.setHorizontalGroup(
            _panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_buttonsLayout.createSequentialGroup()
                .addComponent(no, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(yes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(computer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        _panel_buttonsLayout.setVerticalGroup(
            _panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(no, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(yes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(computer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        _sol_comp.setEditable(false);
        _sol_comp.setBackground(new java.awt.Color(51, 153, 255));
        _sol_comp.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        _sol_comp.setForeground(new java.awt.Color(255, 255, 255));
        _sol_comp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        _sol_comp_area.setBackground(new java.awt.Color(51, 153, 255));
        _sol_comp_area.setEditable(false);
        _sol_comp_area.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        _sol_comp_area.setForeground(new java.awt.Color(255, 255, 255));
        _sol_comp_area.setPreferredSize(new java.awt.Dimension(419, 100));
        _sol_comp_area.setVisible(false);

        _auto.setBackground(new java.awt.Color(51, 153, 255));
        _auto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/broj/gui/icons/auto.png"))); // NOI18N
        _auto.setToolTipText("");
        _auto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        _auto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _autoActionPerformed(evt);
            }
        });

        _res_comp.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        _res_comp.setForeground(new java.awt.Color(255, 255, 255));
        _res_comp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        _res_comp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        _stats.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        _stats.setForeground(new java.awt.Color(255, 255, 255));
        _stats.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        javax.swing.GroupLayout _panel_formLayout = new javax.swing.GroupLayout(_panel_form);
        _panel_form.setLayout(_panel_formLayout);
        _panel_formLayout.setHorizontalGroup(
            _panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _panel_formLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(_stats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(_sol_comp_area, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(_panel_formLayout.createSequentialGroup()
                        .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(_panel_formLayout.createSequentialGroup()
                                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(_panel_numbers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(_panel_ops, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(40, 40, 40)
                                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(_panel_parentheses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(_sol_user, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_sol_comp, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                        .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lab9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_res_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_panel_buttons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_res_comp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(47, 47, 47))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _panel_formLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _panel_formLayout.createSequentialGroup()
                        .addComponent(_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(320, 320, 320))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _panel_formLayout.createSequentialGroup()
                        .addComponent(_auto, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(_restart, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _panel_formLayout.createSequentialGroup()
                        .addComponent(_panel_target, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(265, 265, 265))))
        );
        _panel_formLayout.setVerticalGroup(
            _panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_panel_formLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(_restart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(_auto, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addComponent(_panel_target, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_panel_numbers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(_panel_formLayout.createSequentialGroup()
                        .addComponent(_panel_buttons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(_res_user, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(_panel_formLayout.createSequentialGroup()
                        .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_panel_ops, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_panel_parentheses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(_sol_user, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23)
                .addGroup(_panel_formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(_sol_comp, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .addComponent(_res_comp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1, 1, 1)
                .addComponent(_sol_comp_area, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(_stats, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(180, 180, 180))
        );

        _igra.setText("Igra");
        _igra.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        _newGame.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        _newGame.setText("Nova igra");
        _newGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _newGameActionPerformed(evt);
            }
        });
        _igra.add(_newGame);
        _igra.add(jSeparator1);

        _exit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        _exit.setText("Kraj");
        _exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _exitActionPerformed(evt);
            }
        });
        _igra.add(_exit);

        _menu.add(_igra);

        _opcije.setText("Opcije");
        _opcije.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        _findAll.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        _findAll.setSelected(true);
        _findAll.setText("Nadji sva resenja");
        _findAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _findAllActionPerformed(evt);
            }
        });
        _opcije.add(_findAll);

        _showStats.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        _showStats.setSelected(true);
        _showStats.setText("Prikazi vreme racunanja");
        _showStats.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                _showStatsStateChanged(evt);
            }
        });
        _opcije.add(_showStats);

        _compCanCalc.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        _compCanCalc.setSelected(true);
        _compCanCalc.setText("Omoguci racun kompjutera");
        _compCanCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _compCanCalcActionPerformed(evt);
            }
        });
        _opcije.add(_compCanCalc);

        _menu.add(_opcije);

        setJMenuBar(_menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(_panel_form, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(_panel_form, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        t.interrupt();
        restart();
    }//GEN-LAST:event__restartActionPerformed

    private void lab9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab9MouseClicked
        if (lab9.isEnabled()) {
            if (addNumber(lab9.getText())) {
                lab9.setEnabled(false);
                disabledLabs.add(lab9);
            }
        }
    }//GEN-LAST:event_lab9MouseClicked

    private void lab8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab8MouseClicked
        if (lab8.isEnabled()) {
            if (addNumber(lab8.getText())) {
                lab8.setEnabled(false);
                disabledLabs.add(lab8);
            }
        }
    }//GEN-LAST:event_lab8MouseClicked

    private void lab4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab4MouseClicked
        if (lab4.isEnabled()) {
            if (addNumber(lab4.getText())) {
                lab4.setEnabled(false);
                disabledLabs.add(lab4);
            }
        }
    }//GEN-LAST:event_lab4MouseClicked

    private void lab6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab6MouseClicked
        if (lab6.isEnabled()) {
            if (addNumber(lab6.getText())) {
                lab6.setEnabled(false);
                disabledLabs.add(lab6);
            }
        }
    }//GEN-LAST:event_lab6MouseClicked

    private void lab5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab5MouseClicked
        if (lab5.isEnabled()) {
            if (addNumber(lab5.getText())) {
                lab5.setEnabled(false);
                disabledLabs.add(lab5);
            }
        }
    }//GEN-LAST:event_lab5MouseClicked

    private void lab7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lab7MouseClicked
        if (lab7.isEnabled()) {
            if (addNumber(lab7.getText())) {
                lab7.setEnabled(false);
                disabledLabs.add(lab7);
            }
        }
    }//GEN-LAST:event_lab7MouseClicked

    private void _stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__stopActionPerformed
        set = true;
    }//GEN-LAST:event__stopActionPerformed

    private void _op_minusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_minusActionPerformed
        addOperation("-");
    }//GEN-LAST:event__op_minusActionPerformed

    private void _op_podeljenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_podeljenoActionPerformed
        addOperation("/");
    }//GEN-LAST:event__op_podeljenoActionPerformed

    private void _op_otv_zagradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_otv_zagradaActionPerformed
        addOpenPar();
    }//GEN-LAST:event__op_otv_zagradaActionPerformed

    private void _op_zatv_zagradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__op_zatv_zagradaActionPerformed
        addClosedPar();
    }//GEN-LAST:event__op_zatv_zagradaActionPerformed

    private void yesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesActionPerformed
        if (validExpression()) {
            calculate();
            comp = true;
            //yes.setEnabled(false);
            //no.setEnabled(false);
        }
    }//GEN-LAST:event_yesActionPerformed

    private void computerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_computerActionPerformed
        if (comp) {
            if (started) {
                _sol_comp_area.setText("");
                solve();
                computer.setEnabled(false);
            }
        }
    }//GEN-LAST:event_computerActionPerformed

    private void noActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noActionPerformed
        erase();
    }//GEN-LAST:event_noActionPerformed

    private void _autoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__autoActionPerformed
        stop_all = true;
        set = true;
    }//GEN-LAST:event__autoActionPerformed

    private void _showStatsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event__showStatsStateChanged
        if (_showStats.isSelected()) {
            _stats.setVisible(true);
        } else {
            _stats.setVisible(false);
        }
    }//GEN-LAST:event__showStatsStateChanged

    private void _findAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__findAllActionPerformed
        if (_findAll.isSelected()) {
            _panel_form.setVisible(false);      //ZASTO OVO RESAVA BUG!?!?!?    repaint()??

            _sol_comp_area.setVisible(true);
            _sol_comp.setVisible(false);
            _res_comp.setVisible(false);

            computer.setEnabled(true);

            _sol_comp_area.setText("");
            _stats.setText("");
            _sol_comp.setText("");
            _res_comp.setText("");

            _panel_form.setVisible(true);
        } else {
            _panel_form.setVisible(false);

            _sol_comp_area.setVisible(false);
            _sol_comp.setVisible(true);
            _res_comp.setVisible(true);

            computer.setEnabled(true);

            _stats.setText("");
            _sol_comp.setText("");
            _res_comp.setText("");

            _panel_form.setVisible(true);
        }
    }//GEN-LAST:event__findAllActionPerformed

    private void _newGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__newGameActionPerformed
        thread_run = false;
        t.interrupt();
        restart();
    }//GEN-LAST:event__newGameActionPerformed

    private void _exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__exitActionPerformed
        System.exit(0);
    }//GEN-LAST:event__exitActionPerformed

    private void _compCanCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__compCanCalcActionPerformed
        if (_compCanCalc.isSelected()) {
            comp = true;
        } else {
            comp = false;
        }
    }//GEN-LAST:event__compCanCalcActionPerformed

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
    private javax.swing.JButton _auto;
    private javax.swing.JCheckBoxMenuItem _compCanCalc;
    private javax.swing.JMenuItem _exit;
    private javax.swing.JCheckBoxMenuItem _findAll;
    private javax.swing.JMenu _igra;
    private javax.swing.JMenuBar _menu;
    private javax.swing.JMenuItem _newGame;
    private javax.swing.JButton _op_minus;
    private javax.swing.JButton _op_otv_zagrada;
    private javax.swing.JButton _op_plus;
    private javax.swing.JButton _op_podeljeno;
    private javax.swing.JButton _op_puta;
    private javax.swing.JButton _op_zatv_zagrada;
    private javax.swing.JMenu _opcije;
    private javax.swing.JPanel _panel_buttons;
    private javax.swing.JPanel _panel_form;
    private javax.swing.JPanel _panel_numbers;
    private javax.swing.JPanel _panel_ops;
    private javax.swing.JPanel _panel_parentheses;
    private javax.swing.JPanel _panel_target;
    private javax.swing.JLabel _res_comp;
    private javax.swing.JLabel _res_user;
    private javax.swing.JButton _restart;
    private javax.swing.JCheckBoxMenuItem _showStats;
    private javax.swing.JTextField _sol_comp;
    private java.awt.TextArea _sol_comp_area;
    private javax.swing.JTextField _sol_user;
    private javax.swing.JLabel _stats;
    private javax.swing.JButton _stop;
    private javax.swing.JButton computer;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab3;
    private javax.swing.JLabel lab4;
    private javax.swing.JLabel lab5;
    private javax.swing.JLabel lab6;
    private javax.swing.JLabel lab7;
    private javax.swing.JLabel lab8;
    private javax.swing.JLabel lab9;
    private javax.swing.JButton no;
    private javax.swing.JButton yes;
    // End of variables declaration//GEN-END:variables
}
