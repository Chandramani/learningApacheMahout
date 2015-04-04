package chapter4.src.logistic;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.classifier.evaluation.Auc;
import org.apache.mahout.classifier.sgd.CsvRecordFactory;
import org.apache.mahout.classifier.sgd.LogisticModelParameters;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

public class OnlineLogisticRegressionTest {

	  private static String inputFile="data/chapter4/test_data/input_bank_data.csv";
	  private static String modelFile="data/chapter4/model";
	  private static boolean showAuc;
	  private static boolean showScores;
	  private static boolean showConfusion;
	  static BufferedReader open(String inputFile) throws IOException {
		    InputStream in;
		    try {
		      in = Resources.getResource(inputFile).openStream();
		    } catch (IllegalArgumentException e) {
		      in = new FileInputStream(new File(inputFile));
		    }
		    return new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
		  }
	  
	  public static void main(String[] args) throws Exception {
		   showAuc = true;
	        showConfusion = true;
	        Auc collector = new Auc();
	        LogisticModelParameters lmp = LogisticModelParameters.loadFrom(new File(modelFile));
	        CsvRecordFactory csv = lmp.getCsvRecordFactory();
	        OnlineLogisticRegression lr = lmp.createRegression();
	        BufferedReader in = OnlineLogisticRegressionTest.open(inputFile);
	        String line = in.readLine();
	        csv.firstLine(line);
	        line = in.readLine();
	        PrintWriter output=new PrintWriter(new OutputStreamWriter(System.out, Charsets.UTF_8), true);
	        output.println("\"target\",\"model-output\",\"log-likelihood\"");
	        while (line != null) {
	            Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
	            int target = csv.processLine(line, v);
	            double score = lr.classifyScalar(v);
	            output.printf(Locale.ENGLISH, "%d,%.3f,%.6f%n", target, score, lr.logLikelihood(target, v));
	            collector.add(target, score);
	            line = in.readLine();
	          }
	        output.printf(Locale.ENGLISH, "AUC = %.2f%n", collector.auc());
	        Matrix m = collector.confusion();
	        output.printf(Locale.ENGLISH, "confusion: [[%.1f, %.1f], [%.1f, %.1f]]%n",
	          m.get(0, 0), m.get(1, 0), m.get(0, 1), m.get(1, 1));
	        m = collector.entropy();
	        output.printf(Locale.ENGLISH, "entropy: [[%.1f, %.1f], [%.1f, %.1f]]%n",
	          m.get(0, 0), m.get(1, 0), m.get(0, 1), m.get(1, 1));
		  }

}
