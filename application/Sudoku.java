import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

/**
 * Created by Prime on 23/01/2015.
 */
public class Sudoku {
    int n=9;
    VarIntLS x[];
    LocalSearchManager ls=new LocalSearchManager();
    ConstraintSystem S= new ConstraintSystem(ls);

    public Sudoku(int n, int limitStep){
        this.n=n;
        x= new VarIntLS[n*n];
        for(int i=0;i<n*n;i++){
            x[i]=new VarIntLS(ls,0,n-1);
        }

        // Row
        IFunction[][] f1 = new IFunction[n][n];
        int k=0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++,k++){
                f1[i][j] = new FuncPlus(x[k], 0);
            }
            S.post(new AllDifferent(f1[i]));
        }

        // Col
        IFunction[][] f2=new IFunction[n][n];
        for(int j=0;j<n;j++){
            for(int i=0;i<n;i++){
                f2[j][i]=new FuncPlus(x[i*n+j],0);
            }
            S.post(new AllDifferent(f2[j]));
        }


        // Block different

        IFunction[][] f3 = new IFunction[n][n];
        for(int i=0;i<=2;i++){
            for(int j=0;j<=2;j++){
                int k1=0;
                for(int i1=0;i1<=2;i1++){
                    for(int j1=0;j1<=2;j1++){
                        f3[i*3+j][k1]=new FuncPlus(x[(i*3+i1)*n+(j*3+j1)],0);
                        k1++;
                        System.out.println(k1);
                    }
                }
                S.post(new AllDifferent(f3[i*3+j]));
            }
        }

        ls.close();
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
        }
    }

    void printSolution() {
        for (int i = 0; i < n*n; i++){
            System.out.printf("%d ", x[i].getValue()+1);
            if(i%n==n-1) System.out.println();
        }
    }

    public static void main(String[] args) {
        new Sudoku(9,10000);
    }

}
