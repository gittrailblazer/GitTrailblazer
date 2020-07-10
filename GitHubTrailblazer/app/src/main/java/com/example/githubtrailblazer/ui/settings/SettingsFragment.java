package com.example.githubtrailblazer.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.githubtrailblazer.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SettingsFragment class
 */
public class SettingsFragment extends Fragment {
    // define UI variables
    private Button mLogoutBtn;
    private Button mDeleAccountBtn;
    private TextView mLibsTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        // empty toolbar container
        LinearLayout toolbarContainer = getActivity().findViewById(R.id.toolbar_container);
        toolbarContainer.removeAllViews();

        // assign UI variables to UI elements
        mLogoutBtn = root.findViewById(R.id.main_logout_btn);
        mDeleAccountBtn = root.findViewById(R.id.main_deleAccount_btn);
        mLibsTextView = root.findViewById(R.id.libs_textview);

        // on-click listener for logging user out and sending them to login activity
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                // if user was signed in with Email
                if(LoginActivity.emailFlag) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
                // if user was signed in with GitHub
                else {
                    Intent intent = new Intent(getContext(), InitialActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });

        // on-click listener for deleting user account and sending them to login activity
        mDeleAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
                Intent intent = new Intent(getContext(), InitialActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });

        // enable clicking on links in libs_textview
        mLibsTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return root;
    }
}