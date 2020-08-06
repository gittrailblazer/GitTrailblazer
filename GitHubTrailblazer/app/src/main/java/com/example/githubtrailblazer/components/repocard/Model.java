package com.example.githubtrailblazer.components.repocard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.RepoDetailActivity;
import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.data.Rating;
import com.example.githubtrailblazer.data.RepoCardData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class
 */
public class Model {
    private final RepoCardData data;
    private RepoCard repoCard;

    public Model(RepoCardData data) {
        this.data = data;
    }

    /**
     * Bind view to this model
     * @param repoCard - the view
     * @return this model
     */
    Model bindView(RepoCard repoCard) {
        this.repoCard = repoCard;
        repoCard.update(this);
        return this;
    }

    /**
     * Upvote the repository
     * @return this model
     */
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

            UpdateFireStoreVotes(data.rating);
            repoCard.update(this);
        }
        return this;
    }

    /**
     * Downvote the repository
     * @return this model
     */
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

            UpdateFireStoreVotes(data.rating);
            repoCard.update(this);
        }
        return this;
    }

    /**
     * Update stored repository upvotes
     * @return this model
     */
    private void UpdateFireStoreVotes(int new_val)
    {
        FirebaseFirestore.getInstance().collection("RepoComments").whereEqualTo("RepoUrl", data.url).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty())
                {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("RepoComments").document(doc.getId());
                    docRef.update("Votes", new_val);
                }
                else
                {
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("RepoUrl", data.url);
                    doc.put("Votes", new_val);
                    doc.put("Comments", new ArrayList<String>());
                    FirebaseFirestore.getInstance().collection("RepoComments").add(doc);
                }

            }
        });
    }

    /**
     * Star the repository
     * @return this model
     */
    Model star() {
        if (data != null) {
            if (!data.isStarred) {
                // if unstarred, star repo
                data.isStarred = true;
                data.stars += 1;
                // if github, initiate an API query to star the repo
                if (data.service.equals(Connector.Service.GITHUB.shortName())) {
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
                }
            } else {
                // if starred, unstar repo
                data.isStarred = false;
                data.stars -= 1;
                // if github, initiate an API query to unstar the repo
                if (data.service.equals(Connector.Service.GITHUB.shortName())) {
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
            }
            repoCard.update(this);
        }
        return this;
    }

    /**
     * Fork the repository
     * @return this model
     */
    Model fork() {
        Context context = repoCard.getContext();
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (data.service.equals(Connector.Service.GITHUB.shortName())) {
            i.setData(Uri.parse(data.url + "/fork"));
        }
        if (data.service.equals(Connector.Service.GITLAB.shortName())) {
            i.setData(Uri.parse(data.url + "/-/forks/new"));
        }
        context.startActivity(i);
        return this;
    }

    /**
     * Trigger repository actions
     * @return this model
     */
    Model showActions() {
        repoCard.showActions();
        return this;
    }

    /**
     * Get repository owner profile pic url
     * @return the url
     */
    String getProfilePicUrl() {
        return data.profilePicUrl == null || data.profilePicUrl.isEmpty() ? null : data.profilePicUrl;
    }

    /**
     * Get repository service
     * @return the service provider
     */
    String getService() {
        return data.service + ":";
    }

    /**
     * Get repository name
     * @return the name
     */
    String getName() {
        return data.name;
    }

    /**
     * Get repository language
     * @return the language
     */
    String getLanguage() {
        return data.language == null || data.language.isEmpty() ? null : data.language;
    }

    /**
     * Get repository description
     * @return the description
     */
    String getDescription() {
        return data.description == null || data.description.isEmpty() ? null : data.description;
    }

    /**
     * Get repository formatted rating count
     * @return the rating count
     */
    String getRatings() {
        return Helpers.formatCount(data.rating);
    }

    /**
     * Get repository formatted rating count
     * @return the comment count
     */
    String getComments() {
        return Helpers.formatCount(data.comments);
    }

    /**
     * Get repository formatted star count
     * @return the star count
     */
    String getStars() {
        return Helpers.formatCount(data.stars);
    }

    /**
     * Get repository formatted fork count
     * @return the fork count
     */
    String getForks() {
        return Helpers.formatCount(data.forks);
    }

    /**
     * Get user's rating of repository
     * @return the rating
     */
    Rating getRating() {
        return Rating.from(data.valRated);
    }

    /**
     * Get if user has commented on repository
     * @return if user has commented
     */
    Boolean isCommented() {
        return data.isCommented;
    }

    /**
     * Get if user has starred repository
     * @return if user has starred
     */
    Boolean isStarred() {
        return data.isStarred;
    }

    /**
     * Get if user has forked repository
     * @return if user has forked
     */
    Boolean isForked() {
        return data.isForked;
    }

    /**
     * Open repository in browser
     * @param context - the context
     * @return this instance
     */
    Model openInBrowser(Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(data.url));
        context.startActivity(i);
        return this;
    }

    /**
     * Share the repository
     * @param context - the context
     * @return this instance
     */
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

    /**
     * Navigate to repository details
     * @param context - the context
     * @return this instance
     */
    Model showDetails(Context context) {
        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
        return this;
    }
}
