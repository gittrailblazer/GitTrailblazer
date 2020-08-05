package com.example.githubtrailblazer.connector;

import android.util.Log;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.data.IssueCardData;
import com.example.githubtrailblazer.github.GhIssueFeedQuery;
import com.example.githubtrailblazer.github.GhRepoFeedQuery;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IssueFeedData {
    // Properties accessible in success callback
    public Boolean hasNextPage;
    public String endCursor;
    public IssueCardData[] issues;

    // properties used during construction
    private Connector.ISuccessCallback successCallback;
    private Connector.IErrorCallback errorCallback;

    /**
     * The query response sort options
     */
    public enum SortOption {
        NEWEST,
        MOST_POPULAR
    }

    /**
     * Create issue feed data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     * UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     *
     * @param queryParams     - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback   - the error callback (may be NULL)
     */
    public IssueFeedData(@NotNull Connector.QueryParams queryParams,
                         Connector.ISuccessCallback successCallback,
                         Connector.IErrorCallback errorCallback) {

        // save callbacks
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        // parse args
        SortOption sortOption = (SortOption)queryParams.next();
        String searchString = (String)queryParams.next();
        final IssueFeedData _instance = this;

        // determine sort string for GitHub
        String ghSortString = "type:issue label:bug is:open no:assignee archived:false -linked:pr ";
        switch (sortOption) {
            case NEWEST:
                ghSortString += "sort:created";
                break;
            case MOST_POPULAR:
                ghSortString += "sort:reactions";
                break;
        }
        final String ghSearchString = searchString + (searchString.isEmpty() ? "" : " ") + ghSortString;

        try {
            Connector.getInstance().getGHClient().query(GhIssueFeedQuery.builder().searchString(ghSearchString).build())
                    .enqueue(new ApolloCall.Callback<GhIssueFeedQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GhIssueFeedQuery.Data> response) {
                            GhIssueFeedQuery.Data data = response.getData();
                            if (data != null) {
                                GhIssueFeedQuery.Search search = data.search();
                                GhIssueFeedQuery.PageInfo pageInfo = search.pageInfo();
                                hasNextPage = pageInfo.hasNextPage();
                                endCursor = pageInfo.endCursor();

                                List<GhIssueFeedQuery.Node> nodes = search.nodes();
                                if (nodes != null) {
                                    issues = new IssueCardData[nodes.size()];
                                    for (int i = 0; i < issues.length; ++i) {
                                        IssueCardData issueCardData = new IssueCardData();
                                        issueCardData.service = Connector.Service.GITHUB.shortName();
                                        GhIssueFeedQuery.AsIssue issue = (GhIssueFeedQuery.AsIssue) nodes.get(i);
                                        issueCardData.id = issue.id();
                                        issueCardData.number = issue.number();
                                        issueCardData.title = issue.title();
                                        issueCardData.url = issue.url().toString();
                                        issueCardData.description = issue.bodyText();
                                        issueCardData.createdAt = Helpers.utcToMs(issue.createdAt().toString());
                                        issueCardData.repoData = new IssueCardData.RepoData();
                                        GhIssueFeedQuery.Repository repository = issue.repository();
                                        issueCardData.repoData.name = repository.nameWithOwner();
                                        GhIssueFeedQuery.PrimaryLanguage _pl = repository.primaryLanguage();
                                        issueCardData.repoData.language = (_pl == null) ? null : _pl.name();
                                        issueCardData.repoData.forks = repository.forkCount();
                                        GhIssueFeedQuery.Stargazers _sg = repository.stargazers();
                                        issueCardData.repoData.stars = (_sg == null) ? 0 : _sg.totalCount();
                                        issueCardData.repoData.isStarred = repository.viewerHasStarred();
                                        issueCardData.repoData.forks = repository.forkCount();
                                        GhIssueFeedQuery.Forks _f = repository.forks();
                                        issueCardData.repoData.isForked = (_f == null) ? false : _f.totalCount() > 0;
                                        issues[i] = issueCardData;
                                    }
                                } else {
                                    issues = new IssueCardData[]{};
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
        } catch (Exception e) {
            if (errorCallback != null) errorCallback.error(e.getMessage());
        }
    }

}
