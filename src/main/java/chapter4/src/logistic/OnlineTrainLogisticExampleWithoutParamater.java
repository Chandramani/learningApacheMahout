package chapter4.src.logistic;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.mahout.classifier.sgd.CsvRecordFactory;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.RecordFactory;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;


public class OnlineTrainLogisticExampleWithoutParamater {
	  private static double predictorWeight(OnlineLogisticRegression lr, int row, RecordFactory csv, String predictor) {
		    double weight = 0;
		    for (Integer column : csv.getTraceDictionary().get(predictor)) {
		      weight += lr.getBeta().get(row, column);
		    }
		    return weight;
		  }
	  
static Map<String, String> typeMap;

static List<String> targetCategories=new ArrayList<String>(Arrays.asList("Yes", "No"));
static OnlineLogisticRegression lr;

public static void write(DataOutput out) throws IOException {
    out.writeUTF("y");
    out.writeInt(typeMap.size());
    for (Map.Entry<String,String> entry : typeMap.entrySet()) {
      out.writeUTF(entry.getKey());
      out.writeUTF(entry.getValue());
    }
    out.writeInt(20);
    out.writeBoolean(false);
    out.writeInt(2);

    if (targetCategories == null) {
      out.writeInt(0);
    } else {
      out.writeInt(targetCategories.size());
      for (String category : targetCategories) {
        out.writeUTF(category);
      }
    }
    out.writeDouble(0);
    out.writeDouble(50);
    // skip csv
    lr.write(out);
  }

public static Map<String, String> setTypeMap(Iterable<String> predictorList, List<String> typeList) {
		    Preconditions.checkArgument(!typeList.isEmpty(), "Must have at least one type specifier");
		    typeMap = Maps.newHashMap();
		    Iterator<String> iTypes = typeList.iterator();
		    String lastType = null;
		    for (Object x : predictorList) {
		      // type list can be short .. we just repeat last spec
		      if (iTypes.hasNext()) {
		        lastType = iTypes.next();
		      }
		      typeMap.put(x.toString(), lastType);
		    }
		    return typeMap;
		  }
	  
    public static void main(String[] args) throws IOException 
    {
    	String inputFile = "data/chapter4/train_data/input_bank_data.csv";
    	String outputFile = "data/chapter4/model";
        
        System.out.println("Demonstartion without using the Example class");
        
        List<String> predictorList =Arrays.asList("age","job","marital","education","default",
        		"housing","loan","contact","month","day_of_week","duration","campaign","pdays","previous","poutcome",
        		"emp.var.rate","cons.price.idx","cons.conf.idx","euribor3m","nr.employed");
        List<String> typeList = Arrays.asList("n", "w", "w", "w", "w", "w", "w", "w", "w", "w", "n", "n", "n", "n",
        		"w", "n", "n", "n", "n", "n");
        
/*        LogisticModelParameters lmp = new LogisticModelParameters();
        lmp.setTargetVariable("y");
        lmp.setMaxTargetCategories(2);
        lmp.setNumFeatures(20);
        lmp.setUseBias(false);
        lmp.setTypeMap(predictorList,typeList);
        lmp.setLearningRate(50.0);*/
        
        
        int passes = 50;
        boolean showperf;
        int skipperfnum = 99;
        
        

        CsvRecordFactory csv = new CsvRecordFactory("y",setTypeMap(predictorList,typeList)).maxTargetValue(2).includeBiasTerm(false);
        //lr = lmp.createRegression();
        
        lr =new OnlineLogisticRegression(2,20,new L1()).lambda(0).learningRate(0.5).alpha(1 - 1.0e-3); 

        
        int k = 0;
        
        for (int pass = 0; pass < passes; pass++) {
                BufferedReader in = new BufferedReader(new FileReader(inputFile));

                
                csv.firstLine(in.readLine());

                String line = in.readLine();
                int lineCount = 2;
                while (line != null) {
                  Vector input = new RandomAccessSparseVector(20);
                  int targetValue = csv.processLine(line, input);

                  // update model
                  lr.train(targetValue, input);
                  k++;

                  line = in.readLine();
                  lineCount++;
                }
                in.close();
              }

/*            best = model.getBest();
            if (best != null) {
              learner = best.getPayload().getLearner();
            }*/


            OutputStream modelOutput = new FileOutputStream(outputFile);
            try {
            	write(new DataOutputStream(modelOutput));
            } finally {
              modelOutput.close();
            }
            PrintWriter output=new PrintWriter(new OutputStreamWriter(System.out, Charsets.UTF_8), true);
            output.println(20);
            output.println("y"+ " ~ ");
            String sep = "";
            for (String v : csv.getTraceDictionary().keySet()) {
              double weight = predictorWeight(lr, 0, csv, v);
              if (weight != 0) {
                output.printf(Locale.ENGLISH, "%s%.3f*%s", sep, weight, v);
                sep = " + ";
              }
            }
            output.printf("%n");
            //model = lr;
            for (int row = 0; row < lr.getBeta().numRows(); row++) {
              for (String key : csv.getTraceDictionary().keySet()) {
                double weight = predictorWeight(lr, row, csv, key);
                if (weight != 0) {
                  output.printf(Locale.ENGLISH, "%20s %.5f%n", key, weight);
                }
              }
              for (int column = 0; column < lr.getBeta().numCols(); column++) {
                output.printf(Locale.ENGLISH, "%15.9f ", lr.getBeta().get(row, column));
              }
              output.println();
            }
    }
}
