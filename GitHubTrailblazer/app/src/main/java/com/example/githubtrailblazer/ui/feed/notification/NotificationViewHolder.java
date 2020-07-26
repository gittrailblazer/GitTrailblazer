package com.example.githubtrailblazer.ui.feed.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.components.Notification;

import java.util.List;

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
