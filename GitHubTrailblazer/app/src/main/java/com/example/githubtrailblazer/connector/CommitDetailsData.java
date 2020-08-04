package com.example.githubtrailblazer.connector;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.github.CommitDetailsQuery;
import com.example.githubtrailblazer.github.UserDetailsQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommitDetailsData {
    // Properties accessible in success callback
    public CommitDetailsQuery.Target id = null;
    public String authorName;
    public String authorDate;
    public String messageHeadline;
    public String message;

    /**
     * Create user details data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     *        UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     * @param queryParams - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback - the error callback (may be NULL)
     */
    public CommitDetailsData(@NotNull Connector.QueryParams queryParams,
                           Connector.ISuccessCallback successCallback,
                           Connector.IErrorCallback errorCallback) throws Exception {
        final CommitDetailsData _instance = this;
        Connector.getInstance().getGHClient().query(CommitDetailsQuery.builder().build())
                .enqueue(new ApolloCall.Callback<CommitDetailsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CommitDetailsQuery.Data> response) {
                        CommitDetailsQuery.Data data = response.getData();
                        if (data != null) {
                            id = data.repository().ref().target();
                            CommitDetailsQuery.AsCommit commit = (CommitDetailsQuery.AsCommit) id;
                            List<CommitDetailsQuery.Edge> edges = ((CommitDetailsQuery.AsCommit) id).history().edges();
                            CommitDetailsQuery.Node firstCommit = edges.get(1).node();
                            authorName = firstCommit.author().name();
                            authorDate = firstCommit.author().date().toString();
                            messageHeadline = firstCommit.messageHeadline();
                            message = firstCommit.message();
                            Log.d("ERROR:", message);
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
