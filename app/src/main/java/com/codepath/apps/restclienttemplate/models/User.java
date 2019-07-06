package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel()
public class User {

    // list the attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    public String profileBannerUrl;
    public String description;
    public String followers_count;
    public String following_count;

    public User() {

    }

    // deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        //extract and fill the values
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileBannerUrl = json.getString("profile_banner_url");
        user.description = json.getString("description");

        // Parses URL for higher quality profile image
        String image = json.getString("profile_image_url_https");
        if (image.contains("normal.jpg")) {
            image = image.substring(0, image.indexOf("normal.jpg")) + "bigger.jpg";
        }
        user.profileImageUrl = image;
        user.followers_count = "" + json.getInt("followers_count");
        user.following_count = "" + json.getInt("friends_count");

    return user;
    }
}
