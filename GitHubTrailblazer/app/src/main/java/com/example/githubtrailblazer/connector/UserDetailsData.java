package com.example.githubtrailblazer.connector;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.github.UserDetailsQuery;
import org.jetbrains.annotations.NotNull;

public class UserDetailsData {
    // Properties accessible in success callback
    public String id;
    public String name;
    public String username;
    public String avatarUrl;

    /**
     * Create user details data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     *        UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     * @param queryParams - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback - the error callback (may be NULL)
     */
    public UserDetailsData(@NotNull Connector.QueryParams queryParams,
                           Connector.ISuccessCallback successCallback,
                           Connector.IErrorCallback errorCallback) {
        final UserDetailsData _instance = this;
        Connector.ghclient.query(UserDetailsQuery.builder().build())
            .enqueue(new ApolloCall.Callback<UserDetailsQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<UserDetailsQuery.Data> response) {
                    UserDetailsQuery.Data data = response.getData();
                    if (data != null) {
                        id = data.viewer().id();
                        name = data.viewer().name();
                        username = data.viewer().login();
                        avatarUrl = data.viewer().avatarUrl().toString();
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

//==================================================================================================
//    GitLab version
//
// Requires import com.example.githubtrailblazer.gitlab.UserDetailsQuery;
//==================================================================================================
//    public UserDetailsData(@NotNull Connector.QueryParams queryParams, Connector.ISuccessCallback successCallback, Connector.IErrorCallback errorCallback) {
//        final UserDetailsData _instance = this;
//        Connector.glclient.query(UserDetailsQuery.builder().build())
//                .enqueue(new ApolloCall.Callback<UserDetailsQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<UserDetailsQuery.Data> response) {
//                        UserDetailsQuery.Data data = response.getData();
//                        if (data != null) {
//                            id = data.currentUser().id();
//                            name = data.currentUser().name();
//                            username = data.currentUser().username();
//                            avatarUrl = data.currentUser().avatarUrl();
//                            if (successCallback != null) successCallback.handle(_instance);
//                        } else if (errorCallback != null) {
//                            errorCallback.error("Failed query: data is NULL");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        if (errorCallback != null) errorCallback.error("Failed query: " + e.getMessage());
//                    }
//                });
//    }
}
