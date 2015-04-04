package chapter10.src;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.en.PorterStemFilter;

public class CustomAnalyzer extends Analyzer {
		
		@Override
		public TokenStreamComponents createComponents(String field, Reader reader) {
		    Tokenizer source = new StandardTokenizer(Version.LUCENE_46,reader);
		    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		     TokenStream filter;
			try {
				filter = analyzer.tokenStream(field,reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		     filter = new PorterStemFilter(source);
		     analyzer.close();
		     return new TokenStreamComponents(source, filter);
		}
}
