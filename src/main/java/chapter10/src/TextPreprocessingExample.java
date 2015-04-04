package chapter10.src;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.analysis.Analyzer;
import org.apache.mahout.common.Pair;
import org.apache.mahout.vectorizer.DictionaryVectorizer;
import org.apache.mahout.vectorizer.DocumentProcessor;
import org.apache.mahout.vectorizer.HighDFWordsPruner;
import org.apache.mahout.vectorizer.tfidf.TFIDFConverter;

public class TextPreprocessingExample {

	public static void main(String args[]) throws Exception {

		int minSupport = 5;
		int maxNGramSize = 1;
		int minLLRValue = 50;
		int chunkSize = 200;
		// int normPower = -1.0f;
		boolean logNormalize = false;
		boolean namedVectors = false;
		boolean sequentialAccessOutput = true;

		// process vectors
		int minDf = 5;
		int maxDFPercent = 99;
		int norm = -1;
		int reduceTasks = 1;
		float maxDFSigma = 0;

		Pair<Long[], List<Path>> docFrequenciesFeatures = null;

		String inputDir = "data/chapter10/reuters-out-seqdir";
		Configuration conf = new Configuration();
		String outputDir = "data/chapter10/reuters-features";
		Path tokenizedPath = new Path(outputDir,
				DocumentProcessor.TOKENIZED_DOCUMENT_OUTPUT_FOLDER);
		System.out.println(tokenizedPath);
		CustomAnalyzer analyzer = new CustomAnalyzer();
		DocumentProcessor.tokenizeDocuments(new Path(inputDir), analyzer
				.getClass().asSubclass(Analyzer.class), tokenizedPath, conf);
		boolean shouldPrune = maxDFSigma >= 0.0 || maxDFPercent > 0.00;
		String tfDirName = shouldPrune ? DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER
				+ "-toprune"
				: DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER;
		analyzer.close();
		DictionaryVectorizer.createTermFrequencyVectors(tokenizedPath,
				new Path(outputDir), tfDirName, conf, minSupport, maxNGramSize,
				minLLRValue, norm, logNormalize, reduceTasks, chunkSize,
				sequentialAccessOutput, namedVectors);

		docFrequenciesFeatures = TFIDFConverter.calculateDF(new Path(outputDir,
				tfDirName), new Path(outputDir), conf, chunkSize);

		long maxDF = maxDFPercent; // if we are pruning by std dev, then this
		// will get changed
		long vectorCount = docFrequenciesFeatures.getFirst()[1];
		long maxDFThreshold = (long) (vectorCount * (maxDF / 100.0f));

		// Prune the term frequency vectors
		Path tfDir = new Path(outputDir, tfDirName);
		Path prunedTFDir = new Path(outputDir,
				DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER);
		Path prunedPartialTFDir = new Path(outputDir,
				DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER + "-partial");

		HighDFWordsPruner.pruneVectors(tfDir, prunedTFDir, prunedPartialTFDir,
				maxDFThreshold, minDf, conf, docFrequenciesFeatures, -1.0f,
				false, reduceTasks);

		TFIDFConverter.processTfIdf(new Path(outputDir,
				DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER), new Path(
						outputDir), conf, docFrequenciesFeatures, minDf, maxDFPercent,
						norm, logNormalize, sequentialAccessOutput, namedVectors,
						reduceTasks);

	}
}
