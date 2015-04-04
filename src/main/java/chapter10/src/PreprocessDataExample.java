package chapter10.src;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class PreprocessDataExample {

	private static void displayTokenUsingStandardAnalyzer() throws IOException {
		System.out.println("Example with Sandard Analyzer \n");
		String text = "Lucene is simple but         yet  a powerful Java based at search library. StandardAnalyzer will convert all words to lowercase and remove stop words";
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(
				text));

		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			System.out.println(tokenStream
					.getAttribute(CharTermAttribute.class).toString());
		}
		tokenStream.close();
		System.out.println("Example after the inclusion of stemmimg of words \n");
		String text_stem = "Lucene is simple but         yet  a powerful Java based at search library. This is to check stemming by PorterStemFilter, kicking will become kick";
		tokenStream = analyzer.tokenStream(null, new StringReader(text_stem));
		tokenStream = new PorterStemFilter(tokenStream);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			System.out.println(tokenStream
					.getAttribute(CharTermAttribute.class).toString());
		}
		tokenStream.close();
		analyzer.close();
	}

	public static void main(String args[]) throws IOException {
		displayTokenUsingStandardAnalyzer();
	}
}
