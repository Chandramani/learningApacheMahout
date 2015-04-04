package chapter6.src;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

import java.io.*;
import java.util.*;

public class UserBasedRecommender {

	private UserBasedRecommender() {
	}

	private static void performRecommendation(DataModel model,
			UserNeighborhood neighbour, UserSimilarity similarity, String Type)
			throws TasteException {
		Recommender recommender = new GenericUserBasedRecommender(model,
				neighbour, similarity);

		long userId = 1;
		int numberOfRecommendation = 2;
		List<RecommendedItem> recommendations = recommender.recommend(userId,
				numberOfRecommendation);

		for (RecommendedItem recommendation : recommendations) {
			System.out.println("The two recommended item using similarity "
					+ Type + "for user " + userId + " is " + recommendation);
		}

		int userID = 1;
		long itemID = 1106;

		System.out.println("The estimated prefrence using similarity " + Type
				+ "for user " + userId + " is "
				+ recommender.estimatePreference(userID, itemID));
	}

	public static void main(String[] args) throws Exception {
		File trainingFile = null;
		if (args.length > 0)
			trainingFile = new File(args[0]);
		if (!trainingFile.exists()) {
			System.out.println("Please, pass the input file with ratings");
			System.exit(1);
		}
		DataModel model = new FileDataModel(trainingFile);

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

		performRecommendation(model, pearsonNeighborhood, pearsonSimilarity,
				"pearson ");
		performRecommendation(model, euclideanNeighborhood,
				euclideanSimilarity, "euclidean ");
		performRecommendation(model, tanimotoNeighborhood, tanimotoSimilarity,
				"tanimoto ");
		performRecommendation(model, logLikilihoodNeighborhood,
				logLikilihoodSimilarity, "log-likelihood ");

		performRecommendation(model, pearsonThresNeighborhood,
				pearsonSimilarity, "pearson ");
		performRecommendation(model, euclideanThresNeighborhood,
				euclideanSimilarity, "euclidean ");
		performRecommendation(model, tanimotoThresNeighborhood,
				tanimotoSimilarity, "tanimoto ");
		performRecommendation(model, logLikilihoodThresNeighborhood,
				logLikilihoodSimilarity, "log-likelihood ");

	}

}
