package chapter6.src;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBasedRecommenderEvaluation {

	private static void performEvaluationScoreDiff(
			RecommenderEvaluator evaluator, DataModel model,
			final UserNeighborhood neighborhood, final UserSimilarity similarity)
			throws TasteException {
		// Build the same recommender for testing that we did last time:
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				return new GenericUserBasedRecommender(model, neighborhood,
						similarity);
			}
		};
		// Use 70% of the data to train; test using the other 30%.
		double score = evaluator.evaluate(recommenderBuilder, null, model, 0.7,
				1.0);
		System.out.println("The evaluation score is " + score);
	}

	private static void performEvaluationPrecRecall(
			RecommenderIRStatsEvaluator evaluator, DataModel model,
			final UserNeighborhood neighborhood, final UserSimilarity similarity)
			throws TasteException {
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				return new GenericUserBasedRecommender(model, neighborhood,
						similarity);
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

		UserSimilarity pearsonSimilarity = new PearsonCorrelationSimilarity(
				model);
		UserSimilarity euclideanSimilarity = new EuclideanDistanceSimilarity(
				model);
		UserSimilarity tanimotoSimilarity = new TanimotoCoefficientSimilarity(
				model);
		UserSimilarity logLikilihoodSimilarity = new LogLikelihoodSimilarity(
				model);

		UserNeighborhood pearsonNeighborhood = new NearestNUserNeighborhood(
				1000, pearsonSimilarity, model);
		UserNeighborhood euclideanNeighborhood = new NearestNUserNeighborhood(
				1000, euclideanSimilarity, model);
		UserNeighborhood tanimotoNeighborhood = new NearestNUserNeighborhood(
				1000, tanimotoSimilarity, model);
		UserNeighborhood logLikilihoodNeighborhood = new NearestNUserNeighborhood(
				1000, logLikilihoodSimilarity, model);

		UserNeighborhood pearsonThresNeighborhood = new ThresholdUserNeighborhood(
				0.1, pearsonSimilarity, model);
		UserNeighborhood euclideanThresNeighborhood = new ThresholdUserNeighborhood(
				0.1, euclideanSimilarity, model);
		UserNeighborhood tanimotoThresNeighborhood = new ThresholdUserNeighborhood(
				0.1, tanimotoSimilarity, model);
		UserNeighborhood logLikilihoodThresNeighborhood = new ThresholdUserNeighborhood(
				0.1, logLikilihoodSimilarity, model);

		performEvaluationPrecRecall(precRecevaluator, model,
				pearsonNeighborhood, pearsonSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				euclideanNeighborhood, euclideanSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				tanimotoNeighborhood, tanimotoSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				logLikilihoodNeighborhood, logLikilihoodSimilarity);

		performEvaluationPrecRecall(precRecevaluator, model,
				pearsonThresNeighborhood, pearsonSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				euclideanThresNeighborhood, euclideanSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				tanimotoThresNeighborhood, tanimotoSimilarity);
		performEvaluationPrecRecall(precRecevaluator, model,
				logLikilihoodThresNeighborhood, logLikilihoodSimilarity);

		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				pearsonNeighborhood, pearsonSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				euclideanNeighborhood, euclideanSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				tanimotoNeighborhood, tanimotoSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				logLikilihoodNeighborhood, logLikilihoodSimilarity);

		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				pearsonThresNeighborhood, pearsonSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				euclideanThresNeighborhood, euclideanSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				tanimotoThresNeighborhood, tanimotoSimilarity);
		performEvaluationScoreDiff(scoreBasedEvaluator, model,
				logLikilihoodThresNeighborhood, logLikilihoodSimilarity);

	}

}
