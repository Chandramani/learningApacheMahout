package chapter5.fpm.src;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.Parameters;
import org.apache.mahout.common.iterator.FileLineIterable;
import org.apache.mahout.common.iterator.StringRecordIterator;
import org.apache.mahout.fpm.pfpgrowth.PFPGrowth;
import org.apache.mahout.fpm.pfpgrowth.convertors.ContextStatusUpdater;
import org.apache.mahout.fpm.pfpgrowth.convertors.SequenceFileOutputCollector;
import org.apache.mahout.fpm.pfpgrowth.convertors.string.StringOutputConverter;
import org.apache.mahout.fpm.pfpgrowth.convertors.string.TopKStringPatterns;
import org.apache.mahout.fpm.pfpgrowth.fpgrowth.FPGrowth;
import org.apache.mahout.fpm.pfpgrowth.fpgrowth2.FPGrowthObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FPGrowthExample extends AbstractJob {

	private static final Logger log = LoggerFactory
			.getLogger(FPGrowthExample.class);

	public static void main(String args[]) {
		Configuration conf = new Configuration();
		conf.addResource(new Path("/usr/local/hadoop/conf/core-site.xml"));
		conf.addResource(new Path("/usr/local/hadoop/conf/hdfs-site.xml"));
		FPGrowthExample pf = new FPGrowthExample();
		try {
			pf.run(args, conf);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public int run(String[] args, Configuration conf) throws IOException,
			ClassNotFoundException, InterruptedException {
		addInputOption();
		addOutputOption();

		addOption("minSupport", "s",
				"(Optional) The minimum number of times a co-occurrence must be present."
						+ " Default Value: 3", "3");
		addOption(
				"maxHeapSize",
				"k",
				"(Optional) Maximum Heap Size k, to denote the requirement to mine top K items."
						+ " Default value: 50", "50");
		addOption(
				"numGroups",
				"g",
				"(Optional) Number of groups the features should be divided in the map-reduce version."
						+ " Doesn't work in sequential version Default Value:"
						+ PFPGrowth.NUM_GROUPS_DEFAULT,
				Integer.toString(PFPGrowth.NUM_GROUPS_DEFAULT));
		addOption(
				"splitterPattern",
				"regex",
				"Regular Expression pattern used to split given string transaction into"
						+ " itemsets. Default value splits comma separated itemsets.  Default Value:"
						+ " \"[ ,\\t]*[,|\\t][ ,\\t]*\" ",
				"[ ,\t]*[,|\t][ ,\t]*");
		addOption(
				"numTreeCacheEntries",
				"tc",
				"(Optional) Number of entries in the tree cache to prevent duplicate"
						+ " tree building. (Warning) a first level conditional FP-Tree might consume a lot of memory, "
						+ "so keep this value small, but big enough to prevent duplicate tree building. "
						+ "Default Value:5 Recommended Values: [5-10]", "5");
		addOption("method", "method",
				"Method of processing: sequential|mapreduce", "sequential");
		addOption("encoding", "e",
				"(Optional) The file encoding.  Default value: UTF-8", "UTF-8");
		addFlag("useFPG2", "2", "Use an alternate FPG implementation");

		if (parseArguments(args) == null) {
			return -1;
		}

		Parameters params = new Parameters();

		if (hasOption("minSupport")) {
			String minSupportString = getOption("minSupport");
			params.set("minSupport", minSupportString);
		}
		if (hasOption("maxHeapSize")) {
			String maxHeapSizeString = getOption("maxHeapSize");
			params.set("maxHeapSize", maxHeapSizeString);
		}
		if (hasOption("numGroups")) {
			String numGroupsString = getOption("numGroups");
			params.set("numGroups", numGroupsString);
		}

		if (hasOption("numTreeCacheEntries")) {
			String numTreeCacheString = getOption("numTreeCacheEntries");
			params.set("treeCacheSize", numTreeCacheString);
		}

		if (hasOption("splitterPattern")) {
			String patternString = getOption("splitterPattern");
			params.set("splitPattern", patternString);
		}

		String encoding = "UTF-8";
		if (hasOption("encoding")) {
			encoding = getOption("encoding");
		}
		params.set("encoding", encoding);

		if (hasOption("useFPG2")) {
			params.set(PFPGrowth.USE_FPG2, "true");
		}

		Path inputDir = getInputPath();
		Path outputDir = getOutputPath();

		params.set("input", inputDir.toString());
		params.set("output", outputDir.toString());
		System.out.println("input directory is ="+inputDir.toString());
		System.out.println("output directory is ="+outputDir.toString());

		String classificationMethod = getOption("method");
		if ("sequential".equalsIgnoreCase(classificationMethod)) {
			System.out.println("Sequential run");
			runFPGrowth(params, conf);
		} else if ("mapreduce".equalsIgnoreCase(classificationMethod)) {
			System.out.println("mapreduce run");
			HadoopUtil.delete(conf, outputDir);
			PFPGrowth.runPFPGrowth(params, conf);
		}

		return 0;
	}

	private static void runFPGrowth(Parameters params, Configuration conf)
			throws IOException {
		log.info("Starting Sequential FPGrowth");
		int maxHeapSize = Integer.valueOf(params.get("maxHeapSize", "50"));
		int minSupport = Integer.valueOf(params.get("minSupport", "3"));

		Path output = new Path(params.get("output", "output.txt"));
		Path input = new Path(params.get("input"));

		FileSystem fs = FileSystem.get(output.toUri(), conf);

		Charset encoding = Charset.forName(params.get("encoding"));

		String pattern = params.get("splitPattern",
				PFPGrowth.SPLITTER.toString());

		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, output,
				Text.class, TopKStringPatterns.class);

		FSDataInputStream inputStream = null;
		FSDataInputStream inputStreamAgain = null;

		Collection<String> features = Sets.newHashSet();

		if ("true".equals(params.get(PFPGrowth.USE_FPG2))) {
			FPGrowthObj<String> fp = new FPGrowthObj<String>();

			try {
				inputStream = fs.open(input);
				inputStreamAgain = fs.open(input);
				fp.generateTopKFrequentPatterns(
						new StringRecordIterator(new FileLineIterable(
								inputStream, encoding, false), pattern),
						fp.generateFList(new StringRecordIterator(
								new FileLineIterable(inputStreamAgain,
										encoding, false), pattern), minSupport),
						minSupport,
						maxHeapSize,
						features,
						new StringOutputConverter(
								new SequenceFileOutputCollector<Text, TopKStringPatterns>(
										writer)));
			} finally {
				Closeables.close(writer, false);
				Closeables.close(inputStream, true);
				Closeables.close(inputStreamAgain, true);
			}
		} else {
			FPGrowth<String> fp = new FPGrowth<String>();
			inputStream = fs.open(input);
			inputStreamAgain = fs.open(input);
			try {
				fp.generateTopKFrequentPatterns(
						new StringRecordIterator(new FileLineIterable(
								inputStream, encoding, false), pattern),
						fp.generateFList(new StringRecordIterator(
								new FileLineIterable(inputStreamAgain,
										encoding, false), pattern), minSupport),
						minSupport,
						maxHeapSize,
						features,
						new StringOutputConverter(
								new SequenceFileOutputCollector<Text, TopKStringPatterns>(
										writer)),
						new ContextStatusUpdater<Writable, Writable, Writable, Writable>(
								null));
			} finally {
				Closeables.close(writer, false);
				Closeables.close(inputStream, true);
				Closeables.close(inputStreamAgain, true);
			}
		}

		List<Pair<String, TopKStringPatterns>> frequentPatterns = FPGrowth
				.readFrequentPattern(conf, output);
		for (Pair<String, TopKStringPatterns> entry : frequentPatterns) {
			log.info("Dumping Patterns for Feature: {} \n{}", entry.getFirst(),
					entry.getSecond());
		}
	}

	public int run(String[] arg0) throws Exception {
		return 0;
	}

}
