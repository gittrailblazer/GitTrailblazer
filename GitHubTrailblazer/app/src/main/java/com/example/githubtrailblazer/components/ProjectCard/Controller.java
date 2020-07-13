package com.example.githubtrailblazer.components.ProjectCard;

import android.content.Intent;

import com.example.githubtrailblazer.R;

/**
 * Controller class
 */
public class Controller implements View.OnClickListener {
    Model model;

    /**
     * Create controller
     * @param model - the model
     */
    Controller(Model model) {
        this.model = model;
    }

    /**
     * OnClick handler
     * @param v - the view element
     */
    @Override
    public void onClick(android.view.View v) {
        switch (v.getId()) {
            case R.id.projectCard__btnUpvote:
                model.upvote();
                break;
            case R.id.projectCard__btnDownvote:
                model.downvote();
                break;
            case R.id.projectCard__btnStar:
                model.star();
                break;
            case R.id.projectCard__btnFork:
                model.fork();
                break;
            case R.id.projectCard__btnActions:
                model.showActionsList(v);
                break;
        }
    }
}
