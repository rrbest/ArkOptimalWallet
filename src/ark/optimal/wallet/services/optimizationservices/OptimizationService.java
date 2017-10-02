/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.optimizationservices;

import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.ui.FXMLDelegatesViewController;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.LPOptimizationRequest;
import com.joptimizer.optimizers.LPPrimalDualMethod;
import com.joptimizer.optimizers.OptimizationRequest;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mastadon
 */
public class OptimizationService {
    
    public static Map<String, Double> runConvexOptimizattion(double totalVotes, List<Delegate> delegates) {

        try {
            final DoubleFactory1D F1 = DoubleFactory1D.dense;
            final DoubleFactory2D F2 = DoubleFactory2D.dense;

            ConvexMultivariateRealFunction objectiveFunction = new ConvexMultivariateRealFunction() {
                @Override
                public double value(DoubleMatrix1D dmd) {
                    double obj = 0.0;
                    for (int i = 0; i < delegates.size(); i++) {
                        double x = dmd.getQuick(i);
                        double v = delegates.get(i).getVote() - delegates.get(i).getExcludedVotes();
                        double p = delegates.get(i).getPayoutPercentage();
                        //double h = Math.pow(p * v,2) / (v + x);
                        double h = p * v / (v + x);
                        obj += h;
                    }
                    return obj;

                }

                @Override
                public DoubleMatrix1D gradient(DoubleMatrix1D dmd) {
                    double[] ret = new double[delegates.size()];
                    for (int i = 0; i < delegates.size(); i++) {
                        double x = dmd.getQuick(i);
                        double v = delegates.get(i).getVote() - delegates.get(i).getExcludedVotes();
                        double p = delegates.get(i).getPayoutPercentage();
                        //double h = (p * x) / (v + x);
                        double h = -(p * v) / Math.pow(v+x, 2);
                        ret[i] = h;
                    }
                    return F1.make(ret);
                }

                @Override
                public DoubleMatrix2D hessian(DoubleMatrix1D dmd) {
                    double[][] ret = new double[delegates.size()][delegates.size()];
                    for (int i = 0; i < delegates.size(); i++) {
                        Arrays.fill(ret[i], 0);
                        double x = dmd.getQuick(i);
                        double v = delegates.get(i).getVote() - delegates.get(i).getExcludedVotes();
                        double p = delegates.get(i).getPayoutPercentage();
                        //double h = (p * x) / (v + x);
                        double h = 2*p* v / Math.pow(v+x, 3);
                        ret[i][i] = h;
                    }
                    DoubleMatrix2D retm = F2.make(ret);
                    cern.colt.matrix.DoubleMatrix2D dt = cern.colt.matrix.DoubleFactory2D.dense.make(ret);
                    EigenvalueDecomposition ed = new EigenvalueDecomposition(dt);
                    System.out.println(ed.getD());
                    
                    return retm;
                }
                
                @Override
                public int getDim() {
                    return delegates.size();
                }

            };
            double[][] A = new double[1][delegates.size()];
            Arrays.fill(A[0], 1);
            /*for (int i = 1; i <= delegates.size(); i++) {
                Arrays.fill(A[i], 0);
                A[i][i-1] = 1;
            }*/
            //Bounds on variables
            double[] b = new double[1];
            //Arrays.fill(b, 0);
            b[0] = totalVotes;
            double[] lb = new double[delegates.size()];
            Arrays.fill(lb, 0);
            ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[delegates.size()];
            for (int i =0 ; i<delegates.size(); i++){
                double [] g = new double[delegates.size()];
                Arrays.fill(g, 0);
                g[i] = -1;
                inequalities[i] = new LinearMultivariateRealFunction(g, 0);
            }
            double[] init = new double[delegates.size()];
            Arrays.fill(init, totalVotes / delegates.size());
            //optimization problem
            OptimizationRequest or = new OptimizationRequest();
            or.setF0(objectiveFunction);
            or.setFi(inequalities);
            or.setB(b);
            or.setA(A);
            or.setTolerance(1.E-6);
            or.setToleranceFeas(1.E-6);
            or.setInitialPoint(init);
            or.setCheckKKTSolutionAccuracy(true);
            //optimization
            JOptimizer opt = new JOptimizer();

            opt.setOptimizationRequest(or);
            opt.optimize();
            double[] sol = opt.getOptimizationResponse().getSolution();

            Map<String, Double> map = new HashMap<String, Double>();
            for (int i = 0; i < delegates.size(); i++) {
                map.put(delegates.get(i).getUsername(), sol[i]);
            }
            return map;

        } catch (JOptimizerException ex) {

            Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Map<String, Double> createLP(double totalVotes, List<Delegate> delegates) {

        try {
            double[] c = new double[delegates.size()];
            for (int i = 0; i < delegates.size(); i++) {
                Delegate d = delegates.get(i);
                c[i] = -1.0 * d.getPayoutPercentage() / d.getVote();
            }

            //Inequalities constraints
            double[][] A = new double[1][delegates.size()];
            Arrays.fill(A[0], 1);
            //Bounds on variables
            double[] b = new double[1];
            b[0] = totalVotes;
            double[] lb = new double[delegates.size()];
            Arrays.fill(lb, 0);

            //optimization problem
            LPOptimizationRequest or = new LPOptimizationRequest();
            or.setC(c);
            //or.setG(G);
            //or.setH(h);
            or.setB(b);
            or.setA(A);
            or.setLb(lb);
            or.setDumpProblem(true);

            //optimization
            LPPrimalDualMethod opt = new LPPrimalDualMethod();

            opt.setLPOptimizationRequest(or);
            opt.optimize();
            double[] sol = opt.getOptimizationResponse().getSolution();

            Map<String, Double> map = new HashMap<String, Double>();
            for (int i = 0; i < delegates.size(); i++) {
                map.put(delegates.get(i).getUsername(), sol[i]);
            }
            return map;

        } catch (JOptimizerException ex) {
            Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Map<String, Double> runNumericalOptimizattion(double totalVotes, List<Delegate> delegates) {

        try {

            double rhobeg = 1;
            double rhoend = 1.0e-4;
            int iprint = 1;
            int maxfun = 100000000;
            Calcfc calcfc = new Calcfc() {
                @Override
                public double compute(int n, int m, double[] x, double[] con) {
                    double sum_x = 0.0;
                    double obj = 0.0;
                    for (int i = 0; i < n; i++) {
                        sum_x += x[i];
                        double v = delegates.get(i).getVote();
                        double p = delegates.get(i).getPayoutPercentage()/100.0;
                        obj -= ((p * x[i]) / (v + x[i]));
                    }

                    con[0] = totalVotes - sum_x;
                    for (int i = 1; i <= n; i++) {
                        con[i] = x[i - 1];
                    }

                    return obj;
                }
            };
            double[] x = new double[delegates.size()];
            Arrays.fill(x, totalVotes / delegates.size());
            CobylaExitStatus result = Cobyla.findMinimum(calcfc, delegates.size(), delegates.size() + 1, x, rhobeg, rhoend, iprint, maxfun);

            Map<String, Double> map = new HashMap<String, Double>();
            double objvalue = 0;
            for (int i = 0; i < delegates.size(); i++) {
                double v = delegates.get(i).getVote();
                double p = delegates.get(i).getPayoutPercentage()/100.0;
                objvalue += ((p * x[i]) / (v + x[i]));
                map.put(delegates.get(i).getUsername(), x[i]);
                System.out.println("x " + delegates.get(i). getUsername() + " :" + x[i]);
            }
            System.out.println("obj = "+objvalue);
            return map;

        } catch (Exception ex) {
            Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
}
