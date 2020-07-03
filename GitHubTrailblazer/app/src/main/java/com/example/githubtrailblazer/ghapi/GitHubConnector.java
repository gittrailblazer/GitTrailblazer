package com.example.githubtrailblazer.ghapi;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GitHubConnector {
    private static final String ENDPOINT_URL = "https://api.github.com/graphql";
    private static String authHeader = "Bearer 08971b743da22c51068e53342483f2a39c25f153";

    public static ApolloClient initialize() {
        // This is a singleton HTTP client.
        // We use this to handle our requests.
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", authHeader);
                    builder.header("content-type", "application/json");
                    return chain.proceed(builder.build());
                })
                .build();

        return ApolloClient.builder()
                .serverUrl(ENDPOINT_URL)
                .okHttpClient(okHttpClient)
                .build();
    }
}
