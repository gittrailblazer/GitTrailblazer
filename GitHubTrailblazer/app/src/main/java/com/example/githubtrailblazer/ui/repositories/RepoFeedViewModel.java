package com.example.githubtrailblazer.ui.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.connector.RepoFeedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

/**
 * RepoFeedViewModel class
 */
public class RepoFeedViewModel extends ViewModel {
    private static int maxRandomTopics = 3;
    private static String userId;
    private static ArrayList<String> preferredTopics;
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
    RepoFeedViewModel execQuery() {
        String query = getEnteredTagQuery();
        if (query.length() == 0)
            performRandomQuery(true);
        else
            performQuery(query,true);
        return this;
    }

    /**
     * Execute a new query
     * @return this instance
     */
    RepoFeedViewModel loadMore() {
        String query = getEnteredTagQuery();
        if (query.length() == 0)
            performRandomQuery(true);
        else
            performQuery(query,true);
        return this;
    }

    /**
     * Perform feed query
     */
    private void performQuery(String query, boolean isNewQuery) {
        // TODO: use isNewQuery to do pagination
        // perform the query
        new Connector.Query(Connector.QueryType.REPO_FEED, sortOption, filterOption, query)
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
     * Perform random feed query
     */
    private void performRandomQuery(boolean isNewQuery) {
        // TODO: use isNewQuery to do pagination
        // ensure that the user has not changed, otherwise retrieve topics
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!currentUserId.equals(userId)) {
            userId = currentUserId;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference ref = db.collection("UserData");
            ref.document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null)
                                preferredTopics = (ArrayList<String>) snapshot.get("Topics");
                            if (preferredTopics == null || snapshot == null)
                                preferredTopics = new ArrayList<>();
                            if (preferredTopics.size() == 0) {
                                preferredTopics.add("React");
                                preferredTopics.add("NodeJS");
                                preferredTopics.add("JavaScript");
                                preferredTopics.add("CSS");
                                preferredTopics.add("HTML");
                                preferredTopics.add("Lisp");
                                preferredTopics.add("Python3");
                                preferredTopics.add("20xx");
                                preferredTopics.add("clxx");
                                preferredTopics.add("cl-20xx");
                            }
                            performQuery(randomizeQuery(), isNewQuery);
                        }
                    });
        } else {
            performQuery(randomizeQuery(), isNewQuery);
        }
    }

    private String getEnteredTagQuery() {
        // build tag(s) portion of query
        StringBuilder sb = new StringBuilder();
        Iterator it = tagExistanceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String topic = (String) pair.getKey();
            if (sb.length() != 0) sb.append(" ");
            // TODO: Fix this!! Issue #59
            //            sb.append("topic:");
            sb.append(topic);
        }
        return sb.toString();
    }

    private String randomizeQuery() {
        StringBuilder sb = new StringBuilder();

        // the available topic indicies to choose from
        ArrayList<Integer> availableIndices = new ArrayList<>();
        for (int i = 0 ; i < preferredTopics.size(); ++i)
            availableIndices.add(i);

        // randomly pick number of topics to include, randomly choose indices of topics to include
        Random rand = new Random();
        for (int topicCount = (int)Math.floor(rand.nextInt(Math.min(maxRandomTopics, preferredTopics.size())) + 1); topicCount > 0; --topicCount) {
            if (sb.length() != 0) sb.append(" ");
            int index = (int)Math.floor(rand.nextInt(topicCount));
            String topic = preferredTopics.get(availableIndices.get(index));
            availableIndices.remove(index);
            sb.append(topic);
        }

        Log.e("DEBUG", "random query: " + sb.toString());

        return sb.toString();
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
    RepoFeedViewModel addTags(String[] tags) {
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
    RepoFeedViewModel setOnQueryResponseCB(IQueryResponseCB callback) {
        queryResponseCallback = callback;
        return this;
    }

    /**
     * Set on tag added callback
     * @param callback - the callback
     * @return this instance
     */
    RepoFeedViewModel setOnTagAddedCB(ITagAddedCB callback) {
        tagAddedCallback = callback;
        return this;
    }

}