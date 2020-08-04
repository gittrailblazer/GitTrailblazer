package com.example.githubtrailblazer.data;

import java.io.Serializable;

/**
 * RepoCardData class
 */
public class RepoCardData implements Serializable {
    public String service;
    public String url;
    public String name;
    public String owner;
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
}