package com.example.githubtrailblazer.connector;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.HashMap;

/**
 * Connector class for abstracting GitHub AND GitLab API interfaces
 */
public class Connector {
    private static final String GH_ENDPOINT_URL = "https://api.github.com/graphql";
    private static final String GL_ENDPOINT_URL = "https://gitlab.com/api/graphql";

    private static HashMap<QueryType, Class> queryTypeMap = new HashMap() {{
        put(QueryType.USER_DETAILS, UserDetailsData.class);
        put(QueryType.REPO_FEED, RepoFeedData.class);
    }};
    static ApolloClient ghclient = ApolloClient.builder()
            .serverUrl(GH_ENDPOINT_URL)
            .okHttpClient(new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                        builder.header("content-type", "application/json");
                        return chain.proceed(builder.build());
                    })
                    .build())
            .build();

    static ApolloClient glclient = ApolloClient.builder()
            .serverUrl(GH_ENDPOINT_URL)
            .okHttpClient(new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                        builder.header("content-type", "application/json");
                        return chain.proceed(builder.build());
                    })
                    .build())
            .build();

    /*
     * STEPS: How to retrieve and access API data
     * 1) Add ***Data class to the connector package, following existing ***Data class property syntax and signatures,
     *    constructor should perform query, set properties based off of apollo response data, and invoke success
     *    callback (if provided) passing the instance of itself as the argument
     * 2) Add an enum option corresponding to the new ***Data class in Connector.QueryType enums
     * 3) Map the new enum to the new ***Data class in Connector.queryTypeMap
     * 4) new Connector.Query(<query-id-enum>, <...query-arguments>)
     *             .exec(new Connector.ISuccessCallback() {
     *                 @Override
     *                 public void handle(Object result) {
     *                     <query-data-class> data = (<query-data-class>) result;
     *                     ...
     *                 }
     *             }, new Connector.IErrorCallback() {
     *                 @Override
     *                 public void error(String message) {
     *                     ...
     *                 }
     *             });
     */

    /**
     * Service types
     */
    public enum Service {
        GITHUB(0, "GitHub"), GITLAB(1, "GitLab");
        int id;
        String name;

        Service(int id, String name) {
            this.id = id;
            this.name = name;
        }

        static Service get(int id) {
            for (Service s : values()) if (s.id == id) return s;
            throw new IllegalArgumentException();
        }
    }

    /**
     * On query success callback
     */
    public interface ISuccessCallback {
        /**
         * Handle success result
         *
         * @param result - the query success result
         */
        void handle(Object result);
    }

    /**
     * On query error callback
     */
    public interface IErrorCallback {
        /**
         * Handle error message
         *
         * @param message - the query error message
         */
        void error(String message);
    }

    /**
     * Initialize the connector
     *
     * @param githubAccessToken - github oauth access token
     * @param gitlabPersonalAccessToken - gitlab personal access token
     */
    public static void initialize(String githubAccessToken, String gitlabPersonalAccessToken) {
        // This is a singleton HTTP client.
        // We use this to handle our requests.
        OkHttpClient ghOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", "Bearer " + githubAccessToken);
                    builder.header("content-type", "application/json");
                    return chain.proceed(builder.build());
                })
                .build();

        // create new client instance
        ghclient = ApolloClient.builder()
                .serverUrl(GH_ENDPOINT_URL)
                .okHttpClient(ghOkHttpClient)
                .build();


        OkHttpClient glOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", "Bearer " + gitlabPersonalAccessToken);
                    builder.header("content-type", "application/json");
                    return chain.proceed(builder.build());
                })
                .build();

        // create new client instance
        glclient = ApolloClient.builder()
                .serverUrl(GL_ENDPOINT_URL)
                .okHttpClient(glOkHttpClient)
                .build();
    }

    /**
     * QueryParams class for wrapping query params, used in query reflection
     */
    static class QueryParams {
        private Object[] args;
        private int index = 0;

        private QueryParams(Object... args) {
            this.args = args;
        }

        Object next() {
            assert (index < args.length);
            return args[index++];
        }
    }

    /**
     * The supported query types
     */
    public enum QueryType {
        REPO_FEED,
        USER_DETAILS
    }

    /**
     * Query class for executing generic connector queries
     */
    public static class Query {
        private Class queryDataType;
        private QueryParams queryParams;

        /**
         * Create query
         *
         * @param queryType - the query type to perform
         * @param args      - the list of query arguments
         */
        public Query(QueryType queryType, Object... args) {
            assert (queryTypeMap.containsKey(queryType));
            this.queryParams = new QueryParams(args);
            queryDataType = queryTypeMap.get(queryType);
        }

        /**
         * Execute query with no callbacks
         *
         * @return this instance
         */
        public Query exec() {
            exec(null, null);
            return this;
        }

        /**
         * Execute query with success callback
         *
         * @param successCallback - the success callback
         * @return this instance
         */
        public Query exec(ISuccessCallback successCallback) {
            exec(successCallback, null);
            return this;
        }

        /**
         * Execute query with success and error callbacks
         *
         * @param successCallback - the success callback
         * @param errorCallback   - the error callback
         * @return this instance
         */
        public Query exec(ISuccessCallback successCallback, IErrorCallback errorCallback) {
            try {
                queryDataType
                        .getConstructor(QueryParams.class, ISuccessCallback.class, IErrorCallback.class)
                        .newInstance(queryParams, successCallback, errorCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
