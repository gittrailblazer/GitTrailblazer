package com.example.githubtrailblazer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class CommentActivity extends AppCompatActivity {
    String RepoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Get the repo url as unique ID
//        Intent intent = getIntent();
//        RepoUrl =  (String) intent.getSerializableExtra("url");
        System.out.println(RepoUrl);

    }
}