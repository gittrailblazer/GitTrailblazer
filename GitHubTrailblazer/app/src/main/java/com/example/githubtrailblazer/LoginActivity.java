package com.example.githubtrailblazer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.githubtrailblazer.ghapi.GitHubConnector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity
{
    // define UI variables
    private EditText mEmail, mPassword;
    private Button mGitHubBtn, mLoginBtn;
    private TextView mRegisterHere;
    private ProgressBar mProgressBar;

    // define Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    OAuthProvider.Builder provider;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // assign UI variables to UI elements
        mEmail        = findViewById(R.id.login_email_et);
        mPassword     = findViewById(R.id.login_password_et);
        mGitHubBtn    = findViewById(R.id.login_continue_with_git_btn);
        mLoginBtn     = findViewById(R.id.login_login_btn);
        mRegisterHere = findViewById(R.id.login_registerHere_tv);
        mProgressBar  = findViewById(R.id.login_progressBar_pb);

        // instantiate Firebase variables
        mAuth = FirebaseAuth.getInstance();
        provider = OAuthProvider.newBuilder("github.com");
        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser fFireBaseUser = mAuth.getCurrentUser();

                if(fFireBaseUser != null)
                {
                    //Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(LoginActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                }
            }
        };

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
                                    //authResult.getAdditionalUserInfo().getProfile();
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().

                                    // initialize connector with oauth access token
                                    String accessToken = ((OAuthCredential) authResult.getCredential()).getAccessToken();
                                    GitHubConnector.initialize(accessToken);

                                    // send user to main activity after successfully signing in with GitHub
                                    //Toast.makeText(InitialActivity.this, "Sign in Success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    mAuth
                            .startActivityForSignInWithProvider(/* activity= */ LoginActivity.this, provider.build())
                            .addOnSuccessListener( new OnSuccessListener<AuthResult>()
                            {
                                @Override
                                public void onSuccess(AuthResult authResult)
                                {
                                    // User is signed in.
                                    // IdP data available in
                                    //authResult.getAdditionalUserInfo().getProfile();
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().

                                    // initialize connector with oauth access token
                                    String accessToken = ((OAuthCredential) authResult.getCredential()).getAccessToken();
                                    GitHubConnector.initialize(accessToken);

                                    // send user to main activity after successfully signing in with GitHub
                                    //Toast.makeText(InitialActivity.this, "Sign in Success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener( new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // make clickable text for sending user to login activity
        String text = "Don't have an account? Register here!";
        SpannableString spanStr = new SpannableString(text);
        ClickableSpan clickableSpanLogin = new ClickableSpan()
        {
            // send user to register activity when 'Register here!' is clicked
            @Override
            public void onClick(@NonNull View widget)
            {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }

            // change color of 'Register here!' to light gray and remove the underline
            @Override
            public void updateDrawState(@NonNull TextPaint ds)
            {
                super.updateDrawState(ds);
                ds.setColor(Color.LTGRAY);
                ds.setUnderlineText(false);
            }
        };
        spanStr.setSpan(clickableSpanLogin, 23, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRegisterHere.setText(spanStr);
        mRegisterHere.setMovementMethod(LinkMovementMethod.getInstance()); // need this line for click to work

        // on-click listener for logging user in and sending user to main activity
        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // make the progress bar visible
                mProgressBar.setVisibility(View.VISIBLE);

                // convert all required fields to Strings
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                // check for empty fields
                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Please enter an email address");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Please enter a password");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // sign in with email and password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Invalid email/password combination", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            finish();
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
