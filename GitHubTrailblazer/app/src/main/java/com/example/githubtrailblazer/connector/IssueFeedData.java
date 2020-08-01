package com.example.githubtrailblazer.connector;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.data.IssueCardData;
import com.example.githubtrailblazer.github.GhIssueFeedQuery;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IssueFeedData {
    // Properties accessible in success callback
    public Boolean hasNextPage;
    public String endCursor;
    public IssueCardData[] issues;

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
        String searchString = (String)queryParams.next();
        final IssueFeedData _instance = this;

        Connector.ghclient.query(GhIssueFeedQuery.builder().searchString(searchString).build())
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
                                    // TODO
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

}
