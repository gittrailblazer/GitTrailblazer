package com.example.githubtrailblazer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    // define UI variables
    private Button mLogoutBtn;
    private Button mDeleAccountBtn;

    /**
     * Make a hello world query and log the output.
     */
    private void queryHelloWorld() {
        GitHubConnector.initialize().query(
                SearchReposQuery.builder().searchString("hello world").build())
                .enqueue(new ApolloCall.Callback<SearchReposQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SearchReposQuery.Data> response) {
                        Log.d("QUERY", "Successful query: " + response.getData());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("QUERY", "Failed query: " + e.getMessage(), e);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryHelloWorld();

        // assign UI variables to UI elements
        mLogoutBtn = findViewById(R.id.main_logout_btn);
        mDeleAccountBtn = findViewById(R.id.main_deleAccount_btn);

        // on-click listener for logging user out and sending them to login activity
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        // on-click listener for deleting user account and sending them to login activity
        mDeleAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
                Intent intent = new Intent(MainActivity.this, InitialActivity.class);
                startActivity(intent);
            }
        });
    }
}
