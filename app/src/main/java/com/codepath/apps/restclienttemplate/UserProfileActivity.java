package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

public class UserProfileActivity extends AppCompatActivity {
    // Instances
    private TwitterClient client;
    private User user;
    private ImageView ivUserProfileClose;
    private TextView tvUserProfileName;
    private TextView tvUserProfileScreenName;
    private TextView tvUserProfileDescription;
    private TextView tvUserFollowersCount;
    private TextView tvUserFollowingCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        client = TwitterApp.getRestClient(this);
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        createProfile();
    }

    // Profile is created only once User object is returned form the network call
    public void createProfile() {
        // Uploads banner and profile image
        uploadMyPhotos();

        // Adds listener to close image
        ivUserProfileClose = (ImageView) findViewById(R.id.ivUserProfileClose);
        ivUserProfileClose.setOnClickListener(new View.OnClickListener() {
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
                .into((ImageView) findViewById(R.id.ivUserProfileImage));

        // Uploads the banner
        Glide.with(getApplicationContext())
                .load(user.profileBannerUrl)
                .into((ImageView) findViewById(R.id.ivUserBanner));
    }

    // Fills the text View for a user object
    public void fillTextViews() {
        // Profile Name
        tvUserProfileName = (TextView) findViewById(R.id.tvUserProfileName);
        tvUserProfileName.setText(user.name);

        // Screen Name
        tvUserProfileScreenName = (TextView) findViewById(R.id.tvUserProfileScreenName);
        String screenName = "@" + user.screenName; // Cannot concatenate within setText();
        tvUserProfileScreenName.setText(screenName);

        // Description
        tvUserProfileDescription = (TextView) findViewById(R.id.tvUserProfileDescription);
        tvUserProfileDescription.setText(user.description);

        // Followers Count
        tvUserFollowersCount = (TextView) findViewById(R.id.tvUserFollowersCount);
        tvUserFollowersCount.setText(user.followers_count);

        // Following Count
        tvUserFollowingCount = (TextView) findViewById(R.id.tvUserFollowingCount);
        tvUserFollowingCount.setText(user.following_count);
    }
}
