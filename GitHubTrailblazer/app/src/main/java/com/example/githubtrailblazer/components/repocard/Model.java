package com.example.githubtrailblazer.components.repocard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.RepoDetailActivity;
import com.example.githubtrailblazer.data.RepoCardData;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

public class Model {
    private final RepoCardData data;
    private RepoCard repoCard;
    private static HashMap<String, String> ghColors;

    public Model(RepoCardData data) {
        this.data = data;
    }

    Model bindView(RepoCard repoCard) {
        // setup singleton language color map if not defined
        if (ghColors == null) {
            Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
            ghColors = (HashMap<String, String>) Helpers.fromRawJSON(repoCard.getContext(), R.raw.github_lang_colors, mapType);
        }
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
            data.isStarred = !data.isStarred;
            data.stars += data.isStarred ? 1 : -1;
            repoCard.update(this);
        }
        return this;
    }

    Model fork() {
        if (data != null) {
            data.isForked = !data.isForked;
            data.forks += data.isForked ? 1 : -1;
            repoCard.update(this);
        }
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

    Integer getLanguageColor() {
        String hex = data.language == null ? null : ghColors.get(data.language);
        return hex == null ? R.color.primary1 : Color.parseColor(hex);
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

    RepoCardData.Rating getRating() {
        return RepoCardData.Rating.from(data.valRated);
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

    Model openInBrowser() {
        Context context = repoCard.getContext();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(data.url));
        context.startActivity(i);
        return this;
    }

    Model share() {
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
        repoCard.getContext().startActivity(shareIntent);
        return this;
    }

    Model showDetails() {
        Context context = repoCard.getContext();
        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
        return this;
    }
}