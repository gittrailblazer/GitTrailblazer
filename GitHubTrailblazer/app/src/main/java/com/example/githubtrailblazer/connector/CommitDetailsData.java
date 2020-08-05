package com.example.githubtrailblazer.connector;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.Commit;
import com.example.githubtrailblazer.Contributor;
import com.example.githubtrailblazer.github.CommitDetailsQuery;
import com.example.githubtrailblazer.github.UserDetailsQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommitDetailsData {
    // Properties accessible in success callback
    public CommitDetailsQuery.Target id = null;
    public String authorName;
    public String authorAvatarURL;
    public String commitDate;
    public String messageHeadline;
    public String message;
    public String readMe;

    public ArrayList<Commit> allRepoCommits = new ArrayList<>();
    public ArrayList<Contributor> allRepoContributors = new ArrayList<>();
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
        final String repoName = (String) queryParams.next();
        final String repoOwner = (String) queryParams.next();
        Connector.getInstance().getGHClient().query(CommitDetailsQuery.builder().repoName(repoName).repoOwner(repoOwner).build())
                .enqueue(new ApolloCall.Callback<CommitDetailsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CommitDetailsQuery.Data> response) {
                        CommitDetailsQuery.Data data = response.getData();
                        if (data != null) {
                            CommitDetailsQuery.Repository dataRepo = data.repository();
                            if(dataRepo != null) {
                                if(dataRepo.ref() != null) {
                                    id = data.repository().ref().target();
                                }
                            }


                            // get README
                            if(dataRepo != null) {
                                Object readMeObj = dataRepo.object();
                                CommitDetailsQuery.AsBlob readMeBlob = (CommitDetailsQuery.AsBlob) readMeObj;
                                if(readMeBlob != null && (readMeBlob.text() != null || readMeBlob.text().equals(""))) {
                                    readMe = readMeBlob.text();
                                }
                            }

                            // get list of commits
                            if(id != null) {
                                CommitDetailsQuery.AsCommit commit = (CommitDetailsQuery.AsCommit) id;
                                List<CommitDetailsQuery.Edge> edges = ((CommitDetailsQuery.AsCommit) id).history().edges();

                                // for all commits in this repo, generate commits
                                for(int i = 0; i < edges.size(); i++) {
                                    CommitDetailsQuery.Node currCommit = edges.get(i).node();
                                    authorName = currCommit.author().name();
                                    authorAvatarURL = currCommit.author().avatarUrl().toString();
                                    commitDate = currCommit.author().date().toString();
                                    messageHeadline = currCommit.messageHeadline();
                                    message = currCommit.message();

                                    Commit currCommitObj = new Commit(authorName, authorAvatarURL, commitDate, messageHeadline);
                                    allRepoCommits.add(currCommitObj);
                                }
                            }


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


    /*public CommitDetailsData(@NotNull Connector.QueryParams queryParams,
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

                            // get README
                            Object readMeObj = data.repository().object();
                            CommitDetailsQuery.AsBlob readMeBlob = (CommitDetailsQuery.AsBlob) readMeObj;
                            readMe = readMeBlob.text();

                            // get list of commits
                            CommitDetailsQuery.AsCommit commit = (CommitDetailsQuery.AsCommit) id;
                            List<CommitDetailsQuery.Edge> edges = ((CommitDetailsQuery.AsCommit) id).history().edges();

                            // for all commits in this repo, generate commits
                            for(int i = 0; i < edges.size(); i++) {
                                CommitDetailsQuery.Node currCommit = edges.get(i).node();
                                authorName = currCommit.author().name();
                                authorAvatarURL = currCommit.author().avatarUrl().toString();
                                commitDate = currCommit.author().date().toString();
                                messageHeadline = currCommit.messageHeadline();
                                message = currCommit.message();

                                Commit currCommitObj = new Commit(authorName, authorAvatarURL, commitDate, messageHeadline);
                                allRepoCommits.add(currCommitObj);
                            }

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
    } */
}
