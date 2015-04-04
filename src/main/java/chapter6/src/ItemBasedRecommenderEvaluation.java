package chapter6.src;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBasedRecommenderEvaluation {

	private static void performEvaluationScoreDiff(
			RecommenderEvaluator evaluator, DataModel model,
			final ItemSimilarity itemSimilarity) throws TasteException {
		// Build the same recommender for testing that we did last time:
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				return new GenericItemBasedRecommender(model, itemSimilarity);
			}
		};
		// Use 70% of the data to train; test using the other 30%.
		double score = evaluator.evaluate(recommenderBuilder, null, model, 0.7,
				1.0);
		System.out.println("The evaluation score is " + score);
	}

	private static void performEvaluationPrecRecall(
			RecommenderIRStatsEvaluator evaluator, DataModel model,
			final ItemSimilarity itemSimilarity) throws TasteException {
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				return new GenericItemBasedRecommender(model, itemSimilarity);
			}
		};
		IRStatistics stats = evaluator.evaluate(recommenderBuilder, null,
				model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		System.out.println("The precision is " + stats.getPrecision());
		System.out.println("The recall is " + stats.getRecall());
	}

	public static void main(String args[]) throws IOException, TasteException {
		File trainingFile = null;
		if (args.length > 0)
			trainingFile = new File(args[0]);
		if (!trainingFile.exists()) {
			System.out.println("Please, pass the input file with ratings");
			System.exit(1);
		}
		DataModel model = new FileDataModel(trainingFile);
		RecommenderEvaluator scoreBasedEvaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderIRStatsEvaluator precRecevaluator = new GenericRecommenderIRStatsEvaluator();
		ItemSimilarity pearsonSimilarity = new PearsonCorrelationSimilarity(
				model);
		ItemSimilarity euclideanSimilarity = new EuclideanDistanceSimilarity(
				model);
		ItemSimilarity tanimotoSimilarity = new TanimotoCoefficientSimilarity(
				model);
		ItemSimilarity logLikilihoodSimilarity = new LogLikelihoodSimilarity(
				model);

		performEvaluationPrecRecall(precRecevaluator, model, pearsonSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				euclideanSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model, tanimotoSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				logLikilihoodSimilarity);

		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				pearsonSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				euclideanSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				tanimotoSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				logLikilihoodSimilarity);

	}
}
