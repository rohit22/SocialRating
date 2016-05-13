package main.com.run;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

import main.com.cc.twitter.TwitterSearch;
import main.com.cc.youtube.YouTubeSearch;
import main.com.utils.ResultObject;

public class Search {

	public static JSONArray search(String query, Integer tweets, Integer ytComments) {
		Logger.getLogger(Search.class.getName()).log(Level.INFO, "Received query ->" + query);
		TwitterSearch ts = TwitterSearch.getInstance();
		ArrayList<ResultObject> results = new ArrayList<>();
		ArrayList<ResultObject> rs;
		rs = ts.getComments(query,tweets);
		Logger.getLogger(Search.class.getName()).log(Level.INFO, "Twitter Results Size ->" + rs.size());
		if (rs != null && rs.size() > 0) {
			for (ResultObject r : rs) {
				if (results.size() <= tweets) {
					results.add(r);
				}
			}
		}
		YouTubeSearch ys = YouTubeSearch.getInstance();
		rs = ys.getComments(query, ytComments);
		Logger.getLogger(Search.class.getName()).log(Level.INFO, "Youtube Results Size ->" + rs.size());
		// System.out.println(rs);
		if (rs != null && rs.size() > 0) {
			for (ResultObject r : rs) {
				if (results.size() <= tweets + ytComments) {
					results.add(r);
				}
			}
		}
		
		System.out.println(results.size());
		JSONArray toReturn = new JSONArray();
		for (ResultObject r : results) {
			toReturn.put(r.toJSONObject());
		}
		Logger.getLogger(Search.class.getName()).log(Level.INFO, "Results Size ->" + toReturn.length());
		return toReturn;
	}

	public static void main(String[] args) {
		// System.out.println(Search.search("titanic"));
		System.out.println(Search.search("gossip girl", 200, 250));
	}

}
