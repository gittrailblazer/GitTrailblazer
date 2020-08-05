package com.example.githubtrailblazer.ui.issues;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.connector.IssueFeedData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * IssueFeedViewModel class
 */
public class IssueFeedViewModel extends ViewModel {
    private IQueryResponseCB queryResponseCallback;
    private ITagAddedCB tagAddedCallback;
    private HashMap<String, Boolean> tagExistanceMap = new HashMap<>();
    IssueFeedData.SortOption sortOption = IssueFeedData.SortOption.NEWEST;
//    FilterOption filterOption = FilterOption.EXPLORE;

    /**
     * The query response filter options
     */
//    public enum FilterOption {
//        EXPLORE,
//        STARRED,
//        FOLLOWING,
//        CONTRIBUTED
//    }

    /**
     * Query response callback interface
     */
    interface IQueryResponseCB {
        /**
         * Execute the query response callback
         * @param data - the query data
         */
        void exec(IssueFeedData data);
    }

    /**
     * Tag added callback interface
     */
    interface ITagAddedCB {
        /**
         * Execute the tag added callback
         * @param tag - the tag that was added
         */
        void exec(String tag);
    }

    /**
     * Execute a new query
     * @return this instance
     */
    IssueFeedViewModel execQuery() {
        performQuery(true);
        return this;
    }

    /**
     * Execute a new query
     * @return this instance
     */
    IssueFeedViewModel loadMore() {
        performQuery(false);
        return this;
    }

    /**
     * Perform feed query
     */
    private void performQuery(boolean isNewQuery) {
        // TODO: use isNewQuery to do pagination

        StringBuilder sb = new StringBuilder();
        boolean isEmpty = true;

        // build tag(s) portion of query
        Iterator it = tagExistanceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String topic = (String)pair.getKey();

            if (!isEmpty) {
                sb.append(" ");
            } else {
                isEmpty = false;
            }

            // TODO: Fix this!! Issue #59
            //            sb.append("topic:");
            sb.append(topic);
        }

        // perform the query
        new Connector.Query(Connector.QueryType.ISSUE_FEED, sortOption, sb.toString())
                .exec(new Connector.ISuccessCallback() {
                    @Override
                    public void handle(Object result) {
                        IssueFeedData data = (IssueFeedData) result;
                        queryResponseCallback.exec(data);
                    }
                }, new Connector.IErrorCallback() {
                    @Override
                    public void error(String message) {
                        Log.e("GH_API_QUERY", "Failed query: " + message);
                    }
                });
    }

    /**
     * Remove a tag
     * @param tag - the tag to be removed
     */
    ViewModel removeTag(String tag) {
        tagExistanceMap.remove(tag);
        return this;
    }

    /**
     * Add new tags
     * @param tags - the tags
     */
    IssueFeedViewModel addTags(String[] tags) {
        for (String tag : tags) {
            tagExistanceMap.put(tag, true);
            tagAddedCallback.exec(tag);
        }
        return this;
    }

    /**
     * Check if a tag already exists
     * @param tag - the tag to check for
     * @return if it exists
     */
    Boolean tagExists(String tag) {
        return tagExistanceMap.get(tag);
    }

    /**
     * Set the sort by option
     * @param sortOption - the sort by option
     * @return if a change occurred
     */
    boolean setSort(IssueFeedData.SortOption sortOption) {
        boolean isChanged = (sortOption != this.sortOption);
        if (isChanged) this.sortOption = sortOption;
        return isChanged;
    }

    /**
     * Set the filter by option
     * @param filterOption - the filter by option
     * @return if a change occurred
     */
//    boolean setFilter(FilterOption filterOption) {
//        boolean isChanged = (filterOption != this.filterOption);
//        if (isChanged) this.filterOption = filterOption;
//        return isChanged;
//    }

    /**
     * Set on query response callback
     * @param callback - the callback
     * @return this instance
     */
    IssueFeedViewModel setOnQueryResponseCB(IQueryResponseCB callback) {
        queryResponseCallback = callback;
        return this;
    }

    /**
     * Set on tag added callback
     * @param callback - the callback
     * @return this instance
     */
    IssueFeedViewModel setOnTagAddedCB(ITagAddedCB callback) {
        tagAddedCallback = callback;
        return this;
    }


}