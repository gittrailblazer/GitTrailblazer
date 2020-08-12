package com.git_trailblazer.v1;

import android.util.Log;

import com.git_trailblazer.v1.connector.CommitDetailsData;
import com.git_trailblazer.v1.connector.Connector;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores repo contributor history
 */
public class Contributor {
    private String name, imageURL;
    private int numCommits;

    public Contributor(String name, String imageURL, int numCommits) {
        this.name = name;
        this.imageURL = imageURL;
        this.numCommits = numCommits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getNumCommits() {
        return numCommits;
    }

    public void setNumCommits(int numCommits) {
        this.numCommits = numCommits;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", numCommits=" + numCommits +
                '}';
    }

    /**
     * Generate mock contributor data
     */
    public static ArrayList<Contributor> generateContributorMockData(ArrayList<Contributor> contributors) {
        for(int i = 0; i < 15; i++) {
            String contributorName = "Contributor " + (i+1);
            String contributorImageURL = "drawable://" + R.drawable.default_profile;
            int contributorNumCommits = 0;
            Contributor contributor= new Contributor(contributorName, contributorImageURL, contributorNumCommits);
            contributors.add(contributor);
        }
        return contributors;
    }

    /**
     * Generate real contributor data (all data obtained from commit query)
     */
    public static ArrayList<Contributor> generateContributorData(ArrayList<Contributor> contributors, int safeListSize, String repoName, String repoOwner) {
        boolean finished = false;
        new Connector.Query(Connector.QueryType.COMMIT_DETAILS, repoName, repoOwner)
                .exec(new Connector.ISuccessCallback() {
                    @Override
                    public void handle(Object result) {
                        CommitDetailsData data = (CommitDetailsData) result;
                        ArrayList<Commit> allRepoCommits = data.allRepoCommits;
                        HashMap <String, Contributor> repoContributorMap = new HashMap<>();

                        // populate map of distinct contributors and iteratively update numCommits
                        for(int i = 0; i < allRepoCommits.size(); i++) {
                            // use author name as key
                            String authorKey = allRepoCommits.get(i).getContributorName();

                            // if key exists, then increment numCommits of current contributor by 1
                            if(repoContributorMap.containsKey(authorKey)) {
                                Contributor copy = repoContributorMap.get(authorKey);
                                copy.setNumCommits(copy.getNumCommits() + 1);
                                repoContributorMap.put(authorKey, copy);
                                Log.d("COMMIT: ", authorKey + ": " + copy.getNumCommits() + " commits");
                            } else {
                                // initialize new Contributor with 1 commit
                                Contributor newContributor = new Contributor(authorKey, allRepoCommits.get(i).getContributorImageURL(), 1);
                                repoContributorMap.put(authorKey, newContributor);
                                Log.d("COMMIT: ", authorKey + ": " + newContributor.getNumCommits() + " commits");
                            }
                        }

                        Contributor lastContributor = new Contributor("", "", 0);

                        // generate list of all contributors
                        for (Contributor currContributor : repoContributorMap.values()) {
                            contributors.add(currContributor);
                            lastContributor = currContributor;
                        }

                        while(contributors.size() < safeListSize) {
                            contributors.add(lastContributor);
                        }
                    }
                }, new Connector.IErrorCallback() {
                    @Override
                    public void error(String message) {
                        Log.d("GH_API_QUERY", "Failed query: " + message);
                    }

                });
        while(contributors.size() < safeListSize) {
            // do nothing
        }
        return contributors;
    }
}