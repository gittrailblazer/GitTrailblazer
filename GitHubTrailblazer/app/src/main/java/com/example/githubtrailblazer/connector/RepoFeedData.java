package com.example.githubtrailblazer.connector;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.data.RepoCardData;
import com.example.githubtrailblazer.github.GhRepoFeedQuery;
import com.example.githubtrailblazer.gitlab.GlRepoFeedQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RepoFeedData {
    // Properties accessible in success callback
    public Boolean hasNextPage;
    public String endCursor;
    public RepoCardData[] repositories;

    /**
     * Create repo feed data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     * UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     *
     * @param queryParams     - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback   - the error callback (may be NULL)
     */
    public RepoFeedData(@NotNull Connector.QueryParams queryParams,
                        Connector.ISuccessCallback successCallback,
                        Connector.IErrorCallback errorCallback) {
        String searchString = (String)queryParams.next();
        final RepoFeedData _instance = this;

        Connector.ghclient.query(GhRepoFeedQuery.builder().searchString(searchString).build())
                .enqueue(new ApolloCall.Callback<GhRepoFeedQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GhRepoFeedQuery.Data> response) {
                        GhRepoFeedQuery.Data data = response.getData();
                        if (data != null) {
                            GhRepoFeedQuery.Search search = data.search();
                            GhRepoFeedQuery.PageInfo pageInfo = search.pageInfo();
                            hasNextPage = pageInfo.hasNextPage();
                            endCursor = pageInfo.endCursor();

                            List<GhRepoFeedQuery.Node> nodes = search.nodes();
                            if (nodes != null) {
                                repositories = new RepoCardData[nodes.size()];
                                for (int i = 0; i < repositories.length; ++i) {
                                    GhRepoFeedQuery.AsRepository repository = (GhRepoFeedQuery.AsRepository) nodes.get(i);
                                    String url = repository.url().toString();
                                    String name = repository.nameWithOwner();
                                    GhRepoFeedQuery.PrimaryLanguage _pl = repository.primaryLanguage();
                                    String language = (_pl == null) ? null : _pl.name();
                                    String description = repository.description();
                                    GhRepoFeedQuery.Owner _o = repository.owner();
                                    String profilePicUrl = (_o == null) ? null : _o.avatarUrl().toString();
                                    Integer rating = 0;             // TODO: implement ratings
                                    Integer valRated = 0;           // TODO: implement ratings
                                    Integer comments = 0;           // TODO: implement comments
                                    Boolean isCommented = false;    // TODO: implement comments
                                    GhRepoFeedQuery.Stargazers _sg = repository.stargazers();
                                    Integer stars = (_sg == null) ? null : _sg.totalCount();
                                    Boolean isStarred = repository.viewerHasStarred();
                                    Integer forks = repository.forkCount();
                                    GhRepoFeedQuery.Forks _f = repository.forks();
                                    Boolean isForked = (_f == null) ? false : _f.totalCount() > 0;
                                    repositories[i] = new RepoCardData(
                                            Connector.Service.GITHUB.shortName(), url, name, language, description,
                                            profilePicUrl, rating, valRated, comments, isCommented, stars, isStarred,
                                            forks, isForked
                                    );
                                }
                            } else {
                                repositories = new RepoCardData[]{};
                            }

                            if (successCallback != null) successCallback.handle(_instance);
                        } else if (errorCallback != null) {
                            errorCallback.error("Failed query: data is NULL");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        if (errorCallback != null)
                            errorCallback.error("Failed query: " + e.getMessage());
                    }
                });

        Connector.glclient.query(GlRepoFeedQuery.builder().searchString(searchString).build())
                .enqueue(new ApolloCall.Callback<GlRepoFeedQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GlRepoFeedQuery.Data> response) {
                        GlRepoFeedQuery.Data data = response.getData();
                        if (data != null) {
                            GlRepoFeedQuery.Projects projects = data.projects();
                            assert projects != null;
                            GlRepoFeedQuery.PageInfo pageInfo = projects.pageInfo();
                            hasNextPage = pageInfo.hasNextPage();
                            endCursor = pageInfo.endCursor();

                            List<GlRepoFeedQuery.Node> nodes = projects.nodes();
                            if (nodes != null) {
                                repositories = new RepoCardData[nodes.size()];
                                for (int i = 0; i < repositories.length; ++i) {
                                    GlRepoFeedQuery.Node project = nodes.get(i);
                                    String url = project.webUrl();
                                    String name = project.fullPath();
                                    Log.d("GLQUERY", name);
                                    String language = null; // TODO: Figure out how to get language information from GitLab
                                    String description = project.description();
                                    String profilePicUrl = project.avatarUrl();
                                    Integer rating = 0;             // TODO: implement ratings
                                    Integer valRated = 0;           // TODO: implement ratings
                                    Integer comments = 0;           // TODO: implement comments
                                    Boolean isCommented = false;    // TODO: implement comments
                                    Integer stars = project.starCount();
                                    Boolean isStarred = false; // TODO: Figure out how to get this information from GitLab
                                    Integer forks = project.forksCount();
                                    Boolean isForked = false; // TODO: Figure out how to get this information from GitLab
                                    repositories[i] = new RepoCardData(
                                            Connector.Service.GITLAB.shortName(), url, name, language, description, profilePicUrl, rating, valRated, comments,
                                            isCommented, stars, isStarred, forks, isForked
                                    );
                                }
                            } else {
                                repositories = new RepoCardData[]{};
                            }

                            if (successCallback != null) successCallback.handle(_instance);
                        } else if (errorCallback != null) {
                            errorCallback.error("Failed query: data is NULL");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        if (errorCallback != null)
                            errorCallback.error("Failed query: " + e.getMessage());
                    }
                });
    }

}
