package com.example.githubtrailblazer.components.ProjectComment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CommentThread {
    Comment head_comment;
    ArrayList<Comment> follow_up;

    public CommentThread(Comment head_comment)
    {
        this.head_comment = head_comment;
    }

    public CommentThread(String comments) {
        try {
            String[] comments_comp = comments.split(",");
            head_comment = new Comment(comments_comp[0], comments_comp[1], new SimpleDateFormat("dd/MM/yyyy").parse(comments_comp[2]));
            for (int i = 3; i < comments_comp.length; i += 3) {
                follow_up.add(new Comment(comments_comp[i], comments_comp[i + 1], new SimpleDateFormat("dd/MM/yyyy").parse(comments_comp[i + 2])));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void print()
    {
        System.out.println(head_comment);
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
