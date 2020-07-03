package com.example.githubtrailblazer.ghapi;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GitHubConnector {
    private static final String ENDPOINT_URL = "https://api.github.com/graphql";
    public static ApolloClient client;

    public static void initialize(String accessToken) {
        // This is a singleton HTTP client.
        // We use this to handle our requests.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", "Bearer " + accessToken);
                    builder.header("content-type", "application/json");
                    return chain.proceed(builder.build());
                })
                .build();

        client = ApolloClient.builder()
                .serverUrl(ENDPOINT_URL)
                .okHttpClient(okHttpClient)
                .build();
    }
}
