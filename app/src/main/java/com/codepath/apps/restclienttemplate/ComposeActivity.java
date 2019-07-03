package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    // reference to twitter client
    private TwitterClient client;
    private Button tweetButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        // Adds onClickListener to tweetButton and send network request when clicked
        tweetButton = (Button) findViewById(R.id.tweetButton);
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
                        Log.e("ComposeActivity", "Unable to reach server when sending tweet");
                        Toast.makeText(ComposeActivity.this, errorResponse.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Adds onClickListener to cancelButton and returns to TimelineActivity
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    // Returns tweet in the intent as a result
    public void returnTweet(Tweet tweet) {
        // Prepare data intent
        Intent data = new Intent();
        // Pass tweet back
        data.putExtra("tweet", tweet);
        setResult(RESULT_OK, data);
        finish(); // Closes the activity, pass data to parent
    }
}
