package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context; // Instance so that Glide can reference it
    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    // ViewHolder cache improves speed by caching the values for each element,
    // rather than using the findByID every time

    @NonNull
    @Override
    // This function is only invoked when a new row needs to be created, which
    // is only for the first ones that fill the screen
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflates layout and caches the values
        View tweetView = inflater.inflate(R.layout.item_tweet, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    // This is called for a majority of the time to fill the recycler view
    // Passes a position and previously cached viewHolder to repopulate the data
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);
        // populate the views according to this data
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvDate.setText(getRelativeTimeAgo(tweet.createdAt));
        String screenName = "@" + tweet.user.screenName;
        viewHolder.tvScreenName.setText(screenName);
        viewHolder.tvRetweet.setText(tweet.reTweet_count);
        viewHolder.tvFavorite.setText(tweet.favorite_count);

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size(); // Don't forget to set this to non-zero value!
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvDate;
        public TextView tvScreenName;
        public TextView tvRetweet;
        public TextView tvFavorite;

        public ViewHolder (View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvRetweet = (TextView) itemView.findViewById(R.id.tvRetweet);
            tvFavorite = (TextView) itemView.findViewById(R.id.tvFavorite);
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    // This function uses the "created_at" variable of the tweet class
    // and returns a string with the time since it was created
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            if (relativeDate.contains("hr")) {
                return shorten(relativeDate, 'h');
            } else if (relativeDate.contains("sec")) {
                return shorten(relativeDate, 's');
            } else if (relativeDate.contains("min")) {
                return shorten(relativeDate, 'm');
            } else if (relativeDate.contains("day")) {
                return shorten(relativeDate, 'd');
            } else if (relativeDate.contains("y")) {
                return shorten(relativeDate, 'y');
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // Removes the space between the number and string
    // i.e. '4 min. ago' --> '4min. ago'
    private static String charRemoveAt(String str, int p) {
        return str.substring(0,p-1) + str.substring(p);
    }

    // Removes anything after the first char
    // (i.e. '4min. ago' --> '4m'
    private static String shorten(String str, char ch) {
        str = charRemoveAt(str, str.indexOf(ch));
        return str.substring(0,str.indexOf(ch)+1);
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}