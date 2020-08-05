package com.example.githubtrailblazer.connector;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import com.example.githubtrailblazer.github.UnStarRepoMutation;

import org.jetbrains.annotations.NotNull;

public class UnStarRepo {
    /**
     * Unstar the repo whose ID is given as the first query argument by querying the API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     * UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     *
     * @param queryParams     - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback   - the error callback (may be NULL)
     */
    public UnStarRepo(@NotNull Connector.QueryParams queryParams,
                      Connector.ISuccessCallback successCallback,
                      Connector.IErrorCallback errorCallback) throws Exception {
        final UnStarRepo _instance = this;
        // parse argument
        final String repoID = (String) queryParams.next();

        UnStarRepoMutation starRepoMutation = UnStarRepoMutation.builder().mutationId("").repoID(repoID).build();

        Connector.getInstance().getGHClient().mutate(starRepoMutation)
                .enqueue(new ApolloCall.Callback<UnStarRepoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UnStarRepoMutation.Data> response) {
                        if (response.hasErrors()) {
                            errorCallback.error("Apollo Failed query: " + response.getErrors());
                        }
                        if (successCallback != null) successCallback.handle(_instance);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        if (errorCallback != null)
                            errorCallback.error("Apollo Failed query: " + e.getMessage());
                    }
                });
    }
}
