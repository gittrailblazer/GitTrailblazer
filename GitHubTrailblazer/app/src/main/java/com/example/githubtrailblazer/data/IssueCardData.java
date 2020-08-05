package com.example.githubtrailblazer.data;

/**
 * IsseuCardData class
 */
public class IssueCardData {
    public String service;
    public String id;
    public Integer number;
    public String title;
    public String url;
    public String description;
    public RepoData repoData;
    public Long createdAt;

    /**
     * IssueCardData.RepoData class
     */
    public static class RepoData {
        public String name;
        public String language;
        public Integer rating;
        public Integer valRated;
        public Integer comments;
        public Boolean isCommented;
        public Integer stars;
        public Boolean isStarred;
        public Integer forks;
        public Boolean isForked;
    }
}
