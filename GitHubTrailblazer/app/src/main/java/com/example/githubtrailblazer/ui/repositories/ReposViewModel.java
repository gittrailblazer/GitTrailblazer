package com.example.githubtrailblazer.ui.repositories;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.connector.RepoFeedData;

import java.util.*;

/**
 * FeedViewModel class
 */
public class ReposViewModel extends ViewModel {
    private IQueryResponseCB queryResponseCallback;
    private ITagAddedCB tagAddedCallback;
    private HashMap<String, Boolean> tagExistanceMap = new HashMap<>();
    RepoFeedData.SortOption sortOption = RepoFeedData.SortOption.NEWEST;
    RepoFeedData.FilterOption filterOption = RepoFeedData.FilterOption.EXPLORE;

    /**
     * Query response callback interface
     */
    interface IQueryResponseCB {
        /**
         * Execute the query response callback
         * @param data - the query data
         */
        void exec(RepoFeedData data);
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
    ReposViewModel execQuery() {
        performQuery(true);
        return this;
    }

    /**
     * Execute a new query
     * @return this instance
     */
    ReposViewModel loadMore() {
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
            Map.Entry pair = (Map.Entry) it.next();
            String topic = (String) pair.getKey();

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
        new Connector.Query(Connector.QueryType.REPO_FEED, sortOption, filterOption, sb.toString())
            .exec(new Connector.ISuccessCallback() {
                @Override
                public void handle(Object result) {
                    RepoFeedData data = (RepoFeedData) result;
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
    ReposViewModel addTags(String[] tags) {
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
    boolean setSort(RepoFeedData.SortOption sortOption) {
        boolean isChanged = (sortOption != this.sortOption);
        if (isChanged) this.sortOption = sortOption;
        return isChanged;
    }

    /**
     * Set the filter by option
     * @param filterOption - the filter by option
     * @return if a change occurred
     */
    boolean setFilter(RepoFeedData.FilterOption filterOption) {
        boolean isChanged = (filterOption != this.filterOption);
        if (isChanged) this.filterOption = filterOption;
        return isChanged;
    }

    /**
     * Set on query response callback
     * @param callback - the callback
     * @return this instance
     */
    ReposViewModel setOnQueryResponseCB(IQueryResponseCB callback) {
        queryResponseCallback = callback;
        return this;
    }

    /**
     * Set on tag added callback
     * @param callback - the callback
     * @return this instance
     */
    ReposViewModel setOnTagAddedCB(ITagAddedCB callback) {
        tagAddedCallback = callback;
        return this;
    }

}