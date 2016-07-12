package main.com.cc.twitter;

import java.util.ArrayList;
import java.util.List;

import main.com.cc.sa.AlchemyAPI;
import main.com.cc.sa.SentimentAnalyzer;
import main.com.cc.tm.ReadTModel;
import main.com.utils.ResultObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterSearch {

	private static TwitterSearch instance;
	private static Twitter twitter;
	private static AccessToken accessToken;

	public static TwitterSearch getInstance() {
		if (instance == null) {
			instance = new TwitterSearch();
			twitter = new TwitterFactory().getInstance();
			accessToken = new AccessToken("PUBLIC-KEY",
					"PRIVATE-KEY");

			twitter.setOAuthConsumer("OAUTH-TOKEN", "OAUTH-VALUE");
			twitter.setOAuthAccessToken(accessToken);

		}
		return instance;
	}

	public ArrayList<ResultObject> getComments(String queryWord, Integer total) {
		ArrayList<ResultObject> results = new ArrayList<>();
		try {
			Query query = new Query(queryWord);
			query.setCount(total);
			query.setLang("en");
			QueryResult result;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					if (!tweet.isRetweet()) {
						// System.out.println("@" +
						// tweet.getUser().getScreenName() + " - " +
						// tweet.getText());
						ResultObject rObj = new ResultObject();
						rObj.setType("twitter");
						rObj.setText(tweet.getText());
						rObj.setSource(tweet.getSource());
						rObj.setUser(String.valueOf(tweet.getUser().getId()));
						rObj.setTopicProb(ReadTModel.getInstance().getMaxProb(tweet.getText()));
						rObj.setSentiment(SentimentAnalyzer.getInstance().getSentiment(tweet.getText())+1);
						results.add(rObj);
						if (results.size() > total+50){
							break;
						}
					}
				}
			} while ((query = result.nextQuery()) != null);
			// System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			// System.exit(-1);
		}
		return results;
	}

	public static void main(String[] args) {
		TwitterSearch ts = TwitterSearch.getInstance();
		ts.getComments("titanic",100);
	}

}
