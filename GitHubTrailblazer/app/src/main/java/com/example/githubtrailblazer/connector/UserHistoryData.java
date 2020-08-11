package com.example.githubtrailblazer.connector;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.FlagMaster;
import com.example.githubtrailblazer.data.RepoCardData;
import com.example.githubtrailblazer.github.GhRepoFeedQuery;
import com.example.githubtrailblazer.github.GhStarredRepoFeedQuery;
import com.example.githubtrailblazer.github.GhUserHistoryQuery;
import com.example.githubtrailblazer.gitlab.GlRepoFeedQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserHistoryData {
    // properties accessible in success callback
    public ArrayList<String> suggestedTopics;

    // properties used during construction
    private final Connector.ISuccessCallback successCallback;
    private final Connector.IErrorCallback errorCallback;

    /**
     * Retrieve user's history by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     * UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     *
     * @param queryParams     - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback   - the error callback (may be NULL)
     */
    public UserHistoryData(@NotNull Connector.QueryParams queryParams,
                           Connector.ISuccessCallback successCallback,
                           Connector.IErrorCallback errorCallback) throws Exception {
        final UserHistoryData _instance = this;

        // save callbacks
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        // parse arg
        final String userLogin = (String) queryParams.next();

        // GitHub query
        if(FlagMaster.getInstance().getGHFlag()) {
            Connector.getInstance().getGHClient().query(GhUserHistoryQuery.builder().userLogin(userLogin).build())
                    .enqueue(new ApolloCall.Callback<GhUserHistoryQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GhUserHistoryQuery.Data> response) {
                            suggestedTopics = new ArrayList<String>();

                            GhUserHistoryQuery.Data data = response.getData();
                            if (data != null) {
                                GhUserHistoryQuery.User user = data.user();

                                assert user != null;
                                GhUserHistoryQuery.Repositories repos = user.repositories();
                                GhUserHistoryQuery.RepositoriesContributedTo reposContributedTo = user.repositoriesContributedTo();
                                GhUserHistoryQuery.StarredRepositories starredRepos = user.starredRepositories();

                                List<GhUserHistoryQuery.Node> reposNodes = repos.nodes();
                                if (reposNodes != null) {
                                    for (GhUserHistoryQuery.Node reposNode : reposNodes) {
                                        GhUserHistoryQuery.PrimaryLanguage repoLanguage = reposNode.primaryLanguage();
                                        if (repoLanguage != null) {
                                            String repoLanguageString = repoLanguage.name().toLowerCase();
                                            if (!suggestedTopics.contains(repoLanguageString))
                                                suggestedTopics.add(repoLanguageString);
                                        }
                                        List<GhUserHistoryQuery.Node1> repoTopics = reposNode.repositoryTopics().nodes();
                                        if (repoTopics != null) {
                                            for (GhUserHistoryQuery.Node1 repoTopic : repoTopics) {
                                                String topicName = repoTopic.topic().name().toLowerCase();
                                                // TODO: Allow large strings by updating UI to automatically change font size
                                                if ((!suggestedTopics.contains(topicName)) && (topicName.length() <= 12))
                                                    suggestedTopics.add(topicName);
                                            }
                                        }
                                    }
                                }

                                List<GhUserHistoryQuery.Node2> reposCTNodes = reposContributedTo.nodes();
                                if (reposCTNodes != null) {
                                    for (GhUserHistoryQuery.Node2 reposCTNode : reposCTNodes) {
                                        GhUserHistoryQuery.PrimaryLanguage1 repoLanguage = reposCTNode.primaryLanguage();
                                        if (repoLanguage != null) {
                                            String repoLanguageString = repoLanguage.name().toLowerCase();
                                            if (!suggestedTopics.contains(repoLanguageString))
                                                suggestedTopics.add(repoLanguageString);
                                        }
                                        List<GhUserHistoryQuery.Node3> repoTopics = reposCTNode.repositoryTopics().nodes();
                                        if (repoTopics != null) {
                                            for (GhUserHistoryQuery.Node3 repoTopic : repoTopics) {
                                                String topicName = repoTopic.topic().name().toLowerCase();
                                                // TODO: Allow large strings by updating UI to automatically change font size
                                                if ((!suggestedTopics.contains(topicName)) && (topicName.length() <= 12))
                                                    suggestedTopics.add(topicName);
                                            }
                                        }
                                    }
                                }

                                List<GhUserHistoryQuery.Node4> starredReposNodes = starredRepos.nodes();
                                if (starredReposNodes != null) {
                                    for (GhUserHistoryQuery.Node4 starredReposNode : starredReposNodes) {
                                        GhUserHistoryQuery.PrimaryLanguage2 repoLanguage = starredReposNode.primaryLanguage();
                                        if (repoLanguage != null) {
                                            String repoLanguageString = repoLanguage.name().toLowerCase();
                                            if (!suggestedTopics.contains(repoLanguageString))
                                                suggestedTopics.add(repoLanguageString);
                                        }
                                        List<GhUserHistoryQuery.Node5> repoTopics = starredReposNode.repositoryTopics().nodes();
                                        if (repoTopics != null) {
                                            for (GhUserHistoryQuery.Node5 repoTopic : repoTopics) {
                                                String topicName = repoTopic.topic().name().toLowerCase();
                                                // TODO: Allow large strings by updating UI to automatically change font size
                                                if ((!suggestedTopics.contains(topicName)) && (topicName.length() <= 12))
                                                    suggestedTopics.add(topicName);
                                            }
                                        }
                                    }
                                }

                                if (successCallback != null) successCallback.handle(_instance);
                            } else if (errorCallback != null) {
                                errorCallback.error("Failed query: data is NULL");
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("ERROR", "error: " + e.toString());
                            if (errorCallback != null)
                                errorCallback.error("Failed query: " + e.getMessage());
                        }
                    });
        }

        // GitLab query
        if(FlagMaster.getInstance().getGLFlag()) {
            // TODO: implement query to get user history data for GitLab repos
        }
    }
}
