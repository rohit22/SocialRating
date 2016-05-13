package main.com.cc.sa;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import main.com.run.Search;

public class SentimentAnalyzer {

	private StanfordCoreNLP pipeline;
	private static SentimentAnalyzer instance;

	public static SentimentAnalyzer getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new SentimentAnalyzer();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		props.setProperty("tokenize.options", "untokenizable=noneDelete");
		instance.pipeline = new StanfordCoreNLP(props);
		return instance;
	}

	public Integer getSentiment(String line) {
		if (instance == null) {
			instance = getInstance();
		}
		int mainSentiment = 0;
		if (line != null && line.length() > 0) {
			int longest = 0;
			try {
				Annotation annotation = pipeline.process(line);
				for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
					Tree tree = sentence.get(SentimentAnnotatedTree.class);
					int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
					String partText = sentence.toString();
					if (partText.length() > longest) {
						mainSentiment = sentiment;
						longest = partText.length();
					}

				}
				return mainSentiment;
			} catch (Exception e) {
				Logger.getLogger(SentimentAnalyzer.class.getName()).log(Level.INFO, "exception caught " + e.getMessage());	
			}
		}
		return 2;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String line = "This World is an amazing place";
		System.out.println(SentimentAnalyzer.getInstance().getSentiment(line));
		String line2 = "This World is an amazingly bad place";
		String line3 = "This World is an amazingly beautiful place";
		System.out.println(SentimentAnalyzer.getInstance().getSentiment(line2));
		System.out.println(SentimentAnalyzer.getInstance().getSentiment(line3));
	}

}
