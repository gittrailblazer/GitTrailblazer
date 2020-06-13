package com.example.githubtrailblazer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{
    // define UI variables
    private Button mLogoutBtn;
    private Button mDeleAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign UI variables to UI elements
        mLogoutBtn = findViewById(R.id.main_logout_btn);
        mDeleAccountBtn = findViewById(R.id.main_deleAccount_btn);

        // on-click listener for logging user out and sending them to login activity
        mLogoutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
