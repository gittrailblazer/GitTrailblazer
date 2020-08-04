package com.example.githubtrailblazer;

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
}