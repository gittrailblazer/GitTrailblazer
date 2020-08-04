package com.example.githubtrailblazer.components.ProjectComment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.githubtrailblazer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Comment> mComments;

    public CommentAdapter(Context mContext, ArrayList<Comment> mComments)
    {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComments.get(position);

        holder.comment.setText(comment.comment + "\n");
        holder.user_name.setText(comment.userName);
//        if(comment.comment_date != null) {
//            holder.date.setText(comment.comment_date.toString());
//        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView user_name, comment, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            user_name = itemView.findViewById(R.id.cmt_username);
            comment = itemView.findViewById(R.id.comment_content);
            date = itemView.findViewById(R.id.cmt_date);
        }

    }
}
