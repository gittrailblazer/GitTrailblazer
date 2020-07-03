package com.example.githubtrailblazer.ghapi;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.NameUsernameAvatarQuery;

import org.jetbrains.annotations.NotNull;

public class GHUserBasicData {
    public interface GHUserBasicDataCallback {
        void setData(
                String name,
                String username,
                String avatarUrl);
    }

    /**
     * Query the GitHub API for the name, username, and avatar url of the user.
     */
    public void queryAPI(GHUserBasicDataCallback callback) {
        GitHubConnector.client.query(
                NameUsernameAvatarQuery.builder().build())
                .enqueue(new ApolloCall.Callback<NameUsernameAvatarQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<NameUsernameAvatarQuery.Data> response) {
                        NameUsernameAvatarQuery.Data data = response.getData();
                        if (data != null) {
                            String name = data.viewer().name();
                            String username = data.viewer().login();
                            String avatarUrl = data.viewer().avatarUrl().toString();
                            callback.setData(name, username, avatarUrl);
                        } else {
                            Log.d("GH_API_QUERY", "Failed query: data is NULL");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("GH_API_QUERY", "Failed query: " + e.getMessage(), e);
                    }
                });
    }
}