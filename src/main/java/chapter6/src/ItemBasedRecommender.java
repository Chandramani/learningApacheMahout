package chapter6.src;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

import java.io.*;
import java.util.*;

public class ItemBasedRecommender {

	private ItemBasedRecommender() {
	}

	private static void performItemRecommendation(DataModel model,
			ItemSimilarity itemSimilarity, String Type) throws TasteException {
		long userId = 1;
		int numberOfRecommendation = 2;

		Recommender itemRecommender = new GenericItemBasedRecommender(model,
				itemSimilarity);

		List<RecommendedItem> itemBasedRecommendations = itemRecommender
				.recommend(userId, numberOfRecommendation);

		for (RecommendedItem recommendation : itemBasedRecommendations) {
			System.out.println("The two recommended item using similarity "
					+ Type + "for user " + userId + " is " + recommendation);
		}

		int userID = 1;
		long itemID = 1106;

		System.out.println("The estimated prefrence using similarity " + Type
				+ "for user " + userId + " is "
				+ itemRecommender.estimatePreference(userID, itemID));
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

		ItemSimilarity pearsonSimilarity = new PearsonCorrelationSimilarity(
				model);
		ItemSimilarity euclideanSimilarity = new EuclideanDistanceSimilarity(
				model);
		ItemSimilarity tanimotoSimilarity = new TanimotoCoefficientSimilarity(
				model);
		ItemSimilarity logLikilihoodSimilarity = new LogLikelihoodSimilarity(
				model);

		performItemRecommendation(model, pearsonSimilarity, "pearson ");
		performItemRecommendation(model, euclideanSimilarity, "euclidean ");
		performItemRecommendation(model, tanimotoSimilarity, "tanimoto ");
		performItemRecommendation(model, logLikilihoodSimilarity,
				"log-likelihood ");

	}

}
