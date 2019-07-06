package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel // Need to implement in gradle
public class Tweet {
    // list out the attributes, must be public for parcel
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public String reTweet_count;
    public String favorite_count;
    public boolean favorited;
    public boolean retweeted;

    // Empty function for parecler
    public Tweet() {

    }

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        try {
            tweet.body = jsonObject.getString("full_text");
        } catch(JSONException e) {
            tweet.body = jsonObject.getString("text");
        }
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.reTweet_count = "" + jsonObject.getInt("retweet_count");
        tweet.favorite_count = "" + jsonObject.getInt("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");

        return tweet;
    }
}
