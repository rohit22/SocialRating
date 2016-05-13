package main.com.service;
 
/**
 * @author rbg2134
 * 
 */
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import main.com.run.Search;
import main.com.utils.JsonParseRecursive;
 
@Path("/")
public class RestService {
	@POST
	@Path("/getContent")
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response crunchifyREST(InputStream incomingData) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + sb.toString());
 
		HashMap<String, Object> query = JsonParseRecursive.getMap(sb.toString());
		System.out.println(query.get("word"));
		String queryWords = (String) query.get("word");
		Integer tweets = 250;
		if (query.containsKey("tweets")){
			tweets = Integer.parseInt((String)query.get("tweets"));
		}
		Integer ytComments = 250;
		if (query.containsKey("ytcomments")){
			tweets = Integer.parseInt((String)query.get("ytcomments"));
		}
		
		JSONArray results = Search.search(queryWords, tweets, ytComments);
		JSONObject obj = new JSONObject();
		obj.put("results", results);
		// return HTTP response 200 in case of success
		return Response.status(200).entity(obj.toJSONString()).build();
	}
 
	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService(InputStream incomingData) {
		String result = "RESTService Successfully started..";
 		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}
 
}