package com.example.githubtrailblazer.ui.feed;

import android.util.Log;
import android.widget.TextView;
import androidx.lifecycle.ViewModel;
import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.connector.RepoFeedData;

import java.util.*;

/**
 * FeedViewModel class
 */
public class FeedViewModel extends ViewModel {
    private IQueryResponseCB queryResponseCallback;
    private ITagAddedCB tagAddedCallback;
    private ITagRemovedCB tagRemovedCallback;
    private HashMap<String, Boolean> newTags = new HashMap<>();
    private HashMap<String, Boolean> oldTags = new HashMap<>();
    SortByOptions sortByOptions = SortByOptions.Best_Match;

    /**
     * The query response sorting + ordering options
     */
    public enum SortByOptions {
        Best_Match("Best Match"),
        Most_Stars("Most Stars"),
        Fewest_Stars("Fewest Stars"),
        Most_Forks("Most Forks"),
        Fewest_Forks("Fewest Forks"),
        Recently_Updated("Recently Updated"),
        Least_Recently_Updated("Least Recently Updated");

        private String text;

        SortByOptions(String s) {
            this.text = s;
        }

        public static Optional<SortByOptions> fromString(String text) {
            return Arrays.stream(values())
                    .filter(sbo -> sbo.text.equalsIgnoreCase(text))
                    .findFirst();
        }
    }

    /**
     * Query response callback interface
     */
    interface IQueryResponseCB {
        /**
         * Execute the query response callback
         * @param data - the query data
         * @param isNewQuery - if the query was a new query
         */
        void exec(RepoFeedData data, boolean isNewQuery);
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
     * Tag removed callback interface
     */
    interface ITagRemovedCB {
        /**
         * Execute the tag removed callback
         * @param tagView - the view of the tag being removed
         * @param isNewTag - if the tag was previously searched
         */
        void exec(TextView tagView, boolean isNewTag);
    }

    /**
     * Refresh the feed by re-executing previous query
     * @return
     */
    FeedViewModel execRefresh() {
        performQuery(true);
        return this;
    }

    /**
     * Execute a new query
     * @return this instance
     */
    FeedViewModel execQuery() {
        oldTags.putAll(newTags);
        newTags.clear();
        performQuery(true);
        return this;
    }

    /**
     * Perform feed query
     * @param isNewQuery - if the query is new one
     */
    private void performQuery(boolean isNewQuery) {
        StringBuilder sb = new StringBuilder();
        boolean isEmpty = true;

        // build tag(s) portion of query
        Iterator it = oldTags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String topic = (String)pair.getKey();

            if (!isEmpty) {
                sb.append(" ");
            } else {
                isEmpty = false;
            }

            sb.append("topic:");
            sb.append(topic);
        }

        // build sort and ordering portion of query
        if (sortByOptions != SortByOptions.Best_Match && !isEmpty) sb.append(" ");
        switch (sortByOptions) {
            case Recently_Updated:
                sb.append("sort:updated");
                break;
            case Most_Stars:
                sb.append("sort:stars");
                break;
            case Most_Forks:
                sb.append("sort:forks");
                break;
            case Fewest_Stars:
                sb.append("sort:stars-asc");
                break;
            case Fewest_Forks:
                sb.append("sort:forks-asc");
                break;
            case Least_Recently_Updated:
                sb.append("sort:updated-asc");
                break;
        }

        // perform the query
        new Connector.Query(Connector.QueryType.REPO_FEED, sb.toString())
            .exec(new Connector.ISuccessCallback() {
                @Override
                public void handle(Object result) {
                    RepoFeedData data = (RepoFeedData) result;
                    queryResponseCallback.exec(data, isNewQuery);
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
     * @param tagView - the tag view to be removed
     * @param isNewTag - whether the tag hasn't been searched yet
     * @return if it was successfully removed
     */
    boolean removeTag(TextView tagView, boolean isNewTag) {
        // get tag
        String tag = tagView.getText().toString();

        // try to delete it, returning false if did not actually exist
        Boolean deleted = isNewTag ? newTags.remove(tag) : oldTags.remove(tag);
        if (deleted == null) return false;

        // exec callback
        if (tagRemovedCallback != null) tagRemovedCallback.exec(tagView, isNewTag);
        return true;
    }

    /**
     * Add a new tag
     * @param tag - the tag
     * @return if it was successfully added
     */
    boolean addTag(String tag) {
        // ensure tag is non-empty
        if (tag.isEmpty()) return false;

        // ensure tag doesn't exist in new tags
        Boolean match = oldTags.get(tag);
        if (match != null) return false;

        // ensure tag doesn't exits in old tags
        match = newTags.get(tag);
        if (match != null) return false;

        // tag does not yet exist, add it
        newTags.put(tag, true);

        // exec callbacks
        if (tagAddedCallback != null) tagAddedCallback.exec(tag);
        return true;
    }

    /**
     * Set the sort by option
     * @param sortByOptions - the sort by option
     * @return if a change occurred
     */
    boolean setSortBy(SortByOptions sortByOptions) {
        boolean isChanged = (sortByOptions != this.sortByOptions);
        if (isChanged) this.sortByOptions = sortByOptions;
        return isChanged;
    }

    /**
     * Set on query response callback
     * @param callback - the callback
     * @return this instance
     */
    FeedViewModel setOnQueryResponseCB(IQueryResponseCB callback) {
        queryResponseCallback = callback;
        return this;
    }

    /**
     * Set on tag added callback
     * @param callback - the callback
     * @return this instance
     */
    FeedViewModel setOnTagAddedCB(ITagAddedCB callback) {
        tagAddedCallback = callback;
        return this;
    }

    /**
     * Set on tag removed callback
     * @param callback - the callback
     * @return this instance
     */
    FeedViewModel setOnTagRemovedCB(ITagRemovedCB callback) {
        tagRemovedCallback = callback;
        return this;
    }
}