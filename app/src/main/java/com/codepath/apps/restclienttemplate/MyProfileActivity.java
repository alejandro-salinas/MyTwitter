package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MyProfileActivity extends AppCompatActivity {
    // Instances
    private TwitterClient client;
    private User user;
    private ImageView ivMyProfileClose;
    private TextView tvMyProfileName;
    private TextView tvMyProfileScreenName;
    private TextView tvMyProfileDescription;
    private TextView tvFollowersCount;
    private TextView tvFollowingCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        client = TwitterApp.getRestClient(this);
        // Uses network call to get User
        getUser();
    }

    // Uses network request and glide to upload image
    public void getUser() {
        // Send network request to get user
        client.getMyUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = (User) User.fromJSON(response);
                    Log.d("onSuccess", "User successfully retrieved");
                    createProfile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
            }
        });
    }

    // Profile is created only once User object is returned form the network call
    public void createProfile() {
        // Uploads banner and profile image
        uploadMyPhotos();

        // Adds listener to close image
        ivMyProfileClose = (ImageView) findViewById(R.id.ivMyProfileClose);
        ivMyProfileClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes activity
            }
        });

        // Fill User Text Views
        fillTextViews();
    }

    // Uses glide to upload banner and profile
    public void uploadMyPhotos() {
        // Uploads the profile photo
        Glide.with(getApplicationContext())
                .load(user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into((ImageView) findViewById(R.id.ivMyProfileImage));

        // Uploads the banner
        Glide.with(getApplicationContext())
                .load(user.profileBannerUrl)
                .into((ImageView) findViewById(R.id.ivMyBanner));
    }

    // Fills the text View for a user object
    public void fillTextViews() {
        // Profile Name
        tvMyProfileName = (TextView) findViewById(R.id.tvMyProfileName);
        tvMyProfileName.setText(user.name);

        // Screen Name
        tvMyProfileScreenName = (TextView) findViewById(R.id.tvMyProfileScreenName);
        String screenName = "@" + user.screenName; // Cannot concatenate within setText();
        tvMyProfileScreenName.setText(screenName);

        // Description
        tvMyProfileDescription = (TextView) findViewById(R.id.tvMyProfileDescription);
        tvMyProfileDescription.setText(user.description);

        // Followers Count
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowersCount.setText("123");

        // Following Count
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        tvFollowingCount.setText("456");
    }
}
