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

    // Empty function for parecler
    public Tweet() {

    }

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        return tweet;
    }
}
