package com.example.githubtrailblazer.data;

import java.io.Serializable;

/**
 * RepoCardData class
 */
public class RepoCardData implements Serializable {
    public String service;
    public String url;
    public String name;
    public String language;
    public String description;
    public String profilePicUrl;
    public Integer rating;
    public Integer valRated;
    public Integer comments;
    public Boolean isCommented;
    public Integer stars;
    public Boolean isStarred;
    public Integer forks;
    public Boolean isForked;

    public enum Rating {
        NONE(0),
        UPVOTE(1),
        DOWNVOTE(-1);

        final private int value;

        Rating(int value) {
            this.value = value;
        }

        public static Rating from(Integer valRated) {
            if (valRated == null) return NONE;
            switch (valRated) {
                case 1:
                    return UPVOTE;
                case -1:
                    return DOWNVOTE;
                default:
                    return NONE;
            }
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Create repository card data
     * @param name - project name
     * @param language - project language
     * @param description - project description
     * @param profilePicUrl - project profile photo url
     * @param rating - project rating
     * @param comments - project comments
     * @param stars - project stars
     * @param forks - project forks
     */
    public RepoCardData(String service, String url, String name, String language, String description, String profilePicUrl, Integer rating, Integer valRated, Integer comments, Boolean isCommented, Integer stars, Boolean isStarred, Integer forks,  Boolean isForked) {
        this.service = service;
        this.url = url;
        this.name = name;
        this.language = language;
        this.description = description;
        this.profilePicUrl = profilePicUrl;
        this.rating = rating;
        this.valRated = valRated;
        this.comments = comments;
        this.isCommented = isCommented;
        this.stars = stars;
        this.isStarred = isStarred;
        this.forks = forks;
        this.isForked = isForked;
    }
}