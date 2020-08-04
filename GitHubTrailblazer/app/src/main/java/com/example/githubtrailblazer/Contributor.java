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

    /**
     * Generate demo contributor data
     */
    public static ArrayList<Contributor> generateDemoContributorMockData(ArrayList<Contributor> contributors) {
        String contributorImageURL = "drawable://" + R.drawable.default_profile;

        Contributor contributor1 = new Contributor("vinta", contributorImageURL, "406 commits");
        contributors.add(contributor1);
        Contributor contributor2 = new Contributor("dhamaniasad", contributorImageURL, "11 commits");
        contributors.add(contributor2);
        Contributor contributor3 = new Contributor("vndmtrx", contributorImageURL, "9 commits");
        contributors.add(contributor3);
        Contributor contributor4 = new Contributor("nicoe", contributorImageURL, "7 commits");
        contributors.add(contributor4);
        Contributor contributor5 = new Contributor("ellisonleao", contributorImageURL, "7 commits");
        contributors.add(contributor5);
        Contributor contributor6 = new Contributor("Alir3z4", contributorImageURL, "7 commits");
        contributors.add(contributor6);
        Contributor contributor7 = new Contributor("quobit", contributorImageURL, "6 commits");
        contributors.add(contributor7);
        Contributor contributor8 = new Contributor("agroszer", contributorImageURL, "6 commits");
        contributors.add(contributor8);
        Contributor contributor9 = new Contributor("probar", contributorImageURL, "5 commits");
        contributors.add(contributor9);
        Contributor contributor10 = new Contributor("makrusak", contributorImageURL, "5 commits");
        contributors.add(contributor10);

        return contributors;
    }
}