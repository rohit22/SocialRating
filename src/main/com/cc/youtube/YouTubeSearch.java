/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package main.com.cc.youtube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import main.com.cc.sa.SentimentAnalyzer;
import main.com.cc.tm.ReadTModel;
import main.com.cc.youtube.Auth;
import main.com.utils.ResultObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
public class YouTubeSearch {

	/**
	 * Define a global variable that identifies the name of a file that contains
	 * the developer's API key.
	 */
	private static final String path = "resources";
	
	private static final String PROPERTIES_FILENAME = "youtube.properties";

	private static final long NUMBER_OF_VIDEOS_RETURNED = 15;
	private static final Integer comments_per_video = 75;

	/**
	 * Define a global instance of a Youtube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;
	private static YouTubeSearch instance;
	private static String apiKey;

	public static YouTubeSearch getInstance() {
		if (instance == null) {
			instance = new YouTubeSearch();
			Properties properties = new Properties();
			try {
				File f = new File(path);
				String fileName;
				if (f.exists() || f.isDirectory()){
					fileName = path+"/"+PROPERTIES_FILENAME;
				} else{
					fileName = Thread.currentThread().getContextClassLoader().getResource(PROPERTIES_FILENAME).getFile();
				}
		//		System.out.println(fileName);
				InputStream in = new FileInputStream(new File(fileName));
				properties.load(in);

			} catch (IOException e) {
				System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause() + " : "
						+ e.getMessage());
				System.exit(1);
			}
			apiKey = properties.getProperty("youtube.apikey");
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("youtube-cmdline-search-sample").build();

		}
		return instance;
	}

	public ArrayList<ResultObject> getComments(String queryTerm, Integer total) {
		ArrayList<ResultObject> results = new ArrayList<>();
		try {
			YouTube.Search.List search = youtube.search().list("id,snippet");
			search.setKey(apiKey).setQ(queryTerm).setType("video")
					.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResultList = searchResponse.getItems();
			/*
			 * if (searchResultList != null) {
			 * prettyPrint(searchResultList.iterator(), queryTerm); }
			 */
			ArrayList<String> comments;
			Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
			while (iteratorSearchResults.hasNext()) {
				SearchResult singleVideo = iteratorSearchResults.next();
				ResourceId rId = singleVideo.getId();
				if (rId.getKind().equals("youtube#video")) {
				//	System.out.println(" Video Id: " + rId.getVideoId());
					comments = getCommentsForVideo(rId.getVideoId());
					if (comments != null) {
						for (String comment : comments) {
							ResultObject rObj = new ResultObject();
							rObj.setType("youtube");
							rObj.setText(comment);
							rObj.setSource(rId.getVideoId());
							rObj.setUser(rId.getChannelId());
							rObj.setTopicProb(ReadTModel.getInstance().getMaxProb(comment));
							rObj.setSentiment(SentimentAnalyzer.getInstance().getSentiment(comment)+1);
							//rObj.setSentiment(AlchemyAPI.getSentiment(comment));
							results.add(rObj);
							if (results.size()>total+50){
								break;
							}
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(results.size());
		return results;
	}

	/**
	 * Initialize a YouTube object to search for videos on YouTube. Then display
	 * the name and thumbnail image of each video in the result set.
	 *
	 * @param args
	 *            command line args.
	 */
	public static void main(String[] args) {
		// Read the developer key from the properties file.

		try {
			// This object is used to make YouTube Data API requests. The last
			// argument is required, but since we don't need anything
			// initialized when the HttpRequest is initialized, we override
			// the interface and provide a no-op function.

			// Prompt the user to enter a query term.
			// String queryTerm = getInputQuery();

		    File file = new File("resources.properties");
		    System.out.println(file.getAbsolutePath());

			String queryTerm = "first search";
			YouTubeSearch youtubeS = YouTubeSearch.getInstance();
			youtubeS.getComments(queryTerm,200);
			/*
			 * // Define the API request for retrieving search results.
			 * YouTube.Search.List search = youtube.search().list("id,snippet");
			 * 
			 * // Set your developer key from the Google Developers Console for
			 * // non-authenticated requests. See: //
			 * https://console.developers.google.com/ search.setKey(apiKey);
			 * search.setQ(queryTerm);
			 * 
			 * // Restrict the search results to only include videos. See: //
			 * https://developers.google.com/youtube/v3/docs/search/list#type
			 * search.setType("video");
			 * 
			 * // To increase efficiency, only retrieve the fields that the //
			 * application uses. search.setFields(
			 * "items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)"
			 * ); search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			 * 
			 * // Call the API and print results. SearchListResponse
			 * searchResponse = search.execute(); List<SearchResult>
			 * searchResultList = searchResponse.getItems(); if
			 * (searchResultList != null) {
			 * prettyPrint(searchResultList.iterator(), queryTerm); }
			 */
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/*
	 * Prints out all results in the Iterator. For each result, print the title,
	 * video ID, and thumbnail.
	 *
	 * @param iteratorSearchResults Iterator of SearchResults to print
	 *
	 * @param query Search query (String)
	 */
	private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

		System.out.println("\n=============================================================");
		System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}

		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			// Confirm that the result represents a video. Otherwise, the
			// item will not contain a video ID.
			if (rId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
				System.out.println(" Video Id: " + rId.getVideoId());
				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				System.out.println(" Thumbnail: " + thumbnail.getUrl());
				System.out.println("\n-------------------------------------------------------------\n");
			}
		}
	}

	private ArrayList<String> getCommentsForVideo(String videoId) throws IOException {
		// Call the YouTube Data API's commentThreads.list method to
		// retrieve video comment threads.
		CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads().list("snippet").setKey(apiKey)
				.setVideoId(videoId).setTextFormat("plainText").execute();

		List<CommentThread> videoComments = videoCommentsListResponse.getItems();
		if (videoComments.isEmpty()) {
			System.out.println("Can't get video comments.");
		} else {
			// Print information from the API response.
			ArrayList<String> comments = new ArrayList<>();
			for (CommentThread videoComment : videoComments) {
				CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
		//		System.out.println("  - Comment: " + snippet.getTextDisplay());
				comments.add(snippet.getTextDisplay());
				if (comments.size() > comments_per_video){
					break;
				}
			}
			return comments;
		}
		return null;
	}
}
