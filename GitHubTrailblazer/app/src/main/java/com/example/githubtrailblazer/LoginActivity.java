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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity
{
    // define UI variables
    private EditText mEmail, mPassword;
    private Button mSignInBtn, mCancelButton;
    private TextView mForgotPassword;
    private ProgressBar mProgressBar;

    // flag for email sign in
    public static boolean emailFlag;

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
        mEmail        = findViewById(R.id.edit_text_email);
        mPassword     = findViewById(R.id.edit_text_password);
        mSignInBtn    = findViewById(R.id.btn_sign_in);
        mCancelButton = findViewById(R.id.btn_cancel);
        mProgressBar  = findViewById(R.id.progress_bar_sign_in);

        // set email sign in flag to false
        emailFlag = false;

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



        // on-click listener for logging user in and sending user to main activity
        mSignInBtn.setOnClickListener(new View.OnClickListener()
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
                            // set emailFlag to true (needed for Drawer Activity)
                            emailFlag = true;
                            Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            finish();
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, InitialActivity.class);
                finish();
                startActivity(intent);
            }
        });

        // TO DO: 'Forgot my password' functionality
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
