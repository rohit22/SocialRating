package main.com.cc.tm;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

// Topic Modelling using Mallet

public class BuildTModel {

	public static void main(String[] args) throws Exception {

		String file = "/home/rohitb/Dropbox/Spring16/CloudComputing/PolarityDatasets/rt-polaritydata/rt-polarity.tot";
		String modelFile = "resources/model.out";
		String instFile = "resources/train.inst";
		
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(
				new File("resources/english.txt"), "UTF-8", false,
				false, false));
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		Reader fileReader = new InputStreamReader(new FileInputStream(new File(file)), "UTF-8");
		instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data,
																															// label,
																															// name
		// fields

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		int numTopics = 15;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and
		// combine
		// statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(2000);
		model.estimate();
		
		model.write(new File(modelFile));
		instances.save(new File(instFile));

		String text = "trump and hillary clinton are fighting in elections";

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        System.out.println(text);
        testing.addThruPipe(new Instance(text, null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        inferencer.setRandomSeed(5);
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 1000, 100, 5);
        for (double prob : testProbabilities){
        	System.out.println(prob);
        }
       
 //       System.out.println("0\t" + testProbabilities[0]+"\t"+testProbabilities[1]+"\t"+testProbabilities[2]+"\t"+testProbabilities[3]+"\t"+testProbabilities[4]);
//       System.out.println("0\t" + testProbabilities[0]);

	}

}
