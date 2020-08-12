package com.git_trailblazer.v1.components.issuecard;

import android.view.View;

class Controller implements View.OnClickListener {
    private final Model model;

    Controller(Model model) {
        this.model = model;
    }

    @Override
    public void onClick(View v) {
        model.openInBrowser(v.getContext());
    }
}
