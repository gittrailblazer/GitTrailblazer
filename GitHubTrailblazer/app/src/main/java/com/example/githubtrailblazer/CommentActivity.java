package com.example.githubtrailblazer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.githubtrailblazer.components.ProjectComment.Comment;
import com.example.githubtrailblazer.components.ProjectComment.CommentAdapter;
import com.example.githubtrailblazer.components.ProjectComment.CommentThread;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    String RepoUrl;
    EditText addcomment;
    TextView post;
    ArrayList<Comment> comments;
    ArrayList<String> comments_str = new ArrayList<>();
    DocumentReference docRef;

    CommentAdapter commentAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Get the repo url as unique ID
        Intent intent = getIntent();
        RepoUrl =  (String) intent.getSerializableExtra("url");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Get comments for previous for the same repo
        comments = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("RepoComments").whereEqualTo("RepoUrl", RepoUrl).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty())
                {
                    // user records to create a list of comment thread
                    DocumentSnapshot comment_doc = task.getResult().getDocuments().get(0);
                    comments_str = (ArrayList<String>) comment_doc.get("Comments");
                    for(String thread: comments_str)
                    {
                        Comment ct = new Comment(thread);
                        comments.add(ct);
                    }
                    docRef = FirebaseFirestore.getInstance().collection("RepoComments").document(comment_doc.getId());

                }
                else
                {
                    docRef = FirebaseFirestore.getInstance().collection("RepoComments").document();
                    docRef.update("RepoUrl", RepoUrl);
                }

                // use doc from firebase to update ListView
                commentAdapter = new CommentAdapter( CommentActivity.this, comments);
                recyclerView.setAdapter(commentAdapter);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addcomment = findViewById(R.id.add_comment);
        post = findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals(""))
                {
                    Toast.makeText(CommentActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addComment();
                }
            }
        });
    }

    private void addComment(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Comment newComment = new Comment("kkenttt", addcomment.getText().toString(), new Date());
        comments.add(newComment);
        comments_str.add(newComment.comment_str());
        docRef.update("Comments", comments_str);
        addcomment.setText("");
    }
}