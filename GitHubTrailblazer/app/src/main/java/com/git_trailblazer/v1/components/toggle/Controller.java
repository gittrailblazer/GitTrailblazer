package com.git_trailblazer.v1.components.toggle;

import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;

/**
 * Controller class
 */
public class Controller implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private final Model model;

    Controller(Model model) {
        this.model = model;
    }

    /**
     * Handle toggle button click
     * @param v - the view
     */
    @Override
    public void onClick(View v) {
        model.showOptions();
    }

    /**
     * Handle option selected
     * @param item - the option
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        model.select(item.getItemId());
        return false;
    }
}
