package com.git_trailblazer.v1.components.issuecard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.git_trailblazer.v1.Helpers;
import com.git_trailblazer.v1.data.IssueCardData;
import com.git_trailblazer.v1.data.Rating;

public class Model {
    private IssueCard view;
    private final IssueCardData data;

    public Model(IssueCardData data) {
        this.data = data;
    }

    /**
     * Bind view to this model
     * @param view - the view
     * @return this instance
     */
    Model bindView(IssueCard view) {
        this.view = view;
        view.update(this);
        return this;
    }

    /**
     * Get issue title
     * @return the title
     */
    public String getTitle() {
        return (data.title == null) ? "" : data.title;
    }

    /**
     * Get issue number
     * @return the number
     */
    public String getNumber() {
        return (data.number == null) ? "#0" : ("#" + data.number.toString());
    }

    /**
     * Get issue created at date formatted
     * @return  the created at date formatted
     */
    public String getCreatedAt() {
        if (data.createdAt == null) return "now";
        String createdAtFormatted = Helpers.formatElapsedTime(data.createdAt);
        return (createdAtFormatted == null) ? "now" : createdAtFormatted;
    }

    /**
     * Get issue service
     * @return the service provide
     */
    String getService() {
        return data.service + ":";
    }

    /**
     * Get name of issue's repository
     * @return the repository name
     */
    public String getRepoName() {
        return data.repoData.name;
    }

    /**
     * Get language of issue's repository
     * @return the repository language
     */
    String getLanguage() {
        return data.repoData.language == null || data.repoData.language.isEmpty() ? null : data.repoData.language;
    }

    /**
     * Get the issue description
     * @return the issue description
     */
    String getDescription() {
        return data.description == null || data.description.isEmpty() ? null : data.description;
    }

    /**
     * Get the formatted rating count of the issue's repository
     * @return rating count formatted
     */
    String getRatings() {
        return Helpers.formatCount(data.repoData.rating);
    }

    /**
     * Get the formatted comment count of the issue's repository
     * @return comment count formatted
     */
    String getComments() {
        return Helpers.formatCount(data.repoData.comments);
    }

    /**
     * Get the formatted star count of the issue's repository
     * @return star count formatted
     */
    String getStars() {
        return Helpers.formatCount(data.repoData.stars);
    }

    /**
     * Get the formatted fork count of the issue's repository
     * @return fork count formatted
     */
    String getForks() {
        return Helpers.formatCount(data.repoData.forks);
    }

    /**
     * Get the user rating of the issue's repository
     * @return the user's rating
     */
    Rating getRating() {
        return Rating.from(data.repoData.valRated);
    }

    /**
     * Get if the user has commented on the issue's repository
     * @return if user has commented
     */
    Boolean isCommented() {
        return data.repoData.isCommented;
    }

    /**
     * Get if the user has starred the issue's repository
     * @return if user has starred
     */
    Boolean isStarred() {
        return data.repoData.isStarred;
    }

    /**
     * Get if the user has forked the issue's repository
     * @return if user has forked
     */
    Boolean isForked() {
        return data.repoData.isForked;
    }

    /**
     * Opens the issue in the browser
     * @param context - the context
     * @return this instance
     */
    Model openInBrowser(Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(data.url));
        context.startActivity(i);
        return this;
    }
}
