package com.example.githubtrailblazer.connector;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.FlagMaster;
import com.example.githubtrailblazer.github.GhReadmeQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;
public class ReadmeData {
    // Properties accessible in success callback
    public String readme;

    /**
     * Create readme data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     * UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     *
     * @param queryParams     - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback   - the error callback (may be NULL)
     */
    public ReadmeData(@NotNull Connector.QueryParams queryParams,
                           Connector.ISuccessCallback successCallback,
                           Connector.IErrorCallback errorCallback) throws Exception {
        final ReadmeData _instance = this;
        final String owner = (String)queryParams.next();
        final String name = (String)queryParams.next();

        // GitHub query
        if(FlagMaster.getInstance().getGHFlag()) {
            Connector.getInstance().getGHClient().query(GhReadmeQuery.builder().owner(owner).name(name).build())
                    .enqueue(new ApolloCall.Callback<GhReadmeQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GhReadmeQuery.Data> response) {
                            GhReadmeQuery.Data data = response.getData();
                            if (data != null) {
                                GhReadmeQuery.Repository repository = (GhReadmeQuery.Repository)data.repository();
                                if (repository != null) {
                                    GhReadmeQuery.AsBlob object = (GhReadmeQuery.AsBlob)repository.object();
                                    if (object != null) readme = object.text();
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

        // GitLab query
        if(FlagMaster.getInstance().getGLFlag()) {
            // TODO: Get readme from GitLab repos
        }

    }
}