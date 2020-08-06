package com.example.githubtrailblazer.components.repocard;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;
import com.example.githubtrailblazer.R;

class Controller implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private final Model model;

    Controller(Model model) {
        this.model = model;
    }

    @Override
    public void onClick(View v) {
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
                model.showActions();
                break;
            default:
                model.showDetails(v.getContext());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actions_item_share:
                // share repo details via Android Sharesheet when 'Share' item is selected
                model.share();
                return true;
            case R.id.actions_item_openInGithub:
                // open repo in GitHub when 'Open in GitHub' item is selected
                model.openInBrowser();
                return true;
//            case R.id.actions_item_stopSeeingThis:
//                // TODO: remove repo from feed when 'Stop seeing this' item is selected
            default:
                return false;
        }
    }
}
