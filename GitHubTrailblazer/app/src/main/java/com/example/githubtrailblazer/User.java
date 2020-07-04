package com.example.githubtrailblazer;

public class User {
    private String fullName, email;
    private String GithubToken;

    public User ()
    {
        // empty constructor required for FireStore
    }

    public User(String fullName, String email)
    {
        this.fullName = fullName;
        this.email = email;
    }

    public User(String GithubToken)
    {
        this.GithubToken = GithubToken;
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

    public String getGithubToken() { return  GithubToken; }

    public void setGithubToken(String newToken) { this.GithubToken = newToken; }

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
                    "GithubToken='" + GithubToken + '\'' +
                    '}';
        }
    }
}
