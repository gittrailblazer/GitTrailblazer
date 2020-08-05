package com.example.githubtrailblazer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity
{
    // define UI variables
    private EditText mFullName, mEmail, mPassword, mConfirmPassword;
    private Button mCreateAccountBtn, mCancelBtn;
    private ProgressBar mProgressBar;

    // define Firebase variables
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private CollectionReference usersRef;

    // unique id of current user
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // assign UI variables to UI elements
        mFullName         = findViewById(R.id.reg_edit_text_full_name);
        mEmail            = findViewById(R.id.reg_edit_text_email);
        mPassword         = findViewById(R.id.reg_edit_text_password);
        mConfirmPassword  = findViewById(R.id.reg_edit_text_confirm_password);
        mCreateAccountBtn = findViewById(R.id.reg_btn_create_account);
        mCancelBtn        = findViewById(R.id.reg_btn_cancel);
        mProgressBar      = findViewById(R.id.reg_progress_bar_create_account);

        // instantiate Firebase variables
        fAuth    = FirebaseAuth.getInstance();
        fStore   = FirebaseFirestore.getInstance();
        usersRef = fStore.collection("Users");

        // on-click listener for sending user to main activity
        mCreateAccountBtn.setOnClickListener(new View.OnClickListener()
        {
            // when the user clicks the submit button
            @Override
            public void onClick(View v)
            {
                // make the progress bar visible
                mProgressBar.setVisibility(View.VISIBLE);

                // convert all required fields to Strings
                final String fullName  = mFullName.getText().toString().trim();
                final String email     = mEmail.getText().toString();
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

                            // set emailFlag to true (needed for Drawer Activity)
                            LoginActivity.emailFlag = true;

                            // send user to questionnaire activity
                            Intent intent = new Intent(RegisterActivity.this, QuestionnaireActivity.class);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            finish();
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, InitialActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
