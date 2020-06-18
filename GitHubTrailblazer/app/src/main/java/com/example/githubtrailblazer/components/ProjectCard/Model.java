package com.example.githubtrailblazer.components.ProjectCard;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Model class
 */
public class Model {
    ArrayList<View> views = new ArrayList<>();
    ProjectCard.Data data;

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
     * Update all views
     * @return this model instance
     */
    private void updateViews() {
        Iterator it = views.iterator();
        while (it.hasNext()) ((View)it.next()).update(data);
    }
}
