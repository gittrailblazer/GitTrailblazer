package com.example.githubtrailblazer.data;

/**
 * IsseuCardData class
 */
public class IssueCardData {
    public String service;
    public Integer id;
    public Integer number;
    public String title;
    public String state;
    public String url;
    public RepoData repoData;

    /**
     * IssueCardData.RepoData class
     */
    public class RepoData {
        public String name;
        public String language;
        public String description;
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
