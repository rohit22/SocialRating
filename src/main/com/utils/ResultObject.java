package main.com.utils;

import org.json.simple.JSONObject;

public class ResultObject {

	String source;
	String user;
	String text;
	Integer sentiment = null;
	String type;
	Double topicProb = 0.0;

	public Double getTopicProb() {
		return topicProb;
	}

	public void setTopicProb(Double topicProb) {
		this.topicProb = topicProb;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getSentiment() {
		return sentiment;
	}

	public void setSentiment(Integer sentiment) {
		this.sentiment = sentiment;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("user", user);
		obj.put("text", text);
		obj.put("source", source);
		obj.put("sentiment", String.valueOf(sentiment));
		obj.put("type", type);
		obj.put("topicProb", topicProb);
		return obj.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("user", user);
		obj.put("text", text);
		obj.put("source", source);
		obj.put("sentiment", String.valueOf(sentiment));
		obj.put("type", type);
		obj.put("topicProb", topicProb);
		return obj;
	}

}
