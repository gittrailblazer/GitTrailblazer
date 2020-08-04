package com.example.githubtrailblazer;

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
}