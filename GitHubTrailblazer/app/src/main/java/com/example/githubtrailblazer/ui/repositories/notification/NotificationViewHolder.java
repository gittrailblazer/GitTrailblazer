package com.example.githubtrailblazer.ui.repositories.notification;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.githubtrailblazer.components.Notification;

// stores and recycles views as they are scrolled off screen
public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Notification entryView;

    public NotificationViewHolder(View v) {
        super(v);
        entryView = (Notification) v;
    }

    public void setEntryView(NotificationEntry e) {
        entryView.setEntry(entryView.getContext(), e);
    }

    @Override
    public void onClick(View v) {

    }
}
