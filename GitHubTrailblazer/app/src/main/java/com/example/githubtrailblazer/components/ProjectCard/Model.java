package com.example.githubtrailblazer.components.ProjectCard;

import android.content.Context;
import android.graphics.Color;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Model class
 */
public class Model {
    ArrayList<View> views = new ArrayList<>();
    ProjectCard.Data data;
    static HashMap<String, String> ghColors;

    Model(Context context) {
        // init singleton color map for project cards
        if (Model.ghColors == null) {
            Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
            ghColors = (HashMap<String, String>) Helpers.fromRawJSON(context, R.raw.github_lang_colors, mapType);
        }
    }

    /**
     * Add a view
     * @param view - the view to be added
     * @return this model instance
     */
    Model addView(View view) {
        views.add(view);
        return this;
    }

    /**
     * Set the model's data
     * @param data - the project card data
     * @return this model instance
     */
    Model setData(ProjectCard.Data data) {
        this.data = data;
        updateViews();
        return this;
    }

    /**
     * Perform upvote
     * @return this model instance
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
            updateViews();
        }
        return this;
    }

    /**
     * Perform downvote
     * @return this model instance
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
            updateViews();
        }
        return this;
    }

    /**
     * Perform star
     * @return this model instance
     */
    Model star() {
        if (data != null) {
            data.isStarred = !data.isStarred;
            data.stars += data.isStarred ? 1 : -1;
            updateViews();
        }
        return this;
    }

    /**
     * Perform fork
     * @return this model instance
     */
    Model fork() {
        if (data != null) {
            data.isForked = !data.isForked;
            data.forks += data.isForked ? 1 : -1;
            updateViews();
        }
        return this;
    }

    /**
     * Map language to GitHub color
     * @param language - the language
     * @return the GitHub color
     */
    int getGitHubColor(String language) {
        String hex = ghColors.get(language);
        return hex == null ? R.color.primary1 : Color.parseColor(hex);
    }

    /**
     * Update all views
     * @return this model instance
     */
    private void updateViews() {
        Iterator it = views.iterator();
        while (it.hasNext()) ((View)it.next()).update(data);
    }
}
