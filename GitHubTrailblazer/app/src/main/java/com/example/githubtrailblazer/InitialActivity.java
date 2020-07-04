package com.example.githubtrailblazer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.githubtrailblazer.ghapi.GitHubConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class InitialActivity extends AppCompatActivity
{
    // define UI variables
    private Button mGitHubBtn, mRegisterBtn, mLoginBtn;

    // define Firebase authentication variables
    FirebaseAuth mAuth;
    OAuthProvider.Builder provider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        // assign UI variables to UI elements
        mGitHubBtn = findViewById(R.id.initial_github_btn);
        mRegisterBtn = findViewById(R.id.initial_register_btn);
        mLoginBtn = findViewById(R.id.initial_login_btn);

        // instantiate Firebase authentication variables
        mAuth = FirebaseAuth.getInstance();
        provider = OAuthProvider.newBuilder("github.com");


        // on-click listener for registering a user with GitHub credentials
        mGitHubBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
                if (pendingResultTask != null)
                {
                    // There's something already here! Finish the sign-in for your user.
                    pendingResultTask
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                            {
                                @Override
                                public void onSuccess(AuthResult authResult)
                                {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile();
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken();

                                    // initialize connector with oauth access token
                                    String accessToken = ((OAuthCredential) authResult.getCredential()).getAccessToken();
                                    GitHubConnector.initialize(accessToken);

                                    User user = new User(accessToken);
                                    FirebaseFirestore.getInstance().collection("Users").add(user);

                                    // send user to main activity after successfully signing in with GitHub
                                    //Toast.makeText(InitialActivity.this, "Sign in Success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(InitialActivity.this, DrawerActivity.class);
                                    finish();
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(InitialActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    mAuth
                        .startActivityForSignInWithProvider(/* activity= */ InitialActivity.this, provider.build())
                        .addOnSuccessListener( new OnSuccessListener<AuthResult>()
                        {
                            @Override
                            public void onSuccess(AuthResult authResult)
                            {
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile();
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken();

                                // initialize connector with oauth access token
                                String accessToken = ((OAuthCredential) authResult.getCredential()).getAccessToken();
                                GitHubConnector.initialize(accessToken);

                                User user = new User(accessToken);
                                FirebaseFirestore.getInstance().collection("Users").add(user);

                                // send user to main activity after successfully signing in with GitHub
                                //Toast.makeText(InitialActivity.this, "Sign in Success!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InitialActivity.this, DrawerActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener( new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(InitialActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        // on-click listener for sending user to registration activity
        mRegisterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(InitialActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });

        // on-click listener for sending user to login activity
        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(InitialActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user)
    {
        if(user != null)
        {
            //Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            //startActivity(intent);

        }
        else
        {
            //Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
