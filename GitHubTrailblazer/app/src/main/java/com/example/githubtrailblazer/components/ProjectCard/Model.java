package com.example.githubtrailblazer.components.ProjectCard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;

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
public class Model implements PopupMenu.OnMenuItemClickListener {
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
     * Share repo details via Android Sharesheet
     * @param v - the current view
     * @return this model instance
     */
    Model shareRepo(android.view.View v) {
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
        v.getContext().startActivity(shareIntent);
        return this;
    }

    /**
     * Display actions dropdown menu
     * @param v - the current view
     */
    public void showActionsList(android.view.View v) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.actions_menu);
        popupMenu.show();
    }

    /**
     * Handle actions dropdown menu items
     * @param item - the menu option
     * @return the GitHub color
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actions_item_save:
                // TODO: save repo when 'Save' item is selected
                return true;
            case R.id.actions_item_share:
                // share repo details via Android Sharesheet when 'Share' item is selected
                shareRepo(views.get(0));
                return true;
            case R.id.actions_item_openInGithub:
                // TODO: open repo in GitHub when 'Open in GitHub' item is selected
            case R.id.actions_item_stopSeeingThis:
                // TODO: remove repo from feed when 'Stop seeing this' item is selected
            default:
                return false;
        }
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
