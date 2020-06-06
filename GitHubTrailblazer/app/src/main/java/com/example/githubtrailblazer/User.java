package com.example.githubtrailblazer;

public class User {
    private String fullName, email;

    public User ()
    {
        // empty constructor required for FireStore
    }

    public User(String fullName, String email)
    {
        this.fullName = fullName;
        this.email = email;
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

    @Override
    public String toString()
    {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
