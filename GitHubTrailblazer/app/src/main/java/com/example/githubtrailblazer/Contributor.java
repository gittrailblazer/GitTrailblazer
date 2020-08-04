package com.example.githubtrailblazer;

import java.util.ArrayList;

/**
 * Stores repo contributor history
 */
public class Contributor {
    private String name, imageURL, numCommits;

    public Contributor(String name, String imageURL, String numCommits) {
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

    public String getNumCommits() {
        return numCommits;
    }

    public void setNumCommits(String numCommits) {
        this.numCommits = numCommits;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", numCommits='" + numCommits + '\'' +
                '}';
    }

    /**
     * Generate mock contributor data
     */
    public static ArrayList<Contributor> generateContributorMockData(ArrayList<Contributor> contributors) {
        for(int i = 0; i < 15; i++) {
            String contributorName = "Contributor " + (i+1);
            String contributorImageURL = "drawable://" + R.drawable.default_profile;
            String contributorNumCommits = "X commits";
            Contributor contributor= new Contributor(contributorName, contributorImageURL, contributorNumCommits);
            contributors.add(contributor);
        }
        return contributors;
    }
}