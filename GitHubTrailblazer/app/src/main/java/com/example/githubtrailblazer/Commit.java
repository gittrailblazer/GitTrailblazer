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

    /**
     * Generate mock contribution data
     */
    public static ArrayList<Commit> generateDemoCommitMockData(ArrayList<Commit> commits) {
        String contributorImageURL = "drawable://" + R.drawable.default_profile;
        Commit commit = new Commit("vinta", contributorImageURL, "Aug 2, 2020",
                "Merge pull request #1585 from look4regev/sort-readme");
        commits.add(commit);

        Commit commit2 = new Commit("look4regev", contributorImageURL, "Aug 2, 2020",
                "Sort readme and add to docs build");
        commits.add(commit2);
        Commit commit3 = new Commit("vinta", contributorImageURL, "Jul 27, 2020",
                "Sort readme and add to docs build");
        commits.add(commit3);
        Commit commit4 = new Commit("jcupitt", contributorImageURL, "Jul 26, 2020",
                "Merge branch 'master' of https://github.com/vinta/awesome-python into… ");
        commits.add(commit4);
        Commit commit5 = new Commit("vinta", contributorImageURL, "Jul 25, 2020",
                "Merge pull request #1576 from rpdelaney/structlog");
        commits.add(commit5);
        Commit commit6 = new Commit("rpdelaney", contributorImageURL, "Jul 14, 2020",
                "add structlog");
        commits.add(commit6);
        Commit commit7 = new Commit("vinta", contributorImageURL, "Jul 12, 2020",
                "Merge pull request #1575 from thePrankster/master");
        commits.add(commit7);
        Commit commit8 = new Commit("thePrankster", contributorImageURL, "Jul 12, 2020",
                "Merge pull request #1 from thePrankster/thePrankster ");
        commits.add(commit8);
        Commit commit9 = new Commit("thePrankster", contributorImageURL, "Jul 12, 2020",
                "Added Arcade");
        commits.add(commit9);
        Commit commit10 = new Commit("vinta", contributorImageURL, "Jul 10, 2020",
                "Merge pull request #1573 from codingCapricorn/patch-1");
        commits.add(commit9);

        return commits;
    }
}