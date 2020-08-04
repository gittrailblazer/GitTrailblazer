package com.example.githubtrailblazer.components.ProjectComment;

import java.util.Date;

public class Comment {
    String userName;
    String comment;
    Date comment_date;

    public Comment(String userName, String comment, Date date)
    {
        this.userName = userName;
        this.comment = comment;
        comment_date = date;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user='" + userName + '\'' +
                ", comment='" + comment + '\'' +
                ", date='" + comment_date + '\'' +
                '}';
    }
}
