package com.example.githubtrailblazer.ui.repositories.notification;

import com.example.githubtrailblazer.components.Notification;

public class NotificationEntry {
    private Notification.Type type;
    private String username;
    private String comment;
    private String repository;

    public NotificationEntry() {

    }

    public NotificationEntry(int type, String username, String comment, String repository) {
        this.type = Notification.Type.get(type);
        this.username = username;
        this.comment = comment;
        this.repository = repository;
    }

    public Notification.Type getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public String getRepository() {
        return repository;
    }

    public void setType(int id) {
        type = Notification.Type.get(id);
    }

    public void setUsername(String s) {
        username = s;
    }

    public void setComment(String s) {
        comment = s;
    }

    public void setRepository(String s) {
        repository = s;
    }
}
