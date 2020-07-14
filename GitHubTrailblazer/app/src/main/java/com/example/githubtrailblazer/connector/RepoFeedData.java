package com.example.githubtrailblazer.connector;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.RepoFeedQuery;
import com.example.githubtrailblazer.components.ProjectCard.ProjectCard;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RepoFeedData {
    // Properties accessible in success callback
    public Boolean hasNextPage;
    public String endCursor;
    public ProjectCard.Data[] repositories;

    /**
     * Create repo feed data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     *        UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     * @param queryParams - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback - the error callback (may be NULL)
     */
    public RepoFeedData(@NotNull Connector.QueryParams queryParams, Connector.ISuccessCallback successCallback, Connector.IErrorCallback errorCallback) {
        String searchString = (String) queryParams.next();
        final RepoFeedData _instance = this;
        Connector.client.query(RepoFeedQuery.builder().searchString(searchString).build())
            .enqueue(new ApolloCall.Callback<RepoFeedQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<RepoFeedQuery.Data> response) {
                    RepoFeedQuery.Data data = response.getData();
                    if (data != null) {
                        RepoFeedQuery.Search search = data.search();
                        RepoFeedQuery.PageInfo pageInfo = search.pageInfo();
                        hasNextPage = pageInfo.hasNextPage();
                        endCursor = pageInfo.endCursor();

                        List<RepoFeedQuery.Node> nodes = search.nodes();
                        if (nodes != null) {
                            repositories = new ProjectCard.Data[nodes.size()];
                            for (int i = 0; i < repositories.length; ++i) {
                                RepoFeedQuery.AsRepository repository = (RepoFeedQuery.AsRepository) nodes.get(i);
                                String name = repository.nameWithOwner();
                                RepoFeedQuery.PrimaryLanguage _pl = repository.primaryLanguage();
                                String language = (_pl == null) ? null : _pl.name();
                                String description = repository.description();
                                RepoFeedQuery.Owner _o = repository.owner();
                                String profilePicUrl = (_o == null) ? null : _o.avatarUrl().toString();
                                Integer rating = 3375;
                                Integer valRated = 0;
                                Integer comments = 500;
                                Boolean isCommented = false;
                                RepoFeedQuery.Stargazers _sg = repository.stargazers();
                                Integer stars = (_sg == null) ? null : _sg.totalCount();
                                Boolean isStarred = repository.viewerHasStarred();
                                Integer forks = repository.forkCount();
                                Boolean isForked = false;
                                repositories[i] = new ProjectCard.Data(
                                    name, language, description, profilePicUrl, rating, valRated, comments,
                                    isCommented, stars, isStarred, forks, isForked
                                );
                            }
                        } else {
                            repositories = new ProjectCard.Data[]{};
                        }

                        if (successCallback != null) successCallback.handle(_instance);
                    } else if (errorCallback != null) {
                        errorCallback.error("Failed query: data is NULL");
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    if (errorCallback != null) errorCallback.error("Failed query: " + e.getMessage());
                }
            });
    }

}
