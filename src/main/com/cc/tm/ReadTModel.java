package main.com.cc.tm;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import cc.mallet.pipe.Pipe;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class ReadTModel {
	
	private static ReadTModel readTmodel;
	
	private static final String path = "resources";
	private static final String modelFile = "model.out";
	private static final String instFile = "train.inst";
	private static TopicInferencer inferencer;
	private static ParallelTopicModel model;
	private static Pipe pipe;
	
	public static ReadTModel getInstance(){
		
		if (readTmodel != null){
			return readTmodel;
		}
		readTmodel = new ReadTModel();
		File f;
		try {
			f = new File(path);
			String fileName;
			if (f.exists() || f.isDirectory()){
				fileName = path+"/"+modelFile;
			} else{
				fileName = Thread.currentThread().getContextClassLoader().getResource(modelFile).getFile();
			}
		//	System.out.println(fileName);
			f = new File(fileName);
			model = ParallelTopicModel.read(f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inferencer = model.getInferencer();
		inferencer.setRandomSeed(5);
		f = new File(path);
		String fileName;
		if (f.exists() || f.isDirectory()){
			fileName = path+"/"+instFile;
		} else{
			fileName = Thread.currentThread().getContextClassLoader().getResource(instFile).getFile();
		}
		//System.out.println(fileName);
		
		InstanceList instances = InstanceList.load(new File(fileName));
		pipe = instances.getPipe();
		return readTmodel;
	}
	
	public double getMaxProb(String text){
		
		if (readTmodel == null){
			readTmodel = getInstance();
		}
		InstanceList testing = new InstanceList(pipe);
        //System.out.println(text);
        testing.addThruPipe(new Instance(text, null, "test instance", null));

        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 1000, 100, 5);
     
       // for (double prob : testProbabilities){
       // 	System.out.println(prob);
        //}
        return getMax(testProbabilities);
	}
	
	private static double getMax(double[] probs){
		double toReturn = -1;
		for (double prob : probs){
        	if (prob > toReturn){
        		toReturn = prob;
        	}
        }
		return toReturn;
	}
	
	public static void main(String[] args){

		/*
		String modelFile = "resources/model.out";
		String instFile = "resources/train.inst";
		
		InstanceList instances = InstanceList.load(new File(instFile));
		
		ParallelTopicModel model = null;*/
				// Show the words and topics in the first instance
/*
        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
        }
        System.out.println(out);
        
        // Estimate the topic distribution of the first instance, 
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        
        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            
            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
            int rank = 0;
            while (iterator.hasNext() && rank < 5) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            System.out.println(out);
        }
        
        // Create a new instance with high probability of topic 0
      /*  StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < 5) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
            rank++;
        }*/
		
		String text = "trump and hillary clinton are fighting in elections";
		/*
        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        System.out.println(text);
        testing.addThruPipe(new Instance(text, null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        inferencer.setRandomSeed(5);
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 1000, 100, 5);
        
        for (double prob : testProbabilities){
        	System.out.println(prob);
        }*/
        
        ReadTModel tmodel = ReadTModel.getInstance();
        System.out.println(tmodel.getMaxProb(text));
        //System.out.println("0\t" + testProbabilities[0]+"\t"+testProbabilities[1]+"\t"+testProbabilities[2]+"\t"+testProbabilities[3]+"\t"+testProbabilities[4]);

	}

}
