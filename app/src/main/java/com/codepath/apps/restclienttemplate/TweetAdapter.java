package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;
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

        // Edits image view if tweet has been favorited/retweeted by user
        if (tweet.favorited) {
            viewHolder.ivFavorite.setImageResource(R.drawable.ic_vector_heart);
            viewHolder.tvFavorite.setTextColor(context.getResources().getColor(R.color.inline_action_like));
        } else {
            viewHolder.ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
            viewHolder.tvFavorite.setTextColor(context.getResources().getColor(R.color.icon_default));
        }
        if (tweet.retweeted) {
            viewHolder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            viewHolder.tvRetweet.setTextColor(context.getResources().getColor(R.color.medium_green));
        } else {
            viewHolder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            viewHolder.tvRetweet.setTextColor(context.getResources().getColor(R.color.icon_default));
        }

        //Add Listeners to each object of the tweet that needs one
        addListenersToTweet(viewHolder);
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
        public ImageView ivRetweet;
        public ImageView ivFavorite;
        public ImageView ivReply;
        public ImageView ivShare;

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
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            ivShare = (ImageView) itemView.findViewById(R.id.ivShare);
        }
    }

    // Adds listeners to each icon that needs one
    public void addListenersToTweet(final ViewHolder viewHolder) {

        // Reply Button
        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Reply to a tweet", Toast.LENGTH_LONG).show();
                int position = viewHolder.getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the tweet at the position, this won't work if the class is static
                    Tweet tweet = mTweets.get(position);
                    Intent intent = new Intent(context, ReplyActivity.class);
                    // serialize the tweet using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    context.startActivity(intent);
                }
            }
        });

        // Profile Image
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked on a profile image", Toast.LENGTH_LONG).show();
                int position = viewHolder.getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the tweet at the position, this won't work if the class is static
                    Tweet tweet = mTweets.get(position);
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    // serialize the tweet using parceler, could have passed user
                    intent.putExtra("user", Parcels.wrap(tweet.user));
                    context.startActivity(intent);
                }
            }
        });

        // User Name
        viewHolder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on a profile user name", Toast.LENGTH_LONG).show();
                int position = viewHolder.getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the tweet at the position, this won't work if the class is static
                    Tweet tweet = mTweets.get(position);
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    // serialize the tweet using parceler, could have passed user
                    intent.putExtra("user", Parcels.wrap(tweet.user));
                    context.startActivity(intent);
                }
            }
        });

        // Screen Name
        viewHolder.tvScreenName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on a profile screen name", Toast.LENGTH_LONG).show();
                int position = viewHolder.getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    Tweet tweet = mTweets.get(position);
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("user", Parcels.wrap(tweet.user));
                    context.startActivity(intent);
                }
            }
        });

        // Like Image
        viewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on a like button", Toast.LENGTH_LONG).show();
            }
        });

        // Retweet Image
        viewHolder.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on a retweet button", Toast.LENGTH_LONG).show();
            }
        });

        // Share Image
        viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on a share button", Toast.LENGTH_LONG).show();
            }
        });
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
}
