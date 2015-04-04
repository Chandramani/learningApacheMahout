package chapter4.src.logistic;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class OnlineLogisticRegressionPredict {

	  private static String inputFile="data/chapter4/input_bank_data_without_target.csv";
	  private static String modelFile="data/chapter4/model";
	  private static boolean showScores;
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

	        LogisticModelParametersPredict lmp = LogisticModelParametersPredict.loadFrom(new File(modelFile));
	        CsvRecordFactoryPredict csv = lmp.getCsvRecordFactory();
	        OnlineLogisticRegression lr = lmp.createRegression();
	        BufferedReader in = OnlineLogisticRegressionPredict.open(inputFile);
	        String line = in.readLine();
	        csv.firstLine(line,"y");
	        line = in.readLine();
	        PrintWriter output=new PrintWriter(new OutputStreamWriter(System.out, Charsets.UTF_8), true);
	        //output.println("\"target\",\"model-output\",\"log-likelihood\"");
	        while (line != null) {
	            Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
	            int target = csv.processLine(line, v,false);
	            double score = lr.classifyScalar(v);
	            if(score >0.0)
	            	System.out.println("predicted score: " +score);
	            //
	            line = in.readLine();
	          }
	        
		  }

}
