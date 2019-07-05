package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ReplyActivity extends AppCompatActivity {
    // reference to twitter client
    private TwitterClient client;;
    private Tweet tweet;
    private Button tweetButtonReply;
    private ImageView ivCancelReply;
    private User userReply;
    private TextView tvCountReply;
    private EditText etTweetBodyReply;
    private TextView tvReplyingTo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        client = TwitterApp.getRestClient(this);

        // Uploads profile image
        uploadUserImage(client);

        // Adds onClickListener to tweetButton and sends network request when clicked
        tweetButtonReply = (Button) findViewById(R.id.tweetButtonReply);
        onTweetButtonClick();

        // unwrap the movie passed in via intent, using its simple name as a key
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        String replyingto = "Replying to @" + tweet.user.screenName;
        tvReplyingTo = (TextView) findViewById(R.id.tvReplyingTo);
        tvReplyingTo.setText(replyingto);

        // Adds onClickListener to cancel button and returns to TimelineActivity
        ivCancelReply = (ImageView) findViewById(R.id.ivCancelReply);
        ivCancelReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Stores the view holding the character count in the tweet body
        tvCountReply = (TextView) findViewById(R.id.tvCountReply);

        //Keeps track of count in text body and updates it every time the tweet body is changed
        etTweetBodyReply = (EditText) findViewById(R.id.etTweetBodyReply);
        etTweetBodyReply.setText("@" + tweet.user.screenName + " ");
        onTweetBodyChanged();
    }

    // Uses network request and glide to upload image
    public void uploadUserImage(TwitterClient client) {
        // Send network request to get user
        client.getMyUser(new JsonHttpResponseHandler() { // Attempted to use a network call to find the User
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    userReply = (User) User.fromJSON(response); // Unable to find user
                    // Uses glide to upload the photo
                    Glide.with(getApplicationContext())
                            .load(userReply.profileImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into((ImageView) findViewById(R.id.ivProfileImageReply));

                    Log.d("onSuccess", "User successfully retrieved");
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

    // Sends tweet to network using JsonHttpResonseHandler
    public void onTweetButtonClick() {
        tweetButtonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the body of the tweet
                EditText etTweetBodyReply = (EditText) findViewById(R.id.etTweetBodyReply);
                String message = etTweetBodyReply.getText().toString();
                // Sends tweet as a String
                client.sendTweet(message, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("onSuccess", "Tweet successfully sent");
                        Toast.makeText(getApplicationContext(),"Tweet successfully sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        Toast.makeText(getApplicationContext(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Updates character count when tweet body is changed
    public void onTweetBodyChanged() {
        etTweetBodyReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before the text is changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                tvCountReply.setText("" + s.length() + "/140");
                // Updates color of character count
                if (s.length() < 120) {
                    tvCountReply.setTextColor(getResources().getColor(R.color.twitter_blue));
                } else if (s.length() < 140) {
                    tvCountReply.setTextColor(getResources().getColor(R.color.yellow_char_count));
                } else {
                    tvCountReply.setTextColor(getResources().getColor(R.color.red_char_count));
                }
            }
        });
    }
}
