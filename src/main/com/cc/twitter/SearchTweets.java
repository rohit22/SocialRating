package main.com.cc.twitter;

import java.util.List;

import org.scribe.model.Token;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class SearchTweets {
	/**
	 * Usage: java twitter4j.examples.search.SearchTweets [query]
	 *
	 * @param args
	 *            search query
	 */
	public static void main(String[] args) {
		Twitter twitter = new TwitterFactory().getInstance();

		AccessToken accessToken;

		accessToken = new AccessToken("15960177-Rp90ddg8Rc8FBa99PHdXsUeFXeaMRKx8jNyg4QVJR",
				"NkKS7bfa4x0vgC4wU4EHKrnVM8V7DzzWxjQ5wVS8HsvmL");

		twitter.setOAuthConsumer("CJAXt2iFZRB7wAAJZwQX7Flsk", "QLiRrx8Knx4AVB9ZrEEafmxplzRdQ97EgysEb9Ka7c1LhySOII");
		twitter.setOAuthAccessToken(accessToken);

		try {
			Query query = new Query("Titanic");
			query.setCount(100);
			query.setLang("en");
			QueryResult result;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}
			} while ((query = result.nextQuery()) != null);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}
}
