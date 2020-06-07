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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import io.opencensus.trace.Span;

public class RegisterActivity extends AppCompatActivity
{
    // define UI variables
    private EditText mFullName, mEmail, mPassword, mConfirmPassword;
    private Button mSubmitBtn;
    private TextView mLogin;
    private ProgressBar mProgressBar;

    // define and instantiate Firebase variables
    private FirebaseAuth fAuth;           //= FirebaseAuth.getInstance();;
    private FirebaseFirestore fStore;     //= FirebaseFirestore.getInstance();;
    private CollectionReference usersRef; //= fStore.collection("Users");;

    // unique id of current user
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // assign UI variables to UI elements
        mFullName        = findViewById(R.id.register_fullname_et);
        mEmail           = findViewById(R.id.register_email_et);
        mPassword        = findViewById(R.id.register_password_et);
        mConfirmPassword = findViewById(R.id.register_confirm_password_et);
        mSubmitBtn       = findViewById(R.id.register_register_btn);
        mLogin           = findViewById(R.id.register_login_tv);
        mProgressBar     = findViewById(R.id.register_progressBar_pb);

        // instantiate Firebase variables
        fAuth    = FirebaseAuth.getInstance();
        fStore   = FirebaseFirestore.getInstance();
        usersRef = fStore.collection("Users");

        // make clickable text for sending user to login activity
        String text = "Already have an account? Login!";
        SpannableString spanStr = new SpannableString(text);
        ClickableSpan clickableSpanLogin = new ClickableSpan()
        {
            // send user to login activity when Login! is clicked
            @Override
            public void onClick(@NonNull View widget)
            {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            // change color of Login! to light gray and remove the underline
            @Override
            public void updateDrawState(@NonNull TextPaint ds)
            {
                super.updateDrawState(ds);
                ds.setColor(Color.LTGRAY);
                ds.setUnderlineText(false);
            }
        };
        spanStr.setSpan(clickableSpanLogin, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mLogin.setText(spanStr);
        mLogin.setMovementMethod(LinkMovementMethod.getInstance()); // need this line for click to work

        // on-click listener for sending user to main activity
        mSubmitBtn.setOnClickListener(new View.OnClickListener()
        {
            // when the user clicks the submit button
            @Override
            public void onClick(View v)
            {
                // make the progress bar visible
                mProgressBar.setVisibility(View.VISIBLE);

                // convert all required fields to Strings
                final String fullName        = mFullName.getText().toString().trim();
                final String email           = mEmail.getText().toString();
                String password        = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();

                // check for empty fields
                if(TextUtils.isEmpty(fullName))
                {
                    mFullName.setError("Please enter your name");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }
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
                if(TextUtils.isEmpty(confirmPassword))
                {
                    mConfirmPassword.setError("Please confirm your password");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                // check for valid (i.e., strong) password
                if(password.length() < 6)
                {
                    mPassword.setError("Password must be >= 6 characters");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                // check that password is the same as confirm password
                if(!password.equals(confirmPassword))
                {
                    mPassword.setError("Passwords do not match");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // create new user with email and password
                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // if user account was not created successfully
                        if(!task.isSuccessful())
                        {
                            // print error message and make progress bar invisible
                            Toast.makeText(RegisterActivity.this, "Registration unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                        // if user account was successfully created
                        else
                        {
                            // print success message
                            Toast.makeText(RegisterActivity.this, "Registration was successful!", Toast.LENGTH_SHORT).show();

                            // add user to FireStore cloud database
                            User user = new User(fullName, email);
                            fStore.collection("Users").add(user);

                            // send user to main activity
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}
