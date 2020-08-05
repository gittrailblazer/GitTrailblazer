package com.example.githubtrailblazer.components.issuecard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.data.IssueCardData;
import com.example.githubtrailblazer.data.Rating;

public class Model {
    private IssueCard view;
    private final IssueCardData data;

    public Model(IssueCardData data) {
        this.data = data;
    }

    Model bindView(IssueCard view) {
        this.view = view;
        view.update(this);
        return this;
    }

    public String getTitle() {
        return (data.title == null) ? "" : data.title;
    }

    public String getNumber() {
        return (data.number == null) ? "#0" : ("#" + data.number.toString());
    }

    public String getCreatedAt() {
        if (data.createdAt == null) return "now";
        String createdAtFormatted = Helpers.formatElapsedTime(data.createdAt);
        return (createdAtFormatted == null) ? "now" : createdAtFormatted;
    }

    String getService() {
        return data.service + ":";
    }

    public String getRepoName() {
        return data.repoData.name;
    }

    String getLanguage() {
        return data.repoData.language == null || data.repoData.language.isEmpty() ? null : data.repoData.language;
    }

    String getDescription() {
        return data.description == null || data.description.isEmpty() ? null : data.description;
    }

    String getRatings() {
        return Helpers.formatCount(data.repoData.rating);
    }

    String getComments() {
        return Helpers.formatCount(data.repoData.comments);
    }

    String getStars() {
        return Helpers.formatCount(data.repoData.stars);
    }

    String getForks() {
        return Helpers.formatCount(data.repoData.forks);
    }

    Rating getRating() {
        return Rating.from(data.repoData.valRated);
    }

    Boolean isCommented() {
        return data.repoData.isCommented;
    }

    Boolean isStarred() {
        return data.repoData.isStarred;
    }

    Boolean isForked() {
        return data.repoData.isForked;
    }

    Model openInBrowser(Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(data.url));
        context.startActivity(i);
        return this;
    }
}
