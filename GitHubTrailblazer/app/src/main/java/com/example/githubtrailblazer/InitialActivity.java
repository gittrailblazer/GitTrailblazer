package com.example.githubtrailblazer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.githubtrailblazer.connector.Connector;
import com.example.githubtrailblazer.connector.UserDetailsData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class InitialActivity extends AppCompatActivity {
    private static String gitlabPersonalAccessToken;
    // define UI variables
    private Button mGitHubBtn, mEmailBtn;
    private TextView mCreateAccount;

    // define Firebase authentication variables
    FirebaseAuth mAuth;
    OAuthProvider.Builder provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        // assign UI variables to UI elements
        mGitHubBtn = findViewById(R.id.github_login_btn);
        mEmailBtn = findViewById(R.id.email_login_btn);
        mCreateAccount = findViewById(R.id.text_view_create_account);

        // instantiate Firebase authentication variables
        mAuth = FirebaseAuth.getInstance();
        provider = OAuthProvider.newBuilder("github.com");

        // TODO: Get this from Firebase
        gitlabPersonalAccessToken = "LpuWHYx7gQjidpynpyxF";

        // on-click listener for registering a user with GitHub credentials
        mGitHubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
                if (pendingResultTask != null) {
                    // There's something already here! Finish the sign-in for your user.
                    pendingResultTask
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // initialize connector with oauth access token
                                    String accessToken = ((OAuthCredential) authResult.getCredential()).getAccessToken();
                                    Connector.initialize(accessToken, gitlabPersonalAccessToken);

                                    addGithubUserAndRedirect();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InitialActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mAuth
                            .startActivityForSignInWithProvider(/* activity= */ InitialActivity.this, provider.build())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // initialize connector with oauth access token
                                    String accessToken = ((OAuthCredential) authResult.getCredential()).getAccessToken();
                                    Connector.initialize(accessToken, gitlabPersonalAccessToken);

                                    addGithubUserAndRedirect();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InitialActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // on-click listener for sending user to login activity
        mEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        // on-click listener for sending user to register activity
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            //startActivity(intent);

        } else {
            //Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a Users table entry for the GitHub user (if it doesn't exist) and redirects:
     * 1. new users to the questionnaire; and
     * 2. existing users to the main activity
     */
    private void addGithubUserAndRedirect() {
        new Connector.Query(Connector.QueryType.USER_DETAILS)
                .exec(new Connector.ISuccessCallback() {
                    @Override
                    public void handle(Object result) {
                        UserDetailsData data = (UserDetailsData) result;
                        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("githubID", data.id)
                                .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult().size() == 0) {
                                    User user = new User(data.id, data.username, true);
                                    FirebaseFirestore.getInstance().collection("Users").add(user);
                                    // send new users to questionnaire activity
                                    Intent intent = new Intent(InitialActivity.this, QuestionaireActivity.class);
                                    finish();
                                    startActivity(intent);
                                } else if (task.isSuccessful() && task.getResult().size() == 1) {
                                    // send existing users to the main activity
                                    Intent intent = new Intent(InitialActivity.this, DrawerActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }, new Connector.IErrorCallback() {
                    @Override
                    public void error(String message) {
                        Log.d("GH_API_QUERY", "Failed query: " + message);
                    }
                });
    }
}
