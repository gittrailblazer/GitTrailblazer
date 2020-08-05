package com.example.githubtrailblazer.components.repocard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.RepoDetailActivity;
import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.data.Rating;
import com.example.githubtrailblazer.data.RepoCardData;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

public class Model {
    private final RepoCardData data;
    private RepoCard repoCard;

    public Model(RepoCardData data) {
        this.data = data;
    }

    Model bindView(RepoCard repoCard) {
        this.repoCard = repoCard;
        repoCard.update(this);
        return this;
    }

    Model upvote() {
        if (data != null) {
            switch (data.valRated) {
                case -1:
                    data.valRated = 1;
                    data.rating += 2;
                    break;
                case 0:
                    data.valRated = 1;
                    data.rating += 1;
                    break;
                case 1:
                    data.valRated = 0;
                    data.rating -= 1;
                    break;
            }
            repoCard.update(this);
        }
        return this;
    }

    Model downvote() {
        if (data != null) {
            switch (data.valRated) {
                case 1:
                    data.valRated = -1;
                    data.rating -= 2;
                    break;
                case 0:
                    data.valRated = -1;
                    data.rating -= 1;
                    break;
                case -1:
                    data.valRated = 0;
                    data.rating += 1;
                    break;
            }
            repoCard.update(this);
        }
        return this;
    }

    Model star() {
        if (data != null) {
            if (!data.isStarred) {
                // if unstarred, star repo
                data.isStarred = true;
                data.stars += 1;
                new Connector.Query(Connector.QueryType.STAR_REPO, data.id)
                        .exec(new Connector.ISuccessCallback() {
                            @Override
                            public void handle(Object result) {
                                Log.d("GH_API_QUERY", "Successful query: managed to star repo " + data.url);
                            }
                        }, new Connector.IErrorCallback() {
                            @Override
                            public void error(String message) {
                                Log.e("GH_API_QUERY", "Failed query: " + message);
                            }
                        });
            } else {
                // if starred, unstar repo
                data.isStarred = false;
                data.stars -= 1;
                new Connector.Query(Connector.QueryType.UNSTAR_REPO, data.id)
                        .exec(new Connector.ISuccessCallback() {
                            @Override
                            public void handle(Object result) {
                                Log.d("GH_API_QUERY", "Successful query: managed to unstar repo " + data.url);
                            }
                        }, new Connector.IErrorCallback() {
                            @Override
                            public void error(String message) {
                                Log.e("GH_API_QUERY", "Failed query: " + message);
                            }
                        });
            }
            repoCard.update(this);
        }
        return this;
    }

    Model fork() {
        Context context = repoCard.getContext();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(data.url + "/fork"));
        context.startActivity(i);
        return this;
    }

    Model showActions() {
        repoCard.showActions();
        return this;
    }

    String getProfilePicUrl() {
        return data.profilePicUrl == null || data.profilePicUrl.isEmpty() ? null : data.profilePicUrl;
    }

    String getService() {
        return data.service + ":";
    }

    String getName() {
        return data.name;
    }

    String getLanguage() {
        return data.language == null || data.language.isEmpty() ? null : data.language;
    }

    String getDescription() {
        return data.description == null || data.description.isEmpty() ? null : data.description;
    }

    String getRatings() {
        return Helpers.formatCount(data.rating);
    }

    String getComments() {
        return Helpers.formatCount(data.comments);
    }

    String getStars() {
        return Helpers.formatCount(data.stars);
    }

    String getForks() {
        return Helpers.formatCount(data.forks);
    }

    Rating getRating() {
        return Rating.from(data.valRated);
    }

    Boolean isCommented() {
        return data.isCommented;
    }

    Boolean isStarred() {
        return data.isStarred;
    }

    Boolean isForked() {
        return data.isForked;
    }

    Model openInBrowser(Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(data.url));
        context.startActivity(i);
        return this;
    }

    Model share(Context context) {
        // repo details to be shared
        String details = "Repo Name: " + data.name +
                "\n\nRepo Language: " + data.language +
                "\n\nRepo Description: " + data.description +
                "\n\nWant to see more? Checkout Git Trailblazer at the Google Play Store!";

        // share the repo details
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, details);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
        return this;
    }

    Model showDetails(Context context) {
        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
        return this;
    }
}
