package com.example.githubtrailblazer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Stores repo contributor history
 */
public class Commit {
    private String contributorName, contributorImageURL, commitDate, commitDescription;

    public Commit(String contributorName, String contributorImageURL, String commitDate, String commitDescription) {
        this.contributorName = contributorName;
        this.contributorImageURL = contributorImageURL;
        this.commitDate = commitDate;
        this.commitDescription = commitDescription;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    public String getContributorImageURL() {
        return contributorImageURL;
    }

    public void setContributorImageURL(String contributorImageURL) {
        this.contributorImageURL = contributorImageURL;
    }

    public String getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(String commitDate) {
        this.commitDate = commitDate;
    }

    public String getCommitDescription() {
        return commitDescription;
    }

    public void setCommitDescription(String commitDescription) {
        this.commitDescription = commitDescription;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "contributorName='" + contributorName + '\'' +
                ", contributorImageURL='" + contributorImageURL + '\'' +
                ", commitDate='" + commitDate + '\'' +
                ", commitDescription='" + commitDescription + '\'' +
                '}';
    }

    /**
     * Generate mock contribution data
     */
    public static ArrayList<Commit> generateCommitMockData(ArrayList<Commit> commits) {
        for(int i = 0; i < 15; i++) {
            String contributorName = "Contributor " + (i + 1);
            String contributorImageURL = "drawable://" + R.drawable.default_profile;
            Random r = new Random();
            int month = r.nextInt(13 - 1) + 1;
            int day = r.nextInt(32 - 1) + 1;
            int year = r.nextInt(2001 - 1) + 1990;
            String contributionDate = month + "-" + day + "-" + year;
            String contributionDescription = "This is a dummy description for this commit message.";
            Commit commit = new Commit(contributorName, contributorImageURL, contributionDate, contributionDescription);
            commits.add(commit);
        }
        return commits;
    }
}