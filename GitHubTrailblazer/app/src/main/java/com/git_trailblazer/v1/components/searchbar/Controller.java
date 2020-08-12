package com.git_trailblazer.v1.components.searchbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Controller class
 */
public class Controller implements TextWatcher, TextView.OnEditorActionListener, View.OnClickListener {
    final private Model model;

    Controller(Model model) {
        this.model = model;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // DO NOTHING
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        model.parse(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // DO NOTHING
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            model.search(v.getText());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        ((LinearLayout)v.getParent()).removeView(v);
        model.removeTag(((TextView)v).getText().toString());
    }
}
