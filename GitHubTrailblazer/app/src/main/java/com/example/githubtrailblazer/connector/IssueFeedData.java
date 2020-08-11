package com.example.githubtrailblazer.connector;

import android.annotation.SuppressLint;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.data.IssueCardData;
import com.example.githubtrailblazer.github.GhFriendlyIssueFeedQuery;
import com.example.githubtrailblazer.github.GhIssueFeedQuery;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IssueFeedData extends PaginationData {
    // Properties accessible in success callback
    public IssueCardData[] issues;

    // properties used during construction
    private Connector.ISuccessCallback successCallback;
    private Connector.IErrorCallback errorCallback;

    // Fix some constants
    final int REQUESTED_ISSUES_PER_FRIENDLY_REPO = 3;
    final int EXPECTED_ISSUES_PER_FRIENDLY_REPO = 2;
    final int MIN_FOLLOWERS = 100;
    final int MIN_GOOD_FIRST_ISSUES = 10;
    final int NEWER_THAN_X_DAYS = 300;


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
    @SuppressLint("DefaultLocale")
    public IssueFeedData(@NotNull Connector.QueryParams queryParams,
                         Connector.ISuccessCallback successCallback,
                         Connector.IErrorCallback errorCallback) {

        // save callbacks
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        // parse args
        PaginationData paginationData = (PaginationData)queryParams.next();
        paginationData = (paginationData == null) ? this : paginationData;
        SortOption sortOption = (SortOption) queryParams.next();
        String searchString = (String) queryParams.next();
        boolean showFriendlyFeed = (boolean) queryParams.next();
        final IssueFeedData _instance = this;

        // determine sort string for GitHub
        String ghSortString;
        if (showFriendlyFeed) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.now().minusDays(NEWER_THAN_X_DAYS);

            ghSortString = String.format(
                    "pushed:>%s followers:>=%d good-first-issues:>=%d ",
                    dtf.format(date),
                    MIN_FOLLOWERS,
                    MIN_GOOD_FIRST_ISSUES
            );
        } else {
            ghSortString = "type:issue label:bug is:open no:assignee archived:false -linked:pr ";
        }

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
            if (showFriendlyFeed) {
                Connector.getInstance().getGHClient()
                        .query(GhFriendlyIssueFeedQuery.builder()
                                .searchString(ghSearchString)
                                .requestedIssuesPerRepo(REQUESTED_ISSUES_PER_FRIENDLY_REPO)
                                .cursor(paginationData.getPagination(Connector.Service.GITHUB).endCursor)
                                .build())
                        .enqueue(new ApolloCall.Callback<GhFriendlyIssueFeedQuery.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<GhFriendlyIssueFeedQuery.Data> response) {
                                GhFriendlyIssueFeedQuery.Data data = response.getData();
                                if (data != null) {

                                    GhFriendlyIssueFeedQuery.Search search = data.search();
                                    GhFriendlyIssueFeedQuery.PageInfo pageInfo = search.pageInfo();
                                    setPagination(Connector.Service.GITHUB, new Pagination(pageInfo.hasNextPage(), pageInfo.endCursor()));

                                    List<GhFriendlyIssueFeedQuery.Node> friendlyRepos = search.nodes();
                                    if (friendlyRepos != null) {
                                        // TODO: Use an ArrayList for issues, then we don't have to do any of this sketchy math.
                                        int numIssueCardsExpected = EXPECTED_ISSUES_PER_FRIENDLY_REPO * friendlyRepos.size();
                                        issues = new IssueCardData[numIssueCardsExpected];

                                        int issueIndex = 0;
                                        for (GhFriendlyIssueFeedQuery.Node friendlyRepo : friendlyRepos) {
                                            GhFriendlyIssueFeedQuery.AsRepository fRepo = (GhFriendlyIssueFeedQuery.AsRepository) friendlyRepo;
                                            List<GhFriendlyIssueFeedQuery.Node1> friendlyIssues = fRepo.issues().nodes();
                                            if (friendlyIssues != null) {
                                                for (GhFriendlyIssueFeedQuery.Node1 friendlyIssue : friendlyIssues) {
                                                    IssueCardData issueCardData = new IssueCardData();
                                                    issueCardData.service = Connector.Service.GITHUB.shortName();

                                                    issueCardData.id = friendlyIssue.id();
                                                    issueCardData.number = friendlyIssue.number();
                                                    issueCardData.title = friendlyIssue.title();
                                                    issueCardData.url = friendlyIssue.url().toString();
                                                    issueCardData.description = friendlyIssue.bodyText();
                                                    issueCardData.createdAt = Helpers.utcToMs(friendlyIssue.createdAt().toString());

                                                    issueCardData.repoData = new IssueCardData.RepoData();
                                                    issueCardData.repoData.name = fRepo.nameWithOwner();
                                                    GhFriendlyIssueFeedQuery.PrimaryLanguage _pl = fRepo.primaryLanguage();
                                                    issueCardData.repoData.language = (_pl == null) ? null : _pl.name();
                                                    issueCardData.repoData.forks = fRepo.forkCount();
                                                    GhFriendlyIssueFeedQuery.Stargazers _sg = fRepo.stargazers();
                                                    issueCardData.repoData.stars = (_sg == null) ? 0 : _sg.totalCount();
                                                    issueCardData.repoData.isStarred = fRepo.viewerHasStarred();
                                                    issueCardData.repoData.forks = fRepo.forkCount();
                                                    GhFriendlyIssueFeedQuery.Forks _f = fRepo.forks();
                                                    issueCardData.repoData.isForked = (_f == null) ? false : _f.totalCount() > 0;
                                                    issues[issueIndex] = issueCardData;

                                                    // only increment if we have space
                                                    if ((issueIndex + 1) < numIssueCardsExpected)
                                                        issueIndex++;
                                                }
                                            }
                                        }

                                        Log.d("ISSUE CARDS GOTTEN", String.valueOf(issueIndex + 1));
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
            } else {
                Connector.getInstance().getGHClient().query(GhIssueFeedQuery.builder()
                        .searchString(ghSearchString)
                        .cursor(paginationData.getPagination(Connector.Service.GITHUB).endCursor)
                        .build())
                        .enqueue(new ApolloCall.Callback<GhIssueFeedQuery.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<GhIssueFeedQuery.Data> response) {
                                GhIssueFeedQuery.Data data = response.getData();
                                if (data != null) {
                                    GhIssueFeedQuery.Search search = data.search();
                                    GhIssueFeedQuery.PageInfo pageInfo = search.pageInfo();
                                    setPagination(Connector.Service.GITHUB, new Pagination(pageInfo.hasNextPage(), pageInfo.endCursor()));

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
            }
        } catch (Exception e) {
            if (errorCallback != null) errorCallback.error(e.getMessage());
        }
    }

}
