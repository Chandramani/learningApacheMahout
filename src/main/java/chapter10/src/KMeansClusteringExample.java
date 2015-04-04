package chapter10.src;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.evaluation.ClusterEvaluator;
import org.apache.mahout.clustering.evaluation.RepresentativePointsDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.CosineDistanceMeasure;

public class KMeansClusteringExample {

	public static void main(String args[]) throws IOException,
	ClassNotFoundException, InterruptedException, InstantiationException, IllegalAccessException {
		String outputDir = "data/chapter10/reuters-features";
		Path vectorsFolder = new Path(outputDir, "tfidf-vectors");
		Path centroids = new Path(outputDir, "centroids");
		Path clusterOutput = new Path(outputDir, "clusters");
		Configuration conf = new Configuration();

		RandomSeedGenerator.buildRandom(conf, vectorsFolder, centroids, 20,
				new CosineDistanceMeasure());
		KMeansDriver.run(conf, vectorsFolder, centroids, clusterOutput, 0.01,
				20, true, 0, false);

		CosineDistanceMeasure measure = new CosineDistanceMeasure();

		RepresentativePointsDriver.run(conf, new Path(clusterOutput,"clusters-10-final"), new Path(
				clusterOutput, "clusteredPoints"), clusterOutput, measure, 20, true);
		
		ClusterEvaluator cv = new ClusterEvaluator(conf,new Path(clusterOutput,"clusters-10-final"));
		
		System.out.println(cv.interClusterDensity());

		System.out.println(cv.intraClusterDensity());

	}
}
