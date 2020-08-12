package com.git_trailblazer.v1.connector;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.git_trailblazer.v1.FlagMaster;
import com.git_trailblazer.v1.data.RepoCardData;
import com.git_trailblazer.v1.github.GhRepoFeedQuery;
import com.git_trailblazer.v1.github.GhStarredRepoFeedQuery;
import com.git_trailblazer.v1.gitlab.GlRepoFeedQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RepoFeedData extends PaginationData {
    // Properties accessible in success callback
    public RepoCardData[] repositories;

    // properties used during construction
    private RepoCardData[] ghRepositories;
    private RepoCardData[] glRepositories;
    private final Connector.ISuccessCallback successCallback;
    private final Connector.IErrorCallback errorCallback;


    /**
     * The query response sort options
     */
    public enum SortOption {
        NEWEST,
        MOST_STARS,
        MOST_FORKS
    }

    /**
     * The query response filter options
     */
    public enum FilterOption {
        EXPLORE,
        STARRED,
//        FOLLOWING,
//        CONTRIBUTED
    }

    /**
     * Create repo feed data by querying API via connector
     * NOTE: must be PUBLIC and must have the following signature (for reflection):
     * UserDetailsData(Connector.QueryParams, Connector.ISuccessCallback, Connector.IErrorCallback)
     *
     * @param queryParams     - the parameters
     * @param successCallback - the success callback (may be NULL)
     * @param errorCallback   - the error callback (may be NULL)
     */
    public RepoFeedData(@NotNull Connector.QueryParams queryParams,
                        Connector.ISuccessCallback successCallback,
                        Connector.IErrorCallback errorCallback) {
        // save callbacks
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        // parse args
        final PaginationData paginationData = (PaginationData)queryParams.next();
        final SortOption sortOption = (SortOption)queryParams.next();
        final FilterOption filterOption = (FilterOption)queryParams.next();
        final String searchString = (String)queryParams.next();

        // perform different grapqhl queries based off of repo filter
        switch (filterOption) {
            case EXPLORE:
                getExplore(sortOption, searchString, paginationData == null ? this : paginationData);
                break;
            case STARRED:
                getStarred(sortOption, searchString, paginationData == null ? this : paginationData);
                break;
        }
    }

    private void getStarred(SortOption sortOption, String searchString, PaginationData paginationData) {
        // no GitLab repository info
        glRepositories = new RepoCardData[]{};
        try {
            if (FlagMaster.getInstance().getGHFlag()) {
                Connector.getInstance().getGHClient().query(GhStarredRepoFeedQuery.builder()
                    .cursor(paginationData.getPagination(Connector.Service.GITHUB).endCursor)
                    .build())
                    .enqueue(new ApolloCall.Callback<GhStarredRepoFeedQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GhStarredRepoFeedQuery.Data> response) {
                            GhStarredRepoFeedQuery.Data data = response.getData();
                            if (data != null) {
                                GhStarredRepoFeedQuery.Viewer _v = data.viewer();
                                GhStarredRepoFeedQuery.StarredRepositories _sr = _v.starredRepositories();
                                GhStarredRepoFeedQuery.PageInfo pageInfo = _sr.pageInfo();
                                setPagination(Connector.Service.GITHUB, new Pagination(pageInfo.hasNextPage(), pageInfo.endCursor()));

                                List<GhStarredRepoFeedQuery.Node> nodes = _sr.nodes();
                                if (nodes != null) {
                                    ghRepositories = new RepoCardData[nodes.size()];
                                    for (int i = 0; i < ghRepositories.length; ++i) {
                                        RepoCardData repoCardData = new RepoCardData();
                                        repoCardData.service = Connector.Service.GITHUB.shortName();
                                        GhStarredRepoFeedQuery.Node repository = nodes.get(i);
                                        repoCardData.url = repository.url().toString();
                                        repoCardData.id = repository.id();
                                        repoCardData.name = repository.nameWithOwner();
                                        GhStarredRepoFeedQuery.PrimaryLanguage _pl = repository.primaryLanguage();
                                        repoCardData.language = (_pl == null) ? null : _pl.name();
                                        repoCardData.description = repository.description();
                                        GhStarredRepoFeedQuery.Owner _o = repository.owner();
                                        repoCardData.profilePicUrl = (_o == null) ? null : _o.avatarUrl().toString();
                                        repoCardData.rating = 0;             // TODO: implement ratings
                                        repoCardData.valRated = 0;           // TODO: implement ratings
                                        repoCardData.comments = 0;           // TODO: implement comments
                                        repoCardData.isCommented = false;    // TODO: implement comments
                                        GhStarredRepoFeedQuery.Stargazers _sg = repository.stargazers();
                                        repoCardData.stars = (_sg == null) ? null : _sg.totalCount();
                                        repoCardData.isStarred = repository.viewerHasStarred();
                                        repoCardData.forks = repository.forkCount();
                                        GhStarredRepoFeedQuery.Forks _f = repository.forks();
                                        repoCardData.isForked = (_f == null) ? false : _f.totalCount() > 0;
                                        ghRepositories[i] = repoCardData;
                                    }
                                } else {
                                    ghRepositories = new RepoCardData[]{};
                                }

                                // combine data and execute callback
                                combineData();
                            } else if (errorCallback != null) {
                                errorCallback.error("Failed query: data is NULL");
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("ERROR", "error: " + e.toString());
                            if (errorCallback != null)
                                errorCallback.error("Failed query: " + e.getMessage());
                        }
                    });
            }

            // GitLab query
            if(FlagMaster.getInstance().getGLFlag()) {
                // TODO: implement getStarred for GitLab
            }
        } catch (Exception e) {
            errorCallback.error("Connector exception: " + e.getMessage());
        }



    }

    private void getExplore(SortOption sortOption, String searchString, PaginationData paginationData) {
        // determine sort string for GitHub
        String ghSortString = "";
        switch (sortOption) {
            case NEWEST:
                ghSortString = "sort:updated";
                break;
            case MOST_STARS:
                ghSortString = "sort:stars";
                break;
            case MOST_FORKS:
                ghSortString = "sort:forks";
                break;
        }

        final String ghSearchString = searchString + (searchString.isEmpty() ? "" : " ") + ghSortString;

        try {
            // GitHub query
            if (FlagMaster.getInstance().getGHFlag()) {
                Connector.getInstance().getGHClient().query(GhRepoFeedQuery.builder()
                    .searchString(ghSearchString)
                    .cursor(paginationData.getPagination(Connector.Service.GITHUB).endCursor)
                    .build())
                    .enqueue(new ApolloCall.Callback<GhRepoFeedQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GhRepoFeedQuery.Data> response) {
                            GhRepoFeedQuery.Data data = response.getData();
                            if (data != null) {
                                GhRepoFeedQuery.Search search = data.search();
                                GhRepoFeedQuery.PageInfo pageInfo = search.pageInfo();
                                setPagination(Connector.Service.GITHUB, new Pagination(pageInfo.hasNextPage(), pageInfo.endCursor()));

                                List<GhRepoFeedQuery.Node> nodes = search.nodes();
                                if (nodes != null) {
                                    ghRepositories = new RepoCardData[nodes.size()];
                                    for (int i = 0; i < ghRepositories.length; ++i) {
                                        RepoCardData repoCardData = new RepoCardData();
                                        repoCardData.service = Connector.Service.GITHUB.shortName();
                                        GhRepoFeedQuery.AsRepository repository = (GhRepoFeedQuery.AsRepository) nodes.get(i);
                                        repoCardData.url = repository.url().toString();
                                        repoCardData.id = repository.id();
                                        repoCardData.name = repository.nameWithOwner();
                                        GhRepoFeedQuery.PrimaryLanguage _pl = repository.primaryLanguage();
                                        repoCardData.language = (_pl == null) ? null : _pl.name();
                                        repoCardData.description = repository.description();
                                        GhRepoFeedQuery.Owner _o = repository.owner();
                                        repoCardData.profilePicUrl = (_o == null) ? null : _o.avatarUrl().toString();
                                        repoCardData.rating = 0;             // TODO: implement ratings
                                        repoCardData.valRated = 0;           // TODO: implement ratings
                                        repoCardData.comments = 0;           // TODO: implement comments
                                        repoCardData.isCommented = false;    // TODO: implement comments
                                        GhRepoFeedQuery.Stargazers _sg = repository.stargazers();
                                        repoCardData.stars = (_sg == null) ? null : _sg.totalCount();
                                        repoCardData.isStarred = repository.viewerHasStarred();
                                        repoCardData.forks = repository.forkCount();
                                        GhRepoFeedQuery.Forks _f = repository.forks();
                                        repoCardData.isForked = (_f == null) ? false : _f.totalCount() > 0;
                                        ghRepositories[i] = repoCardData;

                                        FirebaseFirestore.getInstance().collection("RepoComments").whereEqualTo("RepoUrl", repoCardData.url).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful() && task.getResult().size() != 0) {
                                                    repoCardData.rating = Math.toIntExact((long) task.getResult().getDocuments().get(0).get("Votes"));
                                                }
                                                else
                                                {
                                                    repoCardData.rating = 0;
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    ghRepositories = new RepoCardData[]{};
                                }

                                // combine data and execute callback
                                combineData();
                            } else if (errorCallback != null) {
                                errorCallback.error("Failed query: data is NULL");
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("ERROR", "error: " + e.toString());
                            if (errorCallback != null)
                                errorCallback.error("Failed query: " + e.getMessage());
                        }
                    });
            }

            // GitLab query
            if (FlagMaster.getInstance().getGLFlag()) {
                Connector.getInstance().getGLClient().query(GlRepoFeedQuery.builder()
                    .searchString(searchString)
                    .cursor(paginationData.getPagination(Connector.Service.GITLAB).endCursor)
                    .build())
                    .enqueue(new ApolloCall.Callback<GlRepoFeedQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GlRepoFeedQuery.Data> response) {
                            GlRepoFeedQuery.Data data = response.getData();
                            if (data != null) {
                                GlRepoFeedQuery.Projects projects = data.projects();
                                assert projects != null;
                                GlRepoFeedQuery.PageInfo pageInfo = projects.pageInfo();
                                setPagination(Connector.Service.GITLAB, new Pagination(pageInfo.hasNextPage(), pageInfo.endCursor()));

                                List<GlRepoFeedQuery.Node> nodes = projects.nodes();
                                if (nodes != null) {
                                    glRepositories = new RepoCardData[nodes.size()];
                                    for (int i = 0; i < glRepositories.length; ++i) {
                                        RepoCardData repoCardData = new RepoCardData();
                                        GlRepoFeedQuery.Node project = nodes.get(i);
                                        repoCardData.service =  Connector.Service.GITLAB.shortName();
                                        repoCardData.url = project.webUrl();
                                        repoCardData.name = project.fullPath();
                                        repoCardData.language = null; // TODO: Figure out how to get language information from GitLab
                                        repoCardData.description = project.description();
                                        repoCardData.profilePicUrl = project.avatarUrl();
                                        repoCardData.rating = 0;             // TODO: implement ratings
                                        repoCardData.valRated = 0;           // TODO: implement ratings
                                        repoCardData.comments = 0;           // TODO: implement comments
                                        repoCardData.isCommented = false;    // TODO: implement comments
                                        repoCardData.stars = project.starCount();
                                        repoCardData.isStarred = false; // TODO: Figure out how to get this information from GitLab
                                        repoCardData.forks = project.forksCount();
                                        repoCardData.isForked = false; // TODO: Figure out how to get this information from GitLab
                                        glRepositories[i] = repoCardData;
                                    }
                                } else {
                                    glRepositories = new RepoCardData[]{};
                                }

                                // combine data and execute callback
                                combineData();
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
        } catch (Exception e) {
            errorCallback.error("Connector exception: " + e.getMessage());
        }
    }

    private void combineData() {
        // do nothing if we haven't received the data yet
        if(FlagMaster.getInstance().getGHFlag()) {
            if(ghRepositories == null) return;
        }
        if(FlagMaster.getInstance().getGLFlag()) {
            if(glRepositories == null) return;
        }

        // TODO: intelligently combine + order + hide repos of the data sources here based off of repository information
        //       (for now we're going to display GitHub repos before GitLab repos, we're not hiding any repos, etc.)

        // get sizes of repo arrays
        int ghRepoSize = repoCardDataSize(ghRepositories);
        int glRepoSize = repoCardDataSize(glRepositories);

        // initialize repos array to proper size
        repositories = new RepoCardData[ghRepoSize + glRepoSize];

        // populate repos array with GitHub and GitLab data (if exists)
        populateRepos(ghRepoSize, glRepoSize);

        if (successCallback != null) successCallback.handle(this);
    }

    public int repoCardDataSize(RepoCardData[] repoCardData) {
        if(repoCardData != null) {
            return repoCardData.length;
        } else {
            return 0;
        }
    }

    public void populateRepos(int ghRepoSize, int glRepoSize) {
        for (int i = 0; i < ghRepoSize; ++i) {
            repositories[i] = ghRepositories[i];
        }
        for (int i = 0; i < glRepoSize; ++i) {
            repositories[ghRepoSize + i] = glRepositories[i];
        }
    }
}
