package com.example.githubtrailblazer;

public class User {
    private String fullName, email;
    private String GithubID, GithubName;

    public User ()
    {
        // empty constructor required for FireStore
    }

    public User(String fullName, String email)
    {
        this.fullName = fullName;
        this.email = email;
    }

    public User(String GithubID, String login, boolean b)   // this boolean is only used to identify two constructors
    {
        this.GithubID = GithubID;
        GithubName = login;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getGithubID() { return GithubID; }

    public void setGithubID(String newID) { this.GithubID = newID; }

    public String getGithubName() { return GithubName; }

    public void setGithubName(String name) { this.GithubName = name; }

    @Override
    public String toString()
    {
        if(fullName != null) {
            return "User{" +
                    "fullName='" + fullName + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
        else
        {
            return "User{" +
                    "GithubID='" + GithubID + '\'' +
                    ", GithubName='" + GithubName + '\'' +
                    '}';
        }
    }
}
