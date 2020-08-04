package com.example.githubtrailblazer.components.ProjectComment;

import java.util.ArrayList;

public class CommentThread {
    Comment head_comment;
    ArrayList<Comment> follow_up;

    public CommentThread(Comment head_comment)
    {
        this.head_comment = head_comment;
    }

    @Override
    public String toString() {
        String thread = "Thread{";
        for (Comment comment: follow_up)
        {
            thread += comment.toString() + '\'';
        }
        thread += '}';
        return thread;
    }
}
