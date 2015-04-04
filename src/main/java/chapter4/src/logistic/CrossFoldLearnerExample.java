package chapter4.src.logistic;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import org.apache.mahout.classifier.evaluation.Auc;
import org.apache.mahout.classifier.sgd.CrossFoldLearner;
import org.apache.mahout.classifier.sgd.CsvRecordFactory;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.LogisticModelParameters;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.RecordFactory;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public final class CrossFoldLearnerExample {

	static OnlineLogisticRegression lr = new OnlineLogisticRegression();

	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"data/chapter4/cancer.csv"));
		String line = br.readLine();
		int cnt_line = 0;
		CrossFoldLearner clf = new CrossFoldLearner(5, 2, 10, new L1()).lambda(
				1 * 1.0e-3).learningRate(50);

		while (line != null) {
			if (cnt_line > 0) {
				String[] values = line.split(",");
				double[] vecValues = new double[values.length];

				for (int i = 0; i < values.length - 2; i++) {
					vecValues[i] = Double.parseDouble(values[i]);
				}
				int target = Integer.parseInt(values[values.length - 1]);
				Vector v = new SequentialAccessSparseVector(values.length);
				v.assign(vecValues);
				clf.train(target, v);

			}
			line = br.readLine();
			cnt_line++;

		}
		System.out.println("Auc of cross fold learner is "+ clf.auc());
		br.close();
		int model_number=1;
		for (OnlineLogisticRegression model : clf.getModels()) {

			lr = model;
			br = new BufferedReader(new FileReader("data/chapter4/cancer.csv"));
			String pred_line = br.readLine();
			int cnt_pred_line = 0;
			Auc collector = new Auc();
			while (pred_line != null) {
				if (cnt_pred_line > 0) {
					String[] values = pred_line.split(",");
					double[] vecValues = new double[values.length];

					for (int i = 0; i < values.length - 2; i++) {
						vecValues[i] = Double.parseDouble(values[i]);
					}
					int target = Integer.parseInt(values[values.length - 1]);
					Vector v = new SequentialAccessSparseVector(values.length);
					v.assign(vecValues);
					double score = lr.classifyScalar(v);
					collector.add(target, score);
				}
				pred_line = br.readLine();
				cnt_pred_line++;

			}
			br.close();
			System.out.println("Auc of model " +model_number+ " = "+ collector.auc());
			Matrix m = collector.confusion();
			System.out.println("The confusion matrix is" +m);
			model_number++;
		}
	}

}
