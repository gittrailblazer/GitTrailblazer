package com.git_trailblazer.v1.components.searchbar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.git_trailblazer.v1.R;

/**
 * SearchBar class
 */
public class SearchBar extends LinearLayout {
    private Controller controller;
    private Model model;
    private IOnSearchCB searchCB;

    /**
     * On search callback interface
     */
    public interface IOnSearchCB {
        void exec(String[] tags);
    }

    /**
     * On tag check callback interface
     */
    public interface IOnTagCheckCB {
        Boolean check(String tags);
    }

    public SearchBar(Context context) {
        super(context);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initialize the search bar with a tag delimiter
     * @param delimiter
     * @return
     */
    public SearchBar initialize(String delimiter) {
        this.model = new Model();
        model.setSearchContainer(this);
        model.setDelimitter(delimiter);
        ((SearchBarEditText)findViewById(R.id.searchbar_edit)).bindModel(model);
        this.controller = new Controller(model);
        return this;
    }

    /**
     * Set the search bar placeholder
     * @param resStrId - the placeholder string resource id
     * @return this instance
     */
    public SearchBar setPlaceholder(int resStrId) {
        ((SearchBarEditText)findViewById(R.id.searchbar_edit)).setHint(resStrId);
        return this;
    }

    /**
     * Set the on search callback
     * @param callback - the callback
     * @return this instance
     */
    public SearchBar setOnSearch(IOnSearchCB callback) {
        this.searchCB = callback;
        return this;
    }

    /**
     * Set the on tag check callback
     * @param callback - the callback
     * @return this instance
     */
    public SearchBar setOnTagCheck(IOnTagCheckCB callback) {
        this.model.setTagCheckCB(callback);
        return this;
    }

    /**
     * Perform the search (ie. empty the tag container, pass the tags to the search callback in the order they were added)
     * @return this instance
     */
    SearchBar search() {
        LinearLayout tagContainer = findViewById(R.id.newtag_container);
        String[] tags = new String[tagContainer.getChildCount()];
        for (int i = tags.length - 1; i >= 0; --i) {
            tags[i] = ((TextView)tagContainer.getChildAt(i)).getText().toString();
        }
        tagContainer.removeAllViews();
        searchCB.exec(tags);
        return this;
    }

    /**
     * Add a pending tag to the search
     * @param tag - the tag to be added
     * @return this instance
     */
    SearchBar addTag(String tag) {
        Log.e("DEBUG", tag);
        TextView tagView = (TextView) inflate(getContext(), R.layout.tag, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelOffset(R.dimen.app_project_margin_sm);
        lp.setMargins(0, 0, margin, 0);
        tagView.setLayoutParams(lp);
        tagView.setText(tag);
        tagView.setOnClickListener(controller);
        ((LinearLayout)findViewById(R.id.newtag_container)).addView(tagView, 0);
        return this;
    }
}
