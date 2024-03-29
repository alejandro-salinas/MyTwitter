package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    // Instances
    private TwitterClient client;
    private Button tweetButton;
    private ImageView ivCancel;
    private User user;
    private TextView tvCount;
    private EditText etTweetBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);

        // Uploads profile image
        uploadUserImage();

        // Adds onClickListener to tweetButton and sends network request when clicked
        tweetButton = (Button) findViewById(R.id.tweetButton);
        onTweetButtonClick();

        // Adds onClickListener to cancelButton and returns to TimelineActivity
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Stores the view holding the character count in the tweet body
        tvCount = (TextView) findViewById(R.id.tvCount);

        //Keeps track of count in text body and updates it every time the tweet body is changed
        etTweetBody = (EditText) findViewById(R.id.etTweetBody);
        onTweetBodyChanged();

    }

    // Uses network request and glide to upload image
    public void uploadUserImage() {
        // Send network request to get user
        client.getMyUser(new JsonHttpResponseHandler() { // Attempted to use a network call to find the User
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = (User) User.fromJSON(response);
                    // Uses glide to upload the photo
                    Glide.with(getApplicationContext())
                            .load(user.profileImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into((ImageView) findViewById(R.id.ivProfileImage));

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
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the body of the tweet
                EditText etTweetBody = (EditText) findViewById(R.id.etTweetBody);
                String message = etTweetBody.getText().toString();
                // Sends tweet as a String
                client.sendTweet(message, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet tweet = Tweet.fromJSON(response);
                            Log.d("onSuccess", "Tweet successfully sent");
                            returnTweet(tweet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        etTweetBody.addTextChangedListener(new TextWatcher() {
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
                tvCount.setText("" + s.length() + "/140");
                // Updates color of character count
                if (s.length() < 120) {
                    tvCount.setTextColor(getResources().getColor(R.color.twitter_blue));
                } else if (s.length() < 140) {
                    tvCount.setTextColor(getResources().getColor(R.color.yellow_char_count));
                } else {
                    tvCount.setTextColor(getResources().getColor(R.color.red_char_count));
                }
            }
        });
    }

    // Returns tweet in the intent as a result
    public void returnTweet(Tweet tweet) {
        // Prepare data intent
        Intent data = new Intent();
        // Pass tweet back
        data.putExtra("tweet", (Parcelable) tweet);
        setResult(RESULT_OK, data);
        finish(); // Closes the activity, pass data to parent
    }
}
