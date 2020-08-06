package com.example.githubtrailblazer.components.ProjectComment;

import java.text.SimpleDateFormat;
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

    public Comment(String comment_str)
    {
        String[] comment_comp = comment_str.split("&");
        userName = comment_comp[0];
        comment = comment_comp[1];
        try
        {
            comment_date = new SimpleDateFormat("dd/MM/yyyy").parse(comment_comp[2]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get comment string
     * @return the comment string
     */
    public String comment_str()
    {
        return userName + '&' + comment + '&' + comment_date.toString();
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
