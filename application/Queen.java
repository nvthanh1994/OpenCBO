import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Queen {
    int n;
    VarIntLS[] x;
    LocalSearchManager ls = new LocalSearchManager();
    ConstraintSystem S = new ConstraintSystem(ls);

    public Queen(int n, long limitStep) {
        // Modelling
        this.n = n;
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(ls, 0, n - 1);
        }
        S.post(new AllDifferent(x));

        IFunction[] f1 = new IFunction[n];
        for (int i = 0; i < n; i++) {
            f1[i] = new FuncPlus(x[i], i);
        }
        S.post(new AllDifferent(f1));

        IFunction[] f2 = new IFunction[n];
        for (int i = 0; i < n; i++) {
            f2[i] = new FuncPlus(x[i], -i);
        }
        S.post(new AllDifferent(f2));
        ls.close();

        // Searching
        MinMaxSelector mms = new MinMaxSelector(S);
        int it = 0;
        while (it < limitStep && S.violations() > 0) {
            VarIntLS sel_x = mms.selectMostViolatedVariable();
            int sel_v = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_v);
            it++;
            System.out.println("Step " + it + " " + S.violations());
        }
        if (S.violations() == 0) {
            printSolution();
            if(n < 30) prettyOut();
        }
    }

    void printSolution() {
        for (int i = 0; i < n; i++) {
            System.out.printf("%d ", x[i].getValue());
        }
    }

    public void prettyOut() {
        int size = (Toolkit.getDefaultToolkit().getScreenSize().height - 250) / (n + 1);
        try {
            File outFile = new File("out.html");
            PrintWriter out;
            out = new PrintWriter(outFile);
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<body>");
            out.println("<table border=\"1\" style=\"background-color:white\">");
            for (int i = 0; i < n; i++) {
                out.println("<tr>");
                for (int j = 0; j < n; j++) {
                    if (x[i].getValue() == j) {
                        out.println("<td height=\"" + size + "\" width=\"" + size + "\" style=\"background-color:green\"> </td>");
                    } else {
                        out.println("<td height=\"" + size + "\" width=\"" + size + "\"><br></td>");
                    }
                }
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</html>");
            out.println("</body>");
            out.close();
            out.println("Hello HTML");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Queen(500, 10000);
    }


}
